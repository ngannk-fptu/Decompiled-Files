/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.CodePointMap;
import com.ibm.icu.util.CodePointTrie;
import com.ibm.icu.util.ICUException;
import com.ibm.icu.util.VersionInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.MissingResourceException;

public final class UCharacterProperty {
    public static final UCharacterProperty INSTANCE;
    public Trie2_16 m_trie_;
    public VersionInfo m_unicodeVersion_;
    public static final char LATIN_CAPITAL_LETTER_I_WITH_DOT_ABOVE_ = '\u0130';
    public static final char LATIN_SMALL_LETTER_DOTLESS_I_ = '\u0131';
    public static final char LATIN_SMALL_LETTER_I_ = 'i';
    public static final int TYPE_MASK = 31;
    public static final int SRC_NONE = 0;
    public static final int SRC_CHAR = 1;
    public static final int SRC_PROPSVEC = 2;
    public static final int SRC_NAMES = 3;
    public static final int SRC_CASE = 4;
    public static final int SRC_BIDI = 5;
    public static final int SRC_CHAR_AND_PROPSVEC = 6;
    public static final int SRC_CASE_AND_NORM = 7;
    public static final int SRC_NFC = 8;
    public static final int SRC_NFKC = 9;
    public static final int SRC_NFKC_CF = 10;
    public static final int SRC_NFC_CANON_ITER = 11;
    public static final int SRC_INPC = 12;
    public static final int SRC_INSC = 13;
    public static final int SRC_VO = 14;
    public static final int SRC_COUNT = 15;
    static final int MY_MASK = 30;
    private static final int GC_CN_MASK;
    private static final int GC_CC_MASK;
    private static final int GC_CS_MASK;
    private static final int GC_ZS_MASK;
    private static final int GC_ZL_MASK;
    private static final int GC_ZP_MASK;
    private static final int GC_Z_MASK;
    BinaryProperty[] binProps = new BinaryProperty[]{new BinaryProperty(1, 256), new BinaryProperty(1, 128), new BinaryProperty(5){

        @Override
        boolean contains(int c) {
            return UBiDiProps.INSTANCE.isBidiControl(c);
        }
    }, new BinaryProperty(5){

        @Override
        boolean contains(int c) {
            return UBiDiProps.INSTANCE.isMirrored(c);
        }
    }, new BinaryProperty(1, 2), new BinaryProperty(1, 524288), new BinaryProperty(1, 0x100000), new BinaryProperty(1, 1024), new BinaryProperty(1, 2048), new BinaryProperty(8){

        @Override
        boolean contains(int c) {
            Normalizer2Impl impl = Norm2AllModes.getNFCInstance().impl;
            return impl.isCompNo(impl.getNorm16(c));
        }
    }, new BinaryProperty(1, 0x4000000), new BinaryProperty(1, 8192), new BinaryProperty(1, 16384), new BinaryProperty(1, 64), new BinaryProperty(1, 4), new BinaryProperty(1, 0x2000000), new BinaryProperty(1, 0x1000000), new BinaryProperty(1, 512), new BinaryProperty(1, 32768), new BinaryProperty(1, 65536), new BinaryProperty(5){

        @Override
        boolean contains(int c) {
            return UBiDiProps.INSTANCE.isJoinControl(c);
        }
    }, new BinaryProperty(1, 0x200000), new CaseBinaryProperty(22), new BinaryProperty(1, 32), new BinaryProperty(1, 4096), new BinaryProperty(1, 8), new BinaryProperty(1, 131072), new CaseBinaryProperty(27), new BinaryProperty(1, 16), new BinaryProperty(1, 262144), new CaseBinaryProperty(30), new BinaryProperty(1, 1), new BinaryProperty(1, 0x800000), new BinaryProperty(1, 0x400000), new CaseBinaryProperty(34), new BinaryProperty(1, 0x8000000), new BinaryProperty(1, 0x10000000), new NormInertBinaryProperty(8, 37), new NormInertBinaryProperty(9, 38), new NormInertBinaryProperty(8, 39), new NormInertBinaryProperty(9, 40), new BinaryProperty(11){

        @Override
        boolean contains(int c) {
            return Norm2AllModes.getNFCInstance().impl.ensureCanonIterData().isCanonSegmentStarter(c);
        }
    }, new BinaryProperty(1, 0x20000000), new BinaryProperty(1, 0x40000000), new BinaryProperty(6){

        @Override
        boolean contains(int c) {
            return UCharacter.isUAlphabetic(c) || UCharacter.isDigit(c);
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            if (c <= 159) {
                return c == 9 || c == 32;
            }
            return UCharacter.getType(c) == 12;
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            return UCharacterProperty.isgraphPOSIX(c);
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            return UCharacter.getType(c) == 12 || UCharacterProperty.isgraphPOSIX(c);
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            if (c <= 102 && c >= 65 && (c <= 70 || c >= 97) || c >= 65313 && c <= 65350 && (c <= 65318 || c >= 65345)) {
                return true;
            }
            return UCharacter.getType(c) == 9;
        }
    }, new CaseBinaryProperty(49), new CaseBinaryProperty(50), new CaseBinaryProperty(51), new CaseBinaryProperty(52), new CaseBinaryProperty(53), new BinaryProperty(7){

        @Override
        boolean contains(int c) {
            String nfd = Norm2AllModes.getNFCInstance().impl.getDecomposition(c);
            if (nfd != null) {
                c = nfd.codePointAt(0);
                if (Character.charCount(c) != nfd.length()) {
                    c = -1;
                }
            } else if (c < 0) {
                return false;
            }
            if (c >= 0) {
                UCaseProps csp = UCaseProps.INSTANCE;
                UCaseProps.dummyStringBuilder.setLength(0);
                return csp.toFullFolding(c, UCaseProps.dummyStringBuilder, 0) >= 0;
            }
            String folded = UCharacter.foldCase(nfd, true);
            return !folded.equals(nfd);
        }
    }, new CaseBinaryProperty(55), new BinaryProperty(10){

        @Override
        boolean contains(int c) {
            Normalizer2Impl kcf = Norm2AllModes.getNFKC_CFInstance().impl;
            String src = UTF16.valueOf(c);
            StringBuilder dest = new StringBuilder();
            Normalizer2Impl.ReorderingBuffer buffer = new Normalizer2Impl.ReorderingBuffer(kcf, dest, 5);
            kcf.compose(src, 0, src.length(), false, true, buffer);
            return !Normalizer2Impl.UTF16Plus.equal(dest, src);
        }
    }, new BinaryProperty(2, 0x10000000), new BinaryProperty(2, 0x20000000), new BinaryProperty(2, 0x40000000), new BinaryProperty(2, Integer.MIN_VALUE), new BinaryProperty(2, 0x8000000), new BinaryProperty(2){

        @Override
        boolean contains(int c) {
            return 127462 <= c && c <= 127487;
        }
    }, new BinaryProperty(1, Integer.MIN_VALUE), new BinaryProperty(2, 0x4000000)};
    private static final int[] gcbToHst;
    IntProperty[] intProps = new IntProperty[]{new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getClass(c);
        }
    }, new IntProperty(0, 130816, 8), new CombiningClassIntProperty(8){

        @Override
        int getValue(int c) {
            return Normalizer2.getNFDInstance().getCombiningClass(c);
        }
    }, new IntProperty(2, 31, 0), new IntProperty(0, 917504, 17), new IntProperty(1){

        @Override
        int getValue(int c) {
            return UCharacterProperty.this.getType(c);
        }

        @Override
        int getMaxValue(int which) {
            return 29;
        }
    }, new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getJoiningGroup(c);
        }
    }, new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getJoiningType(c);
        }
    }, new IntProperty(2, 0x3F00000, 20), new IntProperty(1){

        @Override
        int getValue(int c) {
            return UCharacterProperty.ntvGetType(UCharacterProperty.getNumericTypeValue(UCharacterProperty.this.getProperty(c)));
        }

        @Override
        int getMaxValue(int which) {
            return 3;
        }
    }, new IntProperty(0, 255, 0){

        @Override
        int getValue(int c) {
            return UScript.getScript(c);
        }
    }, new IntProperty(2){

        @Override
        int getValue(int c) {
            int gcb = (UCharacterProperty.this.getAdditional(c, 2) & 0x3E0) >>> 5;
            if (gcb < gcbToHst.length) {
                return gcbToHst[gcb];
            }
            return 0;
        }

        @Override
        int getMaxValue(int which) {
            return 5;
        }
    }, new NormQuickCheckIntProperty(8, 4108, 1), new NormQuickCheckIntProperty(9, 4109, 1), new NormQuickCheckIntProperty(8, 4110, 2), new NormQuickCheckIntProperty(9, 4111, 2), new CombiningClassIntProperty(8){

        @Override
        int getValue(int c) {
            return Norm2AllModes.getNFCInstance().impl.getFCD16(c) >> 8;
        }
    }, new CombiningClassIntProperty(8){

        @Override
        int getValue(int c) {
            return Norm2AllModes.getNFCInstance().impl.getFCD16(c) & 0xFF;
        }
    }, new IntProperty(2, 992, 5), new IntProperty(2, 1015808, 15), new IntProperty(2, 31744, 10), new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getPairedBracketType(c);
        }
    }, new IntProperty(12){

        @Override
        int getValue(int c) {
            return InPCTrie.INSTANCE.get(c);
        }

        @Override
        int getMaxValue(int which) {
            return 14;
        }
    }, new IntProperty(13){

        @Override
        int getValue(int c) {
            return InSCTrie.INSTANCE.get(c);
        }

        @Override
        int getMaxValue(int which) {
            return 35;
        }
    }, new IntProperty(14){

        @Override
        int getValue(int c) {
            return VoTrie.INSTANCE.get(c);
        }

        @Override
        int getMaxValue(int which) {
            return 3;
        }
    }};
    Trie2_16 m_additionalTrie_;
    int[] m_additionalVectors_;
    int m_additionalColumnsCount_;
    int m_maxBlockScriptValue_;
    int m_maxJTGValue_;
    public char[] m_scriptExtensions_;
    private static final String DATA_FILE_NAME_ = "uprops.icu";
    private static final int NUMERIC_TYPE_VALUE_SHIFT_ = 6;
    private static final int NTV_NONE_ = 0;
    private static final int NTV_DECIMAL_START_ = 1;
    private static final int NTV_DIGIT_START_ = 11;
    private static final int NTV_NUMERIC_START_ = 21;
    private static final int NTV_FRACTION_START_ = 176;
    private static final int NTV_LARGE_START_ = 480;
    private static final int NTV_BASE60_START_ = 768;
    private static final int NTV_FRACTION20_START_ = 804;
    private static final int NTV_RESERVED_START_ = 828;
    public static final int SCRIPT_X_MASK = 0xC000FF;
    private static final int EAST_ASIAN_MASK_ = 917504;
    private static final int EAST_ASIAN_SHIFT_ = 17;
    private static final int BLOCK_MASK_ = 130816;
    private static final int BLOCK_SHIFT_ = 8;
    public static final int SCRIPT_MASK_ = 255;
    public static final int SCRIPT_X_WITH_COMMON = 0x400000;
    public static final int SCRIPT_X_WITH_INHERITED = 0x800000;
    public static final int SCRIPT_X_WITH_OTHER = 0xC00000;
    private static final int WHITE_SPACE_PROPERTY_ = 0;
    private static final int DASH_PROPERTY_ = 1;
    private static final int HYPHEN_PROPERTY_ = 2;
    private static final int QUOTATION_MARK_PROPERTY_ = 3;
    private static final int TERMINAL_PUNCTUATION_PROPERTY_ = 4;
    private static final int MATH_PROPERTY_ = 5;
    private static final int HEX_DIGIT_PROPERTY_ = 6;
    private static final int ASCII_HEX_DIGIT_PROPERTY_ = 7;
    private static final int ALPHABETIC_PROPERTY_ = 8;
    private static final int IDEOGRAPHIC_PROPERTY_ = 9;
    private static final int DIACRITIC_PROPERTY_ = 10;
    private static final int EXTENDER_PROPERTY_ = 11;
    private static final int NONCHARACTER_CODE_POINT_PROPERTY_ = 12;
    private static final int GRAPHEME_EXTEND_PROPERTY_ = 13;
    private static final int GRAPHEME_LINK_PROPERTY_ = 14;
    private static final int IDS_BINARY_OPERATOR_PROPERTY_ = 15;
    private static final int IDS_TRINARY_OPERATOR_PROPERTY_ = 16;
    private static final int RADICAL_PROPERTY_ = 17;
    private static final int UNIFIED_IDEOGRAPH_PROPERTY_ = 18;
    private static final int DEFAULT_IGNORABLE_CODE_POINT_PROPERTY_ = 19;
    private static final int DEPRECATED_PROPERTY_ = 20;
    private static final int LOGICAL_ORDER_EXCEPTION_PROPERTY_ = 21;
    private static final int XID_START_PROPERTY_ = 22;
    private static final int XID_CONTINUE_PROPERTY_ = 23;
    private static final int ID_START_PROPERTY_ = 24;
    private static final int ID_CONTINUE_PROPERTY_ = 25;
    private static final int GRAPHEME_BASE_PROPERTY_ = 26;
    private static final int S_TERM_PROPERTY_ = 27;
    private static final int VARIATION_SELECTOR_PROPERTY_ = 28;
    private static final int PATTERN_SYNTAX = 29;
    private static final int PATTERN_WHITE_SPACE = 30;
    private static final int PREPENDED_CONCATENATION_MARK = 31;
    private static final int PROPS_2_EXTENDED_PICTOGRAPHIC = 26;
    private static final int PROPS_2_EMOJI_COMPONENT = 27;
    private static final int PROPS_2_EMOJI = 28;
    private static final int PROPS_2_EMOJI_PRESENTATION = 29;
    private static final int PROPS_2_EMOJI_MODIFIER = 30;
    private static final int PROPS_2_EMOJI_MODIFIER_BASE = 31;
    private static final int LB_MASK = 0x3F00000;
    private static final int LB_SHIFT = 20;
    private static final int SB_MASK = 1015808;
    private static final int SB_SHIFT = 15;
    private static final int WB_MASK = 31744;
    private static final int WB_SHIFT = 10;
    private static final int GCB_MASK = 992;
    private static final int GCB_SHIFT = 5;
    private static final int DECOMPOSITION_TYPE_MASK_ = 31;
    private static final int FIRST_NIBBLE_SHIFT_ = 4;
    private static final int LAST_NIBBLE_MASK_ = 15;
    private static final int AGE_SHIFT_ = 24;
    private static final int DATA_FORMAT = 1431335535;
    private static final int TAB = 9;
    private static final int CR = 13;
    private static final int U_A = 65;
    private static final int U_F = 70;
    private static final int U_Z = 90;
    private static final int U_a = 97;
    private static final int U_f = 102;
    private static final int U_z = 122;
    private static final int DEL = 127;
    private static final int NL = 133;
    private static final int NBSP = 160;
    private static final int CGJ = 847;
    private static final int FIGURESP = 8199;
    private static final int HAIRSP = 8202;
    private static final int RLM = 8207;
    private static final int NNBSP = 8239;
    private static final int WJ = 8288;
    private static final int INHSWAP = 8298;
    private static final int NOMDIG = 8303;
    private static final int U_FW_A = 65313;
    private static final int U_FW_F = 65318;
    private static final int U_FW_Z = 65338;
    private static final int U_FW_a = 65345;
    private static final int U_FW_f = 65350;
    private static final int U_FW_z = 65370;
    private static final int ZWNBSP = 65279;

    private static final CodePointTrie makeTrie(String data) {
        byte[] bytes = new byte[data.length()];
        for (int i = 0; i < bytes.length; ++i) {
            int c = data.charAt(i);
            if (c == 0) {
                c = 122;
            } else if (c == 122) {
                c = 0;
            }
            assert (0 <= c && c <= 255);
            bytes[i] = (byte)c;
        }
        return CodePointTrie.fromBinary(null, null, ByteBuffer.wrap(bytes));
    }

    public final int getProperty(int ch) {
        return this.m_trie_.get(ch);
    }

    public int getAdditional(int codepoint, int column) {
        assert (column >= 0);
        if (column >= this.m_additionalColumnsCount_) {
            return 0;
        }
        return this.m_additionalVectors_[this.m_additionalTrie_.get(codepoint) + column];
    }

    public VersionInfo getAge(int codepoint) {
        int version = this.getAdditional(codepoint, 0) >> 24;
        return VersionInfo.getInstance(version >> 4 & 0xF, version & 0xF, 0, 0);
    }

    private static final boolean isgraphPOSIX(int c) {
        return (UCharacterProperty.getMask(UCharacter.getType(c)) & (GC_CC_MASK | GC_CS_MASK | GC_CN_MASK | GC_Z_MASK)) == 0;
    }

    public boolean hasBinaryProperty(int c, int which) {
        if (which < 0 || 65 <= which) {
            return false;
        }
        return this.binProps[which].contains(c);
    }

    public int getType(int c) {
        return this.getProperty(c) & 0x1F;
    }

    public int getIntPropertyValue(int c, int which) {
        if (which < 4096) {
            if (0 <= which && which < 65) {
                return this.binProps[which].contains(c) ? 1 : 0;
            }
        } else {
            if (which < 4121) {
                return this.intProps[which - 4096].getValue(c);
            }
            if (which == 8192) {
                return UCharacterProperty.getMask(this.getType(c));
            }
        }
        return 0;
    }

    public int getIntPropertyMaxValue(int which) {
        if (which < 4096) {
            if (0 <= which && which < 65) {
                return 1;
            }
        } else if (which < 4121) {
            return this.intProps[which - 4096].getMaxValue(which);
        }
        return -1;
    }

    final int getSource(int which) {
        if (which < 0) {
            return 0;
        }
        if (which < 65) {
            return this.binProps[which].getSource();
        }
        if (which < 4096) {
            return 0;
        }
        if (which < 4121) {
            return this.intProps[which - 4096].getSource();
        }
        if (which < 16384) {
            switch (which) {
                case 8192: 
                case 12288: {
                    return 1;
                }
            }
            return 0;
        }
        if (which < 16398) {
            switch (which) {
                case 16384: {
                    return 2;
                }
                case 16385: {
                    return 5;
                }
                case 16386: 
                case 16388: 
                case 16390: 
                case 16391: 
                case 16392: 
                case 16393: 
                case 16394: 
                case 16396: {
                    return 4;
                }
                case 16387: 
                case 16389: 
                case 16395: {
                    return 3;
                }
            }
            return 0;
        }
        switch (which) {
            case 28672: {
                return 2;
            }
        }
        return 0;
    }

    public int getMaxValues(int column) {
        switch (column) {
            case 0: {
                return this.m_maxBlockScriptValue_;
            }
            case 2: {
                return this.m_maxJTGValue_;
            }
        }
        return 0;
    }

    public static final int getMask(int type) {
        return 1 << type;
    }

    public static int getEuropeanDigit(int ch) {
        if (ch > 122 && ch < 65313 || ch < 65 || ch > 90 && ch < 97 || ch > 65370 || ch > 65338 && ch < 65345) {
            return -1;
        }
        if (ch <= 122) {
            return ch + 10 - (ch <= 90 ? 65 : 97);
        }
        if (ch <= 65338) {
            return ch + 10 - 65313;
        }
        return ch + 10 - 65345;
    }

    public int digit(int c) {
        int value = UCharacterProperty.getNumericTypeValue(this.getProperty(c)) - 1;
        if (value <= 9) {
            return value;
        }
        return -1;
    }

    public int getNumericValue(int c) {
        int ntv = UCharacterProperty.getNumericTypeValue(this.getProperty(c));
        if (ntv == 0) {
            return UCharacterProperty.getEuropeanDigit(c);
        }
        if (ntv < 11) {
            return ntv - 1;
        }
        if (ntv < 21) {
            return ntv - 11;
        }
        if (ntv < 176) {
            return ntv - 21;
        }
        if (ntv < 480) {
            return -2;
        }
        if (ntv < 768) {
            int mant = (ntv >> 5) - 14;
            int exp = (ntv & 0x1F) + 2;
            if (exp < 9 || exp == 9 && mant <= 2) {
                int numValue = mant;
                do {
                    numValue *= 10;
                } while (--exp > 0);
                return numValue;
            }
            return -2;
        }
        if (ntv < 804) {
            int numValue = (ntv >> 2) - 191;
            int exp = (ntv & 3) + 1;
            switch (exp) {
                case 4: {
                    numValue *= 12960000;
                    break;
                }
                case 3: {
                    numValue *= 216000;
                    break;
                }
                case 2: {
                    numValue *= 3600;
                    break;
                }
                case 1: {
                    numValue *= 60;
                    break;
                }
            }
            return numValue;
        }
        if (ntv < 828) {
            return -2;
        }
        return -2;
    }

    public double getUnicodeNumericValue(int c) {
        int ntv = UCharacterProperty.getNumericTypeValue(this.getProperty(c));
        if (ntv == 0) {
            return -1.23456789E8;
        }
        if (ntv < 11) {
            return ntv - 1;
        }
        if (ntv < 21) {
            return ntv - 11;
        }
        if (ntv < 176) {
            return ntv - 21;
        }
        if (ntv < 480) {
            int numerator = (ntv >> 4) - 12;
            int denominator = (ntv & 0xF) + 1;
            return (double)numerator / (double)denominator;
        }
        if (ntv < 768) {
            int exp;
            int mant = (ntv >> 5) - 14;
            double numValue = mant;
            for (exp = (ntv & 0x1F) + 2; exp >= 4; exp -= 4) {
                numValue *= 10000.0;
            }
            switch (exp) {
                case 3: {
                    numValue *= 1000.0;
                    break;
                }
                case 2: {
                    numValue *= 100.0;
                    break;
                }
                case 1: {
                    numValue *= 10.0;
                    break;
                }
            }
            return numValue;
        }
        if (ntv < 804) {
            int numValue = (ntv >> 2) - 191;
            int exp = (ntv & 3) + 1;
            switch (exp) {
                case 4: {
                    numValue *= 12960000;
                    break;
                }
                case 3: {
                    numValue *= 216000;
                    break;
                }
                case 2: {
                    numValue *= 3600;
                    break;
                }
                case 1: {
                    numValue *= 60;
                    break;
                }
            }
            return numValue;
        }
        if (ntv < 828) {
            int frac20 = ntv - 804;
            int numerator = 2 * (frac20 & 3) + 1;
            int denominator = 20 << (frac20 >> 2);
            return (double)numerator / (double)denominator;
        }
        return -1.23456789E8;
    }

    private static final int getNumericTypeValue(int props) {
        return props >> 6;
    }

    private static final int ntvGetType(int ntv) {
        return ntv == 0 ? 0 : (ntv < 11 ? 1 : (ntv < 21 ? 2 : 3));
    }

    private UCharacterProperty() throws IOException {
        int numChars;
        if (this.binProps.length != 65) {
            throw new ICUException("binProps.length!=UProperty.BINARY_LIMIT");
        }
        if (this.intProps.length != 25) {
            throw new ICUException("intProps.length!=(UProperty.INT_LIMIT-UProperty.INT_START)");
        }
        ByteBuffer bytes = ICUBinary.getRequiredData(DATA_FILE_NAME_);
        this.m_unicodeVersion_ = ICUBinary.readHeaderAndDataVersion(bytes, 1431335535, new IsAcceptable());
        int propertyOffset = bytes.getInt();
        bytes.getInt();
        bytes.getInt();
        int additionalOffset = bytes.getInt();
        int additionalVectorsOffset = bytes.getInt();
        this.m_additionalColumnsCount_ = bytes.getInt();
        int scriptExtensionsOffset = bytes.getInt();
        int reservedOffset7 = bytes.getInt();
        bytes.getInt();
        bytes.getInt();
        this.m_maxBlockScriptValue_ = bytes.getInt();
        this.m_maxJTGValue_ = bytes.getInt();
        ICUBinary.skipBytes(bytes, 16);
        this.m_trie_ = Trie2_16.createFromSerialized(bytes);
        int expectedTrieLength = (propertyOffset - 16) * 4;
        int trieLength = this.m_trie_.getSerializedLength();
        if (trieLength > expectedTrieLength) {
            throw new IOException("uprops.icu: not enough bytes for main trie");
        }
        ICUBinary.skipBytes(bytes, expectedTrieLength - trieLength);
        ICUBinary.skipBytes(bytes, (additionalOffset - propertyOffset) * 4);
        if (this.m_additionalColumnsCount_ > 0) {
            this.m_additionalTrie_ = Trie2_16.createFromSerialized(bytes);
            expectedTrieLength = (additionalVectorsOffset - additionalOffset) * 4;
            trieLength = this.m_additionalTrie_.getSerializedLength();
            if (trieLength > expectedTrieLength) {
                throw new IOException("uprops.icu: not enough bytes for additional-properties trie");
            }
            ICUBinary.skipBytes(bytes, expectedTrieLength - trieLength);
            int size = scriptExtensionsOffset - additionalVectorsOffset;
            this.m_additionalVectors_ = ICUBinary.getInts(bytes, size, 0);
        }
        if ((numChars = (reservedOffset7 - scriptExtensionsOffset) * 2) > 0) {
            this.m_scriptExtensions_ = ICUBinary.getChars(bytes, numChars, 0);
        }
    }

    public UnicodeSet addPropertyStarts(UnicodeSet set) {
        for (Trie2.Range range : this.m_trie_) {
            if (range.leadSurrogate) break;
            set.add(range.startCodePoint);
        }
        set.add(9);
        set.add(10);
        set.add(14);
        set.add(28);
        set.add(32);
        set.add(133);
        set.add(134);
        set.add(127);
        set.add(8202);
        set.add(8208);
        set.add(8298);
        set.add(8304);
        set.add(65279);
        set.add(65280);
        set.add(160);
        set.add(161);
        set.add(8199);
        set.add(8200);
        set.add(8239);
        set.add(8240);
        set.add(12295);
        set.add(12296);
        set.add(19968);
        set.add(19969);
        set.add(20108);
        set.add(20109);
        set.add(19977);
        set.add(19978);
        set.add(22235);
        set.add(22236);
        set.add(20116);
        set.add(20117);
        set.add(20845);
        set.add(20846);
        set.add(19971);
        set.add(19972);
        set.add(20843);
        set.add(20844);
        set.add(20061);
        set.add(20062);
        set.add(97);
        set.add(123);
        set.add(65);
        set.add(91);
        set.add(65345);
        set.add(65371);
        set.add(65313);
        set.add(65339);
        set.add(103);
        set.add(71);
        set.add(65351);
        set.add(65319);
        set.add(8288);
        set.add(65520);
        set.add(65532);
        set.add(917504);
        set.add(921600);
        set.add(847);
        set.add(848);
        return set;
    }

    public void upropsvec_addPropertyStarts(UnicodeSet set) {
        if (this.m_additionalColumnsCount_ > 0) {
            for (Trie2.Range range : this.m_additionalTrie_) {
                if (range.leadSurrogate) break;
                set.add(range.startCodePoint);
            }
        }
    }

    public UnicodeSet ulayout_addPropertyStarts(int src, UnicodeSet set) {
        CodePointTrie trie;
        switch (src) {
            case 12: {
                trie = InPCTrie.INSTANCE;
                break;
            }
            case 13: {
                trie = InSCTrie.INSTANCE;
                break;
            }
            case 14: {
                trie = VoTrie.INSTANCE;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        CodePointMap.Range range = new CodePointMap.Range();
        int start = 0;
        while (trie.getRange(start, null, range)) {
            set.add(start);
            start = range.getEnd() + 1;
        }
        return set;
    }

    static /* synthetic */ CodePointTrie access$000(String x0) {
        return UCharacterProperty.makeTrie(x0);
    }

    static {
        GC_CN_MASK = UCharacterProperty.getMask(0);
        GC_CC_MASK = UCharacterProperty.getMask(15);
        GC_CS_MASK = UCharacterProperty.getMask(18);
        GC_ZS_MASK = UCharacterProperty.getMask(12);
        GC_ZL_MASK = UCharacterProperty.getMask(13);
        GC_ZP_MASK = UCharacterProperty.getMask(14);
        GC_Z_MASK = GC_ZS_MASK | GC_ZL_MASK | GC_ZP_MASK;
        gcbToHst = new int[]{0, 0, 0, 0, 1, 0, 4, 5, 3, 2};
        try {
            INSTANCE = new UCharacterProperty();
        }
        catch (IOException e) {
            throw new MissingResourceException(e.getMessage(), "", "");
        }
    }

    private static final class IsAcceptable
    implements ICUBinary.Authenticate {
        private IsAcceptable() {
        }

        @Override
        public boolean isDataVersionAcceptable(byte[] version) {
            return version[0] == 7;
        }
    }

    private class NormQuickCheckIntProperty
    extends IntProperty {
        int which;
        int max;

        NormQuickCheckIntProperty(int source, int which, int max) {
            super(source);
            this.which = which;
            this.max = max;
        }

        @Override
        int getValue(int c) {
            return Norm2AllModes.getN2WithImpl(this.which - 4108).getQuickCheck(c);
        }

        @Override
        int getMaxValue(int which) {
            return this.max;
        }
    }

    private class CombiningClassIntProperty
    extends IntProperty {
        CombiningClassIntProperty(int source) {
            super(source);
        }

        @Override
        int getMaxValue(int which) {
            return 255;
        }
    }

    private class BiDiIntProperty
    extends IntProperty {
        BiDiIntProperty() {
            super(5);
        }

        @Override
        int getMaxValue(int which) {
            return UBiDiProps.INSTANCE.getMaxValue(which);
        }
    }

    private class IntProperty {
        int column;
        int mask;
        int shift;

        IntProperty(int column, int mask, int shift) {
            this.column = column;
            this.mask = mask;
            this.shift = shift;
        }

        IntProperty(int source) {
            this.column = source;
            this.mask = 0;
        }

        final int getSource() {
            return this.mask == 0 ? this.column : 2;
        }

        int getValue(int c) {
            return (UCharacterProperty.this.getAdditional(c, this.column) & this.mask) >>> this.shift;
        }

        int getMaxValue(int which) {
            return (UCharacterProperty.this.getMaxValues(this.column) & this.mask) >>> this.shift;
        }
    }

    private class NormInertBinaryProperty
    extends BinaryProperty {
        int which;

        NormInertBinaryProperty(int source, int which) {
            super(source);
            this.which = which;
        }

        @Override
        boolean contains(int c) {
            return Norm2AllModes.getN2WithImpl(this.which - 37).isInert(c);
        }
    }

    private class CaseBinaryProperty
    extends BinaryProperty {
        int which;

        CaseBinaryProperty(int which) {
            super(4);
            this.which = which;
        }

        @Override
        boolean contains(int c) {
            return UCaseProps.INSTANCE.hasBinaryProperty(c, this.which);
        }
    }

    private class BinaryProperty {
        int column;
        int mask;

        BinaryProperty(int column, int mask) {
            this.column = column;
            this.mask = mask;
        }

        BinaryProperty(int source) {
            this.column = source;
            this.mask = 0;
        }

        final int getSource() {
            return this.mask == 0 ? this.column : 2;
        }

        boolean contains(int c) {
            return (UCharacterProperty.this.getAdditional(c, this.column) & this.mask) != 0;
        }
    }

    private static final class VoTrie {
        static final CodePointTrie INSTANCE = UCharacterProperty.access$000("3irTBzL\u0004<\u0003\fzzz\u0080\bzz@zYz\u0098zzzzzzzzzzzzzzz\u00d0zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz;\u0003U\u0003c\u0003y\u0003\u0099\u0003\u00b7\u0003\u00d2\u0003\u00ec\u0003U\u0003U\u0003U\u0003\f\u0004U\u0003U\u0003U\u0003\f\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004,\u0004U\u0003U\u0003U\u0003\f\u0004U\u0003U\u0003U\u0003\f\u0004zz\u0010z z0z@zPz`zpzYzizyz\u0089z\u0098z\u00a8z\u00b8z\u00c8zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0z\u00d0z\u00e0z\u00f0zz\u0001zz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u00a9z\u0096z\u001e\u0001,\u0001\u00aez\u00aazzzzzzzzzzzzz\u0003\u0001<\u0001zzL\u0001X\u0001f\u0001\u000b\u0001u\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0084\u0001zzzzzzzzzzzzzzrzzz\u00f6zzzzzzzzzzzzzzzzzzzzzzzzz\u0090\u0001\u0010\u0001\u0098\u0001zzzzzzzz\u0003\u0001\u0010\u0001\u0015\u0001zz\u00ecz\u00a8\u0001\u00b6\u0001\u000e\u0001\u0010\u0001\u0010\u0001\u00c6\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0016\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0018\u0001\n\u0001\u0010\u0001\u00d2\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u000e\u0001\u0010\u0001zzzz\u0016\u0001zzzzzzzzzz\b\u0001\u0010\u0001\u00e2\u0001\u0014\u0001\u0010\u0001zzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u00f1\u0001\u00ff\u0001\u0010\u0001\u000e\u0002\u001d\u0002\u0010\u0001*\u0002\u0010\u00017\u0002F\u0002V\u0002\u0010\u0001*\u0002\u0010\u00017\u0002a\u0002\u0010\u0001\u0010\u0001n\u0002\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001~\u0002\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001~\u0002~\u0002~\u0002~\u0002~\u0002\u0086\u0002\u0010\u0001\u008e\u0002\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzz\u0010\u0001zz\u0010\u0001\u0017\u0001\u009b\u0002\u00aa\u0002zzzzzzzzzzzzzzzzzz\u00ba\u0002\u00c9\u0002\u0010\u0001\u00d9\u0002\u0010\u0001\u00e9\u0002\u00f8\u0002zzzzzzzzzzzzzz\b\u0003\u0018\u0003zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001zzzzzzzzzzzzzzzz(\u0003\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0012\u0001\u0084z\u0098z\u00a8z\u00a8z\u00a8z\u00a8z\u00a8z\u00a8z\u00c8z\fz\u00e8zz\u0001\u0015\u0001\fz\fz\fz4\u0001S\u0001r\u0001\u0091\u0001\fz\u00ab\u0001\fz\u00cb\u0001\u00eb\u0001\u000b\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002\u00fbz\fzC\u0002\fz#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002\fz\fz\fz\fz#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002\u00f8z\fzb\u0002\fz\fz\fz\fz\u0082\u0002\fz\fz\fz\fz\fz\u009c\u0002\fz\fz\u00fdz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz#\u0002#\u0002\u00b9\u0002\fz\fz\fz\fz\fz#\u0002z\u0001\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\u00bc\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002\u00f8z\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\u00da\u0002\u00f8z\fz\fz\fz\fz\fz\fz\fz\fz#\u0002\u00fa\u0002\fz\fz#\u0002\u00fdz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz#\u0002\u001a\u0003#\u0002#\u0002\u00c8z\u00b5\u0002\fz\fz#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002#\u0002\u001b\u0003\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fz\fzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0003z\u0003zzzz\u0003zz\u0003zzzzzzzzzz\u0003\u0003\u0003zzzzzzzzzzzzzzzzzzzzzzz\u0003zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0003zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0003\u0003zzzzzzzzzzzzzzzzzzzz\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003zzzzzzzzz\u0003\u0003zzz\u0003zzzz\u0003\u0003\u0003zzzzzz\u0003z\u0003\u0003\u0003zzzzzzzzzzz\u0003\u0003z\u0003\u0003\u0003\u0003\u0003\u0003\u0003zzzzz\u0003\u0003z\u0003\u0003zzzzzz\u0003\u0003\u0003\u0003z\u0003z\u0003z\u0003zzzz\u0003zzzzz\u0003\u0003\u0003\u0003\u0003\u0003z\u0003\u0003z\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003zz\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003zzzz\u0003\u0003\u0003\u0003\u0003\u0001\u0001\u0003zzzz\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003z\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003zzzz\u0003\u0003\u0003z\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003zzzzzzzzzzzz\u0003\u0003z\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0002\u0003\u0003\u0003\u0003\u0003\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0003\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0002\u0002\u0003\u0003\u0003\u0003\u0002\u0002\u0003\u0003\u0003\u0001\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0002\u0003\u0003\u0003\u0003\u0003\u0001\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0002\u0002\u0002\u0002\u0002\u0003\u0003\u0003\u0003\u0003z\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0003\u0003zzzz\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003z\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0001\u0001\u0003\u0003\u0002z\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0001\u0001zzz\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0001\u0001\u0001\u0001\u0001zzzzzzzzzzzzzzz\u0003\u0003\u0003\u0001\u0003\u0003\u0003\u0003zzzzzzzz\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003zzz\u0003\u0003zz\u0002\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003zzzz");

        private VoTrie() {
        }
    }

    private static final class InSCTrie {
        static final CodePointTrie INSTANCE = UCharacterProperty.access$000("3irTBzB\u0003x\u000f\u0004z@z\u0090zzz@z`z\u0094z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z\u00d4z\u0012\u0001R\u0001\u0090\u0001\u00cf\u0001\r\u0002L\u0002\u008a\u0002\u00ca\u0002\b\u0003F\u0003\u0084\u0003\u00c4\u0003\u0002\u0004A\u0004\u007f\u0004\u00bf\u0004\u00fd\u0004=\u0005}\u0005\u00bc\u0005\u00fc\u0005;\u0006{\u0006\u009b\u0006\u00db\u0006\u001b\u0007X\u0007\u00f8\u0002\u000b\u0003\u0017\u0003\u000b\u00032\u0003zz\u0010z z0z@zPz`zpz`zpz\u0080z\u0090z\u0094z\u00a4z\u00b4z\u00c4z@zPz`zpz@zPz`zpz@zPz`zpz@zPz`zpz@zPz`zpz@zPz`zpz@zPz`zpz@zPz`zpz\u00d4z\u00e4z\u00f4z\u0004\u0001\u0012\u0001\"\u00012\u0001B\u0001R\u0001b\u0001r\u0001\u0082\u0001\u0090\u0001\u00a0\u0001\u00b0\u0001\u00c0\u0001\u00cf\u0001\u00df\u0001\u00ef\u0001\u00ff\u0001\r\u0002\u001d\u0002-\u0002=\u0002L\u0002\\\u0002l\u0002|\u0002\u008a\u0002\u009a\u0002\u00aa\u0002\u00ba\u0002\u00ca\u0002\u00da\u0002\u00ea\u0002\u00fa\u0002\b\u0003\u0018\u0003(\u00038\u0003F\u0003V\u0003f\u0003v\u0003\u0084\u0003\u0094\u0003\u00a4\u0003\u00b4\u0003\u00c4\u0003\u00d4\u0003\u00e4\u0003\u00f4\u0003\u0002\u0004\u0012\u0004\"\u00042\u0004A\u0004Q\u0004a\u0004q\u0004\u007f\u0004\u008f\u0004\u009f\u0004\u00af\u0004\u00bf\u0004\u00cf\u0004\u00df\u0004\u00ef\u0004\u00fd\u0004\r\u0005\u001d\u0005-\u0005=\u0005M\u0005]\u0005m\u0005}\u0005\u008d\u0005\u009d\u0005\u00ad\u0005\u00bc\u0005\u00cc\u0005\u00dc\u0005\u00ec\u0005\u00fc\u0005\f\u0006\u001c\u0006,\u0006;\u0006K\u0006[\u0006k\u0006{\u0006\u008b\u0006\u009b\u0006\u00ab\u0006\u009b\u0006\u00ab\u0006\u00bb\u0006\u00cb\u0006\u00db\u0006\u00eb\u0006\u00fb\u0006\u000b\u0007\u001b\u0007+\u0007;\u0007K\u0007X\u0007h\u0007x\u0007\u0088\u0007\u00e9z\u00e9z\u0098\u0007\u00a3\u0007\u00b3\u0007\u00c3\u0007\u00d2\u0007\u00e1\u0007\u00ef\u0007\u00ff\u0007@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z\u000f\b\u001d\b\u00e6z\u001d\b\u00e6z-\b\u000f\b=\b\u00e9z\u00e9zM\bY\bc\br\b0z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z\u0082\bl\u0001\u0092\b\u00a2\b-\u0002\u00e9z\u00b2\b\u00c2\b\u00e9z\u00e9zt\u0003\u00d2\b\u00e1\b0z@z@z\u00e9z\u00f1\b\u00e9z\u00e9z\u0001\t\u000e\t\u001e\t*\t0z0z@z@z@z@z@z@z:\t\u00e6z\u00e9zJ\tV\t0z@z@zf\t\u00e9zu\t\u0085\t\u00e9z\u00e9z\u0095\t\u00a5\t\u00e9z\u00e9z\u00b5\t\u00c2\t\u00d2\t@z@z@z@z@z@z@z@z\u00e2\t\u00f0\t\u00fe\t@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z\b\n\u0014\n$\n@z@z@z@z@zZ\u00072\n@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@ztz@z@z@zB\n\u00e9zO\n@z\u00e9z_\nm\n|\n\u00d6z\u00e7z\u00e9z\u008c\n\u0098\n0z\u00a8\n\u00b6\n\u00c6\n\u00e9z\u00d4\n\u00e9z\u00e4\n\u00f3\n@z@z\u0003\u000b\u00e9z\u00e9z\u0012\u000b\u0097\u00020z\"\u000b2\u000b\u00e3z\u00e9z\u0089\bB\u000bR\u000b0z\u00e9za\u000b\u00e9z\u00e9z\u00e9zq\u000b\u0081\u000b@z\u0091\u000b\u00a1\u000b@z@z@z@z@z@z@z@z@z@z@z@z\u00b1\u000b\u00c1\u000b\u00ce\u000b0z\u00de\u000b\u00ee\u000b\u00e9z\u00f8\u000b1z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z\b\f\u00e6z\u00e9z\u008a\b\u0018\f&\f0\f@\fP\f\u00e9z\u00e9z`\f@z@z@z@zp\f\u00e9z\u008b\b\u0080\f\u0090\f\u00a0\f\u00e9z\u00ad\f\u00d5z\u00e8z\u00e9z\u00bd\f\u00cd\f0z\u00ba\u00065z\u00e1z\u00eb\u0003\u0086\b\u00dd\f@z@z@z@z\u00ed\fm\u0001\u00fc\f\u00dfz\u00e9z\f\r\u001c\r0z,\rb\u0001r\u0001<\r\b\u0003L\r\\\r\u00ed\t@z@z@z@z@z@z@z@z\u00dbz\u00e9z\u00e9zl\r\u0000\r\u008a\r@z@z\u0099\r\u00e9z\u00e9z\u001f\t\u00a9\r0z@z@z@z@z@z@z@z@z@z@z\u00dbz\u00e9z\u00ffz\u00b9\r\u00c9\r\u00d1\r@z@z\u00dbz\u00e9z\u00e9z\u00e1\r\u00f1\r0z@z@z\u00dfz\u00e9z\u0001\u000e\u000e\u000e0z@z@z@z\u00e9z\u001e\u000e.\u000e>\u000e@z@z@z@z@z@z@z@z@z@z@z@z\u00dfz\u00e9z\u0086\bN\u000e@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z^\u000e\u00e9z\u00e9zk\u000e{\u000e\u008b\u000e\u00e9z\u00e9z\u0097\u000e\u00a1\u000e@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z\u00b1\u000e\u00e9z\u00ffz\u00c1\u000e\u00d1\u000e\u00bb\u0006\u00e1\u000eU\u0005\u00e9z\u00ef\u000e+\u0007\u00ff\u000e@z@z@z@z\u000f\u000f\u00e9z\u00e9z\u001e\u000f.\u000f0z>\u000f\u00e9zJ\u000fW\u000f0z@z@z@z@z@z@z@z@z@z@z@z@z@z@z\u00e9zg\u000f@z@z@z@z@z@z@z@z@z@z@z@z@z@z@z@zEzUzUzUzez\u0085z\u00a5z\u00c5z\u00e5z\u0004z\u0004z\u00f5z\u0014\u00014\u0001T\u0001\u0004zt\u0001\u0004z}\u0001\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u009d\u0001\u00bd\u0001\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u0004z\u00dd\u0001\u0004z\u0004z\u00fd\u0001\u001d\u0002=\u0002]\u0002}\u0002\u009d\u0002\u00bd\u0002\u00d8\u0002zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\fzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\fzzzzzzzzzzzzzzzzz\u001c\u001czzzzzzzzzzzzzzzzzzzzzzz\fzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0002\u0002\u0002 #################\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\"\"\u0017\u0001\"\"\"\"\"\"\"\"\"\"\"\"\"\u001f\"\"z\u0004\u0004zz\"\"\"\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005##\"\"zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zz######\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\f\u0002\u0002 z########zz##zz##\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005zzz\u0005\u0005\u0005\u0005zz\u0017\u0001\"\"\"\"\"zz\"\"zz\"\"\u001f\u0006zzzzzzzz\"zzzz\u0005\u0005z\u0005##\"\"zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0005\u0005zzzzzzzzzz\u0002z\u001cz\u0002\u0002 z######zzzz##zz##\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005z\u0005\u0005z\u0005\u0005zz\u0017z\"\"\"zzzz\"\"zz\"\"\u001fzzz\u0004zzzzzzz\u0005\u0005\u0005\u0005z\u0005zzzzzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0002\u0012\f\fz\u000bzzzzzzzzzz\u0002\u0002 z#########z###z##\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005z\u0005\u0005\u0005\u0005\u0005zz\u0017\u0001\"\"\"\"\"\"z\"\"\"z\"\"\u001fzzzzzzzzzzzzzzzzzz##\"\"zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzzzzzzz\u0005\u0004\u0004\u0004\u0017\u0017\u0017z\u0002\u0002 z########zz##zz##\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005z\u0005\u0005\u0005\u0005\u0005zz\u0017\u0001\"\"\"\"\"zz\"\"zz\"\"\u001fzzzzzzzz\"\"zzzz\u0005\u0005z\u0005##\"\"zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018z\u0005zzzzzzzzzzzzzz\u0002\u0015z######zzz###z###\u0005zzz\u0005\u0005z\u0005z\u0005\u0005zzz\u0005\u0005zzz\u0005\u0005\u0005zzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005zzzz\"\"\"zzz\"\"\"z\"\"\"\u001fzzzzzzzzz\"zzzzzzzzzzzzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzzzzzzzzzzzzzz\u0002\u0002\u0002 \u0002########z###z###\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005zzz\u0001\"\"\"\"\"z\"\"\"z\"\"\"\u001fzzzzzzz\"\"z\u0005\u0005\u0005zzzzz##\"\"zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzzzzzzzzzzzzzz\u0002\u0002 z########z###z###\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005zz\u0017\u0001\"\"\"\"\"z\"\"\"z\"\"\"\u001fzzzzzzz\"\"zzzzzzz\u0005z##\"\"zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018z\u0011\u0011zzzzzzzzzzzzz\u0002\u0002\u0002 z########z###z###\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u001a\u001a\u0001\"\"\"\"\"z\"\"\"z\"\"\"\u001f\rzzzzz\u0006\u0006\u0006\"zzzzzzz###\"\"zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzzzzzzzz\u0006\u0006\u0006\u0006\u0006\u0006zz\u0002 z##################zzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005zz\u0005\u0005\u0005\u0005\u0005\u0005\u0005zzz\u001fzzzz\"\"\"\"\"\"z\"z\"\"\"\"\"\"\"\"zzzzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zz\"\"zzzzzzzzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\"\"\"\"\"\"\"\"\"\"\u001azzzzz\"\"\"\"\"\"z\"\u001e\u001e\u001e\u001e\n\u0002\u001az\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0005\u0005z\u0005zz\u0005\u0005z\u0005zz\u0005zzzzzz\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005z\u0005z\u0005zz\u0005\u0005z\u0005\u0005z\"\"\"\"\"\"\"\"\"\"z\"\u000b\u000bzz\"\"\"\"\"zzz\u001e\u001e\u001e\u001ez\u0002zz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zz\u0005\u0005\u0005\u0005zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018z\u001cz\u001cz\u0017zzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005zzzz\"\"\"\"\"\"\"\"\"\"\"\"\"\u0002 \"\"\u0002\u0002\u001a\u0001zz\b\b\b\b\b\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000fz\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000fzzzzzz\u001czzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0005##########\"\"\"\"\"\"\u0002\u001e \u0013\u001a\u000b\u000b\u000b\u000b\u0005\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018z\fzz\fz\u0005\u0005####\"\"\"\"\u0005\u0005\u0005\u0005\u000b\u000b\u0005\"\u001e\u001e\u0005\u0005\"\"\u001e\u001e\u001e\u001e\u001e\u0005\u0005\"\"\"\"\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u000b\"\"\"\"\u001e\u001e\u001e\u001e\u001e\u001e\u001e\u0005\u001e\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u001e\u001e\"\"zz###\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005z\u0005\u0005\"\"\u001azzzzzzzzzzz\u0005\u0005\"\"zzzzzzzzzzzz\u0005z\"\"zzzzzzzzzzzz\u0005\u0005\u0005#############zz\"\"\"\"\"\"\"\"\"\"\u0002 \"\u001b\u001b\u001c\u0010\n\u001c\u001c\u001a\u0013\u001czzzzzzzz\u0001\u001czz\f\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\"\"\"\"\"\"\"\"\"\u000f\u000f\u000fzzzz\u0007\u0007\u0002\u0007\u0007\u0007\u0007\u0007\u0007\u0007\"\u001czzzz\u0005\u0005\u0005!!!!!!!!!!!zz\u001d\u001d\u001d\u001d\u001dzzzzzzzzzzz\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u001e\u001ezzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\"\"\"\"\"zzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005###\u0005\u0005\u000b\u000b\u000f\u0007\u0007\t\u000f\u000f\u000f\u000fz\u0013\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\u0002\u001e\u001e\u001e\u001e\u001e\u001a\u001c\u001czz\u001c\u0002\u0002\u0002\u0010 ###########\u0005\u0005\u0005\u0005\u0017\"\"\"\"\"\"\"\"\"\"\"\u001f\u0005\u0005\u0005\u0005\u0005\u0005\u0005zzzz\u0002\u0010 #######\u0005\u0005\u0005\u0005\u0005\u0005\u000f\u000f\u000f\"\"\"\"\"\"\u001a\u0013\u000f\u000f\u0005\u0005\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0001\u0005\u0005\u0005\u0007\u0007\u0005\u0005\u0005\u0005##\u0017\"\"\"\"\"\"\"\"\"\u0007\u0007\u001a\u001azzzzzzzzzzzz\u0005\u0005\u0005\u0005\u000f\u000f\"\"\"\"\"\"\"\u0007\u0007\u0007\u0007\u0002\u0002\u001c\u0017zzzzzzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzz\u0005\u0005\u0005\u0004\u0004\u0004z\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004zzzzzzzzzzzzzz  \u0004\u0011\u0011\u0004\u0004\u0004zzzzzzzzzzz\u001czzzzzzzzzzzz\u0016\u0014zz\f\f\f\f\fzzzzzzzzzzz\u001c\u001c\u001czzzzzzzzzzz##z###\u001a\u0005\u0005\u0005\u0005\u0002\u0005\u0005\u0005\u0005\"\"\"\"\"zzzzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005!!\u0005\u0005\u0005\u0005!\u000f\u000f\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u000f\u0005\u0002zzzzzzzzzzzz\u0005\u0005\u0005\u0005\u000b\"\"\"\"\"\"\"\"\"\"\"\u001f\u0002zzzzzzzzzz\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0002\u0002zzzzzzzzzz#\"\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0005\u0005\u0005\u0005\u0005\u0005!!!!!!!!!\u001e\u001e\u001ezz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\"\"\"\"\"\"\"\"\u0007\u0007\u0007\u001azzzzzzzzzzzz\u0002\u0002\u0010 #####\u0005\u0005\u0005###\u0005\u0005\u0005\u0017\"\"\"\"\"\"\"\"\"\u000f\u000b\u000b\u0005\u0005\u0005\u0005\u0005\"z\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0005\u0005\u0005\u0005\u0005z\"\"\"\u000b\u000b\u000b\u000bzzzzzzzzz\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007zz\u0005\u0005\u0005\f\f\fzzz\u0005\u001e\u001e\u001e\u0005\u0005\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\u001e\u001d\u001e\u001dzzzzzzzzzzzzz##\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\"\"\"\"\"zzzzz \u0013zzzzzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005##\u0005#\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0007\u0007\u0007\u0007\u0007\"\"\"\"\"\"\"\"z\u001e\u001azz\u0005\"\"\"z\"\"zzzzz\"\"\u0002 \u0005\u0005\u0005\u0005z\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005zz\u0017\u0017\u0017zzzz\u0013\u0002\u0002 \u0011\u0011###########\"\"\"\"\"\"\u001fzzzzzzzzz\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzzzzzzzzzzzzz\u0019\u0002\u0002 ##########\u0005\u0005\u0005\"\"\"\"\"\"\"\"\"\u001f\u0017zzzzz\u0002\u0002 ####\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\"\"\"\u0013\u001az\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzz\u0005\"\"zzzzzzzzz!!!!!\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0017zzzzzzzzzzzz\u0005\u0005\u0005\"\"\"\"\"\"\"\"\"\"\"\"\"\u001f\u0001\u000e\u000ezzzzz\u001c\u0017\"\"zzz\"\"\"\"\u0002\u001f\u0017\u0012zzzzzz\u0004z####\u0005\u0005\u0005z\u0005z\u0005\u0005\u0005\u0005z\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005zzzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0002\"\"\"\"\"\"\"\"\"\u0017\u001azzzzz\u0002\u0002\u0002 z########zz#\u0005z\u0005\u0005z\u0005\u0005\u0005\u0005\u0005z\u0017\u0017\u0001\"\"zzzzzzz\"zzzzzz\u0002\u0002##\"\"zz\u0004\u0004\u0004\u0004\u0004\u0004\u0004zzz\u0005\u0005\u0005\u0005\u0005\"\"\"\"\"\"\"\"\"\"\"\u001f\u0002\u0002 \u0017\u0001zzzzzzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzz\u001cz##############\u0005\u0002 \u001f\u0017\u0001zzzzzzzzzzz\"\"\"\"\"\"zz\"\"\"\"\u0002\u0002 \u001f\u0017zzzzzzzzzzzzzzz####\"\"zz\"\"\"\"\"\"\"\"\"\"\"\"\"\u0002 \u001f\"zzzzzzzzzzzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0002 \"\"\"\"\"\"\u001f\u0017zzzzzzzz\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005zz\u000b\u000b\u000b\"\"\"\"\"\"\"\"\"\"\"\u001azzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzzz\"\"\"\"\"\"\"\u0002 \u001f\u0017zzzzz#\"\"\"\"\"\"\"\"\"\"\u0005\u0005\u0005\u0005\u0005\u001c\u001a\u0002\u0002\u0002\u0002 \u000e\u000b\u000b\u000b\u000b\fzzzzz\fz\u0013zzzzzzzz#\"\"\"\"\"\"\"\"\"\"\"\u0005\u0005\u0005\u0005zz\u000e\u000e\u000e\u000e\u0007\u0007\u0007\u0007\u0007\u0007\u0002 \u0012\u0013zzz\u0001zz#########z####\u0005\u0005\"\"\"\"\"\"\"z\"\"\"\"\u0002\u0002 \u001f\u0001zzzzzzzzzzzzzzz\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018\u0018zzz\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\u000f\"\"\"\"\"\u0002\u0002zzzzzzzzz#######z##z#\u0005\u0005\u0005\u0005\"\"\"\"\"\"zzz\"z\"\"z\"\u0002 \u0017\"\u001a\u0013\r\u000bzzzzzzzz######z##z##\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\u0005\"\"\"\"\"z\"\"\u0002 \u0013zzzzzzzz\u0005\u0005\f\"\"\"\"zzzzzzzzzz");

        private InSCTrie() {
        }
    }

    private static final class InPCTrie {
        static final CodePointTrie INSTANCE = UCharacterProperty.access$000("3irTBz\u00fd\u0002r\u000b\u0002zzz\u0090zzz@zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0080z\u00c0z\u00ffz?\u0001~\u0001\u00be\u0001~\u0001\u00fe\u0001>\u0002~\u0002\u00bc\u0002\u00fc\u0002<\u0003{\u0003>\u0002\u00bb\u0003\u00fb\u00039\u0004w\u0004\u00ad\u0004\u00e1\u0004!\u00051\u0005q\u0005\u0099\u0005\u00d9\u0005\u0019\u0006V\u0006\u00b7\u0002\u00c6\u0002\u00d2\u0002\u00c6\u0002\u00ed\u0002zz\u0010z z0z@zPz`zpzzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0zzz\u0010z z0z\u0080z\u0090z\u00a0z\u00b0z\u00c0z\u00d0z\u00e0z\u00f0z\u00ffz\u000f\u0001\u001f\u0001/\u0001?\u0001O\u0001_\u0001o\u0001~\u0001\u008e\u0001\u009e\u0001\u00ae\u0001\u00be\u0001\u00ce\u0001\u00de\u0001\u00ee\u0001~\u0001\u008e\u0001\u009e\u0001\u00ae\u0001\u00fe\u0001\u000e\u0002\u001e\u0002.\u0002>\u0002N\u0002^\u0002n\u0002~\u0002\u008e\u0002\u009e\u0002\u00ae\u0002\u00bc\u0002\u00cc\u0002\u00dc\u0002\u00ec\u0002\u00fc\u0002\f\u0003\u001c\u0003,\u0003<\u0003L\u0003\\\u0003l\u0003{\u0003\u008b\u0003\u009b\u0003\u00ab\u0003>\u0002N\u0002^\u0002n\u0002\u00bb\u0003\u00cb\u0003\u00db\u0003\u00eb\u0003\u00fb\u0003\u000b\u0004\u001b\u0004+\u00049\u0004I\u0004Y\u0004i\u0004w\u0004\u0087\u0004\u0097\u0004\u00a7\u0004\u00ad\u0004\u00bd\u0004\u00cd\u0004\u00dd\u0004\u00e1\u0004\u00f1\u0004\u0001\u0005\u0011\u0005!\u00051\u0005A\u0005Q\u00051\u0005A\u0005Q\u0005a\u0005q\u0005\u0081\u0005\u0091\u0005\u00a1\u0005\u0099\u0005\u00a9\u0005\u00b9\u0005\u00c9\u0005\u00d9\u0005\u00e9\u0005\u00f9\u0005\t\u0006\u0019\u0006)\u00069\u0006I\u0006V\u0006f\u0006v\u0006\u0086\u0006zzzz\u008b\u0006\u009a\u0006zz\u00a9\u0006\u00b8\u0006\u00c7\u0006\u00d5\u0006\u00e5\u0006zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u00f3\u0006zz\u00f3\u0006zz\u0001\u0007zz\u0001\u0007zzzzzz\u000b\u0007\u001b\u0007)\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz9\u0007I\u0007zzzzzzzzzzzzzzY\u0007h\u0007zzzzzzr\u0007zzzzzz~\u0007\u008d\u0007\u009b\u0007zzzzzzzzzzzzzzzz\u00ab\u0007zzzz\u00b7\u0007\u00c7\u0007zz\u00cc\u0007,\u0005\u0081zzz\u00dc\u0007zzzzzz\u00ea\u0007\u00fb\u0003zzzz\u00fa\u0007\u0007\bzzzzzzzzzzzzzzzzzz\u0017\b'\b5\bzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u00b3\u0002?\bzzL\bzzzzzzzzzz\u0001\u0001zzzzX\bd\bzzt\b\u0082\bzzzz\u0092\bzz\u00a0\b\u00fb\u0003zzzz\u0080zzzzz\u00b0\b\u00c0\bzz\u00b9\u0002zzzz\u00c7\b\u00d6\b\u00e3\bzzzz\u00f1\bzzzzzz\u0001\t\u00bd\u0002zz\u0011\tQ\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzz!\tzz0\tzzzz@\tzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzP\tzzzzX\tf\tzzzzzz\u0081zzzzzv\tzzzzzzzz-\u0005zz\u0081\t\u0091\t\u00cb\u0003zzzzY\u0006\u0081zzzzz\u009e\t\u00ae\tzzzzzz\u00bb\t\u00cb\tzzzzzzzzzzzzzzzzzzqz\u00db\tzz\u00ffzzzzz\u00e6\t\u00f6\tO\u0001\u0004\n+\u0005zzzzzzzzzzzzzzzz\u009c\t\u0014\no\u0001zzzzzzzzzz$\n3\nzzzzzzzzzzzzzzzzzzzzzzzzzz\u00eb\u0002C\n\u00e3z\u0014\u0002zzzzzzS\n\u00be\u0002zzzzzzzzzzc\ns\nzzzzzzzzzz{\n\u008b\nzzzzzzzzzzzzzzzzzzzzzzzzzz\u0097\n\u00a6\nzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u00b5\nzzzz\u00c2\nzz\u00d1\nzzzz\u00dd\n\u00e7\nzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u00eb\u0002\u00f7\nzzzzzzzzzz\u0007\u000b\u000f\u000b\u001e\u000bzzzzzzzzzzzzzz-\u000b<\u000bzzzzzzD\u000bT\u000bzzzzzzzzzzzzzzzzzzzzzzzzzzzzzza\u000bzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzEzMzMzMz]z}z\u009dz\u00bdz\u00ddz\u0002z\u0002z\u00ecz\n\u0001)\u0001I\u0001\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002zi\u0001\u0088\u0001\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u0002z\u00a8\u0001\u0002z\u0002z\u00c8\u0001\u00e6\u0001\u0003\u0002!\u0002?\u0002_\u0002}\u0002\u0097\u0002zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\b\b\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\u0007\u0001z\u0007\u0004\u0007\u0001\u0001\u0001\u0001\b\b\b\b\u0007\u0007\u0007\u0007\u0001\u0004\u0007z\b\u0001\b\b\b\u0001\u0001zzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\u0007\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0001z\u0007\u0004\u0007\u0001\u0001\u0001\u0001zz\u0004\u0004zz\u0005\u0005\u0001zzzzzzzzz\u0007zzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzzzzzz\bz\b\b\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0001z\u0007\u0004\u0007\u0001\u0001zzzz\b\bzz\b\b\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\bzzz\u0001zzzzzzzzzz\u0007\u0001\u0001\u0001\u0001\bz\b\b\rz\u0007\u0007\u0001zzzzzzzzzzzzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzz\b\b\b\b\b\bz\b\u0007\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0001z\u0007\b\u0007\u0001\u0001\u0001\u0001zz\u0004\u000bzz\u0005\f\u0001zzzzzzzz\b\rzzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzz\bzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0007\u0007\b\u0007\u0007zzz\u0004\u0004\u0004z\u0005\u0005\u0005\bzzzzzzzzz\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\u0007\u0007\u0007\bzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\b\u0007\u0007\u0007\u0007z\b\b\tz\b\b\b\bzzzzzzz\b\u0001zzzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzz\r\u0007\u0007\u0007\u0007z\b\r\rz\r\r\b\bzzzzzzz\u0007\u0007zzzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\b\u0007\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\b\bz\u0007\u0007\u0007\u0001\u0001z\u0004\u0004\u0004z\u0005\u0005\u0005\bzzzzzzzzz\u0007zzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0007\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\bzzzz\u0007\u0007\u0007\b\b\u0001z\u0001z\u0007\u0004\u000b\u0004\u0005\f\u0005\u0007zzzzzzzzzzzzzzzzzz\u0007\u0007zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0007\b\u0007\u0007\b\b\b\b\u0001\u0001\u0001zzzzz\u000e\u000e\u000e\u000e\u000e\u0007z\b\b\b\b\b\b\b\bzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0007\b\u0007\u0007\b\b\b\b\u0001\u0001z\b\u0001zzz\u000e\u000e\u000e\u000e\u000ezzz\b\b\b\b\b\bzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0001\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzz\u0001z\u0001z\bzzzz\u0007\u0004zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0001\b\t\u0001\u0001\t\t\t\t\b\b\b\b\b\u0007\b\t\b\b\u0001z\b\bzzzzz\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001z\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001zzzzzz\u0001zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz\u0007\u0007\b\b\u0001\u0004\b\b\b\b\b\u0001\u0007z\b\u0007z\u0001\u0001zzzzzz\u0007\u0007\u0001\u0001zzzz\u0001\u0001z\u0007\u0007\u0007zz\u0007\u0007\u0007\u0007\u0007\u0007\u0007zz\b\b\b\bzzzzzzzzzzz\u0001\u0007\u0004\b\b\u0007\u0007\u0007\u0007\u0007\u0007\u0001z\u0007zzzzzzzzzz\u0007\u0007\u0007\bzz\b\u0001\u0001zzzzzzzzzzz\b\u0001zzzzzzzzzzzz\u0007\b\b\b\b\u0001\u0001\u0001\u000b\f\u0005\u0004\u0004\u0004\u0005\u0005\b\u0007\u0007\b\b\b\b\b\b\bz\bzzzzzzzzz\bzz\b\b\u0001\u0007\u0007\r\r\b\b\u0007\u0007\u0007zzzz\u0007\u0007\u0001\u0007\u0007\u0007\u0007\u0007\u0007\u0001\b\u0001zzzz\u0007\u0007\u0007\u0007\u0007\u000e\u000e\u000e\u0007\u0007\u000e\u0007\u0007\u0007\u0007\u0007zzzzzzz\u0007\u0007zzzzzzz\b\u0001\u0004\u0007\bzzzzz\u0004\u0001\u0007\b\b\b\u0001\u0001\u0001\u0001z\u0007\b\u0007\u0007\b\b\b\b\u0001\u0001\b\u0001\u0007\u0004\u0004\u0004\b\b\b\b\b\b\b\b\b\bzz\u0001\b\b\b\b\u0007zzzzzzzzzzz\b\u0007\b\b\u0001\u0001\u0001\u0003\t\n\u0004\u0004\u0005\u0005\b\r\u0007zzzzzzzzzzz\b\u0001\b\b\bz\u0007\u0001\u0001\b\u0001\u0004\u0007\b\b\u0007z\u0001\u0001zzzzzz\b\u0007\b\b\u0007\u0007\u0007\b\u0007\bzzzz\u0007\u0007\u0007\u0004\u0004\u000b\u0007\u0007\u0001\b\b\b\b\u0004\u0004\b\u0001zzzzzzzz\b\b\bz\u0006\u0001\u0001\u0001\u0001\u0001\b\b\u0001\u0001\u0001\u0001\b\u0007\u0006\u0006\u0006\u0006\u0006\u0006\u0006zzzz\u0001zzzz\bzz\u0007zzzzzzzz\bzzzz\bzzzz\u0007\u0007\u0001\b\u0007zzzzzzzz\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0007\u0001\bzzzzzzzzzz\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\bzzzzzzzzzzzzz\bzzzzzzzzzzz\u0001\u0001\u0001zzzzzzz\u0001\u0001\u0001\b\u0001\u0001\u0001\u0001\bzzz\b\u0007\u0007\b\b\u0001\u0001\u0004\u0004\b\u0007\u0007\u0002\u0003zzzzzzzzzzzzzzz\b\b\b\b\u0001\b\u0004\b\u0001\u0007\u0004\u0001\u0001zzzzzzzzz\bzzzzzzzz\b\u0007zzzzzzzzzzz\u0007\b\u0007zz\b\u0007\b\b\u0001\u000e\u000e\b\b\u000e\u0007\u000e\u000e\u0007\b\bzzzzzzzzzzz\u0004\u0001\b\u0004\u0007zzz\u0007\u0007\b\u0007\u0007\u0001\u0007\u0007z\u0007\u0001zz\u0006\u0001\u0001z\b\u0006zzzzz\u0001\u0001\u0001\bzzzzzzzz\b\u0001\u0001zzzzz\u0007\b\u0007zzzzzzzzzzzzz\b\b\b\b\u0001\u0001\u0001\u0001\b\b\b\b\bzzzzzzzzz\u0007\u0004\u0007\u0001\u0001\b\b\u0007\u0007\u0001\u0001zzzzzzz\b\b\b\u0001\u0001\u0004\b\t\t\b\u0001\u0001z\bzzzzzzzzzzz\u0007\u0004\u0007\u0001\u0001\u0001\u0001\u0001\u0001\b\b\b\r\u0007zzzzzzzz\u0001z\b\u0001zzzzzzzzzzzz\u0007\u0007\u0007\u0001\b\b\r\r\b\u0007\b\bzzzzzz\bz\u0007\u0004\u0007\u0001\u0001\b\b\b\b\u0001\u0001zzzzzzzzzzz\u0001\u0001z\u0007\u0007\b\u0007\u0007\u0007\u0007zz\u0004\u0004zz\u0005\u0005\u0007zz\u0007\u0007zz\b\b\b\b\b\b\bzzz\u0007\u0007\u0001\b\b\u0007\u0001zzzzzzzzz\u0007\u0004\u0007\u0001\u0001\u0001\u0001\u0001\u0001\u0004\b\u000b\u0005\u0007\u0005\b\u0007\u0001\u0001zzzzzzzzzzzz\u0004\u0007\u0001\u0001\u0001\u0001zz\u0004\u000b\u0005\f\b\b\u0007\u0001\u0007\u0007\u0007\u0001\u0001\u0001\u0001\u0001\u0001\b\b\u0007\u0007\b\u0007\u0001zzzzzzzzzzz\b\u0007\b\u0004\u0007\u0001\u0001\b\b\b\b\u0007\u0001zzzzzzzzzzzzz\u0001z\b\u0007\u0007\b\b\u0001\u0001\u0004\b\u0001\b\b\bzzzzzzzzzzzz\u0007\u0004\u0007\u0001\u0001\u0001\b\b\b\b\b\u0007\u0001\u0001zzzzz\b\u0001\u0001\b\b\b\b\b\b\u0001zzzzz\u0001\u0001\b\b\b\b\u0007z\u0001\u0001\u0001\u0001z\b\u0001\u0001\b\b\b\u0007\u0007\u0001\u0001\u0001zzzzzzzzzz\u0001\u0001\u0001\u0001\u0001\u0001\b\u0007\bzzzzzzz\b\b\u0001\u0001\u0001\u0001\u0001z\b\b\b\b\b\b\u0007\u0001zz\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001z\u0007\u0001\u0001\u0001\u0001\u0001\u0001\u0004\u0001\b\u0007\b\bzzzzzzzzz\b\b\b\b\b\u0001zzz\bz\b\bz\b\b\u0001\b\u0001zz\u0001zzzzzzzzzz\u0007\u0007\u0007\u0007\u0007z\b\bz\u0007\u0007\b\u0007zzzzzzzzz\b\u0001\u0004\u0007zzzzzzzzzz");

        private InPCTrie() {
        }
    }
}

