/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import java.util.Iterator;
import ognl.ElementsAccessor;
import ognl.IteratorEnumeration;

public class IteratorElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration getElements(Object target) {
        return new IteratorEnumeration((Iterator)target);
    }
}

