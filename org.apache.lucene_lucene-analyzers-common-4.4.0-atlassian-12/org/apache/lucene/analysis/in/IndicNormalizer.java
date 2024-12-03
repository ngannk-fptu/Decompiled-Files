/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.in;

import java.util.BitSet;
import java.util.IdentityHashMap;
import org.apache.lucene.analysis.util.StemmerUtil;

public class IndicNormalizer {
    private static final IdentityHashMap<Character.UnicodeBlock, ScriptData> scripts = new IdentityHashMap(9);
    private static final int[][] decompositions;

    private static int flag(Character.UnicodeBlock ub) {
        return IndicNormalizer.scripts.get((Object)ub).flag;
    }

    public int normalize(char[] text, int len) {
        for (int i = 0; i < len; ++i) {
            int ch;
            Character.UnicodeBlock block = Character.UnicodeBlock.of(text[i]);
            ScriptData sd = scripts.get(block);
            if (sd == null || !sd.decompMask.get(ch = text[i] - sd.base)) continue;
            len = this.compose(ch, block, sd, text, i, len);
        }
        return len;
    }

    private int compose(int ch0, Character.UnicodeBlock block0, ScriptData sd, char[] text, int pos, int len) {
        if (pos + 1 >= len) {
            return len;
        }
        int ch1 = text[pos + 1] - sd.base;
        Character.UnicodeBlock block1 = Character.UnicodeBlock.of(text[pos + 1]);
        if (block1 != block0) {
            return len;
        }
        int ch2 = -1;
        if (pos + 2 < len) {
            ch2 = text[pos + 2] - sd.base;
            Character.UnicodeBlock block2 = Character.UnicodeBlock.of(text[pos + 2]);
            if (text[pos + 2] == '\u200d') {
                ch2 = 255;
            } else if (block2 != block1) {
                ch2 = -1;
            }
        }
        for (int i = 0; i < decompositions.length; ++i) {
            if (decompositions[i][0] != ch0 || (decompositions[i][4] & sd.flag) == 0 || decompositions[i][1] != ch1 || decompositions[i][2] >= 0 && decompositions[i][2] != ch2) continue;
            text[pos] = (char)(sd.base + decompositions[i][3]);
            len = StemmerUtil.delete(text, pos + 1, len);
            if (decompositions[i][2] >= 0) {
                len = StemmerUtil.delete(text, pos + 1, len);
            }
            return len;
        }
        return len;
    }

