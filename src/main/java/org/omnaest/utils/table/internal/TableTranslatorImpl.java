package org.omnaest.utils.table.internal;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableIndex;
import org.omnaest.utils.table.components.TableTranslator;
import org.omnaest.utils.table.domain.Row;

public class TableTranslatorImpl implements TableTranslator
{
    private Table table;

    public TableTranslatorImpl(Table table)
    {
        super();
        this.table = table;
    }

    @Override
    public Map<String, String> map()
    {
        return this.map(row -> row.getFirstValue(), row -> row.getSecondValue());
    }

    @Override
    public <K, V> Map<K, V> map(Function<Row, K> keyMapper, Function<Row, V> valueMapper)
    {
        return this.table.stream()
                         .collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public TableIndex index(String columnTitle)
    {
        Map<String, Row> map = this.map(row -> row.getValue(columnTitle), row -> row);

        return new TableIndex()
        {
            @Override
            public Optional<Row> getRowByValue(String value)
            {
                return Optional.ofNullable(map.get(value));
            }
        };
    }

}
