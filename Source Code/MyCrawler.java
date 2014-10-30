////////////////////////////////////////////////////////////
// File: MyCrawler.java
// Author: Shreyansh
// Description: Contains class defination of 
//                       MyCrawler class and implementation 
//                       of its method
/////////////////////////////////////////////////////////////

package javasc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyCrawler 
{
    public Document docDOM;                                     // Latest Document
    public boolean bIsDOMReady = false;                   //  Is the document ready
    public String strHitURL;                                            // URL to be hit
    public String strBaseURL = "http://www.shopping.com/";
    public String strQuery;
    public String strFinalAnchorURL;    
    public String strFinalAnchorText; 
        
    public MyCrawler()
    {
        // Empty Constructor
    }                
        
    /*
    Method:          FindNumberOfProduct(String strQ)
    Description:    This method returns the results to the Type 1 Query
    Arguments:     The query string which is the keyword given by user 
    Return Type:    It returns the total number of results for the given Query string
    */
    public int FindNumberOfProducts(String strQ)
    {
        int iNumOfProducts = 0;
        
        // Generate the correct Query String from given keywords
        this.strQuery = this.GenerateQueryString(strQ);
        
        // Format the Query string according to www.shopping.com query parameter format
        this.strHitURL = this.GenerateHitURL(this.strQuery);
        
        // Hit the URL with the Query string and get the DOM
        this.docDOM = HitURL(this.strHitURL);
        
        if(IsPageNoMatchFound(this.docDOM))
        {
            // Error if no match is found
            System.out.println("Error: No Matches were found for the Given Query !");
            System.out.println("Exiting..");
            System.exit(0);
        }
        
        // Find the Last Page number in the searched query response
        int iLastPage = FindLastPageNumber(docDOM);               
        
        Elements elemResults = this.docDOM.getElementsByClass("numTotalResults");
        
        // If there are no such elements, Exit
        if(elemResults.isEmpty())
        {            
            System.out.println("Error: No Match Found !");
            System.out.println("Exiting..");
            System.exit(0);
        }
        
        // Find the exact number from the entire HTML string, the exact number is located at 4th position and may contain a ','
        iNumOfProducts = Integer.parseInt(elemResults.first().text().split(" ")[3].replaceAll(",", ""));
        
        return iNumOfProducts;
    }
    
    /*
    Method:          FindPageItems(String strQ, int iPageN)
    Description:    This method returns the array of PageItems objects which are found on given page 
    Arguments:     The query string which is the keyword given by user, and the page number from where to 
                              find all the page items
    Return Type:    It returns an array of PageItem objects
    */
    public PageItem[] FindPageItems(String strQ, int iPageN)
    {
        int iPageItems = 0;
        PageItem[] PageItems= null;
        
        // This URL will hit the required page
        this.docDOM = this.HitPageNumber(strQ, iPageN);
        
        // If the DOM returned contains "No Match Found"
        if(this.IsPageNoMatchFound(docDOM))
        {
            System.out.println("Error: No Match found for given Query Arguments");
            System.out.println("Exiting..");
            System.exit(0);            
        }
        
        // Get the range of items shown on page
        String[] strResultStrings = this.docDOM.getElementsByClass("numTotalResults").get(0).text().split(" ");
        
        // The given result string is of the form, for example, "Results 81 - 120 of 185"
        // We want 120 -80 + 1 = 40 Items on given page
        int iRangeOfItems = Integer.parseInt(strResultStrings[3].replaceAll(",", ""))  - Integer.parseInt(strResultStrings[1].replaceAll(",", ""));
        iRangeOfItems +=1;
        
        // Set the Size of PageItem array
        PageItems = new PageItem[iRangeOfItems];
        
        // Traverse through all the product elements to parse and extract individual product items
        Element elemCurrent = null;
        PageItem pageItem = null;
        
        for(int i = 0;i < iRangeOfItems;i++)
        {
            // Target the tag with Id "quickLookItem-<itemNumber>"
            elemCurrent = this.docDOM.getElementById("quickLookItem-" + (i + 1));
            if(elemCurrent != null)
            {
                // If the tag is present, parse the entire tag to create a PageItem Object
                pageItem = this.ParseItem(elemCurrent, i);
            }               
            // Store the PageItem object in the array
            PageItems[i] = pageItem;
        }
        
        // Return the array of the PageItem objects found
        return PageItems;
    }
    
    
    /*
    Method:          FindLastPageNumber(Document docCurrentPage)
    Description:    This function finds the last page number in the given document's DOM
    Arguments:     A Document object which represents the DOM of the given page
    Return Type:    The last page number on that DOM
    */
    private int FindLastPageNumber(Document docCurrentPage)
    {                                     
        // Target all the anchors inside the 2nd span tag of the tag with class name "paginationNew"
        Elements elemsPagination = docCurrentPage.select(" .paginationNew > span:eq(1) > a");
        
        // Only 1 Page, therefore no ".pagination" class element
        if(elemsPagination.size() == 0)
        {
            // Only 1 Page, with no pagination
            return 1;
        }
        else
        {
            // May be More than 1 page                        
            // Go to the last anchor                        
            Element elemLastAnchor = elemsPagination.last();
            // Get the URL to the last page 
            String strLastAnchorURL = elemLastAnchor.attr("href");
            String strLastAnchorText = elemLastAnchor.text();
            
            // Hit at this anchor to see if the page is indeed the last one
            Document docLastPageDOM = this.HitURL("http://www.shopping.com" + strLastAnchorURL);            
            
            // Again target all the anchors inside the span tag of this new document
            elemsPagination = docCurrentPage.select(" .paginationNew > span:eq(1) > a");
            
            Element elemNewAnchor = elemsPagination.last();
            String strNewAnchorText = elemNewAnchor.text();
            
            if(Integer.parseInt(strLastAnchorText.replaceAll(",", "")) > Integer.parseInt(strNewAnchorText.replaceAll(",", "")))
            {                
                // Our Final was final indeed
                this.strFinalAnchorURL = strLastAnchorURL;
                this.strFinalAnchorText = strLastAnchorText;
                
                // No Need to change the Current DOM document
            }
            else
            {
                // Our Final was not final, and we get New final
                this.strFinalAnchorURL = "http://www.shopping.com" + elemNewAnchor.attr("href");
                this.strFinalAnchorText = strNewAnchorText;
                
                // Update the Current DOM document to reflect that it is indeed the DOM document for the last page
                this.docDOM = docLastPageDOM;
            }                        
        }
        
        // Return the Last Page number for given search Query
        return Integer.parseInt(this.strFinalAnchorText);
    }
    
    
    /*
    Method:          HitPageNumber(String strQ, int iPageNumber)
    Description:    This function will hit the particular page number of given search query string
    Arguments:     Given search query string and the page number to be hit
    Return Type:    The document's DOM object of that particular page number
    */   
    private Document HitPageNumber(String strQ, int iPageNumber)
    {        
        Document docPageDOM = null;
        
        // Generate the HitURL based on given search Query string and page number
        this.strQuery = this.GenerateQueryString(strQ);
        this.strHitURL = this.GenerateHitURL(strQuery, iPageNumber);
        
        // Hit the URL
        docPageDOM = HitURL(strHitURL);
        
        // Return this document
        return docPageDOM;
    }       

    /*
    Method:          GenerateQueryString(String strQ)
    Description:    This method generates the query string which is used by the www.shopping.com to search
    Arguments:     The query string which is the keyword given by user
    Return Type:    It returns the required query string in the required format
    */    
    private String GenerateQueryString(String strQ) 
    {
        String strQuery = null;
        StringTokenizer Tok = null;
        
        try 
        {        
            // Taking care of special charachters here
            Tok = new StringTokenizer(URLEncoder.encode(strQ, "UTF-8"));
        } 
        catch (UnsupportedEncodingException ex) 
        {
            System.out.println("Error: Use of unsupported charachters in Query String");
            System.out.println("Exiting..");
            System.exit(0);
        }
        
        int n = 0;        
        while(Tok.hasMoreElements())
        {
            if(n == 0)
            {
                strQuery = Tok.nextElement().toString();
                n = 1;
            }
            else
            {
                strQuery += "+" +  Tok.nextElement();
            }            
        }
                
        // Return the formed query string
        return strQuery;
    }
    
    
    /*
    Method:          GenerateHitURL(String strQuery)
    Description:    This method generates the URL for a single keyword without page numbers 
                             to be hit using the generated Query string
    Arguments:     The query string which is the generated in the previous function
    Return Type:    It returns the URL to be hit
    */    
    private String GenerateHitURL(String strQuery) 
    {
        String strURL = null;
        
        strURL = this.strBaseURL+strQuery.replaceAll("\\+","-")+"/products?CLT=SCH&KW=" + strQuery.replaceAll("-", "\\+");
        
        return strURL;
    }

   /*
    Method:          GenerateHitURL(String strQuery, int iPageN)
    Description:    This method generates the URL for a single keyword with page number 
                             to be hit using the generated Query string
    Arguments:     The query string which is the generated in the GenerateQuery() function
    Return Type:    It returns the URL to be hit
    */ 
    private String GenerateHitURL(String strQuery, int iPageN)
    {
        String strURL = null;
                
        strURL = this.strBaseURL+strQuery.replaceAll("\\+","-")+"/products~PG-"+ iPageN + "?KW=" + strQuery.replaceAll("-", "\\+");
        
        return strURL;
    }
    
   /*
    Method:          HitURL(String strURL)
    Description:    This method hits the given URL and returns its document's DOM object
    Arguments:     The URL whose document DOM object is needed
    Return Type:    It returns the document
    */  
    private Document HitURL(String strURL)
    {
        try 
        {
            // Hit the URL
            return Jsoup.connect(strURL).get();
            // This will get us the entire documents with all the DOM elements                        
        } 
        catch (IOException ex) 
        {
            // Some IO Error
            System.out.println("Error: IO Exception Occured!");
            System.out.println("Exiting..");
            System.exit(0);
        }
        
        // This wont execute since we are exiting if any exception occurs
        return null;
    }
    
     /*
    Method:          IsPageNoMatchFound(Document docDOM)
    Description:    This method checks if the given document matches that of a "No Match Found" type document
    Arguments:     The document Object
    Return Type:    It returns a boolean, true if page is a "No Match FOund" type of page and false otherwise
    */   
    private boolean IsPageNoMatchFound(Document docDOM)
    {
        // This will search for the span which says "No Matches" found
        if(docDOM.getElementsByClass("nomatch").size() != 0)
        {
            // If such span exist, then Page is a "No Match Found Page"
            return true;
        }
        else
            // Page is not a "No Match Found" Page
            return false;
    }

    /*
    Method:          ParseItem(Element elemCurrent, int i)
    Description:    This method parses an html element to get the PageItem object from it
    Arguments:     The html element to be parsed and a index which tells which element is it in the returned result's grid
    Return Type:    It returns a PageItem object representing the html element 
    */   
    private PageItem ParseItem(Element elemCurrent, int i) 
    {
        String strTitle = "";
        String strPrice = "";
        String strVendor = "";
        String strShipping = "";
        boolean bFShippable = false;
        boolean bNoShipping = false;
        
        // Get the Title
        if(elemCurrent.getElementById("nameQA" + (i+1)) != null)
            strTitle = elemCurrent.getElementById("nameQA" + (i+1)).attr("title");
        
        // Get the Price, which may be displyaed in two possible ways
        if(elemCurrent.select(".productPrice > a").size() != 0)
        {
            strPrice = elemCurrent.select(".productPrice > a").text();            
        }
        else if(elemCurrent.getElementById("priceProductQA" + (i+1)) != null)
        {
            strPrice = elemCurrent.getElementById("priceProductQA" + (i + 1)).text();            
        }
        
        // Get the Vendor
        strVendor = elemCurrent.getElementsByClass("newMerchantName").get(0).text();
        
        // Get number of stores if any
        String strStores;
        if(elemCurrent.getElementById("numStoresQA" + (i+1)) != null)
        {
            strStores= elemCurrent.getElementById("numStoresQA" + (i+1)).text();
            strVendor += ", " + strStores;
        }
        
        // Get the Shipping Info
        if(elemCurrent.getElementsByClass("calc").size() != 0)
        {
            strShipping = elemCurrent.getElementsByClass("calc").first().text();            
        }
        else if(elemCurrent.getElementsByClass("freeShip").size() != 0)
        {
            // Free shipping is available
            strShipping = elemCurrent.getElementsByClass("freeShip").first().text();
            bFShippable = true;
        }
        else if(elemCurrent.getElementsByClass("missCalc").size() != 0)
        {
            // No shipping info available
            strShipping = elemCurrent.getElementsByClass("missCalc").first().text();            
        }
        else
        {
            strShipping = "No Shipping Information found";
            bNoShipping = true;
        }
        
        // Return a new PageItem Object made using the parsed values
        return new PageItem(strTitle, strPrice, strVendor, strShipping, bFShippable, bNoShipping);
    }
}
