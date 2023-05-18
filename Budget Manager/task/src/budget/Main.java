package budget;

import java.util.Scanner;

public class Main {

    static BudgetManager manager;
    static Scanner scanner;

    public static void main(String[] args) {
        // write your code here
        manager = new BudgetManager();
        scanner = new Scanner(System.in);
        int choice;
        do {
            printMenu();
            choice = Integer.parseInt(scanner.nextLine());
            Action action = Action.values()[choice];
            switch (action) {
                case ADD_INCOME:
                    handleAddIncome();
                    break;
                case ADD_PURCHASE:
                    handleAddPurchase();
                    break;
                case SHOW_PURCHASES:
                    handleShowPurchases();
                    break;
                case SHOW_BALANCE:
                    System.out.println("\n" + manager.getBalance() + "\n");
                    break;
                case SAVE:
                    handleSave();
                    break;
                case LOAD:
                    handleLoad();
                    break;
                case ANALYZE:
                    handleSort();
                    break;
                case EXIT:
                    System.out.println("\nBye!");
                    break;
                default:
                    break;
            }
        } while (choice != 0);
    }

    public static void printMenu() {
        String menu =
                "Choose your action:" +
                        "\n1) Add income" +
                        "\n2) Add purchase" +
                        "\n3) Show list of purchases" +
                        "\n4) Balance" +
                        "\n5) Save" +
                        "\n6) Load" +
                        "\n7) Analyze (Sort)" +
                        "\n0) Exit";
        System.out.println(menu);
    }

    public static void handleAddIncome() {
        System.out.println("\nEnter income:");
        manager.addIncome(Float.parseFloat(scanner.nextLine()));
        System.out.println("Income was added!\n");
    }

    public static void handleAddPurchase() {
        int choice;
        do {
            System.out.println("\nChoose the type of purchase");
            System.out.println(manager.getCategories());
            System.out.println("5) Back");
            choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= Category.values().length) {
                Category category = Category.values()[choice - 1];
                Expense expense = collectExpenseInfo(scanner);
                manager.insertExpense(category, expense);
                manager.decrementBalance(expense.getAmount());
                manager.incrementTotalExpensesByCategory(category, expense.getAmount());
                System.out.println("Purchase was added!");
            }
        } while (choice != 5);
        System.out.println();
    }

    public static void handleShowPurchases() {
        int choice;
        if (manager.isExpensesEmpty()) {
            System.out.println("The purchase list is empty!");
        } else {
            do {
                System.out.println("\nChoose the type of purchases");
                System.out.println(manager.getCategories());
                System.out.println("5) All");
                System.out.println("6) Back");
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= Category.values().length) {
                    Category category = Category.values()[choice - 1];
                    String categoryName = "\n" + category.name().charAt(0) + category.name().substring(1).toLowerCase();
                    System.out.println(categoryName + ":");
                    String string = manager.getExpenses(category);
                    System.out.println(string);
                    if (!string.contains("list is empty")) {
                        System.out.println(manager.getTotalExpenses(category));
                    }
                } else if (choice == 5) {
                    System.out.println("\nAll:");
                    System.out.println(manager.getExpenses());
                    System.out.println(manager.getTotalExpenses());
                }
            } while (choice != 6);
            System.out.println("\n");
        }
    }

    public static Expense collectExpenseInfo(Scanner scanner) {
        System.out.println("\nEnter purchase name:");
        String title = scanner.nextLine();
        System.out.println("Enter its price:");
        float amount = Float.parseFloat(scanner.nextLine());
        return new Expense(title, amount);
    }

    public static void handleSave() {
        if (manager.saveToFile()) {
            System.out.println("\nPurchases were saved!\n");
        }
    }

    public static void handleLoad() {
        if (manager.loadFromFile()) {
            System.out.println("\nPurchases were loaded!\n");
        }
    }

    public static void handleSort(){
        Scanner scanner1 = new Scanner(System.in);

        while (true) {
            System.out.println("""

                    How do you want to sort?
                    1) Sort all purchases
                    2) Sort by type
                    3) Sort certain type
                    4) Back""");
            int choice = scanner1.nextInt();
            System.out.println();
            if (choice == 4) {
                break;
            }
            else if (choice == 1) {
                System.out.println(manager.getSortedAllPurchase());
            }
            else if(choice == 2) {
                System.out.println(manager.getTypeSorted());
            }
            else if (choice == 3) {
                System.out.println("1) Food\n" +
                        "2) Clothes\n" +
                        "3) Entertainment\n" +
                        "4) Other");
                Scanner tempScan = new Scanner(System.in);
                int catChoice = tempScan.nextInt();
                System.out.println();
                System.out.println(manager.getSortByType(Category.values()[catChoice-1]));

            }
        }
    }
}
