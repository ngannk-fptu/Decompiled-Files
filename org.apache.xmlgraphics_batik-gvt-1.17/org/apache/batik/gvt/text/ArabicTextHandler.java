/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.text;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

public final class ArabicTextHandler {
    private static final int arabicStart = 1536;
    private static final int arabicEnd = 1791;
    private static final AttributedCharacterIterator.Attribute ARABIC_FORM = GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM;
    private static final Integer ARABIC_NONE = GVTAttributedCharacterIterator.TextAttribute.ARABIC_NONE;
    private static final Integer ARABIC_ISOLATED = GVTAttributedCharacterIterator.TextAttribute.ARABIC_ISOLATED;
    private static final Integer ARABIC_TERMINAL = GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL;
    private static final Integer ARABIC_INITIAL = GVTAttributedCharacterIterator.TextAttribute.ARABIC_INITIAL;
    private static final Integer ARABIC_MEDIAL = GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL;
    static int singleCharFirst = 1569;
    static int singleCharLast = 1610;
    static int[][] singleCharRemappings = new int[][]{{65152, -1, -1, -1}, {65153, 65154, -1, -1}, {65155, 65156, -1, -1}, {65157, 65158, -1, -1}, {65159, 65160, -1, -1}, {65161, 65162, 65163, 65164}, {65165, 65166, -1, -1}, {65167, 65168, 65169, 65170}, {65171, 65172, -1, -1}, {65173, 65174, 65175, 65176}, {65177, 65178, 65179, 65180}, {65181, 65182, 65183, 65184}, {65185, 65186, 65187, 65188}, {65189, 65190, 65191, 65192}, {65193, 65194, -1, -1}, {65195, 65196, -1, -1}, {65197, 65198, -1, -1}, {65199, 65200, -1, -1}, {65201, 65202, 65203, 65204}, {65205, 65206, 65207, 65208}, {65209, 65210, 65211, 65212}, {65213, 65214, 65215, 65216}, {65217, 65218, 65219, 65220}, {65221, 65222, 65223, 65224}, {65225, 65226, 65227, 65228}, {65229, 65230, 65231, 65232}, null, null, null, null, null, null, {65233, 65234, 65235, 65236}, {65237, 65238, 65239, 65240}, {65241, 65242, 65243, 65244}, {65245, 65246, 65247, 65248}, {65249, 65250, 65251, 65252}, {65253, 65254, 65255, 65256}, {65257, 65258, 65259, 65260}, {65261, 65262, -1, -1}, {65263, 65264, -1, -1}, {65265, 65266, 65267, 65268}};
    static int doubleCharFirst = 1570;
    static int doubleCharLast = 1618;
    static int[][][] doubleCharRemappings = new int[][][]{new int[][]{{1604, 65269, 65270, -1, -1}}, new int[][]{{1604, 65271, 65272, -1, -1}}, (int[][])null, new int[][]{{1604, 65273, 65274, -1, -1}}, (int[][])null, new int[][]{{1604, 65275, 65276, -1, -1}}, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, (int[][])null, new int[][]{{32, 65136, -1, -1, -1}, {1600, -1, -1, -1, 65137}}, new int[][]{{32, 65138, -1, -1, -1}}, new int[][]{{32, 65140, -1, -1, -1}}, new int[][]{{32, 65142, -1, -1, -1}, {1600, -1, -1, -1, 65143}}, new int[][]{{32, 65144, -1, -1, -1}, {1600, -1, -1, -1, 65145}}, new int[][]{{32, 65146, -1, -1, -1}, {1600, -1, -1, -1, 65147}}, new int[][]{{32, 65148, -1, -1, -1}, {1600, -1, -1, -1, 65149}}, new int[][]{{32, 65150, -1, -1, -1}, {1600, -1, -1, -1, 65151}}};

    private ArabicTextHandler() {
    }

