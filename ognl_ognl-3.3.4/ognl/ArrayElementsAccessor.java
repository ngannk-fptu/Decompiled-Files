/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Array;
import java.util.Enumeration;
import ognl.ElementsAccessor;

public class ArrayElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration getElements(final Object target) {
        return new Enumeration(){
            private int count;
            private int index;
            {
                this.count = Array.getLength(target);
                this.index = 0;
            }

            @Override
            public boolean hasMoreElements() {
                return this.index < this.count;
            }

            public Object nextElement() {
                return Array.get(target, this.index++);
            }
        };
    }
}

