package org.omnaest.utils.table.components;

import java.util.Optional;

import org.omnaest.utils.table.Row;

public interface TableIndex
{
    public Optional<Row> getRowByValue(String value);
}
