/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CharStringCommand {
    private Key commandKey = null;
    public static final Map<Key, String> TYPE1_VOCABULARY;
    public static final Map<Key, String> TYPE2_VOCABULARY;

    public CharStringCommand(int b0) {
        this.setKey(new Key(b0));
    }

    public CharStringCommand(int b0, int b1) {
        this.setKey(new Key(b0, b1));
    }

    public CharStringCommand(int[] values) {
        this.setKey(new Key(values));
    }

    public Key getKey() {
        return this.commandKey;
    }

    private void setKey(Key key) {
        this.commandKey = key;
    }

    public String toString() {
        String str = TYPE2_VOCABULARY.get(this.getKey());
        if (str == null) {
            str = TYPE1_VOCABULARY.get(this.getKey());
        }
        if (str == null) {
            return this.getKey().toString() + '|';
        }
        return str + '|';
    }

    public int hashCode() {
        return this.getKey().hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof CharStringCommand) {
            CharStringCommand that = (CharStringCommand)object;
            return this.getKey().equals(that.getKey());
        }
        return false;
    }

    static {
        LinkedHashMap<Key, String> map = new LinkedHashMap<Key, String>(26);
        map.put(new Key(1), "hstem");
        map.put(new Key(3), "vstem");
        map.put(new Key(4), "vmoveto");
        map.put(new Key(5), "rlineto");
        map.put(new Key(6), "hlineto");
        map.put(new Key(7), "vlineto");
        map.put(new Key(8), "rrcurveto");
        map.put(new Key(9), "closepath");
        map.put(new Key(10), "callsubr");
        map.put(new Key(11), "return");
        map.put(new Key(12), "escape");
        map.put(new Key(12, 0), "dotsection");
        map.put(new Key(12, 1), "vstem3");
        map.put(new Key(12, 2), "hstem3");
        map.put(new Key(12, 6), "seac");
        map.put(new Key(12, 7), "sbw");
        map.put(new Key(12, 12), "div");
        map.put(new Key(12, 16), "callothersubr");
        map.put(new Key(12, 17), "pop");
        map.put(new Key(12, 33), "setcurrentpoint");
        map.put(new Key(13), "hsbw");
        map.put(new Key(14), "endchar");
        map.put(new Key(21), "rmoveto");
        map.put(new Key(22), "hmoveto");
        map.put(new Key(30), "vhcurveto");
        map.put(new Key(31), "hvcurveto");
        TYPE1_VOCABULARY = Collections.unmodifiableMap(map);
        map = new LinkedHashMap(48);
        map.put(new Key(1), "hstem");
        map.put(new Key(3), "vstem");
        map.put(new Key(4), "vmoveto");
        map.put(new Key(5), "rlineto");
        map.put(new Key(6), "hlineto");
        map.put(new Key(7), "vlineto");
        map.put(new Key(8), "rrcurveto");
        map.put(new Key(10), "callsubr");
        map.put(new Key(11), "return");
        map.put(new Key(12), "escape");
        map.put(new Key(12, 3), "and");
        map.put(new Key(12, 4), "or");
        map.put(new Key(12, 5), "not");
        map.put(new Key(12, 9), "abs");
        map.put(new Key(12, 10), "add");
        map.put(new Key(12, 11), "sub");
        map.put(new Key(12, 12), "div");
        map.put(new Key(12, 14), "neg");
        map.put(new Key(12, 15), "eq");
        map.put(new Key(12, 18), "drop");
        map.put(new Key(12, 20), "put");
        map.put(new Key(12, 21), "get");
        map.put(new Key(12, 22), "ifelse");
        map.put(new Key(12, 23), "random");
        map.put(new Key(12, 24), "mul");
        map.put(new Key(12, 26), "sqrt");
        map.put(new Key(12, 27), "dup");
        map.put(new Key(12, 28), "exch");
        map.put(new Key(12, 29), "index");
        map.put(new Key(12, 30), "roll");
        map.put(new Key(12, 34), "hflex");
        map.put(new Key(12, 35), "flex");
        map.put(new Key(12, 36), "hflex1");
        map.put(new Key(12, 37), "flex1");
        map.put(new Key(14), "endchar");
        map.put(new Key(18), "hstemhm");
        map.put(new Key(19), "hintmask");
        map.put(new Key(20), "cntrmask");
        map.put(new Key(21), "rmoveto");
        map.put(new Key(22), "hmoveto");
        map.put(new Key(23), "vstemhm");
        map.put(new Key(24), "rcurveline");
        map.put(new Key(25), "rlinecurve");
        map.put(new Key(26), "vvcurveto");
        map.put(new Key(27), "hhcurveto");
        map.put(new Key(28), "shortint");
        map.put(new Key(29), "callgsubr");
        map.put(new Key(30), "vhcurveto");
        map.put(new Key(31), "hvcurveto");
        TYPE2_VOCABULARY = Collections.unmodifiableMap(map);
    }

    public static class Key {
        private int[] keyValues = null;

        public Key(int b0) {
            this.setValue(new int[]{b0});
        }

        public Key(int b0, int b1) {
            this.setValue(new int[]{b0, b1});
        }

        public Key(int[] values) {
            this.setValue(values);
        }

        public int[] getValue() {
            return this.keyValues;
        }

        private void setValue(int[] value) {
            this.keyValues = value;
        }

        public String toString() {
            return Arrays.toString(this.getValue());
        }

        public int hashCode() {
            if (this.keyValues[0] == 12 && this.keyValues.length > 1) {
                return this.keyValues[0] ^ this.keyValues[1];
            }
            return this.keyValues[0];
        }

        public boolean equals(Object object) {
            if (object instanceof Key) {
                Key that = (Key)object;
                if (this.keyValues[0] == 12 && that.keyValues[0] == 12) {
                    if (this.keyValues.length > 1 && that.keyValues.length > 1) {
                        return this.keyValues[1] == that.keyValues[1];
                    }
                    return this.keyValues.length == that.keyValues.length;
                }
                return this.keyValues[0] == that.keyValues[0];
            }
            return false;
        }
    }
}

