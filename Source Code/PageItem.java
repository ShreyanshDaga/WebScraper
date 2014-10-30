////////////////////////////////////////////////////////////
// File: PageItem.java
// Author: Shreyansh
// Description: Contains the defination of the model class
//                       which represents a PageItem or a product 
/////////////////////////////////////////////////////////////
package javasc;

public class PageItem 
{    
    public String strItemName;    
    public boolean bNoShipInfo;
    public boolean bFShippable;
    public String strVendor;    
    public String strItemPrice;
    public String strShipping;
    
    public PageItem()
    {        
        this.bFShippable = false;
        this.strItemPrice = "";
        this.strShipping = "";
        this.strItemName = "";
        this.strVendor = "";
    }

    /* Constrcutor        
    */
    PageItem(String strTitle, String strPrice, String strVendor, String strShipping, boolean bFShippable, boolean bNoShip) 
    {
        this.strItemName = strTitle;
        this.strVendor = strVendor;
        this.bFShippable = bFShippable;
                
        this.strItemPrice = strPrice;        
        this.strShipping = this.ParseShippingPrice(strShipping);                
    }        
    
    
    /*
    Method:          ParseShippingPrice(String strPrice)
    Description:    This method parses what the shipping information is represented by that particular product
    Arguments:     The shipping string which was exteacted from the html tags
    Return Type:    A string which either is 
                                - No Shipping Information
                                - Free Shipping
                                - A string which shows the actual ammount in $ for shipping
    */
    private String ParseShippingPrice(String strPrice)
    {        
        if(strPrice.contains("No"))
        {
            // No shipping information is provided
            this.bNoShipInfo = true;
            return "No Shipping Information";
        }
        
        if(strPrice.contains("Free"))
        {
            // The shipping is free
            this.bFShippable = true;
            return "Free Shipping";
        }                
        
        // Return the price of shipping in $
        // the price is loacted at index position 1 
        // For example in " + $23.00"
        // The shipping price is $23.00
        return strPrice.split(" ")[1];
    }    
    
     /*
    Method:          WriteItem()
    Description:    This method writes the details of the PageItem object
    Arguments:     None
    Return Type:    None
    */
    public void WriteItem()
    {
        System.out.println("------------------------------------------------------");
        System.out.println("| Title/Product Name\t: " + this.strItemName);
        System.out.println("| Price of the product\t: " + this.strItemPrice);
        System.out.println("| Shipping Price\t: " + this.strShipping);
        System.out.println("| Vendor\t\t: " + this.strVendor);        
        System.out.println("------------------------------------------------------");
    }        
 }

