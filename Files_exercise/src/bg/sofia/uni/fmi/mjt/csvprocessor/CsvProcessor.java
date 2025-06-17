package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor implements CsvProcessorAPI {
    private Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\Q" + delimiter + "\\E");
                table.addData(parts);
            }
        } catch (IOException e) {
            // Handle IO Exception
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        MarkdownTablePrinter printer = new MarkdownTablePrinter();
        List<String> formattedTable = (List<String>) printer.printTable(table, alignments);
        try {
            for (String line : formattedTable) {
                writer.write(line + System.lineSeparator());
            }
            writer.flush();
        } catch (IOException e) {
            // Handle IO Exception
        }
    }
}
