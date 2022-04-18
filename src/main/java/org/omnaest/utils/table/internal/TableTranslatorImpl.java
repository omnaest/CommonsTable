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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.omnaest.utils.PredicateUtils;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableColumnIndex;
import org.omnaest.utils.table.components.TableTranslator;
import org.omnaest.utils.table.domain.Row;

public class TableTranslatorImpl implements TableTranslator
{
    private Table table;

    public TableTranslatorImpl(Table table)
    {
        super();
        this.table = table;
    }

    @Override
    public Map<String, String> map()
    {
        return this.map(row -> row.getFirstValue(), row -> row.getSecondValue());
    }

    @Override
    public <K, V> Map<K, V> map(Function<Row, K> keyMapper, Function<Row, V> valueMapper)
    {
        return this.table.stream()
                         .collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public <K, V> Map<K, List<V>> group(Function<Row, K> keyMapper, Function<Row, V> valueMapper)
    {
        return this.table.stream()
                         .filter(row -> keyMapper.apply(row) != null)
                         .collect(Collectors.groupingBy(keyMapper, Collectors.mapping(valueMapper, Collectors.toList())));
    }

    @Override
    public TableColumnIndex indexOfColumn(String columnTitle)
    {
        Map<String, List<Row>> map = this.group(row -> row.getValue(columnTitle), row -> row);

        return new TableColumnIndex()
        {
            @Override
            public Optional<Row> getRowByValue(String value)
            {
                return this.getRowsByValue(value)
                           .findFirst();
            }

            @Override
            public Stream<Row> getRowsByValue(String value)
            {
                return Optional.ofNullable(map.get(value))
                               .map(List::stream)
                               .orElse(Stream.empty());
            }

            @Override
            public boolean containsValue(String value)
            {
                return this.getRowByValue(value)
                           .isPresent();
            }
        };
    }

    @Override
    public Table tableWithUniqueRows()
    {
        Table result = Table.newInstance()
                            .addColumnTitles(this.table.getColumnTitles());
        this.table.stream()
                  .map(Row::asList)
                  .distinct()
                  .forEach(result::addRow);
        return result;
    }

    @Override
    public Table filteredTable(Predicate<Row> rowInclusionFilter)
    {
        Table result = Table.newInstance()
                            .addColumnTitles(this.table.getColumnTitles());
        this.table.stream()
                  .filter(Optional.ofNullable(rowInclusionFilter)
                                  .orElse(PredicateUtils.allMatching()))
                  .map(Row::asList)
                  .forEach(result::addRow);
        return result;
    }

}
