/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

public class Java2GroovyConverter
extends VisitorAdapter {
    private int[] typeMapping = new int[400];

    public Java2GroovyConverter(String[] tokenNames) {
        this.typeMapping[39] = 39;
        this.typeMapping[1] = 1;
        this.typeMapping[3] = 3;
        this.typeMapping[4] = 4;
        this.typeMapping[5] = 5;
        this.typeMapping[6] = 6;
        this.typeMapping[7] = 7;
        this.typeMapping[8] = 8;
        this.typeMapping[9] = 9;
        this.typeMapping[10] = 10;
        this.typeMapping[11] = 11;
        this.typeMapping[12] = 12;
        this.typeMapping[13] = 13;
        this.typeMapping[14] = 14;
        this.typeMapping[15] = 16;
        this.typeMapping[16] = 17;
        this.typeMapping[17] = 18;
        this.typeMapping[18] = 19;
        this.typeMapping[19] = 20;
        this.typeMapping[20] = 21;
        this.typeMapping[21] = 22;
        this.typeMapping[22] = 23;
        this.typeMapping[23] = 24;
        this.typeMapping[24] = 25;
        this.typeMapping[25] = 26;
        this.typeMapping[26] = 27;
        this.typeMapping[27] = 28;
        this.typeMapping[28] = 57;
        this.typeMapping[29] = 29;
        this.typeMapping[30] = 30;
        this.typeMapping[31] = 31;
        this.typeMapping[32] = 32;
        this.typeMapping[33] = 33;
        this.typeMapping[34] = 34;
        this.typeMapping[35] = 35;
        this.typeMapping[36] = 36;
        this.typeMapping[37] = 37;
        this.typeMapping[38] = 38;
        this.typeMapping[39] = 39;
        this.typeMapping[40] = 43;
        this.typeMapping[41] = 44;
        this.typeMapping[42] = 45;
        this.typeMapping[43] = 47;
        this.typeMapping[44] = 60;
        this.typeMapping[45] = 61;
        this.typeMapping[46] = 62;
        this.typeMapping[47] = 63;
        this.typeMapping[48] = 64;
        this.typeMapping[49] = 65;
        this.typeMapping[50] = 66;
        this.typeMapping[51] = 67;
        this.typeMapping[52] = 68;
        this.typeMapping[53] = 69;
        this.typeMapping[54] = 70;
        this.typeMapping[55] = 71;
        this.typeMapping[56] = 72;
        this.typeMapping[57] = 73;
        this.typeMapping[58] = 74;
        this.typeMapping[59] = 75;
        this.typeMapping[60] = 76;
        this.typeMapping[61] = 81;
        this.typeMapping[62] = 128;
        this.typeMapping[63] = 82;
        this.typeMapping[64] = 83;
        this.typeMapping[65] = 85;
        this.typeMapping[66] = 86;
        this.typeMapping[67] = 87;
        this.typeMapping[68] = 90;
        this.typeMapping[69] = 97;
        this.typeMapping[70] = 98;
        this.typeMapping[71] = 99;
        this.typeMapping[72] = 89;
        this.typeMapping[74] = 101;
        this.typeMapping[73] = 100;
        this.typeMapping[75] = 102;
        this.typeMapping[76] = 103;
        this.typeMapping[77] = 104;
        this.typeMapping[78] = 105;
        this.typeMapping[79] = 106;
        this.typeMapping[80] = 107;
        this.typeMapping[81] = 108;
        this.typeMapping[82] = 109;
        this.typeMapping[83] = 110;
        this.typeMapping[84] = 111;
        this.typeMapping[85] = 112;
        this.typeMapping[86] = 113;
        this.typeMapping[87] = 115;
        this.typeMapping[88] = 116;
        this.typeMapping[89] = 117;
        this.typeMapping[90] = 118;
        this.typeMapping[91] = 119;
        this.typeMapping[92] = 120;
        this.typeMapping[93] = 121;
        this.typeMapping[94] = 122;
        this.typeMapping[95] = 96;
        this.typeMapping[96] = 91;
        this.typeMapping[97] = 123;
        this.typeMapping[98] = 124;
        this.typeMapping[99] = 126;
        this.typeMapping[100] = 127;
        this.typeMapping[101] = 92;
        this.typeMapping[102] = 93;
        this.typeMapping[103] = 94;
        this.typeMapping[104] = 125;
        this.typeMapping[105] = 129;
        this.typeMapping[106] = 131;
        this.typeMapping[107] = 132;
        this.typeMapping[108] = 130;
        this.typeMapping[109] = 133;
        this.typeMapping[110] = 136;
        this.typeMapping[111] = 137;
        this.typeMapping[112] = 138;
        this.typeMapping[113] = 139;
        this.typeMapping[114] = 139;
        this.typeMapping[115] = 144;
        this.typeMapping[116] = 145;
        this.typeMapping[117] = 143;
        this.typeMapping[118] = 140;
        this.typeMapping[119] = 146;
        this.typeMapping[120] = 147;
        this.typeMapping[121] = 141;
        this.typeMapping[122] = 150;
        this.typeMapping[123] = 151;
        this.typeMapping[124] = 152;
        this.typeMapping[125] = 153;
        this.typeMapping[126] = 162;
        this.typeMapping[127] = 163;
        this.typeMapping[128] = 164;
        this.typeMapping[129] = 165;
        this.typeMapping[130] = 166;
        this.typeMapping[131] = 167;
        this.typeMapping[132] = 168;
        this.typeMapping[133] = 169;
        this.typeMapping[134] = 170;
        this.typeMapping[135] = 171;
        this.typeMapping[136] = 172;
        this.typeMapping[137] = 175;
        this.typeMapping[138] = 176;
        this.typeMapping[139] = 134;
        this.typeMapping[140] = 177;
        this.typeMapping[141] = 180;
        this.typeMapping[142] = 181;
        this.typeMapping[143] = 185;
        this.typeMapping[144] = 186;
        this.typeMapping[145] = 158;
        this.typeMapping[146] = 187;
        this.typeMapping[147] = 148;
        this.typeMapping[148] = 149;
        this.typeMapping[149] = 191;
        this.typeMapping[150] = 192;
        this.typeMapping[151] = 190;
        this.typeMapping[152] = 193;
        this.typeMapping[153] = 195;
        this.typeMapping[154] = 196;
        this.typeMapping[155] = 161;
        this.typeMapping[156] = 157;
        this.typeMapping[157] = 160;
        this.typeMapping[158] = 159;
        this.typeMapping[159] = 199;
        this.typeMapping[160] = 88;
        this.typeMapping[161] = 88;
        this.typeMapping[162] = 200;
        this.typeMapping[163] = 201;
        this.typeMapping[164] = 202;
        this.typeMapping[165] = 207;
        this.typeMapping[166] = 209;
        this.typeMapping[167] = 210;
        this.typeMapping[168] = 220;
        this.typeMapping[169] = 222;
        this.typeMapping[170] = 223;
        this.typeMapping[171] = 228;
        this.typeMapping[172] = 229;
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        if (visit == 1) {
            String text;
            t.setType(this.typeMapping[t.getType()]);
            if (t.getType() == 88 && (Java2GroovyConverter.isSingleQuoted(text = t.getText()) || Java2GroovyConverter.isDoubleQuoted(text))) {
                t.setText(text.substring(1, text.length() - 1));
            }
        }
    }

    private static boolean isSingleQuoted(String text) {
        return text != null && text.length() > 2 && text.charAt(0) == '\'' && text.charAt(text.length() - 1) == '\'';
    }

    private static boolean isDoubleQuoted(String text) {
        return text != null && text.length() > 2 && text.charAt(0) == '\"' && text.charAt(text.length() - 1) == '\"';
    }
}

