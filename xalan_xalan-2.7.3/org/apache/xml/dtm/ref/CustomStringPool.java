/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import java.util.Hashtable;
import org.apache.xml.dtm.ref.DTMStringPool;

public class CustomStringPool
extends DTMStringPool {
    final Hashtable m_stringToInt = new Hashtable();
    public static final int NULL = -1;

    @Override
    public void removeAllElements() {
        this.m_intToString.removeAllElements();
        if (this.m_stringToInt != null) {
            this.m_stringToInt.clear();
        }
    }

    @Override
    public String indexToString(int i) throws ArrayIndexOutOfBoundsException {
        return (String)this.m_intToString.elementAt(i);
    }

    @Override
    public int stringToIndex(String s) {
        if (s == null) {
            return -1;
        }
        Integer iobj = (Integer)this.m_stringToInt.get(s);
        if (iobj == null) {
            this.m_intToString.addElement(s);
            iobj = new Integer(this.m_intToString.size());
            this.m_stringToInt.put(s, iobj);
        }
        return iobj;
    }
}

