package bg.sofia.uni.fmi.mjt.cook.exception;

public class RecipeClientException extends Exception {
    private final String requestUrl;
    private final int responseCode;

    public RecipeClientException(String requestUrl, int httpStatusCode, String errorMessage) {
        super("HTTP Error Code: " + httpStatusCode + ", Error Message: " + errorMessage);
        this.requestUrl = requestUrl;
        this.responseCode = httpStatusCode;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
