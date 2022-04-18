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

public interface Cell
{
    public String getValue();

    public Cell setValue(String value);

    /**
     * Returns true, if the value of the cell is null or an empty string
     * 
     * @return
     */
    public boolean isEmpty();

    /**
     * Returns true, if the value of the cell is null or an empty string or a string with blank characters
     * 
     * @return
     */
    public boolean isBlank();

    public Row getRow();

    public Column getColumn();

}
