package bg.sofia.uni.fmi.mjt.cook;

// Recipe.java
import java.util.List;

public class Recipe {
    private String label;
    private List<String> dietLabels;
    private List<String> healthLabels;
    private double totalWeight;
    private List<String> cuisineType;
    private List<String> mealType;
    private List<String> dishType;
    private List<String> ingredientLines;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(List<String> dietLabels) {
        this.dietLabels = dietLabels;
    }

    public List<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(List<String> healthLabels) {
        this.healthLabels = healthLabels;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public List<String> getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(List<String> cuisineType) {
        this.cuisineType = cuisineType;
    }

    public List<String> getMealType() {
        return mealType;
    }

    public void setMealType(List<String> mealType) {
        this.mealType = mealType;
    }

    public List<String> getDishType() {
        return dishType;
    }

    public void setDishType(List<String> dishType) {
        this.dishType = dishType;
    }

    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(List<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "label='" + label + '\'' +
                ", dietLabels=" + dietLabels +
                ", healthLabels=" + healthLabels +
                ", totalWeight=" + totalWeight +
                ", cuisineType=" + cuisineType +
                ", mealType=" + mealType +
                ", dishType=" + dishType +
                ", ingredientLines=" + ingredientLines +
                '}';
    }
}
