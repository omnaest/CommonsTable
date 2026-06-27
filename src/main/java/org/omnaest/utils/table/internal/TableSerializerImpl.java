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
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.FileUtils;
import org.omnaest.utils.csv.CSVUtils;
import org.omnaest.utils.csv.CSVUtils.CSVSerializationConsumer;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableSerializer;
import org.omnaest.utils.table.domain.Cell;
import org.omnaest.utils.table.domain.Column;
import org.omnaest.utils.table.domain.Row;

public class TableSerializerImpl implements TableSerializer
{
    protected static final CSVFormat DEFAULT_CSV_FORMAT = CSVFormat.EXCEL.withDelimiter(';');

    private Table                    table;

    public TableSerializerImpl(Table table)
    {
        this.table = table;
    }

    @Override
    public SerializationResultWriter asCsv(Consumer<CsvWriterOptions> options)
    {
        CSVSerializationConsumer serializer = CSVUtils.serializer()
                                                      .withCSVFormat(DEFAULT_CSV_FORMAT);
        if (options != null)
        {
            options.accept(new CsvWriterOptions() {
                @Override
                public CsvWriterOptions withDelimiter(char delimiter)
                {
                    serializer.withDelimiter(delimiter);
                    return this;
                }
            });
        }

        return this.createSerializationResultWriter(() -> serializer.withHeaders(TableSerializerImpl.this.table.getColumnTitles())
                                                                    .intoString(TableSerializerImpl.this.table.getRows()
                                                                                                              .stream()
                                                                                                              .map(Row::asMap)));
    }

    @Override
    public SerializationResultWriter asCsv()
    {
        return this.asCsv(options ->
        {
            // use defaults
        });
    }

    @Override
    public SerializationResultWriter asTabSeparated()
    {
        return this.asCsv(options -> options.withDelimiter('\t'));
    }

    private SerializationResultWriter createSerializationResultWriter(Supplier<String> serializationContentCreator)
    {
        return new SerializationResultWriter() {
            @Override
            public String get()
            {
                return serializationContentCreator.get();
            }

            @Override
            public SerializationResultWriter writeInto(File file)
            {
                FileUtils.toConsumer(file)
                         .accept(this.get());
                return this;
            }
        };
    }

    @Override
    public SerializationResultWriter asFixColumnSizeFormatted()
    {
        List<Column> effectiveColumns = this.table.getEffectiveColumns();
        Map<String, Integer> columnTitleToMaxLength = effectiveColumns.stream()
                                                                      .collect(Collectors.toMap(Column::getTitle,
                                                                                                column -> Stream.concat(Stream.of(column.getTitle()),
                                                                                                                        column.getValues()
                                                                                                                              .stream())
                                                                                                                .map(StringUtils::defaultString)
                                                                                                                .mapToInt(String::length)
                                                                                                                .max()
                                                                                                                .orElse(0),
                                                                                                (v1, v2) -> v1));
        BiFunction<String, String, String> valuePaddingNormalizer = (value, columnTitle) -> StringUtils.rightPad(StringUtils.defaultString(value),
                                                                                                                 columnTitleToMaxLength.getOrDefault(columnTitle, 0));

        Supplier<String> serializer = () -> org.omnaest.utils.StringUtils.builder()
                                                                         .addLine(this.table.getEffectiveColumnTitles()
                                                                                            .stream()
                                                                                            .map(columnTitle -> valuePaddingNormalizer.apply(columnTitle,
                                                                                                                                             columnTitle))
                                                                                            .collect(Collectors.joining(" ")))
                                                                         .addLines(this.table.stream()
                                                                                             .map(row -> effectiveColumns.stream()
                                                                                                                         .map(Column::getTitle)
                                                                                                                         .map(title -> valuePaddingNormalizer.apply(row.getOptionalCell(title)
                                                                                                                                                                       .map(Cell::getValue)

                                                                                                                                                                       .orElse(""),
                                                                                                                                                                    title))
                                                                                                                         .collect(Collectors.joining(" ")))
                                                                                             .toList())
                                                                         .build();
        return this.createSerializationResultWriter(serializer);
    }

}
