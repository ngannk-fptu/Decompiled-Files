/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.ref.ExtendedType;

public class ExpandedNameTable {
    private ExtendedType[] m_extendedTypes;
    private static int m_initialSize = 128;
    private int m_nextType;
    public static final int ELEMENT = 1;
    public static final int ATTRIBUTE = 2;
    public static final int TEXT = 3;
    public static final int CDATA_SECTION = 4;
    public static final int ENTITY_REFERENCE = 5;
    public static final int ENTITY = 6;
    public static final int PROCESSING_INSTRUCTION = 7;
    public static final int COMMENT = 8;
    public static final int DOCUMENT = 9;
    public static final int DOCUMENT_TYPE = 10;
    public static final int DOCUMENT_FRAGMENT = 11;
    public static final int NOTATION = 12;
    public static final int NAMESPACE = 13;
    ExtendedType hashET = new ExtendedType(-1, "", "");
    private static ExtendedType[] m_defaultExtendedTypes;
    private static float m_loadFactor;
    private static int m_initialCapacity;
    private int m_capacity = m_initialCapacity;
    private int m_threshold = (int)((float)this.m_capacity * m_loadFactor);
    private HashEntry[] m_table = new HashEntry[this.m_capacity];

    public ExpandedNameTable() {
        this.initExtendedTypes();
    }

    private void initExtendedTypes() {
        this.m_extendedTypes = new ExtendedType[m_initialSize];
        for (int i = 0; i < 14; ++i) {
            this.m_extendedTypes[i] = m_defaultExtendedTypes[i];
            this.m_table[i] = new HashEntry(m_defaultExtendedTypes[i], i, i, null);
        }
        this.m_nextType = 14;
    }

    public int getExpandedTypeID(String namespace, String localName, int type) {
        return this.getExpandedTypeID(namespace, localName, type, false);
    }

    public int getExpandedTypeID(String namespace, String localName, int type, boolean searchOnly) {
        HashEntry entry;
        if (null == namespace) {
            namespace = "";
        }
        if (null == localName) {
            localName = "";
        }
        int hash = type + namespace.hashCode() + localName.hashCode();
        this.hashET.redefine(type, namespace, localName, hash);
        int index = hash % this.m_capacity;
        if (index < 0) {
            index = -index;
        }
        HashEntry e = this.m_table[index];
        while (e != null) {
            if (e.hash == hash && e.key.equals(this.hashET)) {
                return e.value;
            }
            e = e.next;
        }
        if (searchOnly) {
            return -1;
        }
        if (this.m_nextType > this.m_threshold) {
            this.rehash();
            index = hash % this.m_capacity;
            if (index < 0) {
                index = -index;
            }
        }
        ExtendedType newET = new ExtendedType(type, namespace, localName, hash);
        if (this.m_extendedTypes.length == this.m_nextType) {
            ExtendedType[] newArray = new ExtendedType[this.m_extendedTypes.length * 2];
            System.arraycopy(this.m_extendedTypes, 0, newArray, 0, this.m_extendedTypes.length);
            this.m_extendedTypes = newArray;
        }
        this.m_extendedTypes[this.m_nextType] = newET;
        this.m_table[index] = entry = new HashEntry(newET, this.m_nextType, hash, this.m_table[index]);
        return this.m_nextType++;
    }

    private void rehash() {
        int newCapacity;
        int oldCapacity = this.m_capacity;
        HashEntry[] oldTable = this.m_table;
        this.m_capacity = newCapacity = 2 * oldCapacity + 1;
        this.m_threshold = (int)((float)newCapacity * m_loadFactor);
        this.m_table = new HashEntry[newCapacity];
        for (int i = oldCapacity - 1; i >= 0; --i) {
            HashEntry old = oldTable[i];
            while (old != null) {
                HashEntry e = old;
                old = old.next;
                int newIndex = e.hash % newCapacity;
                if (newIndex < 0) {
                    newIndex = -newIndex;
                }
                e.next = this.m_table[newIndex];
                this.m_table[newIndex] = e;
            }
        }
    }

    public int getExpandedTypeID(int type) {
        return type;
    }

    public String getLocalName(int ExpandedNameID) {
        return this.m_extendedTypes[ExpandedNameID].getLocalName();
    }

    public final int getLocalNameID(int ExpandedNameID) {
        if (this.m_extendedTypes[ExpandedNameID].getLocalName().length() == 0) {
            return 0;
        }
        return ExpandedNameID;
    }

    public String getNamespace(int ExpandedNameID) {
        String namespace = this.m_extendedTypes[ExpandedNameID].getNamespace();
        return namespace.length() == 0 ? null : namespace;
    }

    public final int getNamespaceID(int ExpandedNameID) {
        if (this.m_extendedTypes[ExpandedNameID].getNamespace().length() == 0) {
            return 0;
        }
        return ExpandedNameID;
    }

    public final short getType(int ExpandedNameID) {
        return (short)this.m_extendedTypes[ExpandedNameID].getNodeType();
    }

    public int getSize() {
        return this.m_nextType;
    }

    public ExtendedType[] getExtendedTypes() {
        return this.m_extendedTypes;
    }

    static {
        m_loadFactor = 0.75f;
        m_initialCapacity = 203;
        m_defaultExtendedTypes = new ExtendedType[14];
        for (int i = 0; i < 14; ++i) {
            ExpandedNameTable.m_defaultExtendedTypes[i] = new ExtendedType(i, "", "");
        }
    }

    private static final class HashEntry {
        ExtendedType key;
        int value;
        int hash;
        HashEntry next;

        protected HashEntry(ExtendedType key, int value, int hash, HashEntry next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }
    }
}