    static {
        scripts.put(Character.UnicodeBlock.DEVANAGARI, new ScriptData(1, 2304));
        scripts.put(Character.UnicodeBlock.BENGALI, new ScriptData(2, 2432));
        scripts.put(Character.UnicodeBlock.GURMUKHI, new ScriptData(4, 2560));
        scripts.put(Character.UnicodeBlock.GUJARATI, new ScriptData(8, 2688));
        scripts.put(Character.UnicodeBlock.ORIYA, new ScriptData(16, 2816));
        scripts.put(Character.UnicodeBlock.TAMIL, new ScriptData(32, 2944));
        scripts.put(Character.UnicodeBlock.TELUGU, new ScriptData(64, 3072));
        scripts.put(Character.UnicodeBlock.KANNADA, new ScriptData(128, 3200));
        scripts.put(Character.UnicodeBlock.MALAYALAM, new ScriptData(256, 3328));
        decompositions = new int[][]{{5, 62, 69, 17, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 62, 70, 18, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {5, 62, 71, 19, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 62, 72, 20, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 62, -1, 6, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.BENGALI) | IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI) | IndicNormalizer.flag(Character.UnicodeBlock.ORIYA)}, {5, 69, -1, 114, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {5, 69, -1, 13, IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 70, -1, 4, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {5, 71, -1, 15, IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 72, -1, 16, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 73, -1, 17, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 74, -1, 18, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {5, 75, -1, 19, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {5, 76, -1, 20, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {6, 69, -1, 17, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {6, 70, -1, 18, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {6, 71, -1, 19, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {6, 72, -1, 20, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {7, 87, -1, 8, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {9, 65, -1, 10, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {9, 87, -1, 10, IndicNormalizer.flag(Character.UnicodeBlock.TAMIL) | IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {14, 70, -1, 16, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {15, 69, -1, 13, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {15, 70, -1, 14, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {15, 71, -1, 16, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {15, 87, -1, 16, IndicNormalizer.flag(Character.UnicodeBlock.ORIYA)}, {18, 62, -1, 19, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {18, 76, -1, 20, IndicNormalizer.flag(Character.UnicodeBlock.TELUGU) | IndicNormalizer.flag(Character.UnicodeBlock.KANNADA)}, {18, 85, -1, 19, IndicNormalizer.flag(Character.UnicodeBlock.TELUGU)}, {18, 87, -1, 20, IndicNormalizer.flag(Character.UnicodeBlock.TAMIL) | IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {19, 87, -1, 20, IndicNormalizer.flag(Character.UnicodeBlock.ORIYA)}, {21, 60, -1, 88, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {22, 60, -1, 89, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {23, 60, -1, 90, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {28, 60, -1, 91, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {33, 60, -1, 92, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.BENGALI) | IndicNormalizer.flag(Character.UnicodeBlock.ORIYA)}, {34, 60, -1, 93, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.BENGALI) | IndicNormalizer.flag(Character.UnicodeBlock.ORIYA)}, {35, 77, 255, 122, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {36, 77, 255, 78, IndicNormalizer.flag(Character.UnicodeBlock.BENGALI)}, {40, 60, -1, 41, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {40, 77, 255, 123, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {43, 60, -1, 94, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {47, 60, -1, 95, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.BENGALI)}, {44, 65, 65, 11, IndicNormalizer.flag(Character.UnicodeBlock.TELUGU)}, {48, 60, -1, 49, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {48, 77, 255, 124, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {50, 77, 255, 125, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {51, 60, -1, 52, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {51, 77, 255, 126, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {53, 65, -1, 46, IndicNormalizer.flag(Character.UnicodeBlock.TELUGU)}, {62, 69, -1, 73, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {62, 70, -1, 74, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI)}, {62, 71, -1, 75, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {62, 72, -1, 76, IndicNormalizer.flag(Character.UnicodeBlock.DEVANAGARI) | IndicNormalizer.flag(Character.UnicodeBlock.GUJARATI)}, {63, 85, -1, 64, IndicNormalizer.flag(Character.UnicodeBlock.KANNADA)}, {65, 65, -1, 66, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {70, 62, -1, 74, IndicNormalizer.flag(Character.UnicodeBlock.TAMIL) | IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {70, 66, 85, 75, IndicNormalizer.flag(Character.UnicodeBlock.KANNADA)}, {70, 66, -1, 74, IndicNormalizer.flag(Character.UnicodeBlock.KANNADA)}, {70, 70, -1, 72, IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {70, 85, -1, 71, IndicNormalizer.flag(Character.UnicodeBlock.TELUGU) | IndicNormalizer.flag(Character.UnicodeBlock.KANNADA)}, {70, 86, -1, 72, IndicNormalizer.flag(Character.UnicodeBlock.TELUGU) | IndicNormalizer.flag(Character.UnicodeBlock.KANNADA)}, {70, 87, -1, 76, IndicNormalizer.flag(Character.UnicodeBlock.TAMIL) | IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {71, 62, -1, 75, IndicNormalizer.flag(Character.UnicodeBlock.BENGALI) | IndicNormalizer.flag(Character.UnicodeBlock.ORIYA) | IndicNormalizer.flag(Character.UnicodeBlock.TAMIL) | IndicNormalizer.flag(Character.UnicodeBlock.MALAYALAM)}, {71, 87, -1, 76, IndicNormalizer.flag(Character.UnicodeBlock.BENGALI) | IndicNormalizer.flag(Character.UnicodeBlock.ORIYA)}, {74, 85, -1, 75, IndicNormalizer.flag(Character.UnicodeBlock.KANNADA)}, {114, 63, -1, 7, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {114, 64, -1, 8, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {114, 71, -1, 15, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {115, 65, -1, 9, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {115, 66, -1, 10, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}, {115, 75, -1, 19, IndicNormalizer.flag(Character.UnicodeBlock.GURMUKHI)}};
        for (ScriptData sd : scripts.values()) {
            sd.decompMask = new BitSet(127);
            for (int i = 0; i < decompositions.length; ++i) {
                int ch = decompositions[i][0];
                int flags = decompositions[i][4];
                if ((flags & sd.flag) == 0) continue;
                sd.decompMask.set(ch);
            }
        }
    }

    private static class ScriptData {
        final int flag;
        final int base;
        BitSet decompMask;

        ScriptData(int flag, int base) {
            this.flag = flag;
            this.base = base;
        }
    }
}

