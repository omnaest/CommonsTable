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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.omnaest.utils.csv.CSVUtils;
import org.omnaest.utils.csv.CSVUtils.Parser;
import org.omnaest.utils.csv.CSVUtils.ParserLoadedAndFormatDeclared;
import org.omnaest.utils.exception.RuntimeIOException;
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
        return this.asCsv()
                   .from(csv);
    }

    @Override
    public Table fromCsv(Function<Parser, ParserLoadedAndFormatDeclared> parserFunction) throws IOException
    {
        return this.asCsv()
                   .from(parserFunction);
    }

    @Override
    public Table fromCsv(File file)
    {
        return this.asCsv()
                   .from(file);
    }

    @Override
    public Table fromCsv(InputStream inputStream)
    {
        return this.asCsv()
                   .from(inputStream);
    }

    @Override
    public CsvReader asCsv()
    {
        Table table = this.table;

        return new CsvReader()
        {

            @Override
            public Table from(String csv)
            {
                try
                {
                    return this.from(parser -> parser.from(csv)
                                                     .withFormat(TableSerializerImpl.DEFAULT_CSV_FORMAT.withFirstRecordAsHeader()));
                }
                catch (IOException e)
                {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public Table from(Function<Parser, ParserLoadedAndFormatDeclared> parserFunction) throws IOException
            {
                return table.load()
                            .fromRows(parserFunction.apply(CSVUtils.parse())
                                                    .get());
            }

            @Override
            public Table from(InputStream inputStream)
            {
                try
                {
                    return this.from(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
                }
                catch (IOException e)
                {
                    throw new RuntimeIOException(e);
                }
            }

            @Override
            public Table from(File file)
            {
                try
                {
                    return this.from(IOUtils.toBufferedInputStream(new FileInputStream(file)));
                }
                catch (IOException e)
                {
                    throw new RuntimeIOException(e);
                }
            }

            @Override
            public Optional<Table> fromIfExists(File file)
            {
                if (file != null && file.exists() && file.isFile())
                {
                    return Optional.of(this.from(file));
                }
                else
                {
                    return Optional.empty();
                }
            }
        };
    }

}
