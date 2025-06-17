package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.Collection;

public interface TablePrinter {


    Collection<String> printTable(Table table, ColumnAlignment... alignments);

}