/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.ref.DTMStringPool;

public class DTMSafeStringPool
extends DTMStringPool {
    @Override
    public synchronized void removeAllElements() {
        super.removeAllElements();
    }

    @Override
    public synchronized String indexToString(int i) throws ArrayIndexOutOfBoundsException {
        return super.indexToString(i);
    }

    @Override
    public synchronized int stringToIndex(String s) {
        return super.stringToIndex(s);
    }

    public static void main(String[] args) {
        String[] word = new String[]{"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty", "Twenty-One", "Twenty-Two", "Twenty-Three", "Twenty-Four", "Twenty-Five", "Twenty-Six", "Twenty-Seven", "Twenty-Eight", "Twenty-Nine", "Thirty", "Thirty-One", "Thirty-Two", "Thirty-Three", "Thirty-Four", "Thirty-Five", "Thirty-Six", "Thirty-Seven", "Thirty-Eight", "Thirty-Nine"};
        DTMSafeStringPool pool = new DTMSafeStringPool();
        System.out.println("If no complaints are printed below, we passed initial test.");
        for (int pass = 0; pass <= 1; ++pass) {
            int j;
            int i;
            for (i = 0; i < word.length; ++i) {
                j = ((DTMStringPool)pool).stringToIndex(word[i]);
                if (j == i) continue;
                System.out.println("\tMismatch populating pool: assigned " + j + " for create " + i);
            }
            for (i = 0; i < word.length; ++i) {
                j = ((DTMStringPool)pool).stringToIndex(word[i]);
                if (j == i) continue;
                System.out.println("\tMismatch in stringToIndex: returned " + j + " for lookup " + i);
            }
            for (i = 0; i < word.length; ++i) {
                String w = ((DTMStringPool)pool).indexToString(i);
                if (word[i].equals(w)) continue;
                System.out.println("\tMismatch in indexToString: returned" + w + " for lookup " + i);
            }
            ((DTMStringPool)pool).removeAllElements();
            System.out.println("\nPass " + pass + " complete\n");
        }
    }
}

