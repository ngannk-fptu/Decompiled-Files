/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import ognl.ElementsAccessor;
import ognl.NumericTypes;
import ognl.OgnlOps;

public class NumberElementsAccessor
implements ElementsAccessor,
NumericTypes {
    @Override
    public Enumeration getElements(final Object target) {
        return new Enumeration(){
            private int type;
            private long next;
            private long finish;
            {
                this.type = OgnlOps.getNumericType(target);
                this.next = 0L;
                this.finish = OgnlOps.longValue(target);
            }

            @Override
            public boolean hasMoreElements() {
                return this.next < this.finish;
            }

            public Object nextElement() {
                if (this.next >= this.finish) {
                    throw new NoSuchElementException();
                }
                return OgnlOps.newInteger(this.type, this.next++);
            }
        };
    }
}

