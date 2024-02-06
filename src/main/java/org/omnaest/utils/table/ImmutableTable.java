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

import org.omnaest.utils.table.components.TableSerializer;
import org.omnaest.utils.table.components.TableTranslator;
import org.omnaest.utils.table.domain.Column;
import org.omnaest.utils.table.domain.Row;

public interface ImmutableTable extends Iterable<Row>
{
    /**
     * @see #as()
     * @see Table#deserialize()
     * @return
     */
    public TableSerializer serialize();

    /**
     * Returns all explicitly created {@link Column} titles.
     * 
     * @see #getEffectiveColumnTitles()
     * @return
     */
    public List<String> getColumnTitles();

    /**
     * Returns all {@link Column} titles, even the ones that were implicitly created.
     * 
     * @see #getColumnTitles()
     * @return
     */
    public List<String> getEffectiveColumnTitles();

    public String getValue(int rowIndex, int columnIndex);

    public String getValue(String rowTitle, String columnTitle);

    public int getRowSize();

    /**
     * @see #serialize()
     * @return
     */
    public TableTranslator as();
}
