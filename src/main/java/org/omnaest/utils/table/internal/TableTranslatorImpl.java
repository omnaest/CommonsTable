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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.ComparatorUtils;
import org.omnaest.utils.ListUtils;
import org.omnaest.utils.MapUtils;
import org.omnaest.utils.PredicateUtils;
import org.omnaest.utils.StreamUtils;
import org.omnaest.utils.element.bi.BiElement;
import org.omnaest.utils.element.bi.UnaryBiElement;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableColumnIndex;
import org.omnaest.utils.table.components.TableTranslator;
import org.omnaest.utils.table.domain.Column;
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
    public Map<String, List<String>> groupedMap()
    {
        return this.group(Row::getFirstValue, Row::getSecondValue);
    }

    @Override
    public <R> Map<String, R> groupedAndProjectedMap(Function<List<String>, R> projector)
    {
        return this.groupedAndProjectedMap(Function.identity(), projector);
    }

    @Override
    public <K, V> Map<K, V> groupedAndProjectedMap(Function<String, K> keyMapper, Function<List<String>, V> projector)
    {
        return this.groupedMap()
                   .entrySet()
                   .stream()
                   .collect(Collectors.toMap(entry -> keyMapper.apply(entry.getKey()), entry -> projector.apply(entry.getValue())));
    }

    @Override
    public <K> Table groupedAndAggregatedAndProjectedRows(Function<Row, K> groupingKeyFunction, BiFunction<K, List<Row>, Row> aggregateProjectionFunction)
    {
        Map<K, List<Row>> keyToRows = this.table.stream()
                                                .collect(Collectors.groupingBy(groupingKeyFunction, () -> new LinkedHashMap<>(), Collectors.toList()));
        return Table.newInstance()
                    .processAndAddRow(keyToRows.entrySet()
                                               .stream(),
                                      (entry, row) -> row.setValuesByColumnTitles(aggregateProjectionFunction.apply(entry.getKey(), entry.getValue())));
    }

    @Override
    public TableColumnIndex indexOfColumn(String columnTitle)
    {
        Map<String, List<Row>> map = this.group(row -> row.getValue(columnTitle), row -> row);

        return new TableColumnIndex() {
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
    public Table filteredRows(Predicate<Row> rowInclusionFilter)
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

    @Override
    public <C extends Comparable<C>> Table sortedBy(Function<Row, C> rowSortingFunction, SortOrder sortOrder)
    {
        Table result = Table.newInstance()
                            .addColumnTitles(this.table.getColumnTitles());
        this.table.stream()
                  .sorted(SortOrder.ASCENDING.equals(sortOrder) ? ComparatorUtils.builder()
                                                                                 .of(rowSortingFunction)
                                                                                 .natural()
                          : ComparatorUtils.builder()
                                           .of(rowSortingFunction)
                                           .natural()
                                           .reversed())
                  .map(Row::asList)
                  .forEach(result::addRow);

        return result;
    }

    @Override
    public Table flipped()
    {
        List<Column> columns = this.table.getEffectiveColumns();
        return Table.newInstance()
                    .addColumnTitles(columns.stream()
                                            .findFirst()
                                            .map(column -> ListUtils.addToNew(column.getValues(), 0, column.getTitle()))
                                            .orElse(Collections.emptyList()))
                    .processAndAddRow(columns.stream()
                                             .skip(1),
                                      (column, row) -> row.setValues(ListUtils.addToNew(column.getValues(), 0, column.getTitle())));
    }

    @Override
    public Table cellContentMappedTable(UnaryOperator<String> cellContentMapper)
    {
        Table result = Table.newInstance()
                            .addColumnTitles(this.table.getEffectiveColumnTitles()
                                                       .stream()
                                                       .map(cellContentMapper)
                                                       .toList());
        this.table.stream()
                  .map(Row::asList)
                  .map(List<String>::stream)
                  .map(cellValues -> cellValues.map(cellContentMapper))
                  .map(Stream<String>::toList)
                  .forEach(result::addRow);

        return result;
    }

    @Override
    public Stream<Table> partitionedByMaxColumns(int maximumNumberOfColumns)
    {
        return StreamUtils.framed(maximumNumberOfColumns, IntStream.range(0, this.table.getEffectiveColumns()
                                                                                       .size()))
                          .filter(columnIndexes -> columnIndexes.length >= 1)
                          .map(columnIndexes -> UnaryBiElement.of(columnIndexes[0], columnIndexes[columnIndexes.length - 1]))
                          .map(columnIndexes -> this.table.as()
                                                          .columnSubsetClosed(columnIndexes.getFirst(), columnIndexes.getSecond()));
    }

    @Override
    public Table columnSubset(int startInclusive, int endExclusive)
    {
        return Table.newInstance()
                    .addColumnTitles(ListUtils.sublist(this.table.getEffectiveColumnTitles(), startInclusive, endExclusive))
                    .processAndAddRow(this.table.stream(), (previousRow, row) -> IntStream.range(startInclusive, endExclusive)
                                                                                          .forEach(columnIndex -> row.getCell(columnIndex - startInclusive)
                                                                                                                     .setValue(previousRow.getValue(columnIndex))));
    }

    @Override
    public Table columnSubset(int startInclusive)
    {
        return this.columnSubsetClosed(startInclusive, this.table.getEffectiveColumns()
                                                                 .size());
    }

    @Override
    public Table columnSubsetClosed(int startInclusive, int endInclusive)
    {
        return this.columnSubset(startInclusive, endInclusive + 1);
    }

    @Override
    public Table columnSubset(String... columnTitles)
    {
        return this.columnSubset(Optional.ofNullable(columnTitles)
                                         .map(Stream::of)
                                         .orElse(Stream.empty())
                                         .map(this.table::getColumn)
                                         .filter(Optional::isPresent)
                                         .map(Optional::get)
                                         .mapToInt(Column::getColumnIndex)
                                         .toArray());
    }

    @Override
    public Table columnSubset(int... columnIndex)
    {
        Map<Integer, Integer> unsortedPreviousColumnIndexToNewColumnIndex = StreamUtils.withIntCounter(Optional.ofNullable(columnIndex)
                                                                                                               .map(ArrayUtils::toObject)
                                                                                                               .map(Stream::of)
                                                                                                               .orElse(Stream.empty()))
                                                                                       .collect(Collectors.toMap(BiElement::getFirst, BiElement::getSecond));
        Map<Integer, Integer> previousColumnIndexToNewColumnIndex = MapUtils.toValueSortedMap(unsortedPreviousColumnIndexToNewColumnIndex);

        List<String> effectiveColumnTitles = this.table.getEffectiveColumnTitles();

        return Table.newInstance()
                    .addColumnTitles(previousColumnIndexToNewColumnIndex.keySet()
                                                                        .stream()
                                                                        .filter(previousColumnIndex -> previousColumnIndex < effectiveColumnTitles.size())
                                                                        .map(effectiveColumnTitles::get)
                                                                        .toList())
                    .processAndAddRow(this.table.stream(),
                                      (previousRow, row) -> previousColumnIndexToNewColumnIndex.forEach((previousColumnIndex, newColumnIndex) -> row.getCell(newColumnIndex)
                                                                                                                                                    .setValue(previousRow.getValue(previousColumnIndex))));
    }

}
