# 📦 Java Order Management System

A console-based Java application that manages inventory, handles customer orders, and supports both urgent and standard queues. Built with object-oriented programming principles and linked lists.

## 🧠 Key Features
- Add, fulfill, and cancel customer orders  
- Distinction between urgent (priority) and standard orders  
- Real-time inventory stock validation  
- Cancelled orders can return to their respective queues  
- Tracks fulfilled and cancelled order history  
- Clean CLI interface with status messages

## 🧰 Technologies Used
- Java (OOP)  
- Linked Lists for queue handling  
- File I/O for saving data

## 📁 File Structure
```
OrderManagerApp.java       # Main application file  
inventory.txt              # Inventory file (optional, if used)  
orders.txt                 # Order save/load file (optional)  
```

## ▶️ How to Run
Make sure your Java file is named `OrderManagerApp.java` and contains:
```java
public class OrderManagerApp {
    public static void main(String[] args) {
        ...
    }
}
```

Then compile and run:
```bash
javac OrderManagerApp.java
java OrderManagerApp
```

