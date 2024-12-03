/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.comparators;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FixedOrderComparator
implements Comparator {
    public static final int UNKNOWN_BEFORE = 0;
    public static final int UNKNOWN_AFTER = 1;
    public static final int UNKNOWN_THROW_EXCEPTION = 2;
    private final Map map = new HashMap();
    private int counter = 0;
    private boolean isLocked = false;
    private int unknownObjectBehavior = 2;

    public FixedOrderComparator() {
    }

    public FixedOrderComparator(Object[] items) {
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        for (int i = 0; i < items.length; ++i) {
            this.add(items[i]);
        }
    }

    public FixedOrderComparator(List items) {
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        Iterator it = items.iterator();
        while (it.hasNext()) {
            this.add(it.next());
        }
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    protected void checkLocked() {
        if (this.isLocked()) {
            throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
        }
    }

    public int getUnknownObjectBehavior() {
        return this.unknownObjectBehavior;
    }

    public void setUnknownObjectBehavior(int unknownObjectBehavior) {
        this.checkLocked();
        if (unknownObjectBehavior != 1 && unknownObjectBehavior != 0 && unknownObjectBehavior != 2) {
            throw new IllegalArgumentException("Unrecognised value for unknown behaviour flag");
        }
        this.unknownObjectBehavior = unknownObjectBehavior;
    }

    public boolean add(Object obj) {
        this.checkLocked();
        Integer position = this.map.put(obj, new Integer(this.counter++));
        return position == null;
    }

    public boolean addAsEqual(Object existingObj, Object newObj) {
        this.checkLocked();
        Integer position = (Integer)this.map.get(existingObj);
        if (position == null) {
            throw new IllegalArgumentException(existingObj + " not known to " + this);
        }
        Integer result = this.map.put(newObj, position);
        return result == null;
    }

    public int compare(Object obj1, Object obj2) {
        this.isLocked = true;
        Integer position1 = (Integer)this.map.get(obj1);
        Integer position2 = (Integer)this.map.get(obj2);
        if (position1 == null || position2 == null) {
            switch (this.unknownObjectBehavior) {
                case 0: {
                    if (position1 == null) {
                        return position2 == null ? 0 : -1;
                    }
                    return 1;
                }
                case 1: {
                    if (position1 == null) {
                        return position2 == null ? 0 : 1;
                    }
                    return -1;
                }
                case 2: {
                    Object unknownObj = position1 == null ? obj1 : obj2;
                    throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
                }
            }
            throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + this.unknownObjectBehavior);
        }
        return position1.compareTo(position2);
    }
}

