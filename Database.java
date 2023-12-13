/**
 * Tanmay Ghorpade
* Version 1.0 29/7/2023
 */
import java.util.*;
import java.io.*;
import java.lang.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

public class Database
{
    private final static String REPORT_FILE = "dataFiles/reports/report_";
    private final static String PAYMENT_FILE = "dataFiles/reports/payment_";

    private static String MYSQL_JDBC_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/restrodb";
    private static String MYSQL_DB_USER = "root";
    private static String MYSQL_DB_USER_PASSWORD = "";
    public static Connection connection;
    private static Statement statement;
    private ArrayList<Staff> staffList = new ArrayList<Staff>();
    private ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();
    private ArrayList<Order> orderList = new ArrayList<Order>();
    
    private Date date;
    int     todaysOrderCounts;
    int     orderIdCounter = 0;
    /****************************************************************************
     * Constructor
     ***************************************************************************/   
    public Database()
    {
        date = new Date();
        todaysOrderCounts = 0;  //Load order file??
        System.out.println("1");
        
        getDatabaseInstance();
    
    }

    void getDatabaseInstance(){
    try{ 
            if(connection == null || statement == null){
                connection = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_DB_USER, MYSQL_DB_USER_PASSWORD);
                Class.forName(MYSQL_JDBC_DRIVER_CLASS); 
                statement =connection.createStatement();  
            }   
                
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver class not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error occured while executing query: ");
            e.printStackTrace();
        } 
    }

    /****************************************************************************
     * Getter
     ***************************************************************************/
     public ArrayList<Staff> getStaffList()
     {
         return staffList;
     }
     
     public ArrayList<MenuItem> getMenuList()
     {
         return menuList;
     }
     
     public ArrayList<Order> getOrderList()
     {
         return orderList;
     }
     
     public int getTodaysOrderCount()
     {
         return this.todaysOrderCounts;
     }
     
     public int getOrderIdCounter()
     {
         return this.orderIdCounter;
     }
     
    //----------------------------------------------------------
    // Find staff from ID
    //----------------------------------------------------------
    public Staff   findStaffByID(String id)
    {
        Iterator<Staff> it = staffList.iterator();
        Staff           re = null;
        boolean         found = false;
        
        if(id == ""){
            return null;
        }
        
        while (it.hasNext() && !found) {
            re = (Staff)it.next();  
            if( re.getID().equals(id))
            {
                found = true;
            }
        }
        
        if(found)
            return re;
        else        
            return null;
    }
    
    //----------------------------------------------------------
    // Find menu item from ID
    //----------------------------------------------------------
    public MenuItem   findMenuItemByID(int id)
    {
        Iterator<MenuItem> it = menuList.iterator();
        MenuItem           re = null;
        boolean         found = false;
        
        if(id < 0){
            return null;
        }
        
        while (it.hasNext() && !found) {
            re = (MenuItem)it.next();  
            if( re.getID() == id)
            {
                found = true;
            }
        }
        
        if(found)
            return re;
        else        
            return null;
    }
    
    //----------------------------------------------------------
    // Find order from ID
    //----------------------------------------------------------
    public Order   findOrderByID(int id)
    {
        Iterator<Order> it = orderList.iterator();
        Order           re = null;
        boolean         found = false;
        
        if(id < 0){
            return null;
        }
        
        while (it.hasNext() && !found) {
            re = it.next();  
            if( re.getOrderID() == id)
            {
                found = true;
            }
        }
        
        if(found)
            return re;
        else        
            return null;
    }
     /****************************************************************************
     * Manipurate datas
     ***************************************************************************/
     //---------------------------------------------------------------
     // Staff information
     //---------------------------------------------------------------
     //edit staff data
     // rStaff reference the staff 
     // which 1:Lastname 2:Firstname 3:passward
     public final static int EDIT_LAST_NAME = 1;
     public final static int EDIT_FIRST_NAME = 2;
     public final static int EDIT_PASSWORD = 3;
     
     public void editStaffData(String staffID, String newPassword, String newFirstName, String newLastName) throws DatabaseException
     {
        Staff  rStaff = findStaffByID(staffID);
        rStaff.setPassword(newPassword);
        rStaff.setLastName(newLastName);
        rStaff.setFirstName(newFirstName);
    
        try
        {
            updateEmployeeDetails(staffID);//update
        }
        catch(DatabaseException dbe)
        {
            throw dbe;
        }
    }
     
     public void editStaffData(Staff rStaff, int which, String newData) throws DatabaseException
     {
         switch(which)
         {
             case EDIT_LAST_NAME:
                rStaff.setLastName(newData);
             break;
             case EDIT_FIRST_NAME:
                rStaff.setFirstName(newData);
             break;
             case EDIT_PASSWORD:
                rStaff.setPassword(newData);
             break;
             default:
             break;
         }
         
         try
         {
             updateEmployeeDetails(rStaff.getID());
         }
         catch(DatabaseException dbe)
         {
             throw dbe;
         }
     }
     
     public void deleteStaff(Staff rStaff) throws DatabaseException
     {
        try
        {
            deleteEmployeeDetails(rStaff.getID());
        }
        catch(DatabaseException dbe)
        {
            throw dbe;
        }
        staffList.remove(rStaff);
     }
     
     
     public void addStaff(String newID, String newPassward, String newFirstName, String newLastName, boolean isManager) throws DatabaseException
     {
         Staff newStaff;
         if(isManager)
            newStaff = new Manager(newID, newLastName, newFirstName, newPassward);
         else
            newStaff = new Employee(newID, newLastName, newFirstName, newPassward);
         staffList.add(newStaff);
        try
        {
            addEmployeeDetails(newID, isManager);
        }
        catch(DatabaseException dbe)
        {
            throw dbe;
        }
     }

     //---------------------------------------------------------------
     // MenuItem
     //---------------------------------------------------------------
     //edit menu item data
     // rMenuItem reference the MenuItem 
     // which 1:name 2:price 3:type
     public final static int EDIT_ITEM_NAME = 1;
     public final static int EDIT_ITEM_PRICE = 2;
     public final static int EDIT_ITEM_TYPE = 3;
     
     public void editMenuItemData(int id, String newName, double newPrice, byte menuType) throws DatabaseException
     {
         MenuItem rMenuItem = findMenuItemByID(id);
         rMenuItem.setName(newName);
         rMenuItem.setPrice(newPrice);
         rMenuItem.setType(menuType);
         updateMenuFile(id);
        
     }
     
     public void editMenuItemData(MenuItem rMenuItem, int which, String newData) throws DatabaseException
     {
         try
         {
             switch(which)
             {
                 case EDIT_ITEM_NAME:
                    rMenuItem.setName(newData);
                    break;
                 case EDIT_ITEM_PRICE:
                    double newPrice = Double.parseDouble(newData);
                    if(newPrice < 0)
                        throw new DatabaseException("Price must be positive number");
                    else
                        rMenuItem.setPrice(newPrice);
                    break;
                 case EDIT_ITEM_TYPE:
                    byte newType = Byte.parseByte(newData);
                    if(newType < MenuItem.MAIN || MenuItem.DESSERT < newType)
                        throw new DatabaseException("Type must be between " + MenuItem.MAIN
                                            + " and " + MenuItem.DESSERT + ")");
                    else
                        rMenuItem.setType(Byte.parseByte(newData));
                    break;
                 default:
                    break;
             }
        }
        catch(DatabaseException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new DatabaseException(e.getMessage());
        }
     }
     
     public void setMenuItemAsPromotionItem(MenuItem rMenuItem, double price)
     {
         rMenuItem.setState(MenuItem.PROMOTION_ITEM, price);
     }
     
     public void resetMenuState(MenuItem rMenuItem)
     {
         rMenuItem.resetState();
     }
     
     public void deleteMenuItem(MenuItem rMenuItem) throws DatabaseException
     {
        deleteMenuItems(rMenuItem.getID());
        menuList.remove(rMenuItem);
     }
     
     public void addMenuItem(int newID, String newName, double newPrice, byte newType) throws DatabaseException
     {
         MenuItem newMenuItem = new MenuItem(newID, newName,newPrice, newType);
         menuList.add(newMenuItem);
         Collections.sort(menuList, new MenuItemComparator());
         addMenuItem(newID);
     }
     //---------------------------------------------------------------
     // Order
     //---------------------------------------------------------------
     public int addOrder(String staffID, String staffName)
     {
         int newOrderID = ++orderIdCounter;
         Order newOrder = new Order(staffID, staffName);
         newOrder.setOrderID( newOrderID);
         orderList.add(newOrder);
         insertOrderDetailsInDB(newOrderID,staffID,staffName);
         return newOrderID;
     }
     
     public void addOrderItem(int orderID, MenuItem rItem, byte quantity, boolean isAdding)
     {
         Order rOrder = findOrderByID(orderID);
         rOrder.addItem(rItem, quantity);
         if(isAdding){
         insertOrderItemsInDB(rOrder, rItem, quantity);
         }
     }
     
     public boolean deleteOrderItem(int orderID, int index)
     {
          Order rOrder = findOrderByID(orderID);
          if(rOrder == null)
            return false;

        ArrayList<OrderDetail> arrOrder = rOrder.getOrderDetail();
        deleteOrderItemsInDB(rOrder,arrOrder.get(index).getItemID());
          return rOrder.deleteItem(index);
     }
     
     
     //Cancel order: order data is not deleted from the database(Just put cancel flag on)
     public boolean cancelOrder(int orderID)
     {
         Order rOrder = findOrderByID(orderID);
        if(rOrder == null)
            return false;
         rOrder.setState(Order.ORDER_CANCELED);
        updateOrderDetailsInDB(orderID, Order.ORDER_CANCELED);
         return true;
     }
     //Delete order: order data is deleted from the database
     public boolean deleteOrder(int orderID)
     {
         Order rOrder = findOrderByID(orderID);
        if(rOrder == null)
            return false;
         orderList.remove(rOrder);
         todaysOrderCounts--;
         orderIdCounter--;
         deletOrderDetailsInDB(rOrder);
         return true;
     }
     
     public boolean closeOrder(int orderID)
     {
         Order rOrder = findOrderByID(orderID);
        if(rOrder == null)
            return false;
         rOrder.setState(Order.ORDER_CLOSED);
         updateOrderDetailsInDB(orderID, Order.ORDER_CLOSED);
         return true;
     }
     
     public void closeAllOrder()
     {
        Iterator<Order> it = orderList.iterator();
        Order           re = null;
        
        while (it.hasNext()) {
            re = it.next();  
            if( re.getState() == 0)//neither closed and canceled
            {
                re.setState(Order.ORDER_CLOSED);
                updateOrderDetailsInDB(re.getOrderID(), Order.ORDER_CLOSED);
            }
        }
     }
     
     public int getOrderState(int orderID)
     {
         Order  re = findOrderByID(orderID);
         if(re == null)
             return -1;
         return re.getState();
     }
     
     public double getOrderTotalCharge(int orderID)
     {
         Order  re = findOrderByID(orderID);
         if(re == null)
             return -1;
         return re.getTotal();
     }
     
     public boolean checkIfAllOrderClosed()
     {
        Iterator<Order> it = orderList.iterator();
        Order           re = null;
        
        while (it.hasNext()) {
            re = it.next();  
            if( re.getState() == 0)//neither closed and canceled
            {
                return false;
            }
        }
        return true;
     }
     
     public boolean checkIfAllStaffCheckout()
     {
        Iterator<Staff> it = staffList.iterator();
        Staff           re = null;
        
        while (it.hasNext()) {
            re = it.next();  
            if( re.getWorkState() == Staff.WORKSTATE_ACTIVE)
            {
                return false;
            }
        }
        return true;
     }
     
     public void forthClockOutAllStaff()
     {
         Iterator<Staff> it = staffList.iterator();
         Staff           re = null;
        
         while (it.hasNext()) {
            re = it.next();  
            if( re.getWorkState() == Staff.WORKSTATE_ACTIVE)
            {
                //re.clockOut(new Date(System.currentTimeMillis()));
                Date currDate= new Date(System.currentTimeMillis());
                re.clockOut(currDate);
                markAttendanceInDB(re.getID(), currDate, "CLOCK_OUT");
            }
         }
     }
     /****************************************************************************
    * File load
    ***************************************************************************/
    public void loadFiles() throws DatabaseException
    {
        loadEmployeeDetails();
        // loadManagerFile();
        Collections.sort(staffList, new StaffComparator());
        loadMenuFile();
        loadOrdersDetails();
        // loadWageInfoFile();
    }
    
    private void loadEmployeeDetails() throws DatabaseException
    {
        try{
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_details ;"); 

            while(resultSet.next())  {

                String id = (""+resultSet.getString(2)).trim();
                String passward = (""+resultSet.getString(8)).trim();
                String firstName = (""+resultSet.getString(4)).trim();
                String lastName = (""+resultSet.getString(5)).trim();
                double wages =(resultSet.getDouble(9));
                // Add the data from file to the registerCourses array list
                if(resultSet.getString(3).equals("MANAGER")){
                    Manager rManager = new Manager(id,lastName,firstName, passward);
                    staffList.add(rManager);
                }else{
                    Employee rEmployee = new Employee(id,lastName, firstName, passward);
                    staffList.add(rEmployee);
                }
                Staff rStaff = findStaffByID(id);
                if(rStaff != null){
                    rStaff.setWageRate(wages);
                }

            }
            
            
        }catch(Exception e){
            e.printStackTrace();
            String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        } 
        //     try {
        //     BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE));
        //     String line = reader.readLine();

        //     while (line != null) {
        //         String[] record = line.split(",");

        //         String id = record[0].trim();
        //         String passward = record[1].trim();
        //         String firstName = record[2].trim();
        //         String lastName = record[3].trim();

        //         // Add the data from file to the registerCourses array list
        //         Employee rEmployee = new Employee(Integer.parseInt(id),lastName, firstName, passward);
        //         staffList.add(rEmployee);
        //         line = reader.readLine();
        //     }
        //     reader.close();
        // } catch (IOException ioe) {
        //     String message = ioe.getMessage() + ioe.getStackTrace();
        //     throw new DatabaseException(message);
        // }
    }

    public void markAttendanceInDB(String userId,Date currDate, String flag)
    {
     try{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(flag.equals("CLOCK_IN")){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM restrodb.attendance_details where user_id = ? and attendance_dt= ?;");
            ps.setString(1, userId);
            Date date = new Date();
                // System.out.println(dateFormat.format(date));
            ps.setString(2,  dateFormat.format(date));
            ResultSet rs = ps.executeQuery();
            if(!rs.isBeforeFirst()){
                PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO `restrodb`.`attendance_details` (`user_id`,`attendance_dt`,`clock_intime`,`work_state`) VALUES (?,?,?,?);");
                insertStmt.setString(1, userId);
                insertStmt.setString(2, dateFormat.format(date));
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String s = formatter.format(currDate);            
                insertStmt.setString(3, s);
                insertStmt.setInt(4, 1);
                insertStmt.executeUpdate();
            }
            else {
                
                int work_state = 0;
                while(rs.next())  {
                    work_state = (rs.getInt(6));
                }
                if(work_state!=1){
                PreparedStatement updateStmt = connection.prepareStatement("UPDATE `restrodb`.`attendance_details` set `clock_intime`=? , `work_state`= ?, `clock_outtime`=? WHERE `user_id` = ? and attendance_dt =?;");
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String s = formatter.format(currDate);
                updateStmt.setString(1, s);
                updateStmt.setInt(2, 1);
                updateStmt.setString(3, null);
                updateStmt.setString(4, userId);
                updateStmt.setString(5, dateFormat.format(date));
                updateStmt.executeUpdate();
                }
            } 
            Iterator<Staff> it = staffList.iterator();
                    while (it.hasNext())
                    {
                        Staff re = it.next();
                        PreparedStatement psp = connection.prepareStatement("SELECT * FROM restrodb.attendance_details where user_id = ? and attendance_dt= ?;");
                        psp.setString(1, re.getID());
                        Date date1 = new Date();
                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                            // System.out.println(dateFormat.format(date));
                        psp.setString(2,  dateFormat1.format(date1));
                        ResultSet rsp = psp.executeQuery();
                        while(rsp.next())  {
                            String clock_intime = (rsp.getString(4));
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date clockdate=formatter.parse(clock_intime);  
                            re.changeStartTime(clockdate);
                            int state = (rsp.getInt(6));
                            if(state == 2){
                                String clock_outtime = (rsp.getString(5));
                                Date clockoutdt=formatter.parse(clock_outtime);  
                                re.changeFinishTime(clockoutdt);
                            }
                            re.setWorkState((byte)state);                        }
                    }
        } else if(flag.equals("CLOCK_OUT")){
                 PreparedStatement updateStmt = connection.prepareStatement("UPDATE `restrodb`.`attendance_details` set `clock_outtime`=? , `work_state`= ? WHERE `user_id` = ? and attendance_dt =?;");
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String s = formatter.format(currDate);
                updateStmt.setString(1, s);
                updateStmt.setInt(2, 2);
                updateStmt.setString(3, userId);
                updateStmt.setString(4, dateFormat.format(date));
                updateStmt.executeUpdate();
        }            
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
        }
    }
 
    private void loadManagerFile() throws DatabaseException
    {
        try{
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_details WHERE user_type = 'MANAGER'"); 

            while(resultSet.next())  {
                System.out.println(resultSet.getInt(1)+"  "+resultSet.getString(2));

                String id = (""+resultSet.getString(2)).trim();
                String passward = (""+resultSet.getString(8)).trim();
                String firstName = (""+resultSet.getString(4)).trim();
                String lastName = (""+resultSet.getString(5)).trim();

                // Add the data from file to the registerCourses array list
                Manager rManager = new Manager(id,lastName,firstName, passward);
                staffList.add(rManager);

            } 
        }catch(Exception e){
            e.printStackTrace();
            String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        } 
        // try {
        //     BufferedReader reader = new BufferedReader(new FileReader(MANAGER_FILE));
        //     String line = reader.readLine();

        //     while (line != null) {
        //         String[] record = line.split(",");

        //         String id = record[0].trim();
        //         String passward = record[1].trim();
        //         String firstName = record[2].trim();
        //         String lastName = record[3].trim();

        //         // Add the data from file to the registerCourses array list
        //         Manager rManager = new Manager(Integer.parseInt(id),lastName,firstName, passward);
        //         staffList.add(rManager);
        //         line = reader.readLine();
        //     }
        //     reader.close();
        // } catch (IOException ioe) {
        //     String message = ioe.getMessage() + ioe.getStackTrace();
        //     throw new DatabaseException(message);
        // }
    }
    
    private void loadMenuFile() throws DatabaseException
    {
       
       try{
            ResultSet resultSet = statement.executeQuery("SELECT * FROM menu_item"); 

            while(resultSet.next())  {
                System.out.println(resultSet.getInt(1)+"  "+resultSet.getString(2));

                String id = (""+resultSet.getString(2)).trim();
                String name = (""+resultSet.getString(3)).trim();
                String price = (""+resultSet.getString(4)).trim();
                String type = (""+resultSet.getString(5)).trim();
                
                // Add the data from file to the registerCourses array list
                MenuItem rMenuItem = new MenuItem(Integer.parseInt(id), name, Double.parseDouble(price), Byte.parseByte(type));
                menuList.add(rMenuItem);

            } 
        }catch(Exception e){
            e.printStackTrace();
            String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        } 
        // try {
        //     BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE));
        //     String line = reader.readLine();

        //     while (line != null) {
        //         String[] record = line.split(",");

        //         String id = record[0].trim();
        //         String name = record[1].trim();
        //         String price = record[2].trim();
        //         String type = record[3].trim();

        //         // Add the data from file to the registerCourses array list
        //         MenuItem rMenuItem = new MenuItem(Integer.parseInt(id), name, Double.parseDouble(price), Byte.parseByte(type));
        //         menuList.add(rMenuItem);
        //         line = reader.readLine();
        //     }
        //     reader.close();
        // } catch (IOException ioe) {
        //     String message = ioe.getMessage() + ioe.getStackTrace();
        //     throw new DatabaseException(message);
        // }
    }
