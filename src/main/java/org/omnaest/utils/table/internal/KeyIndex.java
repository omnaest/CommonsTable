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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KeyIndex
{
    private List<String>         keys           = new ArrayList<>();
    private Map<String, Integer> keyIndex       = new HashMap<>();
    private int                  maxColumnIndex = -1;

    public KeyIndex addKey(String key)
    {
        this.keys.add(key);
        this.rebuildIndex();
        return this;
    }

    private void rebuildIndex()
    {
        this.keyIndex.clear();
        int index = 0;
        for (String key : this.keys)
        {
            this.keyIndex.put(key, index);
            index++;
        }
        this.maxColumnIndex = Math.max(this.maxColumnIndex, this.keys.size() - 1);
    }

    public Optional<Integer> getIndexAsOptional(String key)
    {
        if (!this.keyIndex.containsKey(key))
        {
            return Optional.empty();
        }
        return Optional.of(this.keyIndex.get(key));
    }

    public int getIndex(String key)
    {
        return this.getIndexAsOptional(key)
                   .orElseThrow(() -> new IllegalArgumentException());
    }

    public String getKey(int index)
    {
        if (index < 0 || index >= this.keys.size())
        {
            throw new IndexOutOfBoundsException();
        }
        return this.keys.get(index);
    }

    public Optional<String> getEffectiveKey(int index)
    {
        if (index < 0 || index >= this.keys.size())
        {
            return Optional.empty();
        }
        return Optional.ofNullable(this.keys.get(index));
    }

    public List<String> getKeys()
    {
        return Collections.unmodifiableList(this.keys);
    }

    public int size()
    {
        return this.keys.size();
    }

    public int getEffectiveSize()
    {
        return this.maxColumnIndex + 1;
    }

    public void notifyOfColumnIndexWrite(int columnIndex)
    {
        this.maxColumnIndex = Math.max(this.maxColumnIndex, columnIndex);
    }

    @Override
    public String toString()
    {
        return "KeyIndex [keys=" + this.keys + ", keyIndex=" + this.keyIndex + "]";
    }

    public boolean hasKey(String key)
    {
        return this.keyIndex.containsKey(key);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.keyIndex == null) ? 0 : this.keyIndex.hashCode());
        result = prime * result + ((this.keys == null) ? 0 : this.keys.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        KeyIndex other = (KeyIndex) obj;
        if (this.keyIndex == null)
        {
            if (other.keyIndex != null)
            {
                return false;
            }
        }
        else if (!this.keyIndex.equals(other.keyIndex))
        {
            return false;
        }
        if (this.keys == null)
        {
            if (other.keys != null)
            {
                return false;
            }
        }
        else if (!this.keys.equals(other.keys))
        {
            return false;
        }
        return true;
    }

}
