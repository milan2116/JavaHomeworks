package bg.sofia.uni.fmi.mjt.cook;

import bg.sofia.uni.fmi.mjt.cook.exception.RecipeClientException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RecipeClientTest {


    private final RecipeClient recipeClient = new RecipeClient();


    @Test
    void testConstructURLStringNoParameters()  {
        String url = recipeClient.constructURLString("", "", "", 0);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&page=1", url);
    }

    @Test
    void testConstructURLStringOneKeyword() {
        String url = recipeClient.constructURLString("chicken", "", "", 0);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&q=chicken&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&page=1", url);
    }

    @Test
    void testConstructURLStringMultipleKeyword() {
        String url = recipeClient.constructURLString("chicken BBQ Coca-Cola", "", "", 0);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&q=chicken%20BBQ%20Coca-Cola&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&page=1", url);
    }

    @Test
    void testConstructURLStringOneMealType() {
        String url = recipeClient.constructURLString("", "Dinner", "", 0);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&mealType=Dinner&page=1", url);
    }

    @Test
    void testConstructURLStringMultipleMealTypes() {
        String url = recipeClient.constructURLString("", "Breakfast Dinner", "", 0);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&mealType=Breakfast&mealType=Dinner&page=1", url);
    }

    @Test
    void testConstructURLStringOneHealth() {
        String url = recipeClient.constructURLString("", "", "gluten-free", 0);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&health=gluten-free&page=1", url);
    }

    @Test
    void testConstructURLStringMultipleHealth() {
        String url = recipeClient.constructURLString("", "", "fish-free gluten-free", 0);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&health=fish-free&health=gluten-free&page=1", url);
    }

    @Test
    void testConstructURLStringWithPageChange()  {
        String url = recipeClient.constructURLString("", "", "", 4);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&page=5", url);
    }

    @Test
    void testConstructURLStringWithAllParameters() {
        String url = recipeClient.constructURLString("chicken BBQ Coca-Cola", "Breakfast Dinner", "fish-free gluten-free", 3);
        assertEquals("https://api.edamam.com/api/recipes/v2/?type=public&q=chicken%20BBQ%20Coca-Cola&app_id=" + RecipeClient.APP_ID + "&app_key=" + RecipeClient.APP_KEY + "&health=fish-free&health=gluten-free&mealType=Breakfast&mealType=Dinner&page=4", url);
    }

    @Test
    void testSearchRecipesSuccess() throws Exception {
        RecipeClient recipeClient = new RecipeClient();

        List<Recipe> recipes = recipeClient.searchRecipes("chickens", "Lunch", "fish-free");
        assertEquals(100, recipes.size());
    }

    @Test
    void testSearchRecipes_ApiException() {
        RecipeClient recipeClient = new RecipeClient();

        RecipeClientException exception = assertThrows(RecipeClientException.class,
                () -> recipeClient.searchRecipes("", "invalid", "invalid"));

        assertEquals("HTTP Error Code: 400, Error Message: Failed to retrieve recipes", exception.getMessage());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, exception.getResponseCode());
    }

}
