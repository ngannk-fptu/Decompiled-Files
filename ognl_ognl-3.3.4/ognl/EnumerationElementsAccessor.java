/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import ognl.ElementsAccessor;

public class EnumerationElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration getElements(Object target) {
        return (Enumeration)target;
    }
}

