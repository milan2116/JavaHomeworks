package bg.sofia.uni.fmi.mjt.csvprocessor.exceptions;

public class CsvDataNotCorrectException extends Exception {

    public CsvDataNotCorrectException() {
        super("CSV data is not in the correct format");
    }

    public CsvDataNotCorrectException(String message) {
        super(message);
    }

    public CsvDataNotCorrectException(String message, Throwable cause) {
        super(message, cause);
    }
}
