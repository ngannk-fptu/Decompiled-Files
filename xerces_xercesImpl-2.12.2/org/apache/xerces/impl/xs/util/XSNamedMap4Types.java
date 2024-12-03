/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import org.apache.xerces.impl.xs.util.XSNamedMapImpl;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSTypeDefinition;

public final class XSNamedMap4Types
extends XSNamedMapImpl {
    private final short fType;

    public XSNamedMap4Types(String string, SymbolHash symbolHash, short s) {
        super(string, symbolHash);
        this.fType = s;
    }

    public XSNamedMap4Types(String[] stringArray, SymbolHash[] symbolHashArray, int n, short s) {
        super(stringArray, symbolHashArray, n);
        this.fType = s;
    }

    @Override
    public synchronized int getLength() {
        if (this.fLength == -1) {
            int n;
            int n2 = 0;
            for (n = 0; n < this.fNSNum; ++n) {
                n2 += this.fMaps[n].getLength();
            }
            n = 0;
            Object[] objectArray = new XSObject[n2];
            for (int i = 0; i < this.fNSNum; ++i) {
                n += this.fMaps[i].getValues(objectArray, n);
            }
            this.fLength = 0;
            this.fArray = new XSObject[n2];
            for (int i = 0; i < n2; ++i) {
                XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)objectArray[i];
                if (xSTypeDefinition.getTypeCategory() != this.fType) continue;
                this.fArray[this.fLength++] = xSTypeDefinition;
            }
        }
        return this.fLength;
    }

    @Override
    public XSObject itemByName(String string, String string2) {
        for (int i = 0; i < this.fNSNum; ++i) {
            if (!XSNamedMap4Types.isEqual(string, this.fNamespaces[i])) continue;
            XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)this.fMaps[i].get(string2);
            if (xSTypeDefinition != null && xSTypeDefinition.getTypeCategory() == this.fType) {
                return xSTypeDefinition;
            }
            return null;
        }
        return null;
    }

    @Override
    public synchronized XSObject item(int n) {
        if (this.fArray == null) {
            this.getLength();
        }
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
    }
}

