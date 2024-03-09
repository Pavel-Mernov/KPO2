# Restaurant Management System

This is a simple restaurant management system implemented in Kotlin. It allows users to manage orders in a restaurant, supporting two types of users: visitors and administrators. The system processes orders in a multi-threaded environment, allowing customers to add dishes to their orders in real-time and view the status of their orders. Administrators can manage the menu by adding or removing items. The implementation adheres to the principles of object-oriented programming (OOP) and SOLID, utilizing design patterns where appropriate.

## Usage

1. **Menu Management**:
   - Administrators can add new food items to the menu by entering the name, price, and preparation time for each item.

2. **Placing Orders**:
   - Visitors can place orders by selecting items from the displayed menu.
   - Customers can add multiple items to a single order.
   - Orders are processed asynchronously in separate threads, simulating the cooking process.

3. **Order Status**:
   - The system displays the current status of each order (e.g., received, in progress, ready).
   - Visitors have the option to cancel their orders before they are ready.

4. **Order Completion**:
   - Upon completion of an order, customers have the option to receive the order or cancel it.
   - The total income earned from orders is displayed after each completed order.

## Design Patterns Used

- **Singleton Pattern**: Used for the `RestaurantManager` and `Mutex` objects to ensure there is only one instance responsible for managing the restaurant system and waiting for user input, respectively.
- **Observer Pattern**: Inherent in the multi-threaded processing of orders, where each order's status is updated asynchronously.

## How to Run

1. Ensure you have Kotlin installed on your machine.
2. Make a new project on Kotlin
3. Insert this piece of code to the main page of the project
4. Make sure you can use classes java.util.concurrent.Executors and java.util.concurrent.TimeUnit. If you cannot use them, then include appropriate dependencies

## Evaluation Criteria

- **OOP and SOLID Principles**: The code adheres to object-oriented programming principles and SOLID design principles.
- **Authentication**: Implemented authentication for visitors and administrators.
- **Design Patterns**: Utilized design patterns such as Singleton and Observer.
- **Data Storage**: State of the program, including menu, total income, and order details, is maintained.
- **Multi-threading**: Orders are processed concurrently using multi-threading.
- **Code Style**: Followed Kotlin coding conventions and maintained readable code.
- **README**: Provides clear instructions on how to use the program, describes the design patterns used, and explains the system's functionality.


