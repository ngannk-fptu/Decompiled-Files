/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;

public class XSNamedMapImpl
extends AbstractMap
implements XSNamedMap {
    public static final XSNamedMapImpl EMPTY_MAP = new XSNamedMapImpl(new XSObject[0], 0);
    final String[] fNamespaces;
    final int fNSNum;
    final SymbolHash[] fMaps;
    XSObject[] fArray = null;
    int fLength = -1;
    private Set fEntrySet = null;

    public XSNamedMapImpl(String string, SymbolHash symbolHash) {
        this.fNamespaces = new String[]{string};
        this.fMaps = new SymbolHash[]{symbolHash};
        this.fNSNum = 1;
    }

    public XSNamedMapImpl(String[] stringArray, SymbolHash[] symbolHashArray, int n) {
        this.fNamespaces = stringArray;
        this.fMaps = symbolHashArray;
        this.fNSNum = n;
    }

    public XSNamedMapImpl(XSObject[] xSObjectArray, int n) {
        if (n == 0) {
            this.fNamespaces = null;
            this.fMaps = null;
            this.fNSNum = 0;
            this.fArray = xSObjectArray;
            this.fLength = 0;
            return;
        }
        this.fNamespaces = new String[]{xSObjectArray[0].getNamespace()};
        this.fMaps = null;
        this.fNSNum = 1;
        this.fArray = xSObjectArray;
        this.fLength = n;
    }

    @Override
    public synchronized int getLength() {
        if (this.fLength == -1) {
            this.fLength = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                this.fLength += this.fMaps[i].getLength();
            }
        }
        return this.fLength;
    }

    @Override
    public XSObject itemByName(String string, String string2) {
        for (int i = 0; i < this.fNSNum; ++i) {
            if (!XSNamedMapImpl.isEqual(string, this.fNamespaces[i])) continue;
            if (this.fMaps != null) {
                return (XSObject)this.fMaps[i].get(string2);
            }
            for (int j = 0; j < this.fLength; ++j) {
                XSObject xSObject = this.fArray[j];
                if (!xSObject.getName().equals(string2)) continue;
                return xSObject;
            }
            return null;
        }
        return null;
    }

    @Override
    public synchronized XSObject item(int n) {
        if (this.fArray == null) {
            this.getLength();
            this.fArray = new XSObject[this.fLength];
            int n2 = 0;
            for (int i = 0; i < this.fNSNum; ++i) {
                n2 += this.fMaps[i].getValues(this.fArray, n2);
            }
        }
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
    }

    static boolean isEqual(String string, String string2) {
        return string != null ? string.equals(string2) : string2 == null;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.get(object) != null;
    }

    public Object get(Object object) {
        if (object instanceof QName) {
            QName qName = (QName)object;
            String string = qName.getNamespaceURI();
            if ("".equals(string)) {
                string = null;
            }
            String string2 = qName.getLocalPart();
            return this.itemByName(string, string2);
        }
        return null;
    }

    @Override
    public int size() {
        return this.getLength();
    }

    public synchronized Set entrySet() {
        if (this.fEntrySet == null) {
            final int n = this.getLength();
            final XSNamedMapEntry[] xSNamedMapEntryArray = new XSNamedMapEntry[n];
            for (int i = 0; i < n; ++i) {
                XSObject xSObject = this.item(i);
                xSNamedMapEntryArray[i] = new XSNamedMapEntry(new QName(xSObject.getNamespace(), xSObject.getName()), xSObject);
            }
            this.fEntrySet = new AbstractSet(){

                @Override
                public Iterator iterator() {
                    return new Iterator(){
                        private int index = 0;

                        @Override
                        public boolean hasNext() {
                            return this.index < n;
                        }

                        public Object next() {
                            if (this.index < n) {
                                return xSNamedMapEntryArray[this.index++];
                            }
                            throw new NoSuchElementException();
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                @Override
                public int size() {
                    return n;
                }
            };
        }
        return this.fEntrySet;
    }

    private static final class XSNamedMapEntry
    implements Map.Entry {
        private final QName key;
        private final XSObject value;

        public XSNamedMapEntry(QName qName, XSObject xSObject) {
            this.key = qName;
            this.value = xSObject;
        }

        public Object getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public Object setValue(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)object;
                Object k = entry.getKey();
                Object v = entry.getValue();
                return (this.key == null ? k == null : this.key.equals(k)) && (this.value == null ? v == null : this.value.equals(v));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(String.valueOf(this.key));
            stringBuffer.append('=');
            stringBuffer.append(String.valueOf(this.value));
            return stringBuffer.toString();
        }
    }
}

