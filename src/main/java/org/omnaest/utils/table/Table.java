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
package org.omnaest.utils.table;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.omnaest.utils.table.components.TableDeserializer;
import org.omnaest.utils.table.domain.Column;
import org.omnaest.utils.table.domain.Row;
import org.omnaest.utils.table.internal.ArrayTable;

public interface Table extends ImmutableTable
{
    public Table addRow(String... values);

    public Table addRow(List<String> values);

    /**
     * Adds a new {@link Row} based on the given {@link Map} that represents the row entry. All keys are column fields. If a key is not yet known to the
     * {@link Table} it will be added to the columns.
     * 
     * @param row
     * @return
     */
    public Table addRow(Map<String, String> row);

    public Row newRow();

    public Table addColumnTitles(String... titles);

    public Table addColumnTitles(List<String> titles);

    public Table addColumnTitle(String title);

    public Table addRowTitle(String title);

    public Table addRowTitles(List<String> titles);

    public Table addRowTitles(String... titles);

    public List<Column> getColumns();

    public Row getRow(int rowIndex);

    public List<Row> getRows();

    public Stream<Row> stream();

    public static interface TableDataLoader
    {
        public Table fromRows(Stream<Map<String, String>> rows);

        public Table fromRows(Iterable<Map<String, String>> rows);
    }

    /**
     * @see #deserialize()
     * @return
     */
    public TableDataLoader load();

    /**
     * @see #load()
     * @see #serialize()
     * @return
     */
    public TableDeserializer deserialize();

    public static Table newInstance()
    {
        return tableSupplier.get();
    }

    public static Supplier<Table> tableSupplier = () -> new ArrayTable();

}
