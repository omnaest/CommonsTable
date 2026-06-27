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
package org.omnaest.utils.table.components;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.omnaest.utils.table.Table;

public interface TableSerializer
{
    public SerializationResultWriter asCsv();

    public SerializationResultWriter asCsv(Consumer<CsvWriterOptions> options);

    public static interface CsvWriterOptions
    {
        public CsvWriterOptions withDelimiter(char delimiter);
    }

    public SerializationResultWriter asTabSeparated();

    /**
     * Returns a {@link SerializationResultWriter} that represents the {@link Table} with all column values using the space of the column value with the maximal
     * length.
     * 
     * @return
     */
    public SerializationResultWriter asFixColumnSizeFormatted();

    public static interface SerializationResultWriter extends Supplier<String>
    {
        public SerializationResultWriter writeInto(File file);
    }

}
