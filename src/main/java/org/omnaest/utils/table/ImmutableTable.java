package org.omnaest.utils.table;

import java.util.List;

import org.omnaest.utils.table.components.TableSerializer;
import org.omnaest.utils.table.components.TableTranslator;

public interface ImmutableTable extends Iterable<Row>
{
    public TableSerializer serialize();

    public List<String> getColumnTitles();

    public String getValue(int rowIndex, int columnIndex);

    public String getValue(String rowTitle, String columnTitle);

    public int getRowSize();

    public TableTranslator as();
}
