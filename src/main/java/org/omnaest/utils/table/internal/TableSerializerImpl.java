package org.omnaest.utils.table.internal;

import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.omnaest.utils.csv.CSVUtils;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableSerializer;

public class TableSerializerImpl implements TableSerializer
{
    private Table table;

    public TableSerializerImpl(Table table)
    {
        this.table = table;
    }

    @Override
    public String asCsv()
    {
        return CSVUtils.serializer()
                       .withHeaders(this.getColumnTitles())
                       .withCSVFormat(CSVFormat.EXCEL.withDelimiter(';'))
                       .intoString(this.table.getRows()
                                             .stream()
                                             .map(Row::asMap));
    }

    private List<String> getColumnTitles()
    {
        List<String> columnTitles = this.table.getColumnTitles();
        return columnTitles;
    }

}
