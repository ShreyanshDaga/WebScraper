////////////////////////////////////////////////////////////
// File: JavaSC.java   (short for Java Scraper)
// Author: Shreyansh
// Description: Contains the main method of the program and 
//                       few other methods
/////////////////////////////////////////////////////////////

package javasc;

public class JavaSC 
{    
    /*
    The main method, the entry point of the program, receives input arguments from the user
    */
    public static void main(String[] args) 
    {
        // Initialize Query Variables
        String strQuery = null;
        String strPageNum = null;
        
        // Get the Query and/or page number from the user
        if(args.length > 2 || args.length == 0)
        {
            // Wrong number of arguments
            System.out.println("Error : Improper Arguments. Can not be empty or more than two!");
            System.out.println("Please use  any of the following");
            System.out.println("  java -jar JavaSC.jar <keyword> (e.g. java -jar Assignment.jar \"baby strollers\")");
            System.out.println("  java -jar JavaSC.jar <keyword> <page number> (e.g. java -jar Assignment.jar \"baby strollers\" 2)");
            
            // Exit the program
            System.out.println("Exiting..");                        
        }
        else if(args.length == 2)
        {
            // For Case 2: Query 2
            strQuery = args[0];
            strPageNum = args[1];                        
            
            // Process this Query Request
            System.out.println("Given Query Type 2: " + strQuery + " " + strPageNum);
            System.out.println("Working...");
            ProcessQuery2(strQuery, strPageNum);
        }
        else if(args.length == 1)
        {
            // For Case 1: Query 1
            strQuery = args[0];
            
            // Process this Query Request
            System.out.println("Given Query Type 1: " + strQuery);
            System.out.println("Working...");
            ProcessQuery1(strQuery);
        }                                
    }
    
    /*
    Method:         ProcessQuery1(String strQ)
    Description:    This method processes the given query string for Type 1 and returns the total
                             number of results found for given query string
    Arguments:     The given keyword from the user
    Return Type:    None
    */
    public static void ProcessQuery1(String strQ)
    {
        // Case 1: Query 1
        int iNumOfProd = 0;
                                               
        MyCrawler cr = new MyCrawler();               
        
        iNumOfProd = cr.FindNumberOfProducts(strQ);
        
        System.out.println("---------------------------------------------------");
        System.out.println("| Total Number of Results Found: " + iNumOfProd);
        System.out.println("| Exiting..");
        System.out.println("---------------------------------------------------");
    }

    
    /*
    Method:         ProcessQuery2(String strQ, String strPage)
    Description:    This method processes the given query string and given page number for Type 2, and lists all the
                            Items on the given page number
    Arguments:     The given keyword and page number from the user
    Return Type:    None
    */    
    public static void ProcessQuery2(String strQ, String strPage)
    {
        //  Case 2: Query 2 
        PageItem[] PageItems = null;
        int iPageN = 0;
        
        try
        {
            iPageN = Integer.parseInt(strPage);
            if(iPageN <= 0)
            {
                System.out.println("Error: <page number> argument can not be 0 or less than 0, please enter positive value");
                System.out.println("Exiting..");
                System.exit(0);
            }
        }
        catch(NumberFormatException e)
        {
            System.out.println("Error: Please use numerals for <page number> argument as shown below");
            System.out.println("  java -jar JavaSC.jar <keyword> <page number> (e.g. java -jar Assignment.jar \"baby strollers\" 2)");
            System.out.println("Exiting..");
            System.exit(0);            
        }
        
        MyCrawler cr = new MyCrawler();
        
        PageItems = cr.FindPageItems(strQ, iPageN);
        
        for(PageItem item : PageItems)
        {
            item.WriteItem();
        }        
        System.out.println("| Exiting..");
        System.out.println("---------------------------------------------------");
    }           
}