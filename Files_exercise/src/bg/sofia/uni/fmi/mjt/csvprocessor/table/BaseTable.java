package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.*;

public class BaseTable implements Table {
    private List<Column> columns;

    public BaseTable() {
        this.columns = new ArrayList<>();
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        if (columns.isEmpty()) {
            createColumns(data);
        } else {
            if (data.length != columns.size()) {
                throw new CsvDataNotCorrectException("Number of provided data parts doesn't match the number of columns");
            }

            for (int i = 0; i < data.length; i++) {
                columns.get(i).addData(data[i]);
            }
        }
    }

    private void createColumns(String[] headers) {
        for (String header : headers) {
            columns.add(new BaseColumn());
        }

        for (int i = 0; i < headers.length; i++) {
            columns.get(i).addData(headers[i]);
        }
    }

    @Override
    public Collection<String> getColumnNames() {
        List<String> names = new ArrayList<>();
        for (Column column : columns) {
            Collection<String> data = column.getData();
            if (!data.isEmpty()) {
                names.add(data.iterator().next());
            }
        }
        return Collections.unmodifiableCollection(names);
    }

    @Override
    public Collection<String> getColumnData(String column) {
        for (Column col : columns) {
            Collection<String> data = col.getData();
            if (!data.isEmpty() && data.iterator().next().equals(column)) {
                return Collections.unmodifiableCollection(data);
            }
        }
        throw new IllegalArgumentException("Column not found: " + column);
    }

    @Override
    public int getRowsCount() {
        if (columns.isEmpty()) {
            return 0;
        }
        return columns.get(0).getData().size();
    }
}
