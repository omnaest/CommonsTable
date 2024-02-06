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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.omnaest.utils.element.bi.BiElement;
import org.omnaest.utils.table.components.TableDeserializer;
import org.omnaest.utils.table.domain.Column;
import org.omnaest.utils.table.domain.Row;
import org.omnaest.utils.table.internal.ArrayTable;

public interface Table extends ImmutableTable
{
    public Table addRow(String... values);

    public Table addRow(Iterable<String> values);

    /**
     * Similar to {@link #addRow(Iterable)}, adding both {@link BiElement} values.
     * 
     * @param tuple
     * @return
     */
    public Table addRow(BiElement<String, String> tuple);

    /**
     * Adds a new {@link Row} based on the given {@link Map} that represents the row entry. All keys are column fields. If a key is not yet known to the
     * {@link Table} it will be added to the columns.
     * 
     * @param row
     * @return
     */
    public Table addRow(Map<String, String> row);

    /**
     * Processes a given {@link Stream} of elements and creates a new {@link Row} which can be initialized based on a single element each.
     * 
     * @param elements
     * @param elementAndRowConsumer
     * @return
     */
    public <E> Table processAndAddRow(Stream<E> elements, BiConsumer<E, Row> elementAndRowConsumer);

    public Row newRow();

    public Table addColumnTitles(String... titles);

    public Table addColumnTitles(List<String> titles);

    public Table addColumnTitle(String title);

    public Table addRowTitle(String title);

    public Table addRowTitles(List<String> titles);

    public Table addRowTitles(String... titles);

    /**
     * Returns the defined {@link Column}s of the {@link Table}
     * 
     * @return
     */
    public List<Column> getColumns();

    public Optional<Column> getColumn(String columnTitle);

    /**
     * Returns the effective {@link Column}s of the {@link Table}. This includes pseudo {@link Column}s that have been created due to a writing of a cell/value
     * in a row, even if no column was defined for that cell/value.
     * 
     * @return
     */
    public List<Column> getEffectiveColumns();

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

    public TableJoiner join();

    public static interface TableJoiner
    {
        public <PK> TableJoinerWithLeftColumn<PK> usingColumnFunction(Function<Row, PK> primaryKeyFunction);

        public TableJoinerWithLeftColumn<String> usingColumn(String columnTitle);

        public TableJoinerWithLeftColumn<Integer> usingRowIndex();
    }

    public static interface TableJoinerWithLeftColumn<PK>
    {
        public TableJoinerWithRightTable<PK> with(Table tableRight);
    }

    public static interface TableJoinerWithRightTable<PK>
    {
        public <UR> TableJoinerWithRightColumn<PK> usingColumnFunction(Function<Row, PK> columnFunction);

        public TableJoinerWithRightColumn<String> usingColumn(String columnTitle);

        public TableJoinerWithRightColumn<Integer> usingRowIndex();
    }

    public static interface TableJoinerWithRightColumn<PK>
    {
        public Table inner();
    }

    public static Table newInstance()
    {
        return tableSupplier.get();
    }

    public static Supplier<Table> tableSupplier = () -> new ArrayTable();
}
