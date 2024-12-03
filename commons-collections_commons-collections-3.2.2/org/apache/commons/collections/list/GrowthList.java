/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.list.AbstractSerializableListDecorator;

public class GrowthList
extends AbstractSerializableListDecorator {
    private static final long serialVersionUID = -3620001881672L;

    public static List decorate(List list) {
        return new GrowthList(list);
    }

    public GrowthList() {
        super(new ArrayList());
    }

    public GrowthList(int initialSize) {
        super(new ArrayList(initialSize));
    }

    protected GrowthList(List list) {
        super(list);
    }

    public void add(int index, Object element) {
        int size = this.getList().size();
        if (index > size) {
            this.getList().addAll(Collections.nCopies(index - size, null));
        }
        this.getList().add(index, element);
    }

    public boolean addAll(int index, Collection coll) {
        int size = this.getList().size();
        boolean result = false;
        if (index > size) {
            this.getList().addAll(Collections.nCopies(index - size, null));
            result = true;
        }
        return this.getList().addAll(index, coll) | result;
    }

    public Object set(int index, Object element) {
        int size = this.getList().size();
        if (index >= size) {
            this.getList().addAll(Collections.nCopies(index - size + 1, null));
        }
        return this.getList().set(index, element);
    }
}

