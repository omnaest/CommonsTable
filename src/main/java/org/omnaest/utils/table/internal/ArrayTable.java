package org.omnaest.utils.table.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.omnaest.utils.JSONHelper;
import org.omnaest.utils.StringUtils;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableDeserializer;
import org.omnaest.utils.table.components.TableSerializer;
import org.omnaest.utils.table.components.TableTranslator;
import org.omnaest.utils.table.domain.Cell;
import org.omnaest.utils.table.domain.Column;
import org.omnaest.utils.table.domain.Row;

public class ArrayTable implements Table
{
    private KeyIndex  columnIndex = new KeyIndex();
    private KeyIndex  rowIndex    = new KeyIndex();
    private TableData data        = new TableData();

    public class RowImpl implements Row
    {
        private int rowIndex;

        public RowImpl(int rowIndex)
        {
            super();
            this.rowIndex = rowIndex;
        }

        @Override
        public Row addValues(String... values)
        {
            if (values != null)
            {
                Arrays.asList(values)
                      .forEach(this::addValue);
            }
            return this;
        }

        @Override
        public List<Cell> getCells()
        {
            List<Cell> result = new ArrayList<>();

            for (int ii = 0; ii < this.size(); ii++)
            {
                int index = ii;
                result.add(this.getCell(index));
            }

            return result;
        }

        @Override
        public int size()
        {
            return ArrayTable.this.data.getColumnSize();
        }

        @Override
        public Cell getCell(int columnIndex)
        {
            return new Cell()
            {
                @Override
                public String getValue()
                {
                    return columnIndex >= 0 && columnIndex < RowImpl.this.size() ? ArrayTable.this.data.get(RowImpl.this.rowIndex, columnIndex) : null;
                }

                @Override
                public Row getRow()
                {
                    return RowImpl.this;
                }

                @Override
                public Column getColumn()
                {
                    return ArrayTable.this.getColumn(columnIndex);
                }

                @Override
                public boolean isEmpty()
                {
                    return org.apache.commons.lang3.StringUtils.isEmpty(this.getValue());
                }

                @Override
                public boolean isBlank()
                {
                    return org.apache.commons.lang3.StringUtils.isBlank(this.getValue());
                }
            };
        }

        @Override
        public Cell getCell(String columnTitle)
        {
            return this.getCell(ArrayTable.this.columnIndex.getIndex(columnTitle));
        }

        @Override
        public List<String> getValues()
        {
            String[] values = new String[this.size()];
            for (int columnIndex = 0; columnIndex < this.size(); columnIndex++)
            {
                values[columnIndex] = this.getValue(columnIndex);
            }
            return Arrays.asList(values);
        }

        @Override
        public Map<String, String> asMap()
        {
            Map<String, String> map = new LinkedHashMap<>();
            for (int ii = 0; ii < this.size(); ii++)
            {
                map.put(StringUtils.defaultIfNull(ArrayTable.this.getColumnTitle(ii), "" + ii), this.getValue(ii));
            }
            return map;
        }

        @Override
        public String getValue(int columnIndex)
        {
            if (columnIndex < 0 && columnIndex >= this.size())
            {
                throw new IndexOutOfBoundsException();
            }
            return ArrayTable.this.data.get(this.rowIndex, columnIndex);
        }

        @Override
        public String getValue(String columnTitle)
        {
            return this.getValue(ArrayTable.this.columnIndex.getIndex(columnTitle));
        }

        @Override
        public Row addValue(String value)
        {
            int columnIndex = 0;
            while (columnIndex < this.size() && this.getValue(columnIndex) != null)
            {
                columnIndex++;
            }
            this.setValue(columnIndex, value);
            return this;
        }

        private Row setValue(int columnIndex, String value)
        {
            ArrayTable.this.data.set(this.rowIndex, columnIndex, value);
            return this;
        }

        @Override
        public Cell getFirstCell()
        {
            return this.getCell(0);
        }

        @Override
        public String getFirstValue()
        {
            return this.size() == 0 ? null : this.getValue(0);
        }

        @Override
        public String getSecondValue()
        {
            return this.size() <= 1 ? null : this.getValue(1);
        }

        @Override
        public String toString()
        {
            return "RowImpl [rowIndex=" + this.rowIndex + ", getValues()=" + this.getValues() + "]";
        }

        @Override
        public List<String> asList()
        {
            return Arrays.asList(ArrayTable.this.data.getRow(this.rowIndex));
        }

        @Override
        public Iterator<String> iterator()
        {
            return this.asList()
                       .iterator();
        }

        @Override
        public Stream<String> stream()
        {
            return this.asList()
                       .stream();
        }

        @Override
        public <T> T asBean(Class<T> type)
        {
            return JSONHelper.toObjectWithType(this.asMap(), type);
        }

    }

    @Override
    public Table addColumnTitle(String title)
    {
        this.columnIndex.addKey(title);
        return this;
    }

    public String getColumnTitle(int index)
    {
        return this.columnIndex.getKey(index);
    }

