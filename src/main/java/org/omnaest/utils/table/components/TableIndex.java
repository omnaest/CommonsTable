package org.omnaest.utils.table.components;

import java.util.Optional;

import org.omnaest.utils.table.domain.Row;

public interface TableIndex
{
    public Optional<Row> getRowByValue(String value);
}
