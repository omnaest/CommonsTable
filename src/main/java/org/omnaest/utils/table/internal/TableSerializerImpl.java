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

import org.apache.commons.csv.CSVFormat;
import org.omnaest.utils.FileUtils;
import org.omnaest.utils.csv.CSVUtils;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableSerializer;
import org.omnaest.utils.table.domain.Row;

public class TableSerializerImpl implements TableSerializer
{
    protected static final CSVFormat DEFAULT_CSV_FORMAT = CSVFormat.EXCEL.withDelimiter(';');

    private Table table;

    public TableSerializerImpl(Table table)
    {
        this.table = table;
    }

    @Override
    public CsvWriter asCsv()
    {
        return new CsvWriter()
        {
            @Override
            public String get()
            {
                return CSVUtils.serializer()
                               .withHeaders(TableSerializerImpl.this.table.getColumnTitles())
                               .withCSVFormat(DEFAULT_CSV_FORMAT)
                               .intoString(TableSerializerImpl.this.table.getRows()
                                                                         .stream()
                                                                         .map(Row::asMap));
            }

            @Override
            public CsvWriter writeInto(File file)
            {
                FileUtils.toConsumer(file)
                         .accept(this.get());
                return this;
            }
        };
    }

}
