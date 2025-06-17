package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MarkdownTablePrinter implements TablePrinter {

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        List<String> formattedTable = new ArrayList<>();

        // Get column names
        Collection<String> columnNames = table.getColumnNames();
        StringBuilder headerRow = new StringBuilder("|");
        for (String columnName : columnNames) {
            headerRow.append(" ").append(columnName).append(" |");
        }
        formattedTable.add(headerRow.toString());

        // Generate alignment row
        StringBuilder alignmentRow = new StringBuilder("|");
        for (ColumnAlignment alignment : alignments) {
            alignmentRow.append(" ").append(getAlignmentString(alignment)).append(" |");
        }
        formattedTable.add(alignmentRow.toString());

        // Generate data rows
        int rowCount = table.getRowsCount();
        for (int i = 0; i < rowCount; i++) {
            StringBuilder dataRow = new StringBuilder("|");
            for (String columnName : columnNames) {
                Collection<String> columnData = table.getColumnData(columnName);
                String data = new ArrayList<>(columnData).get(i);
                dataRow.append(" ").append(data).append(" |");
            }
            formattedTable.add(dataRow.toString());
        }

        return formattedTable;
    }

    private String getAlignmentString(ColumnAlignment alignment) {
        switch (alignment) {
            case LEFT:
                return ":---";
            case CENTER:
                return ":---:";
            case RIGHT:
                return "---:";
            case NOALIGNMENT:
            default:
                return "---";
        }
    }
}
