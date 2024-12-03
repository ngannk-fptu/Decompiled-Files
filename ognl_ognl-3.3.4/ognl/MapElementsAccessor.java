/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import java.util.Map;
import ognl.ElementsAccessor;
import ognl.IteratorEnumeration;

public class MapElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration getElements(Object target) {
        return new IteratorEnumeration(((Map)target).values().iterator());
    }
}

