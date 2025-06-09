package OrderManagerApp;

import java.io.*;
import java.util.*;

// Node class to represent each element in a linked list of orders
class Node {
    Order order;
    Node next;

    public Node(Order order) {
        this.order = order;
        this.next = null; // Initialize next as null
    }
}

// ProductStock class to manage stock levels of a product
class ProductStock {
    int Amount; // Current stock amount
    String productId; // Unique identifier for the product

    public ProductStock(int Amount, String productId) {
        this.Amount = Amount;
        this.productId = productId;
    }

    // Add additional stock to the inventory
    public void FillStock(int additionalQuantity) {
        Amount += additionalQuantity;
    }

    // Check if the required stock is available
    public boolean isAvailable(int NeededAmount) {
        return Amount >= NeededAmount;
    }

    // Decrease stock if the required amount is available
    public void DecreaseInventory(int NeededAmount) {
        if (isAvailable(NeededAmount)) {
            Amount -= NeededAmount;
        }
    }
}

// Order class to represent a customer's order
class Order {
    int orderQuantity; // Quantity of the product ordered
    String customerName; // Name of the customer
    String status; // Status of the order (e.g., Awaiting, Fulfilled)
    boolean Precedence; // Indicates if the order is a priority
    int orderNum; // Unique order number
    String productId; // ID of the product ordered

    public Order(String productId, boolean Precedence, int orderQuantity, String customerName, int orderNum) {
        this.productId = productId;
        this.Precedence = Precedence;
        this.orderQuantity = orderQuantity;
        this.customerName = customerName;
        this.orderNum = orderNum;
        this.status = "Awaiting"; // Default status is Awaiting
    }

    // Format the order details for display
    @Override
    public String toString() {
        return String.format("(The Product: %s  -  Is Priority: %b  -  Status: %s  -  Order Quantity: %d  -  Customer Name: %s   -  ID: %d)",
                productId, Precedence, status, orderQuantity, customerName, orderNum);
    }
}

// Inventory class to manage a list of product stocks
class Inventory {
    List<ProductStock> productRecords; // List of all product stocks

    public Inventory() {
        productRecords = new ArrayList<>(); // Initialize the product records list
    }

    // Update stock by decreasing the inventory for a product
    public void updateStock(String productCode, int Amount) {
        for (ProductStock item : productRecords) {
            if (item.productId.equals(productCode)) {
                item.DecreaseInventory(Amount);
                break;
            }
        }
    }

    // Check if sufficient inventory is available for a product
    public boolean InventoryCheck(String productCode, int Amount) {
        for (ProductStock item : productRecords) {
            if (item.productId.equals(productCode)) {
                return item.isAvailable(Amount);
            }
        }
        return false;
    }

    // Check if a product exists in the inventory
    public boolean productExists(String productCode) {
        for (ProductStock item : productRecords) {
            if (item.productId.equals(productCode)) {
                return true;
            }
        }
        return false;
    }

    // Add new stock to the inventory or update existing stock
    public void addStock(String productCode, int Amount) {
        for (ProductStock item : productRecords) {
            if (item.productId.equals(productCode)) {
                item.FillStock(Amount);
                return;
            }
        }
        productRecords.add(new ProductStock(Amount, productCode));
    }

    // Print the current inventory
    public void printStock() {
        System.out.println("Current Inventory:");
        for (ProductStock item : productRecords) {
            System.out.printf("Product ID: %s, Stock: %d\n", item.productId, item.Amount);
        }
    }
}

// Linked list of orders
class OrderList {
    Node head; // Head of the linked list

    // Add an order to the list
    public void add(Order order) {
        Node newNode = new Node(order);
        if (head == null) {
            head = newNode; // Add the first node if the list is empty
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode; // Add the new node at the end of the list
        }
    }

    // Print all orders in the list
    public void printList() {
        if (head == null) {
            System.out.println("No orders in the list.");
            return;
        }
        Node temp = head;
        while (temp != null) {
            System.out.println("Order Info: " + temp.order);
            temp = temp.next;
        }
    }

