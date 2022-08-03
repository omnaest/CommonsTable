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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.domain.Row;

public interface TableTranslator
{
    public Map<String, String> map();

    public <K, V> Map<K, V> map(Function<Row, K> keyMapper, Function<Row, V> valueMapper);

    /**
     * Returns a {@link TableColumnIndex} on the given column
     * 
     * @param columnTitle
     * @return
     */
    public TableColumnIndex indexOfColumn(String columnTitle);

    public <K, V> Map<K, List<V>> group(Function<Row, K> keyMapper, Function<Row, V> valueMapper);

    public Table tableWithUniqueRows();

    public Table filteredTable(Predicate<Row> rowInclusionFilter);

    public <C extends Comparable<C>> Table sortedBy(Function<Row, C> rowSortingFunction, SortOrder sortOrder);

    public static enum SortOrder
    {
        ASCENDING, DESCENDING
    }
}
