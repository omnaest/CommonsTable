package org.omnaest.utils.table.internal;

import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.omnaest.utils.csv.CSVUtils;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableSerializer;
import org.omnaest.utils.table.domain.Row;

public class TableSerializerImpl implements TableSerializer
{
    protected static final CSVFormat DEFAULT_CSV_FORMAT = CSVFormat.EXCEL.withDelimiter(';');
    private Table                    table;

    public TableSerializerImpl(Table table)
    {
        this.table = table;
    }

    @Override
    public String asCsv()
    {
        return CSVUtils.serializer()
                       .withHeaders(this.getColumnTitles())
                       .withCSVFormat(DEFAULT_CSV_FORMAT)
                       .intoString(this.table.getRows()
                                             .stream()
                                             .map(Row::asMap));
    }

    private List<String> getColumnTitles()
    {
        return this.table.getColumnTitles();
    }

}
