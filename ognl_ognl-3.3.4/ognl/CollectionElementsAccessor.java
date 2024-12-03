/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Collection;
import java.util.Enumeration;
import ognl.ElementsAccessor;
import ognl.IteratorEnumeration;

public class CollectionElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration getElements(Object target) {
        return new IteratorEnumeration(((Collection)target).iterator());
    }
}

