package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;

import java.util.Collection;

public interface Table {

    void addData(String[] data) throws CsvDataNotCorrectException;

    Collection<String> getColumnNames();

    Collection<String> getColumnData(String column);

    int getRowsCount();

}