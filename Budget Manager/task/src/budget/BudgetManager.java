package budget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BudgetManager {
    private final Map<Category, List<Expense>> expenses;
    private final Map<Category, Float> totalExpensesPerCategory;
    private float balance;

    BudgetManager() {
        balance = 0;
        expenses = new LinkedHashMap<>();
        totalExpensesPerCategory = new LinkedHashMap<>();

    }


    public void decrementBalance(float amount) {
        setBalance(balance - amount);
    }

    public void incrementTotalExpensesByCategory(Category category, float amount) {
        float currentTotal = totalExpensesPerCategory.getOrDefault(category, 0f);
        totalExpensesPerCategory.put(category, currentTotal + amount);
    }

    public void insertExpense(Category category, Expense expense) {
        List<Expense> currentExpenses = expenses.getOrDefault(category, new ArrayList<>());
        currentExpenses.add(expense);
        expenses.put(category, currentExpenses);
    }

    public String getBalance() {
        return String.format("Balance: $%.2f", balance);
    }

    private void setBalance(float balance) {
        if (balance < 0) {
            this.balance = 0;
        } else {
            this.balance = balance;
        }
    }

    public void addIncome(float income) {
        balance += income;
    }

    public String getExpenses() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Category category : expenses.keySet()) {
            stringBuilder.append(getExpenses(category)).append("\n");
        }
        return stringBuilder.toString().trim();
    }

    public String getExpenses(Category category) {
        StringBuilder stringBuilder = new StringBuilder();
        if (expenses.get(category) == null || expenses.get(category).size() == 0) {
            stringBuilder.append("The purchase list is empty!");
        } else {
            for (Expense expense : expenses.get(category)) {
                stringBuilder.append(expense.toString()).append("\n");
            }
        }
        return stringBuilder.toString().trim();
    }

    public String getTotalExpenses() {
        float total = 0;
        for (float amount : totalExpensesPerCategory.values()) {
            total += amount;
        }
        return String.format("Total sum: $%.2f", total);
    }

    public String getTotalExpenses(Category category) {
        return String.format("Total sum: $%.2f", totalExpensesPerCategory.get(category));
    }

    public String getCategories() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Category category : Category.values()) {
            int sequenceNumber = category.ordinal() + 1;
            String name = category.name().charAt(0) + category.name().substring(1).toLowerCase();
            String formattedString = String.format("%d) %s\n", sequenceNumber, name);
            stringBuilder.append(formattedString);
        }
        return stringBuilder.toString().trim();
    }

    public boolean isExpensesEmpty() {
        return expenses.entrySet().size() == 0;
    }

    public boolean saveToFile() {
        File file = new File("purchases.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(balance + "\n");
            for (Map.Entry<Category, List<Expense>> entry : expenses.entrySet()) {
                for (Expense expense : entry.getValue()) {
                    writer.write(expense.toString() + "\n");
                }
                writer.write(entry.getKey().toString() + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadFromFile() {
        File file = new File("purchases.txt");
        try {
            float totalExpensesForCurrentCategory = 0;
            Scanner scanner = new Scanner(file);
            balance = Float.parseFloat(scanner.nextLine());
            List<Expense> tempExpenses = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("$")) {
                    String title = line.split("\\$d+.d{2}")[0];
                    float amount = Float.parseFloat(line.substring(line.lastIndexOf('$') + 1));
                    Expense expense = new Expense(title, amount);
                    tempExpenses.add(expense);
                    totalExpensesForCurrentCategory += amount;
                } else {
                    Category category = Category.valueOf(line);
                    expenses.put(category, tempExpenses);
                    totalExpensesPerCategory.put(category, totalExpensesForCurrentCategory);
                    totalExpensesForCurrentCategory = 0;
                    tempExpenses = new ArrayList<>();
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getSortedAllPurchase() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Expense> expenseListTemp = new ArrayList<>();
        float sum = 0;

        if (expenses.size() == 0) {
            stringBuilder.append("The purchase list is empty!");
        } else {
            stringBuilder.append("All:").append("\n");
            for (List<Expense> entry : expenses.values()) {
                expenseListTemp.addAll(entry);
            }
            expenseListTemp.sort(Comparator.comparingDouble(Expense::getAmount).reversed());
            for (Expense expense : expenseListTemp) {

                stringBuilder.append(expense.getTitle()).append("\n");
                if (expense.getTitle().equals("debt")) {
                    sum -= expense.getAmount();
                } else {
                    sum += expense.getAmount();
                }
            }
            stringBuilder.append("Total: $").append(new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP));
        }

        return stringBuilder.toString().trim();
    }

    public String getTypeSorted() {
        StringBuilder stringBuilder = new StringBuilder();
        float sum = 0;
        stringBuilder.append("Types: ").append("\n");
        if (totalExpensesPerCategory.size() == 0) {
            stringBuilder.append("Food - $0\n" +
                    "Entertainment - $0\n" +
                    "Clothes - $0\n" +
                    "Other - $0\n" +
                    "Total sum: $0");
        }
        else {
            List<Map.Entry<Category, Float>> sortedEntries = new ArrayList<>(totalExpensesPerCategory.entrySet());
            sortedEntries.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
            for (Map.Entry<Category, Float> entry : sortedEntries) {
                stringBuilder.append((entry.getKey().getFormattedName())).append(" - $").append(BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP)).append("\n");
                sum += entry.getValue();
            }
            stringBuilder.append("Total: $").append(new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP));
        }
        return stringBuilder.toString().trim();
    }

    public String getSortByType(Category category) {
        StringBuilder stringBuilder = new StringBuilder();
        if (expenses.get(category) == null || expenses.get(category).size() == 0) {
            stringBuilder.append("The purchase list is empty!");
        } else {
            stringBuilder.append(category.getFormattedName()).append(":\n");
            List<Expense> typeListTemp = new ArrayList<>(expenses.get(category));
            typeListTemp.sort(Comparator.comparingDouble(Expense::getAmount).reversed());
            for (Expense expense : typeListTemp) {
                stringBuilder.append(expense.getTitle()).append("\n");
            }
        }
        return stringBuilder.toString().trim();
    }

}