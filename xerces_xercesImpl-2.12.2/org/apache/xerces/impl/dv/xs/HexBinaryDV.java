/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.util.ByteListImpl;
import org.apache.xerces.impl.dv.util.HexBin;
import org.apache.xerces.impl.dv.xs.TypeValidator;

public class HexBinaryDV
extends TypeValidator {
    @Override
    public short getAllowedFacets() {
        return 2079;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        byte[] byArray = HexBin.decode(string);
        if (byArray == null) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "hexBinary"});
        }
        return new XHex(byArray);
    }

    @Override
    public int getDataLength(Object object) {
        return ((XHex)object).getLength();
    }

    private static final class XHex
    extends ByteListImpl {
        public XHex(byte[] byArray) {
            super(byArray);
        }

        @Override
        public synchronized String toString() {
            if (this.canonical == null) {
                this.canonical = HexBin.encode(this.data);
            }
            return this.canonical;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof XHex)) {
                return false;
            }
            int n = this.data.length;
            byte[] byArray = ((XHex)object).data;
            if (n != byArray.length) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (this.data[i] == byArray[i]) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int n = 0;
            for (int i = 0; i < this.data.length; ++i) {
                n = n * 37 + (this.data[i] & 0xFF);
            }
            return n;
        }
    }
}

