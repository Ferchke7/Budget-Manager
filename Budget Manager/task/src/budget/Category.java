package budget;

public enum Category {
    FOOD,
    CLOTHES,
    ENTERTAINMENT,
    OTHER;

    public String getFormattedName() {
        String name = name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}