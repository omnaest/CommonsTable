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

import org.junit.Test;

public class TableDataTest
{
    private TableData tableBody = new TableData();

    @Test
    public void testEmpty() throws Exception
    {
        assertEquals(0, this.tableBody.getColumnSize());
        assertEquals(0, this.tableBody.getRowSize());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEmpty2() throws Exception
    {
        this.tableBody.get(0, 0);
    }

    @Test
    public void testWithData() throws Exception
    {
        for (int ii = 0; ii < 1000; ii++)
        {
            for (int jj = 0; jj < 20; jj++)
            {
                String value = ii + ":" + jj;
                this.tableBody.set(ii, jj, value);
                assertEquals(value, this.tableBody.get(ii, jj));
            }
        }
    }

}
