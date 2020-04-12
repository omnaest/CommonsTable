package org.omnaest.utils.table.internal;

public class TableData
{
    private String[][] data            = new String[1][1];
    private int        numberOfRows    = 0;
    private int        numberOfColumns = 0;

    public String get(int rowIndex, int columnIndex)
    {
        this.validateIndexOutOfBounds(rowIndex, columnIndex);
        this.extendRawArrayIfNecessary(rowIndex, columnIndex);
        return this.data[rowIndex][columnIndex];
    }

    private void validateIndexOutOfBounds(int rowIndex, int columnIndex)
    {
        this.validateIndexOutOfBoundsForRow(rowIndex);
        this.validateIndexOutOfBoundsForColumn(columnIndex);
    }

    private void validateIndexOutOfBoundsForRow(int rowIndex)
    {
        if (rowIndex < 0 || rowIndex >= this.numberOfRows)
        {
            throw new IndexOutOfBoundsException();
        }
    }

    private void validateIndexOutOfBoundsForColumn(int columnIndex)
    {
        if (columnIndex < 0 || columnIndex >= this.numberOfColumns)
        {
            throw new IndexOutOfBoundsException();
        }
    }

    public void set(int rowIndex, int columnIndex, String value)
    {
        this.applyNewNumberOfRowsAndColumns(rowIndex, columnIndex);
        this.extendRawArrayIfNecessary(rowIndex, columnIndex);
        this.data[rowIndex][columnIndex] = value;
    }

    private void extendRawArrayIfNecessary(int rowIndex, int columnIndex)
    {
        if (rowIndex >= this.getRawRowSize(this.data))
        {
            this.extendRows();
        }

        if (columnIndex >= this.getRawColumnSize(this.data))
        {
            this.extendColumns();
        }
    }

    public int getRowSize()
    {
        return this.numberOfRows;
    }

    public int getColumnSize()
    {
        return this.numberOfColumns;
    }

    private void applyNewNumberOfRowsAndColumns(int rowIndex, int columnIndex)
    {
        this.numberOfRows = Math.max(this.numberOfRows, rowIndex + 1);
        this.numberOfColumns = Math.max(this.numberOfColumns, columnIndex + 1);
    }

    private void extendRows()
    {
        String[][] oldData = this.data;

        int newRowSize = this.getRawRowSize(oldData) * 2;
        int columnSize = this.getRawColumnSize(oldData);
        this.data = new String[newRowSize][columnSize];

        this.applyOldData(oldData);
    }

    private void applyOldData(String[][] oldData)
    {
        for (int ii = 0; ii < this.getRawRowSize(oldData); ii++)
        {
            for (int jj = 0; jj < this.getRawColumnSize(oldData); jj++)
            {
                this.data[ii][jj] = oldData[ii][jj];
            }
        }
    }

    private int getRawColumnSize(String[][] oldData)
    {
        return oldData[0].length;
    }

    private int getRawRowSize(String[][] oldData)
    {
        return oldData.length;
    }

    private void extendColumns()
    {
        String[][] oldData = this.data;

        int rowSize = this.getRawRowSize(oldData);
        int newColumnSize = this.getRawColumnSize(oldData) * 2;
        this.data = new String[rowSize][newColumnSize];

        this.applyOldData(oldData);
    }

    public void setRowSize(int rowSize)
    {
        this.applyNewNumberOfRowsAndColumns(rowSize - 1, this.getColumnSize() - 1);
    }

    public String[] getRow(int rowIndex)
    {
        this.validateIndexOutOfBoundsForRow(rowIndex);

        String[] result = new String[this.numberOfColumns];
        for (int ii = 0; ii < result.length; ii++)
        {
            result[ii] = this.data[rowIndex][ii];
        }
        return result;
    }
}