private void loadOrdersDetails()
    {
       
       try{
            ResultSet resultSet = statement.executeQuery("SELECT * FROM restrodb.cust_orders where orderDate = CURDATE() order by orderID asc;"); 

            while(resultSet.next())  {
                
                int orderID = (resultSet.getInt(2));
                String staffID = (""+resultSet.getInt(3)).trim();
                String staffName = (""+resultSet.getString(4)).trim();
                int state = (resultSet.getInt(6));
                //String total = (""+resultSet.getDouble(5)).toString();
                Order mOrder = new Order(staffID, staffName);
                mOrder.setOrderID( orderID);
                mOrder.setState(state);
                orderList.add(mOrder);

                this.todaysOrderCounts = orderID;
            } 


            Iterator<Order> it = orderList.iterator();
            Order           re = null;
            while (it.hasNext()) {
                re = it.next();
                int orderID = re.getOrderID();
                ResultSet orderResultSet = statement.executeQuery("SELECT * FROM restrodb.order_details where orderID = "+orderID); 
                while(orderResultSet.next())  {
                    int itemID = (orderResultSet.getInt(3));
                    byte itemQuantity = (byte)(orderResultSet.getInt(6));
                    MenuItem  rNewItem = findMenuItemByID(itemID);
                    if(rNewItem != null)
                    {
                        addOrderItem(orderID, rNewItem, itemQuantity, false);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            String message = e.getMessage() + e.getStackTrace();
        } 
        
    }

    public void insertOrderDetailsInDB(int newOrderID, String staffID, String staffName)
    {
     try{ 
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO `restrodb`.`cust_orders` (`orderID`,`staffID`,`staffName`,`orderDate`,`state`) VALUES (?,?,?,?,?);");
            insertStmt.setInt(1, newOrderID);
            insertStmt.setString(2, staffID);
            insertStmt.setString(3, staffName);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            // System.out.println(dateFormat.format(date));
            insertStmt.setString(4, dateFormat.format(date));
            insertStmt.setInt(5, 0);
            insertStmt.executeUpdate();
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
        }
    }
    
    public void updateOrderDetailsInDB(int orderID, int state)  
    {
     try{ 

            PreparedStatement updateStmt = connection.prepareStatement("UPDATE `restrodb`.`cust_orders` SET `state` = ? WHERE `orderID` = ?;");
            updateStmt.setInt(1, state);
            updateStmt.setInt(2, orderID);
            updateStmt.executeUpdate();
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
        }
    }

    public void deletOrderDetailsInDB(Order rOrder)
    {
     try{ 

        PreparedStatement ps = connection.prepareStatement("DELETE FROM restrodb.cust_orders where orderID = ?;");
        ps.setInt(1, rOrder.getOrderID());
        ps.execute();
            
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
        }
    }
    

    public void insertOrderItemsInDB(Order rOrder, MenuItem rItem, byte quantity)
    {
     try{ 

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM restrodb.order_details where orderID = ? and itemID= ?;");
        ps.setInt(1, rOrder.getOrderID());
        ps.setInt(2,  rItem.getID());
        ResultSet rs = ps.executeQuery();
        if(!rs.isBeforeFirst()){
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO `restrodb`.`order_details` (`orderID`,`itemID`,`itemName`,`price`,`quantity`) VALUES (?,?,?,?,?);");
            insertStmt.setInt(1, rOrder.getOrderID());
            insertStmt.setInt(2, rItem.getID());
            insertStmt.setString(3, rItem.getName());
            insertStmt.setDouble(4, rItem.getPrice());
            insertStmt.setInt(5, quantity);
            insertStmt.executeUpdate();
        }
        else {
            int iQuantity = 0;
             while(rs.next())  {
                iQuantity = (rs.getInt(6));
             }
            PreparedStatement updateStmt = connection.prepareStatement("UPDATE `restrodb`.`order_details` set `orderID`=? , `itemID`= ? , `itemName` =? ,`price` =? ,`quantity`=? WHERE `orderID` = ? and itemID =?;");
            updateStmt.setInt(1, rOrder.getOrderID());
            updateStmt.setInt(2, rItem.getID());
            updateStmt.setString(3, rItem.getName());
            updateStmt.setDouble(4, rItem.getPrice());
            updateStmt.setInt(5, (iQuantity+quantity));
            updateStmt.setInt(6, rOrder.getOrderID());
            updateStmt.setInt(7, rItem.getID());
            updateStmt.executeUpdate();
        } 
            
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
        }
    }
    
    public void deleteOrderItemsInDB(Order rOrder, int rItem)
    {
     try{ 

        PreparedStatement ps = connection.prepareStatement("DELETE FROM restrodb.order_details where orderID = ? and itemID= ?;");
        ps.setInt(1, rOrder.getOrderID());
        ps.setInt(2,  rItem);
        ps.execute();
            
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
        }
    }
    
    // private void loadWageInfoFile() throws DatabaseException
    // {
    //     try {
    //         BufferedReader reader = new BufferedReader(new FileReader(WAGE_INFO_FILE));
    //         String line = reader.readLine();

    //         while (line != null) {
    //             String[] record = line.split(",");

    //             String id = record[0].trim();
    //             String rate = record[1].trim();
                
    //             double dRate = Double.parseDouble(rate);
    //             String iId = (id);
                
    //             Staff rStaff = findStaffByID(iId);
    //             if(rStaff == null)
    //             {
    //                 throw new DatabaseException("Load wage file error\n Staff ID:" + iId + " is not found.");
    //             }
    //             rStaff.setWageRate(dRate);

    //             line = reader.readLine();
    //         }
    //         reader.close();
    //     } catch (IOException ioe) {
    //         String message = ioe.getMessage() + ioe.getStackTrace();
    //         throw new DatabaseException(message);
    //     }
    //     catch(Exception e)
    //     {
    //         String message = e.getMessage() + e.getStackTrace();
    //         throw new DatabaseException(message);
    //     }
    // }
    
    /****************************************************************************
    * File Edit
    ***************************************************************************/
    public void updateEmployeeDetails(String empID) throws DatabaseException
    {

        try{ 
            Staff rStaff = findStaffByID(empID);
            // String strQuery = "UPDATE `restrodb`.`user_details` SET `first_name` = "+ rStaff.getFirstName()+",`last_name` = "+ rStaff.getLastName()+
            //     ", `password` = "+ rStaff.getPassword()+",`modify_at` = CURRENT_TIMESTAMP WHERE `user_id` = "+empID+";";
            // ResultSet resultSet = statement.executeQuery(strQuery); 

            PreparedStatement updateStmt = connection.prepareStatement("UPDATE `restrodb`.`user_details` SET `first_name` = ?,`last_name` = ?, `password` = ?, `wages` = ?,`modify_at` = ? WHERE `user_id` = ?;");
            updateStmt.setString(1, rStaff.getFirstName());
            updateStmt.setString(2, rStaff.getLastName());
            updateStmt.setString(3, rStaff.getPassword());
            updateStmt.setString(4, ""+rStaff.getWageRate());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            updateStmt.setTimestamp(5, timestamp);
            updateStmt.setString(6, empID);
            updateStmt.executeUpdate();

        }catch(Exception e){
            String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        }

        // Writer          writer;
        // String          id;
        // String          line;
        // String          fileName;
        // String          tempFileName = "dataFiles/temp.txt";
            
        
        // Collections.sort(staffList, new StaffComparator());
        // File tempFile = new File(tempFileName);
     
        // try{
        //     writer = new BufferedWriter(new FileWriter(tempFile));
        //     Iterator it = staffList.iterator();
        
        //     while (it.hasNext())
        //     {
        //         Staff re = (Staff)it.next();
            
        //         //-------- skip writing data ----------
        //         if(isManager)
        //         {
        //             //skip employee data
        //             if(re instanceof Employee)
        //             //if(re.getClass().getName().equalsIgnoreCase("Employee"))
        //             continue;
        //         }
        //         else
        //         {
        //             //skip managere data
        //             //if(re.getClass().getName().equalsIgnoreCase("Manager"))
        //             if(re instanceof Manager)
        //             continue;
        //         }
            
        //         writer.write(re.getID() + "," + re.getPassword() + "," + re.getFirstName() + "," + re.getLastName()+ "\r\n");
        //     }
        //     writer.flush();
        //     writer.close();
        // }
        // catch(IOException e)
        // {
        //     String message = e.getMessage() + e.getStackTrace();
        //     throw new DatabaseException(message);
        // }
        
        // //delete current file
        // File deleteFile = new File(fileName);
        // deleteFile.delete();
        
        // // renames temporaly file to new file
        // File newFile = new File(fileName);  
        // tempFile.renameTo(newFile);
        
        //updateWageFile();
    }

    
    public void deleteEmployeeDetails(String empID) throws DatabaseException
    {
     try{ 
            PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM `restrodb`.`user_details`  WHERE `user_id` = ? ;");
            deleteStmt.setString(1, empID);
            deleteStmt.executeUpdate();
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        }
    }

    public void addEmployeeDetails(String empID, boolean isManager) throws DatabaseException
    {
     try{ 
            Staff rStaff = findStaffByID(empID);
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO `restrodb`.`user_details` (`user_id`,`user_type`,`first_name`,`last_name`,`contact_no`,`address`,`password`,`wages`,`created_at`,`modify_at`) VALUES (?,?,?,?,?,?,?,?,?,?);");
            insertStmt.setString(1, rStaff.getID());
            if(isManager)
                insertStmt.setString(2, "MANAGER");
            else
                insertStmt.setString(2, "STAFF");

            insertStmt.setString(3, rStaff.getFirstName());
            insertStmt.setString(4, rStaff.getLastName());
            insertStmt.setString(5, "9999999999");
            insertStmt.setString(6, "BADLAPUR");
            insertStmt.setString(7, rStaff.getPassword());
            if(isManager)
                insertStmt.setString(8, "200");
            else
                insertStmt.setString(8, "100");

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            insertStmt.setTimestamp(9, timestamp);
            insertStmt.setTimestamp(10, timestamp);
            insertStmt.executeUpdate();
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        }
    }
    
    // public void updateWageFile() throws DatabaseException
    // {
    //     Writer          writer;
    //     String          id;
    //     String          line;
    //     String          fileName;
    //     String          tempFileName = "dataFiles/temp.txt";
            
    //     File tempFile = new File(tempFileName);
     
    //     try{
    //         writer = new BufferedWriter(new FileWriter(tempFile));
    //         Iterator it = staffList.iterator();
        
    //         while (it.hasNext())
    //         {
    //             Staff re = (Staff)it.next();
    //             writer.write(re.getID() + "," + re.getWageRate() + "\r\n");
    //         }
    //         writer.flush();
    //         writer.close();
    //     }
    //     catch(IOException e)
    //     {
    //         String message = e.getMessage() + e.getStackTrace();
    //         throw new DatabaseException(message);
    //     }
        
    //     //delete current file
    //     File deleteFile = new File(WAGE_INFO_FILE);
    //     deleteFile.delete();
        
    //     // renames temporaly file to new file
    //     File newFile = new File(WAGE_INFO_FILE);  
    //     tempFile.renameTo(newFile);
    // }
    
    public void updateMenuFile(int menuid) throws DatabaseException
    {
        try{ 
            MenuItem menu = findMenuItemByID(menuid);
            // String strQuery = "UPDATE `restrodb`.`menu_item` SET `menu_name` = "+ menu.getName()+",`price` = "+ menu.getPrice()+
            //     ", `menu_type` = "+ menu.getType()+",`updated_at` = CURRENT_TIMESTAMP WHERE `menu_id` = "+menuid+";";
            // ResultSet resultSet = statement.executeQuery(strQuery); 

            PreparedStatement updateStmt = connection.prepareStatement("UPDATE `restrodb`.`menu_item` SET `menu_name` = ?,`price` = ?, `menu_type` = ?,`updated_at` = ? WHERE `menu_id` = ?;");
            updateStmt.setString(1, menu.getName());
            updateStmt.setString(2, ""+menu.getPrice());
            updateStmt.setString(3, ""+menu.getType());  
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            updateStmt.setTimestamp(4, timestamp);          
            updateStmt.setString(5, ""+menuid);
            updateStmt.executeUpdate();
        }catch(Exception e){
            String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        }

        // Writer          writer;
        // String          id;
        // String          line;
        // String          tempFileName = "dataFiles/temp.txt";
        
        // //Collections.sort(menuList, new MenuItemComparator());
        // File tempFile = new File(tempFileName);
     
        // try{
        //     writer = new BufferedWriter(new FileWriter(tempFile));
        //     Iterator it = menuList.iterator();
        
        //     while (it.hasNext())
        //     {
        //         MenuItem re = (MenuItem)it.next();

        //         writer.write(re.getID() + "," + re.getName() + "," + re.getPrice() + "," + re.getType()+ "\r\n");
        //     }
        //     writer.flush();
        //     writer.close();
        // }
        // catch(IOException e)
        // {
        //     String message = e.getMessage() + e.getStackTrace();
        //     throw new DatabaseException(message);
        // }
        
        // //delete current file
        // File deleteFile = new File(MENU_FILE);
        // deleteFile.delete();
        
        // // renames temporaly file to new file
        // File newFile = new File(MENU_FILE);  
        // tempFile.renameTo(newFile);
    }
    
     public void deleteMenuItems(int menuid) throws DatabaseException
    {
     try{ 

            PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM `restrodb`.`menu_item`  WHERE `menu_id` = ? ;");
            deleteStmt.setInt(1, menuid);
            deleteStmt.executeUpdate();

        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        }
    }

    public void addMenuItem(int menu_id) throws DatabaseException
    {
     try{ 
            MenuItem menu = findMenuItemByID(menu_id);
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO `restrodb`.`menu_item` (`menu_id`,`menu_name`,`price`,`menu_type`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?);");
            insertStmt.setString(1, ""+menu.getID());
            insertStmt.setString(2, menu.getName());
            insertStmt.setString(3, ""+menu.getPrice());
            insertStmt.setString(4, ""+ menu.getType());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            insertStmt.setTimestamp(5, timestamp);
            insertStmt.setTimestamp(6, timestamp);
            insertStmt.executeUpdate();
        }catch(Exception e){
             String message = e.getMessage() + e.getStackTrace();
            throw new DatabaseException(message);
        }
    }

    public String generateOrderReport( String todaysDate) throws DatabaseException
    {
        Writer          writer = null;
        String          line;
        int             state;
        double          totalAllOrder = 0;
        String          generateFileName;
        File            newFile;
        int             orderCnt = 0;
        int             cancelCnt = 0;
        double          cancelTotal = 0;
        
        String[] record = todaysDate.split("/");
        String today = record[0].trim() + "_" + record[1].trim() + "_" + record[2].trim();
        generateFileName = REPORT_FILE + today + ".txt";
        newFile = new File(generateFileName);
        
        try{
            writer = new BufferedWriter(new FileWriter(newFile));

            line = "*********** Order List (" + today + ") ***********\r\n";
            writer.write(line);
            
            Iterator<Order> it = orderList.iterator();
            while (it.hasNext())
            {
                Order re = it.next();
                state = re.getState();
                String stateString = "";
                double totalOfEachOrder = re.getTotal();
                switch(state)
                {
                    case Order.ORDER_CLOSED:
                        stateString = "";
                        totalAllOrder += totalOfEachOrder;
                        orderCnt++;
                    break;
                    case Order.ORDER_CANCELED:
                        stateString = "Canceled";
                        cancelTotal += totalOfEachOrder;
                        cancelCnt++;
                    break;
                    default:
                        stateString = "";
                        totalAllOrder += totalOfEachOrder;
                        orderCnt++;
                    break;
                }
                String output = String.format("Order ID:%4d  StaffName:%-30s  Total:\u20B9%-5.2f %s\r\n",
                                            re.getOrderID(),re.getStaffName(),totalOfEachOrder, stateString);
                writer.write(output);
                
                
            }
            writer.write("-------------------------------------------------------\r\n");
            
            writer.write("Total sales:\u20B9" + totalAllOrder + "(" + orderCnt + ")" +
                    "  Canceled:\u20B9" + cancelTotal + "(" + cancelCnt + ")\r\n");
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            String message = e.getMessage() + e.getStackTrace();
            newFile.delete();
            throw new DatabaseException(message);
        }
        return generateFileName;
        //System.out.println("File <" + generateFileName + "> has been generated.");
    }
    
    public String generatePaymentReport( String todaysDate) throws DatabaseException
    {
        Writer          writer = null;
        String          line;
        double          totalPayment = 0;
        String          generateFileName;
        File            newFile;
        int             staffNum = 0;
        
        String[] record = todaysDate.split("/");
        String today = record[0].trim() + "_" + record[1].trim() + "_" + record[2].trim();
        generateFileName = PAYMENT_FILE + today + ".txt";
        newFile = new File(generateFileName);
        
        try{
            writer = new BufferedWriter(new FileWriter(newFile));

            line = "*********** Payment List (" + today + ") ***********\r\n";
            writer.write(line);
            
            Iterator<Staff> it = staffList.iterator();
            while (it.hasNext())
            {
                Staff re = it.next();
 
                if(re.getWorkState() == Staff.WORKSTATE_FINISH)
                {
                    double pay = re.culculateWages();
                    String output = String.format("Order ID:%4d  StaffName:%-30s  Work time:%-5.2f Pay:%-5.2f\r\n",
                                                Integer.parseInt(re.getID()),re.getFullName(),re.culculateWorkTime(), pay);
                    writer.write(output);
                    staffNum++;
                    totalPayment += pay;
                }
            }
            writer.write("-------------------------------------------------------\r\n");
            
            writer.write("Total payment:\u20B9" + totalPayment + "(" + staffNum + ")\r\n");
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            String message = e.getMessage() + e.getStackTrace();
            newFile.delete();
            throw new DatabaseException(message);
        }

// PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO `restrodb`.`user_details` (`user_id`,`user_type`,`first_name`,`last_name`,`contact_no`,`address`,`password`,`wages`,`created_at`,`modify_at`) VALUES (?,?,?,?,?,?,?,?,?,?);");
//             insertStmt.setString(1, rStaff.getID());
//             if(isManager)
//                 insertStmt.setString(2, "MANAGER");
//             else
//                 insertStmt.setString(2, "STAFF");

//             insertStmt.setString(3, rStaff.getFirstName());
//             insertStmt.setString(4, rStaff.getLastName());
//             insertStmt.setString(5, "9999999999");
//             insertStmt.setString(6, "BADLAPUR");
//             insertStmt.setString(7, rStaff.getPassword());
//             if(isManager)
//                 insertStmt.setString(8, "200");
//             else
//                 insertStmt.setString(8, "100");

//             Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//             insertStmt.setTimestamp(9, timestamp);
//             insertStmt.setTimestamp(10, timestamp);
//             insertStmt.executeUpdate();

        return generateFileName;
    }
    
    /****************************************************************************
    * Comparator
    ***************************************************************************/
    private class StaffComparator implements Comparator<Staff> {

        @Override
        public int compare(Staff s1, Staff s2) {
            return s1.getID().compareToIgnoreCase(s2.getID());
        }
    }
    
    private class MenuItemComparator implements Comparator<MenuItem> {

        @Override
        public int compare(MenuItem m1, MenuItem m2) {
            return m1.getID() < m2.getID() ? -1 : 1;
        }
    }
}
