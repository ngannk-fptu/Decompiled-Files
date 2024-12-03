/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DTMStringPool {
    Vector m_intToString = new Vector();
    Map m_stringToInt = new HashMap();
    public static final int NULL = -1;

    public DTMStringPool(int chainSize) {
        this.removeAllElements();
        this.stringToIndex("");
    }

    public DTMStringPool() {
        this(512);
    }

    public void removeAllElements() {
        this.m_intToString.removeAllElements();
        this.m_stringToInt.clear();
    }

    public String indexToString(int i) throws ArrayIndexOutOfBoundsException {
        if (i == -1) {
            return null;
        }
        return (String)this.m_intToString.elementAt(i);
    }

    public int stringToIndex(String s) {
        if (s == null) {
            return -1;
        }
        Integer index = (Integer)this.m_stringToInt.get(s);
        if (index != null) {
            return index;
        }
        int newIndex = this.m_intToString.size();
        this.m_intToString.addElement(s);
        this.m_stringToInt.put(s, new Integer(newIndex));
        return newIndex;
    }

    public static void main(String[] args) {
        String[] word = new String[]{"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty", "Twenty-One", "Twenty-Two", "Twenty-Three", "Twenty-Four", "Twenty-Five", "Twenty-Six", "Twenty-Seven", "Twenty-Eight", "Twenty-Nine", "Thirty", "Thirty-One", "Thirty-Two", "Thirty-Three", "Thirty-Four", "Thirty-Five", "Thirty-Six", "Thirty-Seven", "Thirty-Eight", "Thirty-Nine"};
        DTMStringPool pool = new DTMStringPool();
        System.out.println("If no complaints are printed below, we passed initial test.");
        for (int pass = 0; pass <= 1; ++pass) {
            int j;
            int i;
            for (i = 0; i < word.length; ++i) {
                j = pool.stringToIndex(word[i]);
                if (j == i) continue;
                System.out.println("\tMismatch populating pool: assigned " + j + " for create " + i);
            }
            for (i = 0; i < word.length; ++i) {
                j = pool.stringToIndex(word[i]);
                if (j == i) continue;
                System.out.println("\tMismatch in stringToIndex: returned " + j + " for lookup " + i);
            }
            for (i = 0; i < word.length; ++i) {
                String w = pool.indexToString(i);
                if (word[i].equals(w)) continue;
                System.out.println("\tMismatch in indexToString: returned" + w + " for lookup " + i);
            }
            pool.removeAllElements();
            System.out.println("\nPass " + pass + " complete\n");
        }
    }
}

