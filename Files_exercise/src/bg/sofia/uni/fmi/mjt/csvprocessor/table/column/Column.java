package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import java.util.Collection;

public interface Column {


    void addData(String data);


    Collection<String> getData();
}