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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.components.TableTranslator.SortOrder;
import org.omnaest.utils.table.domain.Cell;
import org.omnaest.utils.table.domain.Column;
import org.omnaest.utils.table.domain.Row;
import org.omnaest.utils.table.domain.ValueAccessor;

public class ArrayTableTest
{

    @Test
    public void testAddRow() throws Exception
    {
        Table table = Table.newInstance();
        table.addColumnTitles("column1", "column2", "column3")
             .addRow("1.0", "2.0", "3.0");

        assertEquals("1.0", table.getValue(0, 0));
        assertEquals("2.0", table.getValue(0, 1));
        assertEquals("3.0", table.getValue(0, 2));

        assertEquals(Arrays.asList("1.0", "2.0", "3.0"), table.getRow(0)
                                                              .asList());

        String csv = table.serialize()
                          .asCsv()
                          .get();
        //        System.out.println(csv);
        StringBuilder csvExpected = new StringBuilder();
        csvExpected.append("column1;column2;column3");
        csvExpected.append("\r\n");
        csvExpected.append("1.0;2.0;3.0");
        csvExpected.append("\r\n");

        assertEquals(csvExpected.toString(), csv);
    }

    @Test
    public void testAddRow2() throws Exception
    {
        Table table = Table.newInstance();
        table.addColumnTitles("column1", "column2", "column3")
             .addRow("1.0", "2.0", "3.0")
             .addRow("4.0", "5.0", "6.0");

        assertEquals(Arrays.asList("1.0", "2.0", "3.0"), table.getRow(0)
                                                              .asList());
        assertEquals(Arrays.asList("4.0", "5.0", "6.0"), table.getRow(1)
                                                              .asList());

    }

    @Test
    public void testAddRowWithNewColumn() throws Exception
    {
        Table table = Table.newInstance()
                           .processAndAddRow(Arrays.asList("a", "b", "c")
                                                   .stream(),
                                             (element, row) -> row.getCellOrNew(element)
                                                                  .setValue("true"));

        assertEquals(Table.newInstance()
                          .addColumnTitles("a", "b", "c")
                          .addRow("true", null, null)
                          .addRow(null, "true", null)
                          .addRow(null, null, "true"),
                     table);

    }

    public static class TestBean
    {
        private String column1;
        private String column2;
        private String column3;

        private TestBean()
        {
            super();
        }

        public String getColumn1()
        {
            return this.column1;
        }

        public String getColumn2()
        {
            return this.column2;
        }

        public String getColumn3()
        {
            return this.column3;
        }

        public List<String> columns()
        {
            return Arrays.asList(this.column1, this.column2, this.column3);
        }

        @Override
        public String toString()
        {
            return "TestBean [column1=" + this.column1 + ", column2=" + this.column2 + ", column3=" + this.column3 + "]";
        }

    }

    @Test
    public void testAsBean() throws Exception
    {
        Table table = Table.newInstance();
        table.addColumnTitles("column1", "column2", "column3")
             .addRow("1.0", "2.0", "3.0")
             .addRow("4.0", "5.0", "6.0");

        List<TestBean> beans = table.stream()
                                    .map(row -> row.asBean(TestBean.class))
                                    .collect(Collectors.toList());

        assertEquals(2, beans.size());

        assertEquals(Arrays.asList("1.0", "2.0", "3.0"), beans.get(0)
                                                              .columns());
        assertEquals(Arrays.asList("4.0", "5.0", "6.0"), beans.get(1)
                                                              .columns());

    }

    @Test
    public void testDeserialize() throws Exception
    {
        Table table = Table.newInstance()
                           .addColumnTitles("column1", "column2")
                           .addRow("0.0", "0.1")
                           .addRow("1.0", "1.1");
        String csv = table.serialize()
                          .asCsv()
                          .get();
        Table tableDeserialized = Table.newInstance()
                                       .deserialize()
                                       .fromCsv(csv);
        assertEquals(table, tableDeserialized);
    }

    @Test
    public void testGetEffectiveColumns() throws Exception
    {
        Table table = Table.newInstance()
                           .addRow("a1", "b1")
                           .addRow("a2", "b2", "c2");
        assertEquals(3, table.getEffectiveColumns()
                             .size());
        assertEquals(Arrays.asList(null, null, null), table.getEffectiveColumns()
                                                           .stream()
                                                           .map(Column::getTitle)
                                                           .collect(Collectors.toList()));
        assertEquals(Arrays.asList("a1", "a2"), table.getEffectiveColumns()
                                                     .stream()
                                                     .findFirst()
                                                     .map(Column::getCells)
                                                     .map(List::stream)
                                                     .orElse(Stream.empty())
                                                     .map(Cell::getValue)
                                                     .collect(Collectors.toList()));
        assertEquals(Arrays.asList(null, "c2"), table.getEffectiveColumns()
                                                     .stream()
                                                     .skip(2)
                                                     .findFirst()
                                                     .map(Column::getCells)
                                                     .map(List::stream)
                                                     .orElse(Stream.empty())
                                                     .map(Cell::getValue)
                                                     .collect(Collectors.toList()));
    }

