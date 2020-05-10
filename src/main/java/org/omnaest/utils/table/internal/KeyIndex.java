package org.omnaest.utils.table.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyIndex
{
    private List<String>         keys     = new ArrayList<>();
    private Map<String, Integer> keyIndex = new HashMap<>();

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
    }

    public int getIndex(String key)
    {
        if (!this.keyIndex.containsKey(key))
        {
            throw new IllegalArgumentException();
        }
        return this.keyIndex.get(key);
    }

    public String getKey(int index)
    {
        if (index < 0 || index >= this.keys.size())
        {
            throw new IndexOutOfBoundsException();
        }
        return this.keys.get(index);
    }

    public List<String> getKeys()
    {
        return Collections.unmodifiableList(this.keys);
    }

    public int size()
    {
        return this.keys.size();
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
