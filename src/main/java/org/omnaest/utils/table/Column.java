package org.omnaest.utils.table;

import java.util.List;

public interface Column
{
    public String getTitle();

    public List<Cell> getCells();
}