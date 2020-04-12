package org.omnaest.utils.table.internal;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.omnaest.utils.table.Table;

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
                          .asCsv();
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

}
