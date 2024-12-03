/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.lang.reflect.Array;
import java.util.List;
import org.apache.velocity.tools.config.DefaultKey;

@Deprecated
@DefaultKey(value="lists")
public class ListTool {
    public Object get(Object list, int index) {
        if (this.isArray(list)) {
            return this.getFromArray(list, index);
        }
        if (!this.isList(list)) {
            return null;
        }
        try {
            return ((List)list).get(index);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private Object getFromArray(Object array, int index) {
        try {
            return Array.get(array, index);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Object set(Object list, int index, Object value) {
        if (this.isArray(list)) {
            return this.setToArray(list, index, value);
        }
        if (!this.isList(list)) {
            return null;
        }
        try {
            ((List)list).set(index, value);
            return "";
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private Object setToArray(Object array, int index, Object value) {
        try {
            Array.set(array, index, value);
            return "";
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Integer size(Object list) {
        if (this.isArray(list)) {
            return Array.getLength(list);
        }
        if (!this.isList(list)) {
            return null;
        }
        return ((List)list).size();
    }

    public boolean isArray(Object object) {
        if (object == null) {
            return false;
        }
        return object.getClass().isArray();
    }

    public boolean isList(Object object) {
        return object instanceof List;
    }

    public Boolean isEmpty(Object list) {
        Integer size = this.size(list);
        if (size == null) {
            return null;
        }
        return size == 0;
    }

    public Boolean contains(Object list, Object element) {
        if (this.isArray(list)) {
            return this.arrayContains(list, element);
        }
        if (!this.isList(list)) {
            return null;
        }
        return ((List)list).contains(element);
    }

    private Boolean arrayContains(Object array, Object element) {
        int size = this.size(array);
        for (int index = 0; index < size; ++index) {
            if (!this.equals(element, this.getFromArray(array, index))) continue;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private boolean equals(Object what, Object with) {
        if (what == null) {
            return with == null;
        }
        return what.equals(with);
    }
}