    // Check if an order with a specific ID exists
    public boolean orderExists(int orderNum) {
        Node temp = head;
        while (temp != null) {
            if (temp.order.orderNum == orderNum) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    // Remove an order by its ID
    public Order removeById(int orderNum) {
        if (head == null) {
            return null;
        }
        if (orderNum == head.order.orderNum) {
            return removeFirst();
        }
        Node temp = head;
        while (temp.next != null) {
            if (orderNum == temp.next.order.orderNum) {
                Node removedNode = temp.next;
                temp.next = temp.next.next;
                return removedNode.order;
            }
            temp = temp.next;
        }
        return null;
    }

    // Remove the first order in the list
    public Order removeFirst() {
        if (head == null) {
            return null;
        }
        Node temp = head;
        head = head.next; // Update the head to the next node
        return temp.order;
    }
}

// OrderManager class to handle order processing and inventory management
class OrderManager {
    OrderList cancelledOrders; // List of cancelled orders
    OrderList urgentQueue; // Queue for priority orders
    OrderList finalizedOrders; // List of completed orders
    OrderList standardQueue; // Queue for regular orders
    Inventory stockManager; // Inventory manager

    public OrderManager(Inventory stockManager) {
        this.stockManager = stockManager;
        cancelledOrders = new OrderList();
        urgentQueue = new OrderList();
        finalizedOrders = new OrderList();
        standardQueue = new OrderList();
    }

    // Add a new order to the appropriate queue
    public void enqueueOrder(Order order) {
        if (!stockManager.InventoryCheck(order.productId, order.orderQuantity)) {
            System.out.println("Order rejected: Insufficient stock for " + order.productId);
            return;
        }
        stockManager.updateStock(order.productId, order.orderQuantity);
        (order.Precedence ? urgentQueue : standardQueue).add(order);
        System.out.println("++ Added Order: " + order);
    }

    // Show the history of finalized and cancelled orders
    public void showOrderHistory() {
        System.out.println("Finalized Orders:");
        finalizedOrders.printList();
        System.out.println("\nCancelled Orders:");
        cancelledOrders.printList();
    }

    // Cancel an order and return the stock to the inventory
    public void cancelOrder(int orderId) {
        Order order = urgentQueue.removeById(orderId);
        if (order == null) {
            order = standardQueue.removeById(orderId);
        }
        if (order != null) {
            order.status = "Cancelled";
            cancelledOrders.add(order);
            stockManager.addStock(order.productId, order.orderQuantity);
            System.out.println("Cancelled Order: " + order);
        } else {
            System.out.println("No such order exists.");
        }
    }

    // Fulfill the next order in the queue
    public void fulfillOrder() {
        Order order;
        if (urgentQueue.head != null) {
            order = urgentQueue.removeFirst();
        } else {
            order = standardQueue.removeFirst();
        }
        if (order != null) {
            order.status = "Fulfilled";
            finalizedOrders.add(order);
            System.out.println("Fulfilled Order: " + order);
        } else {
            System.out.println("No orders to fulfill.");
        }
    }

    // Print all orders in the queues
    public void printQueues() {
        System.out.println("Urgent Queue:");
        urgentQueue.printList();
        System.out.println("\nStandard Queue:");
        standardQueue.printList();
    }
}

// Main class to run the application
public class OrderManagerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Inventory inventory = new Inventory(); // Create inventory manager
        OrderManager orderManager = new OrderManager(inventory); // Create order manager

        System.out.println("=== Welcome to the Order Management System ===\n");

        while (true) {
            // Display menu options
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Add Order");
            System.out.println("2. Fulfill Order");
            System.out.println("3. Cancel Order");
            System.out.println("4. Show Order History");
            System.out.println("5. Show Queues");
            System.out.println("6. Show Inventory");
            System.out.println("7. Add or Restock Product to Inventory");
            System.out.println("8. Exit\n");

            int choice;
            while (true) {
                System.out.print("Choose an option: ");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice >= 1 && choice <= 8) {
                        break; // Valid choice entered
                    } else {
                        System.out.println("Invalid option. Please choose a number between 1 and 8.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a numeric value.");
                }
            }

            // Process user input
            switch (choice) {
                case 1:
                    // Add a new order
                    System.out.println("\n=== Add Order ===");
                    String productId = "";
                    while (true) {
                        System.out.print("Enter Product ID (or type 'back' to return to the main menu): ");
                        productId = scanner.nextLine();
                        if (productId.equalsIgnoreCase("back")) {
                            break; // Return to main menu
                        }
                        if (inventory.productExists(productId)) {
                            break; // Valid product ID entered
                        } else {
                            System.out.println("Product ID does not exist in inventory. Please add it first.");
                        }
                    }

                    if (productId.equalsIgnoreCase("back")) {
                        continue; // Skip this case
                    }

                    int quantity = 0;
                    while (true) {
                        System.out.print("Enter Order Quantity (or type 'back' to return to the main menu): ");
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("back")) {
                            break; // Return to main menu
                        }
                        try {
                            quantity = Integer.parseInt(input);
                            if (quantity > 0) {
                                if (inventory.InventoryCheck(productId, quantity)) {
                                    break; // Valid quantity
                                } else {
                                    System.out.println("Insufficient stock for this quantity. Try again.");
                                }
                            } else {
                                System.out.println("Order quantity must be greater than zero.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a numeric value.");
                        }
                    }

                    if (quantity == 0) {
                        continue; // Skip this case
                    }

                    System.out.print("Enter Customer Name (or type 'back' to return to the main menu): ");
                    String customerName = scanner.nextLine();
                    if (customerName.equalsIgnoreCase("back")) {
                        continue; // Skip this case
                    }

                    int orderId = -1;
                    while (true) {
                        System.out.print("Enter Order ID (or type 'back' to return to the main menu): ");
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("back")) {
                            break; // Return to main menu
                        }
                        try {
                            orderId = Integer.parseInt(input);
                            if (orderManager.urgentQueue.orderExists(orderId) ||
                                    !orderManager.standardQueue.orderExists(orderId)) {
                                break; // Valid order ID
                            } else {
                                System.out.println("Order ID already exists. Please enter a unique ID.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a numeric value.");
                        }
                    }

                    if (orderId == -1) {
                        continue; // Skip this case
                    }

                    boolean isPriority = false;
                    String priorityInput;
                    while (true) {
                        System.out.print("Is this a priority order? (true/false or 'back' to return to main menu): ");
                        priorityInput = scanner.nextLine().toLowerCase();
                        if (priorityInput.equalsIgnoreCase("back")) {
                            break; // Return to main menu
                        }
                        if (priorityInput.equals("true") || priorityInput.equals("false")) {
                            isPriority = Boolean.parseBoolean(priorityInput);
                            break; // Valid input
                        } else {
                            System.out.println("Invalid input. Please enter 'true' or 'false'.");
                        }
                    }

                    if (priorityInput.equalsIgnoreCase("back")) {
                        continue; // Skip this case
                    }

                    // Create and enqueue a new order
                    Order newOrder = new Order(productId, isPriority, quantity, customerName, orderId);
                    orderManager.enqueueOrder(newOrder);
                    continue;

                case 2:
                    // Fulfill the next order
                    System.out.println("\n=== Fulfill Order ===");
                    orderManager.fulfillOrder();
                    continue;

                case 3:
                    // Cancel an order
                    System.out.println("\n=== Cancel Order ===");
                    int cancelId = -1;
                    while (true) {
                        System.out.print("Enter Order ID to cancel (or type 'back' to return to the main menu): ");
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("back")) {
                            break; // Return to main menu
                        }
                        try {
                            cancelId = Integer.parseInt(input);
                            if (orderManager.urgentQueue.orderExists(cancelId) ||
                                    orderManager.standardQueue.orderExists(cancelId)) {
                                break; // Valid ID
                            } else {
                                System.out.println("Order ID does not exist. Please try again.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a numeric value.");
                        }
                    }

                    if (cancelId == -1) {
                        continue; // Skip this case
                    }

                    // Cancel the order
                    orderManager.cancelOrder(cancelId);
                    continue;

                case 4:
                    // Show the order history
                    System.out.println("\n=== Order History ===");
                    orderManager.showOrderHistory();
                    continue;

                case 5:
                    // Show all orders in the queues
                    System.out.println("\n=== Current Queues ===");
                    orderManager.printQueues();
                    continue;

                case 6:
                    // Display the current inventory
                    System.out.println("\n=== Inventory Status ===");
                    inventory.printStock();
                    continue;

                case 7:
                    // Add or restock a product in the inventory
                    System.out.println("\n=== Add or Restock Product to Inventory ===");
                    String newProductId;
                    while (true) {
                        System.out.print("Enter Product ID (or type 'back' to return to the main menu): ");
                        newProductId = scanner.nextLine();
                        if (newProductId.equalsIgnoreCase("back")) {
                            break; // Return to main menu
                        }
                        if (inventory.productExists(newProductId)) {
                            System.out.println("Product exists. Restocking...");
                            break; // Restock existing product
                        } else {
                            System.out.println("Product does not exist. Adding new product...");
                            break; // Add new product
                        }
                    }

                    if (newProductId.equalsIgnoreCase("back")) {
                        continue; // Skip this case
                    }

                    int newAmount = 0;
                    while (true) {
                        System.out.print("Enter Stock Amount (or type 'back' to return to the main menu): ");
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("back")) {
                            break; // Return to main menu
                        }
                        try {
                            newAmount = Integer.parseInt(input);
                            if (newAmount > 0) {
                                break; // Valid amount
                            } else {
                                System.out.println("Stock amount cannot be negative or zero. Try again.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a numeric value.");
                        }
                    }

                    if (newAmount == 0) {
                        continue; // Skip this case
                    }

                    // Add or restock the product
                    inventory.addStock(newProductId, newAmount);
                    System.out.println("Stock added / restocked successfully!");
                    continue;

                case 8:
                    // Exit the system
                    System.out.println("\nThank you for using the Order Management System. Goodbye!");
                    return;
            }
        }
    }
}
