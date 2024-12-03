/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CFFOperator {
    private Key operatorKey = null;
    private String operatorName = null;
    private static Map<Key, CFFOperator> keyMap = new LinkedHashMap<Key, CFFOperator>(52);
    private static Map<String, CFFOperator> nameMap = new LinkedHashMap<String, CFFOperator>(52);

    private CFFOperator(Key key, String name) {
        this.setKey(key);
        this.setName(name);
    }

    public Key getKey() {
        return this.operatorKey;
    }

    private void setKey(Key key) {
        this.operatorKey = key;
    }

    public String getName() {
        return this.operatorName;
    }

    private void setName(String name) {
        this.operatorName = name;
    }

    public String toString() {
        return this.getName();
    }

    public int hashCode() {
        return this.getKey().hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof CFFOperator) {
            CFFOperator that = (CFFOperator)object;
            return this.getKey().equals(that.getKey());
        }
        return false;
    }

    private static void register(Key key, String name) {
        CFFOperator operator = new CFFOperator(key, name);
        keyMap.put(key, operator);
        nameMap.put(name, operator);
    }

    public static CFFOperator getOperator(Key key) {
        return keyMap.get(key);
    }

    public static CFFOperator getOperator(String name) {
        return nameMap.get(name);
    }

    static {
        CFFOperator.register(new Key(0), "version");
        CFFOperator.register(new Key(1), "Notice");
        CFFOperator.register(new Key(12, 0), "Copyright");
        CFFOperator.register(new Key(2), "FullName");
        CFFOperator.register(new Key(3), "FamilyName");
        CFFOperator.register(new Key(4), "Weight");
        CFFOperator.register(new Key(12, 1), "isFixedPitch");
        CFFOperator.register(new Key(12, 2), "ItalicAngle");
        CFFOperator.register(new Key(12, 3), "UnderlinePosition");
        CFFOperator.register(new Key(12, 4), "UnderlineThickness");
        CFFOperator.register(new Key(12, 5), "PaintType");
        CFFOperator.register(new Key(12, 6), "CharstringType");
        CFFOperator.register(new Key(12, 7), "FontMatrix");
        CFFOperator.register(new Key(13), "UniqueID");
        CFFOperator.register(new Key(5), "FontBBox");
        CFFOperator.register(new Key(12, 8), "StrokeWidth");
        CFFOperator.register(new Key(14), "XUID");
        CFFOperator.register(new Key(15), "charset");
        CFFOperator.register(new Key(16), "Encoding");
        CFFOperator.register(new Key(17), "CharStrings");
        CFFOperator.register(new Key(18), "Private");
        CFFOperator.register(new Key(12, 20), "SyntheticBase");
        CFFOperator.register(new Key(12, 21), "PostScript");
        CFFOperator.register(new Key(12, 22), "BaseFontName");
        CFFOperator.register(new Key(12, 23), "BaseFontBlend");
        CFFOperator.register(new Key(12, 30), "ROS");
        CFFOperator.register(new Key(12, 31), "CIDFontVersion");
        CFFOperator.register(new Key(12, 32), "CIDFontRevision");
        CFFOperator.register(new Key(12, 33), "CIDFontType");
        CFFOperator.register(new Key(12, 34), "CIDCount");
        CFFOperator.register(new Key(12, 35), "UIDBase");
        CFFOperator.register(new Key(12, 36), "FDArray");
        CFFOperator.register(new Key(12, 37), "FDSelect");
        CFFOperator.register(new Key(12, 38), "FontName");
        CFFOperator.register(new Key(6), "BlueValues");
        CFFOperator.register(new Key(7), "OtherBlues");
        CFFOperator.register(new Key(8), "FamilyBlues");
        CFFOperator.register(new Key(9), "FamilyOtherBlues");
        CFFOperator.register(new Key(12, 9), "BlueScale");
        CFFOperator.register(new Key(12, 10), "BlueShift");
        CFFOperator.register(new Key(12, 11), "BlueFuzz");
        CFFOperator.register(new Key(10), "StdHW");
        CFFOperator.register(new Key(11), "StdVW");
        CFFOperator.register(new Key(12, 12), "StemSnapH");
        CFFOperator.register(new Key(12, 13), "StemSnapV");
        CFFOperator.register(new Key(12, 14), "ForceBold");
        CFFOperator.register(new Key(12, 15), "LanguageGroup");
        CFFOperator.register(new Key(12, 16), "ExpansionFactor");
        CFFOperator.register(new Key(12, 17), "initialRandomSeed");
        CFFOperator.register(new Key(19), "Subrs");
        CFFOperator.register(new Key(20), "defaultWidthX");
        CFFOperator.register(new Key(21), "nominalWidthX");
    }

    public static class Key {
        private int[] value = null;

        public Key(int b0) {
            this(new int[]{b0});
        }

        public Key(int b0, int b1) {
            this(new int[]{b0, b1});
        }

        private Key(int[] value) {
            this.setValue(value);
        }

        public int[] getValue() {
            return this.value;
        }

        private void setValue(int[] value) {
            this.value = value;
        }

        public String toString() {
            return Arrays.toString(this.getValue());
        }

        public int hashCode() {
            return Arrays.hashCode(this.getValue());
        }

        public boolean equals(Object object) {
            if (object instanceof Key) {
                Key that = (Key)object;
                return Arrays.equals(this.getValue(), that.getValue());
            }
            return false;
        }
    }
}

