package org.omnaest.utils.table.components;

import java.util.Map;
import java.util.function.Function;

import org.omnaest.utils.table.domain.Row;

public interface TableTranslator
{
    public Map<String, String> map();

    public <K, V> Map<K, V> map(Function<Row, K> keyMapper, Function<Row, V> valueMapper);

    /**
     * Returns a {@link TableIndex} on the given column
     * 
     * @param columnTitle
     * @return
     */
    public TableIndex index(String columnTitle);
}
