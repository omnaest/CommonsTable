package org.omnaest.utils.table.domain;

public interface Cell
{
    public String getValue();

    /**
     * Returns true, if the value of the cell is null or an empty string
     * 
     * @return
     */
    public boolean isEmpty();

    /**
     * Returns true, if the value of the cell is null or an empty string or a string with blank characters
     * 
     * @return
     */
    public boolean isBlank();

    public Row getRow();

    public Column getColumn();
}