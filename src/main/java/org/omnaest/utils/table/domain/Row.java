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
package org.omnaest.utils.table.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface Row extends Iterable<String>
{
    public List<String> getValues();

    public String getValue(int columnIndex);

    public String getValue(String columnTitle);

    public String getFirstValue();

    public String getSecondValue();

    public Map<String, String> asMap();

    public Row addValue(String value);

    public Row addValues(String... values);

    public Row addValues(List<String> values);

    public Cell getCell(int index);

    public Cell getCell(String columnTitle);

    public List<Cell> getCells();

    public Cell getFirstCell();

    public List<String> asList();

    public Stream<String> stream();

    public <T> T asBean(Class<T> type);

    /**
     * Returns the number of cells
     * 
     * @return
     */
    public int size();

}
