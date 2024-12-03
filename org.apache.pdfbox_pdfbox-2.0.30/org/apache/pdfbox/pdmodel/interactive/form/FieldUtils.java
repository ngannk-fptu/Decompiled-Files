/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;

public final class FieldUtils {
    private FieldUtils() {
    }

    static List<KeyValue> toKeyValueList(List<String> key, List<String> value) {
        ArrayList<KeyValue> list = new ArrayList<KeyValue>(key.size());
        for (int i = 0; i < key.size(); ++i) {
            list.add(new KeyValue(key.get(i), value.get(i)));
        }
        return list;
    }

    static void sortByValue(List<KeyValue> pairs) {
        Collections.sort(pairs, new KeyValueValueComparator());
    }

    static void sortByKey(List<KeyValue> pairs) {
        Collections.sort(pairs, new KeyValueKeyComparator());
    }

    static List<String> getPairableItems(COSBase items, int pairIdx) {
        if (pairIdx < 0 || pairIdx > 1) {
            throw new IllegalArgumentException("Only 0 and 1 are allowed as an index into two-element arrays");
        }
        if (items instanceof COSString) {
            ArrayList<String> array = new ArrayList<String>(1);
            array.add(((COSString)items).getString());
            return array;
        }
        if (items instanceof COSArray) {
            ArrayList<String> entryList = new ArrayList<String>();
            for (COSBase entry : (COSArray)items) {
                COSArray cosArray;
                if (entry instanceof COSString) {
                    entryList.add(((COSString)entry).getString());
                    continue;
                }
                if (!(entry instanceof COSArray) || (cosArray = (COSArray)entry).size() < pairIdx + 1 || !(cosArray.get(pairIdx) instanceof COSString)) continue;
                entryList.add(((COSString)cosArray.get(pairIdx)).getString());
            }
            return entryList;
        }
        return Collections.emptyList();
    }

    static class KeyValueValueComparator
    implements Serializable,
    Comparator<KeyValue> {
        private static final long serialVersionUID = -3984095679894798265L;

        KeyValueValueComparator() {
        }

        @Override
        public int compare(KeyValue o1, KeyValue o2) {
            return o1.value.compareTo(o2.value);
        }
    }

    static class KeyValueKeyComparator
    implements Serializable,
    Comparator<KeyValue> {
        private static final long serialVersionUID = 6715364290007167694L;

        KeyValueKeyComparator() {
        }

        @Override
        public int compare(KeyValue o1, KeyValue o2) {
            return o1.key.compareTo(o2.key);
        }
    }

    static class KeyValue {
        private final String key;
        private final String value;

        KeyValue(String theKey, String theValue) {
            this.key = theKey;
            this.value = theValue;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return "(" + this.key + ", " + this.value + ")";
        }
    }
}

