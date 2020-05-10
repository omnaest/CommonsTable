package org.omnaest.utils.table.components;

import java.io.IOException;
import java.util.function.Function;

import org.omnaest.utils.csv.CSVUtils.Parser;
import org.omnaest.utils.csv.CSVUtils.ParserLoadedAndFormatDeclared;
import org.omnaest.utils.table.Table;

public interface TableDeserializer
{
    public Table fromCsv(String csv);

    public Table fromCsv(Function<Parser, ParserLoadedAndFormatDeclared> parserFunction) throws IOException;

}
