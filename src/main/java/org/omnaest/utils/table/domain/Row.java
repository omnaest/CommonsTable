package org.omnaest.utils.table.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface Row extends Iterable<String>
{
    public List<String> getValues();

    public String getValue(int columnIndex);

    public String getValue(String columnTitle);

    public String getFirstValue();

    public String getSecondValue();

    public Map<String, String> asMap();

    public Row addValue(String value);

    public Row addValues(String... values);

    public Cell getCell(int index);

    public Cell getCell(String columnTitle);

    public List<Cell> getCells();

    public Cell getFirstCell();

    public List<String> asList();

    public Stream<String> stream();

    public <T> T asBean(Class<T> type);

    /**
     * Returns the number of cells
     * 
     * @return
     */
    public int size();

}