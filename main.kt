import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Enum to represent the status of the order
enum class OrderStatus {
    RECEIVED, IN_PROGRESS, READY, CANCELLED
}

// Data class for representing a food menu item
data class Food(
    val name: String,
    val price: Double,
    val time: Int // Time in minutes to prepare
)

// Data class for representing an order item
data class OrderItem(
    val food: Food,
    var status: OrderStatus = OrderStatus.RECEIVED
)

// Data class for representing an order
data class Order(
    val orderId: Int,
    val customerName: String,
    val items: MutableList<OrderItem> = mutableListOf(),
    var totalAmount: Double = 0.0,
    var status: OrderStatus = OrderStatus.RECEIVED
)

// Singleton mutex object responsible for waiting for finishing user input 
object Mutex {
  private var isOutputLocked : Boolean = false

  // Function responsible for waiting until use input finishes
  fun waitOutput() {
    while (true) {
      if (!isOutputLocked) {
        break
      }
    }
  }

  fun lockOutput() {
    isOutputLocked = true
  }

  fun unlockOutput() {
    isOutputLocked = false
  }
}

// Singleton object to manage the restaurant system
object RestaurantManager {
    val menu: MutableList<Food> = mutableListOf()
    private val orders: MutableMap<Int, Order> = mutableMapOf()
    private var orderCounter = 1
    private var totalIncome : Double = 0.0

    // Function to add a new food item to the menu
    fun addItemToMenu(item: Food) {
        menu.add(item)
    }

    // Function to remove an item from the menu
    fun removeItemFromMenu(itemName: String) {
        menu.removeIf { it.name == itemName }
    }

    // Function to display the current menu
    fun displayMenu() {
        println("Menu:")
        menu.forEach { println("${it.name} - $${it.price}") }
    }

    // Function to place a new order
    fun placeOrder(customerName: String, chosenItems: List<Food>): Order {
        val newOrder = Order(orderCounter++, customerName)
        chosenItems.forEach { food ->
            val orderItem = OrderItem(food)
            newOrder.items.add(orderItem)
            newOrder.totalAmount += food.price
        }
        orders[newOrder.orderId] = newOrder
        return newOrder
    }

    // Function to process orders
    fun processOrders() {
        val executor = Executors.newFixedThreadPool(5) // Simulate 5 chefs processing orders concurrently
        orders.forEach { (_, order) ->
            if (order.status == OrderStatus.RECEIVED) {
              executor.submit {
                  
                  order.items.forEach {
                      it.status = OrderStatus.IN_PROGRESS
                      TimeUnit.SECONDS.sleep(it.food.time.toLong())
                      it.status = OrderStatus.READY
                  }
                  // waiting whether user will not cancel this order
                  
                  
                  order.status = OrderStatus.READY

                  Mutex.waitOutput()
                  
                  Mutex.lockOutput()
                  print("Order ${order.orderId} is ready. Do yo want to receive the order? (yes/no): ")
                  // manager is becoming busy
                  
  
                  val responce = readln().split(' ')[0].lowercase()
                  if (responce == "yes") {
                    totalIncome += order.totalAmount
                    println("Order ${order.orderId} was given out.")
                  }
                  else {
                    println("Order ${order.orderId} was not given out.")
                  }
                  println("Total income: ${totalIncome}.")
                  Mutex.unlockOutput()
              }
            }
        }
        executor.shutdown()
    }

    // Function to display the status of all orders
    fun displayOrderStatus() {
        println("Current Orders:")
        orders.forEach { (_, order) ->
            println("Order ${order.orderId}: ${order.status}")
        }
    }

    // Function to cancel an order
    fun cancelOrder(orderId: Int) {
        orders[orderId]?.status = OrderStatus.CANCELLED
    }
}

fun fillMenu() {
  while (true) {
    print("enter the name of the current food, string: ")
    val foodName : String = readLine()?.lowercase() ?: ""

    if (RestaurantManager.menu.any{ food -> food.name == foodName }) {
      println("food with name ${foodName} already exists in the menu.")
      continue
    }
    
    print("enter the price for ${foodName}, double: ")
    val foodPrice : Double = readln().toDouble()

    try {
    print("enter the time for cooking ${foodName}, int: ")
    val foodTime : Int = readln().toInt()

    val newFood = Food(foodName, foodPrice, foodTime)

    RestaurantManager.addItemToMenu(newFood)
    }
    catch (ex : Exception) {
      println("Something went wrong.")
      continue
    }

    Mutex.waitOutput()
    Mutex.lockOutput()
    print("Do you want to add one else type of food to menu? (yes / no) : ")
    val responce : String = readln().split(' ')[0].lowercase()
    if (responce != "yes") {
      Mutex.unlockOutput()
      return
    }
    Mutex.unlockOutput()
  }
}

fun processNewOrder(customerName : String) {
  println("Please select items from the menu (enter item names separated by commas and spaces):")
  val chosenItemsInput = readLine() ?: ""
  val chosenItems = chosenItemsInput.split(',', ' ').map { it -> it.trim() }



  val order = RestaurantManager.placeOrder(customerName, chosenItems.mapNotNull { menuItemName ->
      RestaurantManager.menu.find { it.name == menuItemName }
  })
  if (order.items.isEmpty()) {
    println("Cannot parse the line ${chosenItemsInput}.")
    return
  }

  // Processing orders
  RestaurantManager.processOrders()

  // Displaying order status
  RestaurantManager.displayOrderStatus()

  // Cancelling an order
  Mutex.waitOutput()
  Mutex.lockOutput()
  print("Do you want to cancel the order? (yes/no) : ")
  val cancelChoice = readLine()?.lowercase() ?: ""
  if (cancelChoice == "yes") {
      RestaurantManager.cancelOrder(order.orderId)
      println("Order cancelled.")
  }
  Mutex.unlockOutput()
}

fun main() {
    // Adding some items to the menu
    fillMenu()

    // Displaying the menu
    RestaurantManager.displayMenu()

    // Placing an order
    println("Please enter your name:")
    val customerName = readLine() ?: ""

    while (true) {
      processNewOrder(customerName)

      // waiting until we can output anything
      /*
      while (true) {
        if (RestaurantManager.canGiveOutOrders) {
          break
        }
      }
      */
      Mutex.waitOutput()
      Mutex.lockOutput()
      print("Do you want to process one more order? (yes/no) : ")
      
      val responce : String = readln().split(' ', ',')[0].trim(' ').lowercase()
      Mutex.unlockOutput()
      if (responce != "yes") {
        
        break
      }
      
      
    }

  // processNewOrder(customerName)
}
