package org.omnaest.utils.table.domain;

import java.util.List;

public interface Column
{
    public String getTitle();

    public List<Cell> getCells();
}