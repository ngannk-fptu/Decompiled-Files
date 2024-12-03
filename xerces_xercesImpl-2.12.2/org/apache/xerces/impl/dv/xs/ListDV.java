/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.util.AbstractList;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.xs.datatypes.ObjectList;

public class ListDV
extends TypeValidator {
    @Override
    public short getAllowedFacets() {
        return 2079;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        return string;
    }

    @Override
    public int getDataLength(Object object) {
        return ((ListData)object).getLength();
    }

    static final class ListData
    extends AbstractList
    implements ObjectList {
        final Object[] data;
        private String canonical;

        public ListData(Object[] objectArray) {
            this.data = objectArray;
        }

        @Override
        public synchronized String toString() {
            if (this.canonical == null) {
                int n = this.data.length;
                StringBuffer stringBuffer = new StringBuffer();
                if (n > 0) {
                    stringBuffer.append(this.data[0].toString());
                }
                for (int i = 1; i < n; ++i) {
                    stringBuffer.append(' ');
                    stringBuffer.append(this.data[i].toString());
                }
                this.canonical = stringBuffer.toString();
            }
            return this.canonical;
        }

        @Override
        public int getLength() {
            return this.data.length;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof ListData)) {
                return false;
            }
            int n = this.data.length;
            Object[] objectArray = ((ListData)object).data;
            if (n != objectArray.length) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (this.data[i].equals(objectArray[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int n = 0;
            for (int i = 0; i < this.data.length; ++i) {
                n ^= this.data[i].hashCode();
            }
            return n;
        }

        @Override
        public boolean contains(Object object) {
            for (int i = 0; i < this.data.length; ++i) {
                if (object != this.data[i]) continue;
                return true;
            }
            return false;
        }

        @Override
        public Object item(int n) {
            if (n < 0 || n >= this.data.length) {
                return null;
            }
            return this.data[n];
        }

        public Object get(int n) {
            if (n >= 0 && n < this.data.length) {
                return this.data[n];
            }
            throw new IndexOutOfBoundsException("Index: " + n);
        }

        @Override
        public int size() {
            return this.getLength();
        }
    }
}

