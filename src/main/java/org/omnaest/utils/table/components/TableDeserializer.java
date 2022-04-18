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
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

import org.omnaest.utils.csv.CSVUtils.Parser;
import org.omnaest.utils.csv.CSVUtils.ParserLoadedAndFormatDeclared;
import org.omnaest.utils.exception.RuntimeIOException;
import org.omnaest.utils.table.Table;

public interface TableDeserializer
{
    public Table fromCsv(File file);

    public Table fromCsv(InputStream inputStream);

    public Table fromCsv(String csv);

    public Table fromCsv(Function<Parser, ParserLoadedAndFormatDeclared> parserFunction) throws IOException;

    public CsvReader asCsv();

    public static interface CsvReader
    {
        /**
         * Reads the {@link Table} from a {@link File}.
         * 
         * @throws RuntimeIOException
         * @param file
         * @return
         */
        public Table from(File file);

        public Table from(InputStream inputStream);

        public Table from(String csv);

        public Table from(Function<Parser, ParserLoadedAndFormatDeclared> parserFunction) throws IOException;

        /**
         * Reads the {@link Table} from a {@link File} if the {@link File} exists and is a readable {@link File}. Returns {@link Optional#empty()} if the
         * {@link File} does not exist.
         * 
         * @param file
         * @return
         */
        public Optional<Table> fromIfExists(File file);
    }
}