    public static AttributedString assignArabicForms(AttributedString as) {
        int i;
        char c;
        if (!ArabicTextHandler.containsArabic(as)) {
            return as;
        }
        AttributedCharacterIterator aci = as.getIterator();
        int numChars = aci.getEndIndex() - aci.getBeginIndex();
        int[] charOrder = null;
        if (numChars >= 3) {
            char prevChar = aci.first();
            c = aci.next();
            i = 1;
            char nextChar = aci.next();
            while (nextChar != '\uffff') {
                if (ArabicTextHandler.arabicCharTransparent(c) && ArabicTextHandler.hasSubstitute(prevChar, nextChar)) {
                    if (charOrder == null) {
                        charOrder = new int[numChars];
                        for (int j = 0; j < numChars; ++j) {
                            charOrder[j] = j + aci.getBeginIndex();
                        }
                    }
                    int temp = charOrder[i];
                    charOrder[i] = charOrder[i - 1];
                    charOrder[i - 1] = temp;
                }
                prevChar = c;
                c = nextChar;
                nextChar = aci.next();
                ++i;
            }
        }
        if (charOrder != null) {
            StringBuffer reorderedString = new StringBuffer(numChars);
            for (i = 0; i < numChars; ++i) {
                c = aci.setIndex((int)charOrder[i]);
                reorderedString.append(c);
            }
            AttributedString reorderedAS = new AttributedString(reorderedString.toString());
            for (int i2 = 0; i2 < numChars; ++i2) {
                aci.setIndex(charOrder[i2]);
                Map<AttributedCharacterIterator.Attribute, Object> attributes = aci.getAttributes();
                reorderedAS.addAttributes(attributes, i2, i2 + 1);
            }
            if (charOrder[0] != aci.getBeginIndex()) {
                aci.setIndex(charOrder[0]);
                Float x = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.X);
                Float y = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.Y);
                if (x != null && !x.isNaN()) {
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, Float.valueOf(Float.NaN), charOrder[0], charOrder[0] + 1);
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, x, 0, 1);
                }
                if (y != null && !y.isNaN()) {
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, Float.valueOf(Float.NaN), charOrder[0], charOrder[0] + 1);
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, y, 0, 1);
                }
            }
            as = reorderedAS;
        }
        aci = as.getIterator();
        int runStart = -1;
        int idx = aci.getBeginIndex();
        char c2 = aci.first();
        while (c2 != '\uffff') {
            if (c2 >= '\u0600' && c2 <= '\u06ff') {
                if (runStart == -1) {
                    runStart = idx;
                }
            } else if (runStart != -1) {
                as.addAttribute(ARABIC_FORM, ARABIC_NONE, runStart, idx);
                runStart = -1;
            }
            c2 = aci.next();
            ++idx;
        }
        if (runStart != -1) {
            as.addAttribute(ARABIC_FORM, ARABIC_NONE, runStart, idx);
        }
        aci = as.getIterator();
        int end = aci.getBeginIndex();
        Integer currentForm = ARABIC_NONE;
        block5: while (aci.setIndex(end) != '\uffff') {
            int start = aci.getRunStart(ARABIC_FORM);
            end = aci.getRunLimit(ARABIC_FORM);
            char currentChar = aci.setIndex(start);
            currentForm = (Integer)aci.getAttribute(ARABIC_FORM);
            if (currentForm == null) continue;
            int currentIndex = start;
            int prevCharIndex = start - 1;
            while (currentIndex < end) {
                char prevChar = currentChar;
                currentChar = aci.setIndex(currentIndex);
                while (ArabicTextHandler.arabicCharTransparent(currentChar) && currentIndex < end) {
                    currentChar = aci.setIndex(++currentIndex);
                }
                if (currentIndex >= end) continue block5;
                Integer prevForm = currentForm;
                currentForm = ARABIC_NONE;
                if (prevCharIndex >= start) {
                    if (ArabicTextHandler.arabicCharShapesRight(prevChar) && ArabicTextHandler.arabicCharShapesLeft(currentChar)) {
                        prevForm = prevForm + 1;
                        as.addAttribute(ARABIC_FORM, prevForm, prevCharIndex, prevCharIndex + 1);
                        currentForm = ARABIC_INITIAL;
                    } else if (ArabicTextHandler.arabicCharShaped(currentChar)) {
                        currentForm = ARABIC_ISOLATED;
                    }
                } else if (ArabicTextHandler.arabicCharShaped(currentChar)) {
                    currentForm = ARABIC_ISOLATED;
                }
                if (currentForm != ARABIC_NONE) {
                    as.addAttribute(ARABIC_FORM, currentForm, currentIndex, currentIndex + 1);
                }
                prevCharIndex = currentIndex++;
            }
        }
        return as;
    }

    public static boolean arabicChar(char c) {
        return c >= '\u0600' && c <= '\u06ff';
    }

    public static boolean containsArabic(AttributedString as) {
        return ArabicTextHandler.containsArabic(as.getIterator());
    }

    public static boolean containsArabic(AttributedCharacterIterator aci) {
        char c = aci.first();
        while (c != '\uffff') {
            if (ArabicTextHandler.arabicChar(c)) {
                return true;
            }
            c = aci.next();
        }
        return false;
    }

    public static boolean arabicCharTransparent(char c) {
        char charVal = c;
        if (charVal < '\u064b' || charVal > '\u06ed') {
            return false;
        }
        return charVal <= '\u0655' || charVal == '\u0670' || charVal >= '\u06d6' && charVal <= '\u06e4' || charVal >= '\u06e7' && charVal <= '\u06e8' || charVal >= '\u06ea';
    }

    private static boolean arabicCharShapesRight(char c) {
        char charVal = c;
        return charVal >= '\u0622' && charVal <= '\u0625' || charVal == '\u0627' || charVal == '\u0629' || charVal >= '\u062f' && charVal <= '\u0632' || charVal == '\u0648' || charVal >= '\u0671' && charVal <= '\u0673' || charVal >= '\u0675' && charVal <= '\u0677' || charVal >= '\u0688' && charVal <= '\u0699' || charVal == '\u06c0' || charVal >= '\u06c2' && charVal <= '\u06cb' || charVal == '\u06cd' || charVal == '\u06cf' || charVal >= '\u06d2' && charVal <= '\u06d3' || ArabicTextHandler.arabicCharShapesDuel(c);
    }

    private static boolean arabicCharShapesDuel(char c) {
        char charVal = c;
        return charVal == '\u0626' || charVal == '\u0628' || charVal >= '\u062a' && charVal <= '\u062e' || charVal >= '\u0633' && charVal <= '\u063a' || charVal >= '\u0641' && charVal <= '\u0647' || charVal >= '\u0649' && charVal <= '\u064a' || charVal >= '\u0678' && charVal <= '\u0687' || charVal >= '\u069a' && charVal <= '\u06bf' || charVal == '\u06c1' || charVal == '\u06cc' || charVal == '\u06ce' || charVal >= '\u06d0' && charVal <= '\u06d1' || charVal >= '\u06fa' && charVal <= '\u06fc';
    }

    private static boolean arabicCharShapesLeft(char c) {
        return ArabicTextHandler.arabicCharShapesDuel(c);
    }

    private static boolean arabicCharShaped(char c) {
        return ArabicTextHandler.arabicCharShapesRight(c);
    }

    public static boolean hasSubstitute(char ch1, char ch2) {
        if (ch1 < doubleCharFirst || ch1 > doubleCharLast) {
            return false;
        }
        int[][] remaps = doubleCharRemappings[ch1 - doubleCharFirst];
        if (remaps == null) {
            return false;
        }
        for (int[] remap : remaps) {
            if (remap[0] != ch2) continue;
            return true;
        }
        return false;
    }

    public static int getSubstituteChar(char ch1, char ch2, int form) {
        if (form == 0) {
            return -1;
        }
        if (ch1 < doubleCharFirst || ch1 > doubleCharLast) {
            return -1;
        }
        int[][] remaps = doubleCharRemappings[ch1 - doubleCharFirst];
        if (remaps == null) {
            return -1;
        }
        for (int[] remap : remaps) {
            if (remap[0] != ch2) continue;
            return remap[form];
        }
        return -1;
    }

    public static int getSubstituteChar(char ch, int form) {
        if (form == 0) {
            return -1;
        }
        if (ch < singleCharFirst || ch > singleCharLast) {
            return -1;
        }
        int[] chars = singleCharRemappings[ch - singleCharFirst];
        if (chars == null) {
            return -1;
        }
        return chars[form - 1];
    }

    public static String createSubstituteString(AttributedCharacterIterator aci) {
        int start = aci.getBeginIndex();
        int end = aci.getEndIndex();
        int numChar = end - start;
        StringBuffer substString = new StringBuffer(numChar);
        for (int i = start; i < end; ++i) {
            int substChar;
            char c = aci.setIndex(i);
            if (!ArabicTextHandler.arabicChar(c)) {
                substString.append(c);
                continue;
            }
            Integer form = (Integer)aci.getAttribute(ARABIC_FORM);
            if (ArabicTextHandler.charStartsLigature(c) && i + 1 < end) {
                char nextChar = aci.setIndex(i + 1);
                Integer nextForm = (Integer)aci.getAttribute(ARABIC_FORM);
                if (form != null && nextForm != null) {
                    int substChar2;
                    if (form.equals(ARABIC_TERMINAL) && nextForm.equals(ARABIC_INITIAL)) {
                        substChar2 = ArabicTextHandler.getSubstituteChar(c, nextChar, ARABIC_ISOLATED);
                        if (substChar2 > -1) {
                            substString.append((char)substChar2);
                            ++i;
                            continue;
                        }
                    } else if (form.equals(ARABIC_TERMINAL)) {
                        substChar2 = ArabicTextHandler.getSubstituteChar(c, nextChar, ARABIC_TERMINAL);
                        if (substChar2 > -1) {
                            substString.append((char)substChar2);
                            ++i;
                            continue;
                        }
                    } else if (form.equals(ARABIC_MEDIAL) && nextForm.equals(ARABIC_MEDIAL) && (substChar2 = ArabicTextHandler.getSubstituteChar(c, nextChar, ARABIC_MEDIAL)) > -1) {
                        substString.append((char)substChar2);
                        ++i;
                        continue;
                    }
                }
            }
            if (form != null && form > 0 && (substChar = ArabicTextHandler.getSubstituteChar(c, form)) > -1) {
                c = (char)substChar;
            }
            substString.append(c);
        }
        return substString.toString();
    }

    public static boolean charStartsLigature(char c) {
        char charVal = c;
        return charVal == '\u064b' || charVal == '\u064c' || charVal == '\u064d' || charVal == '\u064e' || charVal == '\u064f' || charVal == '\u0650' || charVal == '\u0651' || charVal == '\u0652' || charVal == '\u0622' || charVal == '\u0623' || charVal == '\u0625' || charVal == '\u0627';
    }

    public static int getNumChars(char c) {
        if (ArabicTextHandler.isLigature(c)) {
            return 2;
        }
        return 1;
    }

    public static boolean isLigature(char c) {
        char charVal = c;
        if (charVal < '\ufe70' || charVal > '\ufefc') {
            return false;
        }
        return charVal <= '\ufe72' || charVal == '\ufe74' || charVal >= '\ufe76' && charVal <= '\ufe7f' || charVal >= '\ufef5';
    }
}