    @Override
    public Table addColumnTitles(String... titles)
    {
        return this.addColumnTitles(Arrays.asList(titles));
    }

    @Override
    public Table addColumnTitles(List<String> titles)
    {
        if (titles != null)
        {
            titles.forEach(this::addColumnTitle);
        }
        return this;
    }

    @Override
    public Table addRowTitles(List<String> titles)
    {
        if (titles != null)
        {
            titles.forEach(this::addRowTitle);
        }
        return this;
    }

    @Override
    public Table addRowTitles(String... titles)
    {
        return this.addRowTitles(Arrays.asList(titles));
    }

    @Override
    public Table addRowTitle(String title)
    {
        this.rowIndex.addKey(title);
        return this;
    }

    public String getRowTitle(int index)
    {
        return this.rowIndex.getKey(index);
    }

    @Override
    public Table addRow(String... values)
    {
        this.newRow()
            .addValues(values);
        return this;
    }

    @Override
    public Table addRow(Map<String, String> row)
    {
        if (row != null)
        {
            String[] values = new String[row.size()];
            row.forEach((key, value) ->
            {
                if (!this.columnIndex.hasKey(key))
                {
                    this.columnIndex.addKey(key);
                }

                int index = this.columnIndex.getIndex(key);
                values[index] = value;
            });
            this.newRow()
                .addValues(values);
        }
        return this;
    }

    @Override
    public Row newRow()
    {
        int rowIndex = this.getRowSize();
        Row result = new RowImpl(rowIndex);
        this.data.setRowSize(rowIndex + 1);
        return result;
    }

    @Override
    public int getRowSize()
    {
        return this.data.getRowSize();
    }

    @Override
    public List<Row> getRows()
    {
        return Collections.unmodifiableList(this.stream()
                                                .collect(Collectors.toList()));
    }

    @Override
    public Stream<Row> stream()
    {
        return IntStream.range(0, this.getRowSize())
                        .mapToObj(rowIndex -> this.getRow(rowIndex));
    }

    @Override
    public Row getRow(int rowIndex)
    {
        return new RowImpl(rowIndex);
    }

    public Column getColumn(int index)
    {
        return new Column()
        {
            @Override
            public String getTitle()
            {
                return ArrayTable.this.getColumnTitle(index);
            }

            @Override
            public List<Cell> getCells()
            {
                List<Cell> result = new ArrayList<>();

                for (Row row : ArrayTable.this.getRows())
                {
                    result.add(row.getCell(index));
                }

                return result;
            }
        };
    }

    @Override
    public TableSerializer serialize()
    {
        return new TableSerializerImpl(this);
    }

    @Override
    public List<String> getColumnTitles()
    {
        return this.columnIndex.getKeys();
    }

    @Override
    public List<Column> getColumns()
    {
        List<Column> result = new ArrayList<>();
        for (int ii = 0; ii < this.columnIndex.size(); ii++)
        {
            result.add(this.getColumn(ii));
        }
        return result;
    }

    @Override
    public String getValue(int rowIndex, int columnIndex)
    {
        return this.data.get(rowIndex, columnIndex);
    }

    @Override
    public String getValue(String rowTitle, String columnTitle)
    {
        return this.getValue(this.rowIndex.getIndex(rowTitle), this.columnIndex.getIndex(columnTitle));
    }

    @Override
    public Iterator<Row> iterator()
    {
        return this.getRows()
                   .iterator();
    }

    @Override
    public String toString()
    {
        return this.serialize()
                   .asCsv();
    }

    @Override
    public TableTranslator as()
    {
        return new TableTranslatorImpl(this);
    }

    @Override
    public TableDataLoader load()
    {
        return new TableDataLoader()
        {
            @Override
            public Table fromRows(Stream<Map<String, String>> rows)
            {
                if (rows != null)
                {
                    rows.forEach(ArrayTable.this::addRow);
                }
                return ArrayTable.this;
            }

            @Override
            public Table fromRows(Iterable<Map<String, String>> rows)
            {
                if (rows != null)
                {
                    rows.forEach(ArrayTable.this::addRow);
                }
                return ArrayTable.this;
            }
        };
    }

    @Override
    public TableDeserializer deserialize()
    {
        return new TableDeserializerImpl(this);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.columnIndex == null) ? 0 : this.columnIndex.hashCode());
        result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
        result = prime * result + ((this.rowIndex == null) ? 0 : this.rowIndex.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        ArrayTable other = (ArrayTable) obj;
        if (this.columnIndex == null)
        {
            if (other.columnIndex != null)
            {
                return false;
            }
        }
        else if (!this.columnIndex.equals(other.columnIndex))
        {
            return false;
        }
        if (this.data == null)
        {
            if (other.data != null)
            {
                return false;
            }
        }
        else if (!this.data.equals(other.data))
        {
            return false;
        }
        if (this.rowIndex == null)
        {
            if (other.rowIndex != null)
            {
                return false;
            }
        }
        else if (!this.rowIndex.equals(other.rowIndex))
        {
            return false;
        }
        return true;
    }

}
