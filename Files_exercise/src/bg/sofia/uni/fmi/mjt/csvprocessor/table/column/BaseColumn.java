package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class BaseColumn implements Column {
    private Set<String> values;

    public BaseColumn() {
        this(new LinkedHashSet<>());
    }

    public BaseColumn(Set<String> values) {
        this.values = values;
    }

    @Override
    public void addData(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        values.add(data);
    }

    @Override
    public Collection<String> getData() {
        return values;
    }
}
