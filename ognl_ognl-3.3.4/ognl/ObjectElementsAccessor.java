/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import ognl.ElementsAccessor;

public class ObjectElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration getElements(Object target) {
        final Object object = target;
        return new Enumeration(){
            private boolean seen = false;

            @Override
            public boolean hasMoreElements() {
                return !this.seen;
            }

            public Object nextElement() {
                Object result = null;
                if (!this.seen) {
                    result = object;
                    this.seen = true;
                }
                return result;
            }
        };
    }
}

