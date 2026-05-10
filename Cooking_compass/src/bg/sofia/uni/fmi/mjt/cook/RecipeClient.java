package bg.sofia.uni.fmi.mjt.cook;

import com.google.gson.Gson;
import  bg.sofia.uni.fmi.mjt.cook.exception.RecipeClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeClient {

    private final Gson gson = new Gson();
    public static final int MAX_PAGES = 5;

    public List<Recipe> searchRecipes(String query, String mealType, String health) throws RecipeClientException {
        List<Recipe> allRecipes = new ArrayList<>();
        int currentPage = 0;
        try {
            while (currentPage < MAX_PAGES) {
                URL pageUrl = new URL(constructURLString(query, mealType, health, currentPage));
                HttpURLConnection pageConnection = (HttpURLConnection) pageUrl.openConnection();
                setupConnection(pageConnection);
                int pageResponseCode = pageConnection.getResponseCode();
                if (pageResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader pageReader = new BufferedReader(new
                            InputStreamReader(pageConnection.getInputStream()));
                    StringBuilder pageResponse = readPage(pageReader);
                    pageReader.close();
                    RecipeResponse pageRecipeResponse = gson.fromJson(pageResponse.toString(), RecipeResponse.class);
                    List<Recipe> pageRecipes = toRecipes(pageRecipeResponse);
                    allRecipes.addAll(pageRecipes);
                    currentPage++;
                } else {
                    throw new RecipeClientException(pageUrl.toString(), pageResponseCode, "Failed to retrieve recipes");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allRecipes;
    }

    private List<Recipe> toRecipes(RecipeResponse pageRecipeResponse) {
        return pageRecipeResponse.getHits().stream()
                .map(RecipeResponse.Hit::getRecipe)
                .toList();
    }

    private StringBuilder readPage(BufferedReader pageReader) throws IOException {
        StringBuilder pageResponse = new StringBuilder();
        String pageInputLine;
        while ((pageInputLine = pageReader.readLine()) != null) {
            pageResponse.append(pageInputLine);
        }
        return pageResponse;
    }

    private void setupConnection(HttpURLConnection connection) {
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
        } catch (IOException e) {
            throw new RuntimeException("Error setting up connection", e);
        }
    }

    public String constructURLString(String query, String mealType, String health, int page) {
        StringBuilder myUrlBuilder = new StringBuilder(API_URL + "/?type=public");

        if (!query.isEmpty()) {
            String[] keywords = query.split(" ");
            String formattedQuery = String.join("%20", keywords);
            myUrlBuilder.append("&q=").append(formattedQuery);
        }

        myUrlBuilder.append("&app_id=").append(APP_ID).append("&app_key=").append(APP_KEY);

        if (!health.isEmpty()) {
            String[] healthArray = health.split(" ");
            for (String elem : healthArray) {
                myUrlBuilder.append("&health=").append(elem);
            }
        }

        if (!mealType.isEmpty()) {
            String[] mealTypes = mealType.split(" ");
            for (String elem : mealTypes) {
                myUrlBuilder.append("&mealType=").append(elem);
            }
        }

        myUrlBuilder.append("&page=").append(page + 1);
        return myUrlBuilder.toString();
    }

}
