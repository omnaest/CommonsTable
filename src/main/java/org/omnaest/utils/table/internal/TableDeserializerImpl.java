/*******************************************************************************
 * Copyright 2021 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.omnaest.utils.table.internal;

import java.io.IOException;
import java.util.function.Function;

import org.omnaest.utils.csv.CSVUtils;
import org.omnaest.utils.csv.CSVUtils.Parser;
import org.omnaest.utils.csv.CSVUtils.ParserLoadedAndFormatDeclared;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableDeserializer;

public class TableDeserializerImpl implements TableDeserializer
{
    private Table table;

    public TableDeserializerImpl(Table table)
    {
        this.table = table;
    }

    @Override
    public Table fromCsv(String csv)
    {
        try
        {
            return this.fromCsv(parser -> parser.from(csv)
                                                .withFormat(TableSerializerImpl.DEFAULT_CSV_FORMAT.withFirstRecordAsHeader()));
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Table fromCsv(Function<Parser, ParserLoadedAndFormatDeclared> parserFunction) throws IOException
    {
        return this.table.load()
                         .fromRows(parserFunction.apply(CSVUtils.parse())
                                                 .get());
    }

}