    @Test
    public void testRowAsList() throws Exception
    {
        Table table = Table.newInstance()
                           .addColumnTitles("column1", "column2")
                           .addRow("a1")
                           .addRow("a2", "b2");
        assertEquals(Arrays.asList("a1", null), table.stream()
                                                     .findFirst()
                                                     .get()
                                                     .asList());
        assertEquals(Arrays.asList("a2", "b2"), table.stream()
                                                     .skip(1)
                                                     .findFirst()
                                                     .get()
                                                     .asList());
    }

    @Test
    public void testInnerJoin() throws Exception
    {
        Table tableLeft = Table.newInstance()
                               .addColumnTitles("column1", "column2")
                               .addRow("a1", "b1")
                               .addRow("a2", "b2");
        Table tableRight = Table.newInstance()
                                .addColumnTitles("column1", "column3")
                                .addRow("a1", "c1")
                                .addRow("a2", "c2");

        Table innerJoin = tableLeft.join()
                                   .usingColumn("column1")
                                   .with(tableRight)
                                   .usingColumn("column1")
                                   .inner();
        assertEquals(Table.newInstance()
                          .addColumnTitles("column1", "column2", "column3")
                          .addRow("a1", "b1", "c1")
                          .addRow("a2", "b2", "c2"),
                     innerJoin);
    }

    @Test
    public void testUniqueRows()
    {
        assertEquals(Table.newInstance()
                          .addRow("a", "b")
                          .addRow("c", "d")
                          .addRow("a", "c"),
                     Table.newInstance()
                          .addRow("a", "b")
                          .addRow("c", "d")
                          .addRow("a", "b")
                          .addRow("a", "c")
                          .as()
                          .tableWithUniqueRows());
        assertEquals(Table.newInstance()
                          .addColumnTitles("c1", "c2"),
                     Table.newInstance()
                          .addColumnTitles("c1", "c2")
                          .as()
                          .tableWithUniqueRows());
    }

    @Test
    public void testFilteredTable()
    {
        assertEquals(Table.newInstance()
                          .addRow("a", "b")
                          .addRow("a", "b")
                          .addRow("a", "c"),
                     Table.newInstance()
                          .addRow("a", "b")
                          .addRow("c", "d")
                          .addRow("a", "b")
                          .addRow("a", "c")
                          .as()
                          .filteredTable(row -> row.getFirstValue()
                                                   .equals("a")));
        assertEquals(Table.newInstance()
                          .addColumnTitles("c1", "c2"),
                     Table.newInstance()
                          .addColumnTitles("c1", "c2")
                          .as()
                          .filteredTable(row -> true));
    }

    @Test
    public void testSortedTable()
    {
        assertEquals(Table.newInstance()
                          .addRow("a", "c")
                          .addRow("b", "b")
                          .addRow("c", "a"),
                     Table.newInstance()
                          .addRow("b", "b")
                          .addRow("c", "a")
                          .addRow("a", "c")
                          .as()
                          .sortedBy(row -> row.getFirstValue(), SortOrder.ASCENDING));
        assertEquals(Table.newInstance()
                          .addRow("c", "a")
                          .addRow("b", "b")
                          .addRow("a", "c"),
                     Table.newInstance()
                          .addRow("b", "b")
                          .addRow("c", "a")
                          .addRow("a", "c")
                          .as()
                          .sortedBy(Row::getFirstValue, SortOrder.DESCENDING));
    }

    @Test
    public void testValueAccessor()
    {
        Table table = Table.newInstance()
                           .addColumnTitles("intColumn", "doubleColumn", "booleanColumn")
                           .addRow("1", "1.3", "true")
                           .addRow("", "", "")
                           .addRow();
        assertEquals(1, table.getRow(0)
                             .getOptionalValueAs("intColumn")
                             .map(ValueAccessor::intValue)
                             .get()
                             .intValue());
        assertEquals(1.3, table.getRow(0)
                               .getOptionalValueAs("doubleColumn")
                               .map(ValueAccessor::doubleValue)
                               .get()
                               .doubleValue(),
                     0.01);
        assertEquals(true, table.getRow(0)
                                .getOptionalValueAs("booleanColumn")
                                .map(ValueAccessor::booleanValue)
                                .get()
                                .booleanValue());
        assertEquals(0, table.getRow(1)
                             .getOptionalValueAs("intColumn")
                             .map(ValueAccessor::intValue)
                             .get()
                             .intValue());
        assertEquals(0.0, table.getRow(1)
                               .getOptionalValueAs("doubleColumn")
                               .map(ValueAccessor::doubleValue)
                               .get()
                               .doubleValue(),
                     0.01);
        assertEquals(false, table.getRow(1)
                                 .getOptionalValueAs("booleanColumn")
                                 .map(ValueAccessor::booleanValue)
                                 .get()
                                 .booleanValue());
        assertEquals(false, table.getRow(2)
                                 .getOptionalValueAs("intColumn")
                                 .map(ValueAccessor::intValue)
                                 .isPresent());
        assertEquals(false, table.getRow(2)
                                 .getOptionalValueAs("doubleColumn")
                                 .map(ValueAccessor::doubleValue)
                                 .isPresent());
        assertEquals(false, table.getRow(2)
                                 .getOptionalValueAs("booleanColumn")
                                 .map(ValueAccessor::booleanValue)
                                 .isPresent());
    }

}
