/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.ArrayUtil
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.ArrayUtil;

public final class ASCIIFoldingFilter
extends TokenFilter {
    private char[] output = new char[512];
    private int outputPos;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public ASCIIFoldingFilter(TokenStream input) {
        super(input);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            for (int i = 0; i < length; ++i) {
                char c = buffer[i];
                if (c < '\u0080') continue;
                this.foldToASCII(buffer, length);
                this.termAtt.copyBuffer(this.output, 0, this.outputPos);
                break;
            }
            return true;
        }
        return false;
    }

    public void foldToASCII(char[] input, int length) {
        int maxSizeNeeded = 4 * length;
        if (this.output.length < maxSizeNeeded) {
            this.output = new char[ArrayUtil.oversize((int)maxSizeNeeded, (int)2)];
        }
        this.outputPos = ASCIIFoldingFilter.foldToASCII(input, 0, this.output, 0, length);
    }

    public static final int foldToASCII(char[] input, int inputPos, char[] output, int outputPos, int length) {
        int end = inputPos + length;
        block231: for (int pos = inputPos; pos < end; ++pos) {
            char c = input[pos];
            if (c < '\u0080') {
                output[outputPos++] = c;
                continue;
            }
            switch (c) {
                case '\u00c0': 
                case '\u00c1': 
                case '\u00c2': 
                case '\u00c3': 
                case '\u00c4': 
                case '\u00c5': 
                case '\u0100': 
                case '\u0102': 
                case '\u0104': 
                case '\u018f': 
                case '\u01cd': 
                case '\u01de': 
                case '\u01e0': 
                case '\u01fa': 
                case '\u0200': 
                case '\u0202': 
                case '\u0226': 
                case '\u023a': 
                case '\u1d00': 
                case '\u1e00': 
                case '\u1ea0': 
                case '\u1ea2': 
                case '\u1ea4': 
                case '\u1ea6': 
                case '\u1ea8': 
                case '\u1eaa': 
                case '\u1eac': 
                case '\u1eae': 
                case '\u1eb0': 
                case '\u1eb2': 
                case '\u1eb4': 
                case '\u1eb6': 
                case '\u24b6': 
                case '\uff21': {
                    output[outputPos++] = 65;
                    continue block231;
                }
                case '\u00e0': 
                case '\u00e1': 
                case '\u00e2': 
                case '\u00e3': 
                case '\u00e4': 
                case '\u00e5': 
                case '\u0101': 
                case '\u0103': 
                case '\u0105': 
                case '\u01ce': 
                case '\u01df': 
                case '\u01e1': 
                case '\u01fb': 
                case '\u0201': 
                case '\u0203': 
                case '\u0227': 
                case '\u0250': 
                case '\u0259': 
                case '\u025a': 
                case '\u1d8f': 
                case '\u1d95': 
                case '\u1e01': 
                case '\u1e9a': 
                case '\u1ea1': 
                case '\u1ea3': 
                case '\u1ea5': 
                case '\u1ea7': 
                case '\u1ea9': 
                case '\u1eab': 
                case '\u1ead': 
                case '\u1eaf': 
                case '\u1eb1': 
                case '\u1eb3': 
                case '\u1eb5': 
                case '\u1eb7': 
                case '\u2090': 
                case '\u2094': 
                case '\u24d0': 
                case '\u2c65': 
                case '\u2c6f': 
                case '\uff41': {
                    output[outputPos++] = 97;
                    continue block231;
                }
                case '\ua732': {
                    output[outputPos++] = 65;
                    output[outputPos++] = 65;
                    continue block231;
                }
                case '\u00c6': 
                case '\u01e2': 
                case '\u01fc': 
                case '\u1d01': {
                    output[outputPos++] = 65;
                    output[outputPos++] = 69;
                    continue block231;
                }
                case '\ua734': {
                    output[outputPos++] = 65;
                    output[outputPos++] = 79;
                    continue block231;
                }
                case '\ua736': {
                    output[outputPos++] = 65;
                    output[outputPos++] = 85;
                    continue block231;
                }
                case '\ua738': 
                case '\ua73a': {
                    output[outputPos++] = 65;
                    output[outputPos++] = 86;
                    continue block231;
                }
                case '\ua73c': {
                    output[outputPos++] = 65;
                    output[outputPos++] = 89;
                    continue block231;
                }
                case '\u249c': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 97;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\ua733': {
                    output[outputPos++] = 97;
                    output[outputPos++] = 97;
                    continue block231;
                }
                case '\u00e6': 
                case '\u01e3': 
                case '\u01fd': 
                case '\u1d02': {
                    output[outputPos++] = 97;
                    output[outputPos++] = 101;
                    continue block231;
                }
                case '\ua735': {
                    output[outputPos++] = 97;
                    output[outputPos++] = 111;
                    continue block231;
                }
                case '\ua737': {
                    output[outputPos++] = 97;
                    output[outputPos++] = 117;
                    continue block231;
                }
                case '\ua739': 
                case '\ua73b': {
                    output[outputPos++] = 97;
                    output[outputPos++] = 118;
                    continue block231;
                }
                case '\ua73d': {
                    output[outputPos++] = 97;
                    output[outputPos++] = 121;
                    continue block231;
                }
                case '\u0181': 
                case '\u0182': 
                case '\u0243': 
                case '\u0299': 
                case '\u1d03': 
                case '\u1e02': 
                case '\u1e04': 
                case '\u1e06': 
                case '\u24b7': 
                case '\uff22': {
                    output[outputPos++] = 66;
                    continue block231;
                }
                case '\u0180': 
                case '\u0183': 
                case '\u0253': 
                case '\u1d6c': 
                case '\u1d80': 
                case '\u1e03': 
                case '\u1e05': 
                case '\u1e07': 
                case '\u24d1': 
                case '\uff42': {
                    output[outputPos++] = 98;
                    continue block231;
                }
                case '\u249d': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 98;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00c7': 
                case '\u0106': 
                case '\u0108': 
                case '\u010a': 
                case '\u010c': 
                case '\u0187': 
                case '\u023b': 
                case '\u0297': 
                case '\u1d04': 
                case '\u1e08': 
                case '\u24b8': 
                case '\uff23': {
                    output[outputPos++] = 67;
                    continue block231;
                }
                case '\u00e7': 
                case '\u0107': 
                case '\u0109': 
                case '\u010b': 
                case '\u010d': 
                case '\u0188': 
                case '\u023c': 
                case '\u0255': 
                case '\u1e09': 
                case '\u2184': 
                case '\u24d2': 
                case '\ua73e': 
                case '\ua73f': 
                case '\uff43': {
                    output[outputPos++] = 99;
                    continue block231;
                }
                case '\u249e': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 99;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00d0': 
                case '\u010e': 
                case '\u0110': 
                case '\u0189': 
                case '\u018a': 
                case '\u018b': 
                case '\u1d05': 
                case '\u1d06': 
                case '\u1e0a': 
                case '\u1e0c': 
                case '\u1e0e': 
                case '\u1e10': 
                case '\u1e12': 
                case '\u24b9': 
                case '\ua779': 
                case '\uff24': {
                    output[outputPos++] = 68;
                    continue block231;
                }
                case '\u00f0': 
                case '\u010f': 
                case '\u0111': 
                case '\u018c': 
                case '\u0221': 
                case '\u0256': 
                case '\u0257': 
                case '\u1d6d': 
                case '\u1d81': 
                case '\u1d91': 
                case '\u1e0b': 
                case '\u1e0d': 
                case '\u1e0f': 
                case '\u1e11': 
                case '\u1e13': 
                case '\u24d3': 
                case '\ua77a': 
                case '\uff44': {
                    output[outputPos++] = 100;
                    continue block231;
                }
                case '\u01c4': 
                case '\u01f1': {
                    output[outputPos++] = 68;
                    output[outputPos++] = 90;
                    continue block231;
                }
                case '\u01c5': 
                case '\u01f2': {
                    output[outputPos++] = 68;
                    output[outputPos++] = 122;
                    continue block231;
                }
                case '\u249f': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 100;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0238': {
                    output[outputPos++] = 100;
                    output[outputPos++] = 98;
                    continue block231;
                }
                case '\u01c6': 
                case '\u01f3': 
                case '\u02a3': 
                case '\u02a5': {
                    output[outputPos++] = 100;
                    output[outputPos++] = 122;
                    continue block231;
                }
                case '\u00c8': 
                case '\u00c9': 
                case '\u00ca': 
                case '\u00cb': 
                case '\u0112': 
                case '\u0114': 
                case '\u0116': 
                case '\u0118': 
                case '\u011a': 
                case '\u018e': 
                case '\u0190': 
                case '\u0204': 
                case '\u0206': 
                case '\u0228': 
                case '\u0246': 
                case '\u1d07': 
                case '\u1e14': 
                case '\u1e16': 
                case '\u1e18': 
                case '\u1e1a': 
                case '\u1e1c': 
                case '\u1eb8': 
                case '\u1eba': 
                case '\u1ebc': 
                case '\u1ebe': 
                case '\u1ec0': 
                case '\u1ec2': 
                case '\u1ec4': 
                case '\u1ec6': 
                case '\u24ba': 
                case '\u2c7b': 
                case '\uff25': {
                    output[outputPos++] = 69;
                    continue block231;
                }
                case '\u00e8': 
                case '\u00e9': 
                case '\u00ea': 
                case '\u00eb': 
                case '\u0113': 
                case '\u0115': 
                case '\u0117': 
                case '\u0119': 
                case '\u011b': 
                case '\u01dd': 
                case '\u0205': 
                case '\u0207': 
                case '\u0229': 
                case '\u0247': 
                case '\u0258': 
                case '\u025b': 
                case '\u025c': 
                case '\u025d': 
                case '\u025e': 
                case '\u029a': 
                case '\u1d08': 
                case '\u1d92': 
                case '\u1d93': 
                case '\u1d94': 
                case '\u1e15': 
                case '\u1e17': 
                case '\u1e19': 
                case '\u1e1b': 
                case '\u1e1d': 
                case '\u1eb9': 
                case '\u1ebb': 
                case '\u1ebd': 
                case '\u1ebf': 
                case '\u1ec1': 
                case '\u1ec3': 
                case '\u1ec5': 
                case '\u1ec7': 
                case '\u2091': 
                case '\u24d4': 
                case '\u2c78': 
                case '\uff45': {
                    output[outputPos++] = 101;
                    continue block231;
                }
                case '\u24a0': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 101;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0191': 
                case '\u1e1e': 
                case '\u24bb': 
                case '\ua730': 
                case '\ua77b': 
                case '\ua7fb': 
                case '\uff26': {
                    output[outputPos++] = 70;
                    continue block231;
                }
                case '\u0192': 
                case '\u1d6e': 
                case '\u1d82': 
                case '\u1e1f': 
                case '\u1e9b': 
                case '\u24d5': 
                case '\ua77c': 
                case '\uff46': {
                    output[outputPos++] = 102;
                    continue block231;
                }
                case '\u24a1': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 102;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\ufb00': {
                    output[outputPos++] = 102;
                    output[outputPos++] = 102;
                    continue block231;
                }
                case '\ufb03': {
                    output[outputPos++] = 102;
                    output[outputPos++] = 102;
                    output[outputPos++] = 105;
                    continue block231;
                }
                case '\ufb04': {
                    output[outputPos++] = 102;
                    output[outputPos++] = 102;
                    output[outputPos++] = 108;
                    continue block231;
                }
                case '\ufb01': {
                    output[outputPos++] = 102;
                    output[outputPos++] = 105;
                    continue block231;
                }
                case '\ufb02': {
                    output[outputPos++] = 102;
                    output[outputPos++] = 108;
                    continue block231;
                }
                case '\u011c': 
                case '\u011e': 
                case '\u0120': 
                case '\u0122': 
                case '\u0193': 
                case '\u01e4': 
                case '\u01e5': 
                case '\u01e6': 
                case '\u01e7': 
                case '\u01f4': 
                case '\u0262': 
                case '\u029b': 
                case '\u1e20': 
                case '\u24bc': 
                case '\ua77d': 
                case '\ua77e': 
                case '\uff27': {
                    output[outputPos++] = 71;
                    continue block231;
                }
                case '\u011d': 
                case '\u011f': 
                case '\u0121': 
                case '\u0123': 
                case '\u01f5': 
                case '\u0260': 
                case '\u0261': 
                case '\u1d77': 
                case '\u1d79': 
                case '\u1d83': 
                case '\u1e21': 
                case '\u24d6': 
                case '\ua77f': 
                case '\uff47': {
                    output[outputPos++] = 103;
                    continue block231;
                }
                case '\u24a2': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 103;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0124': 
                case '\u0126': 
                case '\u021e': 
                case '\u029c': 
                case '\u1e22': 
                case '\u1e24': 
                case '\u1e26': 
                case '\u1e28': 
                case '\u1e2a': 
                case '\u24bd': 
                case '\u2c67': 
                case '\u2c75': 
                case '\uff28': {
                    output[outputPos++] = 72;
                    continue block231;
                }
                case '\u0125': 
                case '\u0127': 
                case '\u021f': 
                case '\u0265': 
                case '\u0266': 
                case '\u02ae': 
                case '\u02af': 
                case '\u1e23': 
                case '\u1e25': 
                case '\u1e27': 
                case '\u1e29': 
                case '\u1e2b': 
                case '\u1e96': 
                case '\u24d7': 
                case '\u2c68': 
                case '\u2c76': 
                case '\uff48': {
                    output[outputPos++] = 104;
                    continue block231;
                }
                case '\u01f6': {
                    output[outputPos++] = 72;
                    output[outputPos++] = 86;
                    continue block231;
                }
                case '\u24a3': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 104;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0195': {
                    output[outputPos++] = 104;
                    output[outputPos++] = 118;
                    continue block231;
                }
                case '\u00cc': 
                case '\u00cd': 
                case '\u00ce': 
                case '\u00cf': 
                case '\u0128': 
                case '\u012a': 
                case '\u012c': 
                case '\u012e': 
                case '\u0130': 
                case '\u0196': 
                case '\u0197': 
                case '\u01cf': 
                case '\u0208': 
                case '\u020a': 
                case '\u026a': 
                case '\u1d7b': 
                case '\u1e2c': 
                case '\u1e2e': 
                case '\u1ec8': 
                case '\u1eca': 
                case '\u24be': 
                case '\ua7fe': 
                case '\uff29': {
                    output[outputPos++] = 73;
                    continue block231;
                }
                case '\u00ec': 
                case '\u00ed': 
                case '\u00ee': 
                case '\u00ef': 
                case '\u0129': 
                case '\u012b': 
                case '\u012d': 
                case '\u012f': 
                case '\u0131': 
                case '\u01d0': 
                case '\u0209': 
                case '\u020b': 
                case '\u0268': 
                case '\u1d09': 
                case '\u1d62': 
                case '\u1d7c': 
                case '\u1d96': 
                case '\u1e2d': 
                case '\u1e2f': 
                case '\u1ec9': 
                case '\u1ecb': 
                case '\u2071': 
                case '\u24d8': 
                case '\uff49': {
                    output[outputPos++] = 105;
                    continue block231;
                }
                case '\u0132': {
                    output[outputPos++] = 73;
                    output[outputPos++] = 74;
                    continue block231;
                }
                case '\u24a4': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 105;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0133': {
                    output[outputPos++] = 105;
                    output[outputPos++] = 106;
                    continue block231;
                }
                case '\u0134': 
                case '\u0248': 
                case '\u1d0a': 
                case '\u24bf': 
                case '\uff2a': {
                    output[outputPos++] = 74;
                    continue block231;
                }
                case '\u0135': 
                case '\u01f0': 
                case '\u0237': 
                case '\u0249': 
                case '\u025f': 
                case '\u0284': 
                case '\u029d': 
                case '\u24d9': 
                case '\u2c7c': 
                case '\uff4a': {
                    output[outputPos++] = 106;
                    continue block231;
                }
                case '\u24a5': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 106;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0136': 
                case '\u0198': 
                case '\u01e8': 
                case '\u1d0b': 
                case '\u1e30': 
                case '\u1e32': 
                case '\u1e34': 
                case '\u24c0': 
                case '\u2c69': 
                case '\ua740': 
                case '\ua742': 
                case '\ua744': 
                case '\uff2b': {
                    output[outputPos++] = 75;
                    continue block231;
                }
                case '\u0137': 
                case '\u0199': 
                case '\u01e9': 
                case '\u029e': 
                case '\u1d84': 
                case '\u1e31': 
                case '\u1e33': 
                case '\u1e35': 
                case '\u24da': 
                case '\u2c6a': 
                case '\ua741': 
                case '\ua743': 
                case '\ua745': 
                case '\uff4b': {
                    output[outputPos++] = 107;
                    continue block231;
                }
                case '\u24a6': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 107;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0139': 
                case '\u013b': 
                case '\u013d': 
                case '\u013f': 
                case '\u0141': 
                case '\u023d': 
                case '\u029f': 
                case '\u1d0c': 
                case '\u1e36': 
                case '\u1e38': 
                case '\u1e3a': 
                case '\u1e3c': 
                case '\u24c1': 
                case '\u2c60': 
                case '\u2c62': 
                case '\ua746': 
                case '\ua748': 
                case '\ua780': 
                case '\uff2c': {
                    output[outputPos++] = 76;
                    continue block231;
                }
                case '\u013a': 
                case '\u013c': 
                case '\u013e': 
                case '\u0140': 
                case '\u0142': 
                case '\u019a': 
                case '\u0234': 
                case '\u026b': 
                case '\u026c': 
                case '\u026d': 
                case '\u1d85': 
                case '\u1e37': 
                case '\u1e39': 
                case '\u1e3b': 
                case '\u1e3d': 
                case '\u24db': 
                case '\u2c61': 
                case '\ua747': 
                case '\ua749': 
                case '\ua781': 
                case '\uff4c': {
                    output[outputPos++] = 108;
                    continue block231;
                }
                case '\u01c7': {
                    output[outputPos++] = 76;
                    output[outputPos++] = 74;
                    continue block231;
                }
                case '\u1efa': {
                    output[outputPos++] = 76;
                    output[outputPos++] = 76;
                    continue block231;
                }
                case '\u01c8': {
                    output[outputPos++] = 76;
                    output[outputPos++] = 106;
                    continue block231;
                }
                case '\u24a7': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 108;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u01c9': {
                    output[outputPos++] = 108;
                    output[outputPos++] = 106;
                    continue block231;
                }
                case '\u1efb': {
                    output[outputPos++] = 108;
                    output[outputPos++] = 108;
                    continue block231;
                }
                case '\u02aa': {
                    output[outputPos++] = 108;
                    output[outputPos++] = 115;
                    continue block231;
                }
                case '\u02ab': {
                    output[outputPos++] = 108;
                    output[outputPos++] = 122;
                    continue block231;
                }
                case '\u019c': 
                case '\u1d0d': 
                case '\u1e3e': 
                case '\u1e40': 
                case '\u1e42': 
                case '\u24c2': 
                case '\u2c6e': 
                case '\ua7fd': 
                case '\ua7ff': 
                case '\uff2d': {
                    output[outputPos++] = 77;
                    continue block231;
                }
                case '\u026f': 
                case '\u0270': 
                case '\u0271': 
                case '\u1d6f': 
                case '\u1d86': 
                case '\u1e3f': 
                case '\u1e41': 
                case '\u1e43': 
                case '\u24dc': 
                case '\uff4d': {
                    output[outputPos++] = 109;
                    continue block231;
                }
                case '\u24a8': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 109;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00d1': 
                case '\u0143': 
                case '\u0145': 
                case '\u0147': 
                case '\u014a': 
                case '\u019d': 
                case '\u01f8': 
                case '\u0220': 
                case '\u0274': 
                case '\u1d0e': 
                case '\u1e44': 
                case '\u1e46': 
                case '\u1e48': 
                case '\u1e4a': 
                case '\u24c3': 
                case '\uff2e': {
                    output[outputPos++] = 78;
                    continue block231;
                }
                case '\u00f1': 
                case '\u0144': 
                case '\u0146': 
                case '\u0148': 
                case '\u0149': 
                case '\u014b': 
                case '\u019e': 
                case '\u01f9': 
                case '\u0235': 
                case '\u0272': 
                case '\u0273': 
                case '\u1d70': 
                case '\u1d87': 
                case '\u1e45': 
                case '\u1e47': 
                case '\u1e49': 
                case '\u1e4b': 
                case '\u207f': 
                case '\u24dd': 
                case '\uff4e': {
                    output[outputPos++] = 110;
                    continue block231;
                }
                case '\u01ca': {
                    output[outputPos++] = 78;
                    output[outputPos++] = 74;
                    continue block231;
                }
                case '\u01cb': {
                    output[outputPos++] = 78;
                    output[outputPos++] = 106;
                    continue block231;
                }
                case '\u24a9': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 110;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u01cc': {
                    output[outputPos++] = 110;
                    output[outputPos++] = 106;
                    continue block231;
                }
                case '\u00d2': 
                case '\u00d3': 
                case '\u00d4': 
                case '\u00d5': 
                case '\u00d6': 
                case '\u00d8': 
                case '\u014c': 
                case '\u014e': 
                case '\u0150': 
                case '\u0186': 
                case '\u019f': 
                case '\u01a0': 
                case '\u01d1': 
                case '\u01ea': 
                case '\u01ec': 
                case '\u01fe': 
                case '\u020c': 
                case '\u020e': 
                case '\u022a': 
                case '\u022c': 
                case '\u022e': 
                case '\u0230': 
                case '\u1d0f': 
                case '\u1d10': 
                case '\u1e4c': 
                case '\u1e4e': 
                case '\u1e50': 
                case '\u1e52': 
                case '\u1ecc': 
                case '\u1ece': 
                case '\u1ed0': 
                case '\u1ed2': 
                case '\u1ed4': 
                case '\u1ed6': 
                case '\u1ed8': 
                case '\u1eda': 
                case '\u1edc': 
                case '\u1ede': 
                case '\u1ee0': 
                case '\u1ee2': 
                case '\u24c4': 
                case '\ua74a': 
                case '\ua74c': 
                case '\uff2f': {
                    output[outputPos++] = 79;
                    continue block231;
                }
                case '\u00f2': 
                case '\u00f3': 
                case '\u00f4': 
                case '\u00f5': 
                case '\u00f6': 
                case '\u00f8': 
                case '\u014d': 
                case '\u014f': 
                case '\u0151': 
                case '\u01a1': 
                case '\u01d2': 
                case '\u01eb': 
                case '\u01ed': 
                case '\u01ff': 
                case '\u020d': 
                case '\u020f': 
                case '\u022b': 
                case '\u022d': 
                case '\u022f': 
                case '\u0231': 
                case '\u0254': 
                case '\u0275': 
                case '\u1d16': 
                case '\u1d17': 
                case '\u1d97': 
                case '\u1e4d': 
                case '\u1e4f': 
                case '\u1e51': 
                case '\u1e53': 
                case '\u1ecd': 
                case '\u1ecf': 
                case '\u1ed1': 
                case '\u1ed3': 
                case '\u1ed5': 
                case '\u1ed7': 
                case '\u1ed9': 
                case '\u1edb': 
                case '\u1edd': 
                case '\u1edf': 
                case '\u1ee1': 
                case '\u1ee3': 
                case '\u2092': 
                case '\u24de': 
                case '\u2c7a': 
                case '\ua74b': 
                case '\ua74d': 
                case '\uff4f': {
                    output[outputPos++] = 111;
                    continue block231;
                }
                case '\u0152': 
                case '\u0276': {
                    output[outputPos++] = 79;
                    output[outputPos++] = 69;
                    continue block231;
                }
                case '\ua74e': {
                    output[outputPos++] = 79;
                    output[outputPos++] = 79;
                    continue block231;
                }
                case '\u0222': 
                case '\u1d15': {
                    output[outputPos++] = 79;
                    output[outputPos++] = 85;
                    continue block231;
                }
                case '\u24aa': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 111;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0153': 
                case '\u1d14': {
                    output[outputPos++] = 111;
                    output[outputPos++] = 101;
                    continue block231;
                }
                case '\ua74f': {
                    output[outputPos++] = 111;
                    output[outputPos++] = 111;
                    continue block231;
                }
                case '\u0223': {
                    output[outputPos++] = 111;
                    output[outputPos++] = 117;
                    continue block231;
                }
                case '\u01a4': 
                case '\u1d18': 
                case '\u1e54': 
                case '\u1e56': 
                case '\u24c5': 
                case '\u2c63': 
                case '\ua750': 
                case '\ua752': 
                case '\ua754': 
                case '\uff30': {
                    output[outputPos++] = 80;
                    continue block231;
                }
                case '\u01a5': 
                case '\u1d71': 
                case '\u1d7d': 
                case '\u1d88': 
                case '\u1e55': 
                case '\u1e57': 
                case '\u24df': 
                case '\ua751': 
                case '\ua753': 
                case '\ua755': 
                case '\ua7fc': 
                case '\uff50': {
                    output[outputPos++] = 112;
                    continue block231;
                }
                case '\u24ab': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 112;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u024a': 
                case '\u24c6': 
                case '\ua756': 
                case '\ua758': 
                case '\uff31': {
                    output[outputPos++] = 81;
                    continue block231;
                }
                case '\u0138': 
                case '\u024b': 
                case '\u02a0': 
                case '\u24e0': 
                case '\ua757': 
                case '\ua759': 
                case '\uff51': {
                    output[outputPos++] = 113;
                    continue block231;
                }
                case '\u24ac': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 113;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0239': {
                    output[outputPos++] = 113;
                    output[outputPos++] = 112;
                    continue block231;
                }
                case '\u0154': 
                case '\u0156': 
                case '\u0158': 
                case '\u0210': 
                case '\u0212': 
                case '\u024c': 
                case '\u0280': 
                case '\u0281': 
                case '\u1d19': 
                case '\u1d1a': 
                case '\u1e58': 
                case '\u1e5a': 
                case '\u1e5c': 
                case '\u1e5e': 
                case '\u24c7': 
                case '\u2c64': 
                case '\ua75a': 
                case '\ua782': 
                case '\uff32': {
                    output[outputPos++] = 82;
                    continue block231;
                }
                case '\u0155': 
                case '\u0157': 
                case '\u0159': 
                case '\u0211': 
                case '\u0213': 
                case '\u024d': 
                case '\u027c': 
                case '\u027d': 
                case '\u027e': 
                case '\u027f': 
                case '\u1d63': 
                case '\u1d72': 
                case '\u1d73': 
                case '\u1d89': 
                case '\u1e59': 
                case '\u1e5b': 
                case '\u1e5d': 
                case '\u1e5f': 
                case '\u24e1': 
                case '\ua75b': 
                case '\ua783': 
                case '\uff52': {
                    output[outputPos++] = 114;
                    continue block231;
                }
                case '\u24ad': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 114;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u015a': 
                case '\u015c': 
                case '\u015e': 
                case '\u0160': 
                case '\u0218': 
                case '\u1e60': 
                case '\u1e62': 
                case '\u1e64': 
                case '\u1e66': 
                case '\u1e68': 
                case '\u24c8': 
                case '\ua731': 
                case '\ua785': 
                case '\uff33': {
                    output[outputPos++] = 83;
                    continue block231;
                }
                case '\u015b': 
                case '\u015d': 
                case '\u015f': 
                case '\u0161': 
                case '\u017f': 
                case '\u0219': 
                case '\u023f': 
                case '\u0282': 
                case '\u1d74': 
                case '\u1d8a': 
                case '\u1e61': 
                case '\u1e63': 
                case '\u1e65': 
                case '\u1e67': 
                case '\u1e69': 
                case '\u1e9c': 
                case '\u1e9d': 
                case '\u24e2': 
                case '\ua784': 
                case '\uff53': {
                    output[outputPos++] = 115;
                    continue block231;
                }
                case '\u1e9e': {
                    output[outputPos++] = 83;
                    output[outputPos++] = 83;
                    continue block231;
                }
                case '\u24ae': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 115;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00df': {
                    output[outputPos++] = 115;
                    output[outputPos++] = 115;
                    continue block231;
                }
                case '\ufb06': {
                    output[outputPos++] = 115;
                    output[outputPos++] = 116;
                    continue block231;
                }
                case '\u0162': 
                case '\u0164': 
                case '\u0166': 
                case '\u01ac': 
                case '\u01ae': 
                case '\u021a': 
                case '\u023e': 
                case '\u1d1b': 
                case '\u1e6a': 
                case '\u1e6c': 
                case '\u1e6e': 
                case '\u1e70': 
                case '\u24c9': 
                case '\ua786': 
                case '\uff34': {
                    output[outputPos++] = 84;
                    continue block231;
                }
                case '\u0163': 
                case '\u0165': 
                case '\u0167': 
                case '\u01ab': 
                case '\u01ad': 
                case '\u021b': 
                case '\u0236': 
                case '\u0287': 
                case '\u0288': 
                case '\u1d75': 
                case '\u1e6b': 
                case '\u1e6d': 
                case '\u1e6f': 
                case '\u1e71': 
                case '\u1e97': 
                case '\u24e3': 
                case '\u2c66': 
                case '\uff54': {
                    output[outputPos++] = 116;
                    continue block231;
                }
                case '\u00de': 
                case '\ua766': {
                    output[outputPos++] = 84;
                    output[outputPos++] = 72;
                    continue block231;
                }
                case '\ua728': {
                    output[outputPos++] = 84;
                    output[outputPos++] = 90;
                    continue block231;
                }
                case '\u24af': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 116;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u02a8': {
                    output[outputPos++] = 116;
                    output[outputPos++] = 99;
                    continue block231;
                }
                case '\u00fe': 
                case '\u1d7a': 
                case '\ua767': {
                    output[outputPos++] = 116;
                    output[outputPos++] = 104;
                    continue block231;
                }
                case '\u02a6': {
                    output[outputPos++] = 116;
                    output[outputPos++] = 115;
                    continue block231;
                }
                case '\ua729': {
                    output[outputPos++] = 116;
                    output[outputPos++] = 122;
                    continue block231;
                }
                case '\u00d9': 
                case '\u00da': 
                case '\u00db': 
                case '\u00dc': 
                case '\u0168': 
                case '\u016a': 
                case '\u016c': 
                case '\u016e': 
                case '\u0170': 
                case '\u0172': 
                case '\u01af': 
                case '\u01d3': 
                case '\u01d5': 
                case '\u01d7': 
                case '\u01d9': 
                case '\u01db': 
                case '\u0214': 
                case '\u0216': 
                case '\u0244': 
                case '\u1d1c': 
                case '\u1d7e': 
                case '\u1e72': 
                case '\u1e74': 
                case '\u1e76': 
                case '\u1e78': 
                case '\u1e7a': 
                case '\u1ee4': 
                case '\u1ee6': 
                case '\u1ee8': 
                case '\u1eea': 
                case '\u1eec': 
                case '\u1eee': 
                case '\u1ef0': 
                case '\u24ca': 
                case '\uff35': {
                    output[outputPos++] = 85;
                    continue block231;
                }
                case '\u00f9': 
                case '\u00fa': 
                case '\u00fb': 
                case '\u00fc': 
                case '\u0169': 
                case '\u016b': 
                case '\u016d': 
                case '\u016f': 
                case '\u0171': 
                case '\u0173': 
                case '\u01b0': 
                case '\u01d4': 
                case '\u01d6': 
                case '\u01d8': 
                case '\u01da': 
                case '\u01dc': 
                case '\u0215': 
                case '\u0217': 
                case '\u0289': 
                case '\u1d64': 
                case '\u1d99': 
                case '\u1e73': 
                case '\u1e75': 
                case '\u1e77': 
                case '\u1e79': 
                case '\u1e7b': 
                case '\u1ee5': 
                case '\u1ee7': 
                case '\u1ee9': 
                case '\u1eeb': 
                case '\u1eed': 
                case '\u1eef': 
                case '\u1ef1': 
                case '\u24e4': 
                case '\uff55': {
                    output[outputPos++] = 117;
                    continue block231;
                }
                case '\u24b0': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 117;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u1d6b': {
                    output[outputPos++] = 117;
                    output[outputPos++] = 101;
                    continue block231;
                }
                case '\u01b2': 
                case '\u0245': 
                case '\u1d20': 
                case '\u1e7c': 
                case '\u1e7e': 
                case '\u1efc': 
                case '\u24cb': 
                case '\ua75e': 
                case '\ua768': 
                case '\uff36': {
                    output[outputPos++] = 86;
                    continue block231;
                }
                case '\u028b': 
                case '\u028c': 
                case '\u1d65': 
                case '\u1d8c': 
                case '\u1e7d': 
                case '\u1e7f': 
                case '\u24e5': 
                case '\u2c71': 
                case '\u2c74': 
                case '\ua75f': 
                case '\uff56': {
                    output[outputPos++] = 118;
                    continue block231;
                }
                case '\ua760': {
                    output[outputPos++] = 86;
                    output[outputPos++] = 89;
                    continue block231;
                }
                case '\u24b1': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 118;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\ua761': {
                    output[outputPos++] = 118;
                    output[outputPos++] = 121;
                    continue block231;
                }
                case '\u0174': 
                case '\u01f7': 
                case '\u1d21': 
                case '\u1e80': 
                case '\u1e82': 
                case '\u1e84': 
                case '\u1e86': 
                case '\u1e88': 
                case '\u24cc': 
                case '\u2c72': 
                case '\uff37': {
                    output[outputPos++] = 87;
                    continue block231;
                }
                case '\u0175': 
                case '\u01bf': 
                case '\u028d': 
                case '\u1e81': 
                case '\u1e83': 
                case '\u1e85': 
                case '\u1e87': 
                case '\u1e89': 
                case '\u1e98': 
                case '\u24e6': 
                case '\u2c73': 
                case '\uff57': {
                    output[outputPos++] = 119;
                    continue block231;
                }
                case '\u24b2': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 119;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u1e8a': 
                case '\u1e8c': 
                case '\u24cd': 
                case '\uff38': {
                    output[outputPos++] = 88;
                    continue block231;
                }
                case '\u1d8d': 
                case '\u1e8b': 
                case '\u1e8d': 
                case '\u2093': 
                case '\u24e7': 
                case '\uff58': {
                    output[outputPos++] = 120;
                    continue block231;
                }
                case '\u24b3': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 120;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00dd': 
                case '\u0176': 
                case '\u0178': 
                case '\u01b3': 
                case '\u0232': 
                case '\u024e': 
                case '\u028f': 
                case '\u1e8e': 
                case '\u1ef2': 
                case '\u1ef4': 
                case '\u1ef6': 
                case '\u1ef8': 
                case '\u1efe': 
                case '\u24ce': 
                case '\uff39': {
                    output[outputPos++] = 89;
                    continue block231;
                }
                case '\u00fd': 
                case '\u00ff': 
                case '\u0177': 
                case '\u01b4': 
                case '\u0233': 
                case '\u024f': 
                case '\u028e': 
                case '\u1e8f': 
                case '\u1e99': 
                case '\u1ef3': 
                case '\u1ef5': 
                case '\u1ef7': 
                case '\u1ef9': 
                case '\u1eff': 
                case '\u24e8': 
                case '\uff59': {
                    output[outputPos++] = 121;
                    continue block231;
                }
                case '\u24b4': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 121;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u0179': 
                case '\u017b': 
                case '\u017d': 
                case '\u01b5': 
                case '\u021c': 
                case '\u0224': 
                case '\u1d22': 
                case '\u1e90': 
                case '\u1e92': 
                case '\u1e94': 
                case '\u24cf': 
                case '\u2c6b': 
                case '\ua762': 
                case '\uff3a': {
                    output[outputPos++] = 90;
                    continue block231;
                }
                case '\u017a': 
                case '\u017c': 
                case '\u017e': 
                case '\u01b6': 
                case '\u021d': 
                case '\u0225': 
                case '\u0240': 
                case '\u0290': 
                case '\u0291': 
                case '\u1d76': 
                case '\u1d8e': 
                case '\u1e91': 
                case '\u1e93': 
                case '\u1e95': 
                case '\u24e9': 
                case '\u2c6c': 
                case '\ua763': 
                case '\uff5a': {
                    output[outputPos++] = 122;
                    continue block231;
                }
                case '\u24b5': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 122;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2070': 
                case '\u2080': 
                case '\u24ea': 
                case '\u24ff': 
                case '\uff10': {
                    output[outputPos++] = 48;
                    continue block231;
                }
                case '\u00b9': 
                case '\u2081': 
                case '\u2460': 
                case '\u24f5': 
                case '\u2776': 
                case '\u2780': 
                case '\u278a': 
                case '\uff11': {
                    output[outputPos++] = 49;
                    continue block231;
                }
                case '\u2488': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2474': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00b2': 
                case '\u2082': 
                case '\u2461': 
                case '\u24f6': 
                case '\u2777': 
                case '\u2781': 
                case '\u278b': 
                case '\uff12': {
                    output[outputPos++] = 50;
                    continue block231;
                }
                case '\u2489': {
                    output[outputPos++] = 50;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2475': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 50;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00b3': 
                case '\u2083': 
                case '\u2462': 
                case '\u24f7': 
                case '\u2778': 
                case '\u2782': 
                case '\u278c': 
                case '\uff13': {
                    output[outputPos++] = 51;
                    continue block231;
                }
                case '\u248a': {
                    output[outputPos++] = 51;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2476': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 51;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2074': 
                case '\u2084': 
                case '\u2463': 
                case '\u24f8': 
                case '\u2779': 
                case '\u2783': 
                case '\u278d': 
                case '\uff14': {
                    output[outputPos++] = 52;
                    continue block231;
                }
                case '\u248b': {
                    output[outputPos++] = 52;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2477': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 52;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2075': 
                case '\u2085': 
                case '\u2464': 
                case '\u24f9': 
                case '\u277a': 
                case '\u2784': 
                case '\u278e': 
                case '\uff15': {
                    output[outputPos++] = 53;
                    continue block231;
                }
                case '\u248c': {
                    output[outputPos++] = 53;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2478': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 53;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2076': 
                case '\u2086': 
                case '\u2465': 
                case '\u24fa': 
                case '\u277b': 
                case '\u2785': 
                case '\u278f': 
                case '\uff16': {
                    output[outputPos++] = 54;
                    continue block231;
                }
                case '\u248d': {
                    output[outputPos++] = 54;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2479': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 54;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2077': 
                case '\u2087': 
                case '\u2466': 
                case '\u24fb': 
                case '\u277c': 
                case '\u2786': 
                case '\u2790': 
                case '\uff17': {
                    output[outputPos++] = 55;
                    continue block231;
                }
                case '\u248e': {
                    output[outputPos++] = 55;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u247a': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 55;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2078': 
                case '\u2088': 
                case '\u2467': 
                case '\u24fc': 
                case '\u277d': 
                case '\u2787': 
                case '\u2791': 
                case '\uff18': {
                    output[outputPos++] = 56;
                    continue block231;
                }
                case '\u248f': {
                    output[outputPos++] = 56;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u247b': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 56;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2079': 
                case '\u2089': 
                case '\u2468': 
                case '\u24fd': 
                case '\u277e': 
                case '\u2788': 
                case '\u2792': 
                case '\uff19': {
                    output[outputPos++] = 57;
                    continue block231;
                }
                case '\u2490': {
                    output[outputPos++] = 57;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u247c': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 57;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2469': 
                case '\u24fe': 
                case '\u277f': 
                case '\u2789': 
                case '\u2793': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 48;
                    continue block231;
                }
                case '\u2491': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 48;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u247d': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 48;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u246a': 
                case '\u24eb': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 49;
                    continue block231;
                }
                case '\u2492': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 49;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u247e': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 49;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u246b': 
                case '\u24ec': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 50;
                    continue block231;
                }
                case '\u2493': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 50;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u247f': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 50;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u246c': 
                case '\u24ed': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 51;
                    continue block231;
                }
                case '\u2494': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 51;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2480': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 51;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u246d': 
                case '\u24ee': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 52;
                    continue block231;
                }
                case '\u2495': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 52;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2481': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 52;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u246e': 
                case '\u24ef': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 53;
                    continue block231;
                }
                case '\u2496': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 53;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2482': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 53;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u246f': 
                case '\u24f0': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 54;
                    continue block231;
                }
                case '\u2497': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 54;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2483': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 54;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2470': 
                case '\u24f1': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 55;
                    continue block231;
                }
                case '\u2498': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 55;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2484': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 55;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2471': 
                case '\u24f2': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 56;
                    continue block231;
                }
                case '\u2499': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 56;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2485': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 56;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2472': 
                case '\u24f3': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 57;
                    continue block231;
                }
                case '\u249a': {
                    output[outputPos++] = 49;
                    output[outputPos++] = 57;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2486': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 49;
                    output[outputPos++] = 57;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2473': 
                case '\u24f4': {
                    output[outputPos++] = 50;
                    output[outputPos++] = 48;
                    continue block231;
                }
                case '\u249b': {
                    output[outputPos++] = 50;
                    output[outputPos++] = 48;
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2487': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 50;
                    output[outputPos++] = 48;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u00ab': 
                case '\u00bb': 
                case '\u201c': 
                case '\u201d': 
                case '\u201e': 
                case '\u2033': 
                case '\u2036': 
                case '\u275d': 
                case '\u275e': 
                case '\u276e': 
                case '\u276f': 
                case '\uff02': {
                    output[outputPos++] = 34;
                    continue block231;
                }
                case '\u2018': 
                case '\u2019': 
                case '\u201a': 
                case '\u201b': 
                case '\u2032': 
                case '\u2035': 
                case '\u2039': 
                case '\u203a': 
                case '\u275b': 
                case '\u275c': 
                case '\uff07': {
                    output[outputPos++] = 39;
                    continue block231;
                }
                case '\u2010': 
                case '\u2011': 
                case '\u2012': 
                case '\u2013': 
                case '\u2014': 
                case '\u207b': 
                case '\u208b': 
                case '\uff0d': {
                    output[outputPos++] = 45;
                    continue block231;
                }
                case '\u2045': 
                case '\u2772': 
                case '\uff3b': {
                    output[outputPos++] = 91;
                    continue block231;
                }
                case '\u2046': 
                case '\u2773': 
                case '\uff3d': {
                    output[outputPos++] = 93;
                    continue block231;
                }
                case '\u207d': 
                case '\u208d': 
                case '\u2768': 
                case '\u276a': 
                case '\uff08': {
                    output[outputPos++] = 40;
                    continue block231;
                }
                case '\u2e28': {
                    output[outputPos++] = 40;
                    output[outputPos++] = 40;
                    continue block231;
                }
                case '\u207e': 
                case '\u208e': 
                case '\u2769': 
                case '\u276b': 
                case '\uff09': {
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u2e29': {
                    output[outputPos++] = 41;
                    output[outputPos++] = 41;
                    continue block231;
                }
                case '\u276c': 
                case '\u2770': 
                case '\uff1c': {
                    output[outputPos++] = 60;
                    continue block231;
                }
                case '\u276d': 
                case '\u2771': 
                case '\uff1e': {
                    output[outputPos++] = 62;
                    continue block231;
                }
                case '\u2774': 
                case '\uff5b': {
                    output[outputPos++] = 123;
                    continue block231;
                }
                case '\u2775': 
                case '\uff5d': {
                    output[outputPos++] = 125;
                    continue block231;
                }
                case '\u207a': 
                case '\u208a': 
                case '\uff0b': {
                    output[outputPos++] = 43;
                    continue block231;
                }
                case '\u207c': 
                case '\u208c': 
                case '\uff1d': {
                    output[outputPos++] = 61;
                    continue block231;
                }
                case '\uff01': {
                    output[outputPos++] = 33;
                    continue block231;
                }
                case '\u203c': {
                    output[outputPos++] = 33;
                    output[outputPos++] = 33;
                    continue block231;
                }
                case '\u2049': {
                    output[outputPos++] = 33;
                    output[outputPos++] = 63;
                    continue block231;
                }
                case '\uff03': {
                    output[outputPos++] = 35;
                    continue block231;
                }
                case '\uff04': {
                    output[outputPos++] = 36;
                    continue block231;
                }
                case '\u2052': 
                case '\uff05': {
                    output[outputPos++] = 37;
                    continue block231;
                }
                case '\uff06': {
                    output[outputPos++] = 38;
                    continue block231;
                }
                case '\u204e': 
                case '\uff0a': {
                    output[outputPos++] = 42;
                    continue block231;
                }
                case '\uff0c': {
                    output[outputPos++] = 44;
                    continue block231;
                }
                case '\uff0e': {
                    output[outputPos++] = 46;
                    continue block231;
                }
                case '\u2044': 
                case '\uff0f': {
                    output[outputPos++] = 47;
                    continue block231;
                }
                case '\uff1a': {
                    output[outputPos++] = 58;
                    continue block231;
                }
                case '\u204f': 
                case '\uff1b': {
                    output[outputPos++] = 59;
                    continue block231;
                }
                case '\uff1f': {
                    output[outputPos++] = 63;
                    continue block231;
                }
                case '\u2047': {
                    output[outputPos++] = 63;
                    output[outputPos++] = 63;
                    continue block231;
                }
                case '\u2048': {
                    output[outputPos++] = 63;
                    output[outputPos++] = 33;
                    continue block231;
                }
                case '\uff20': {
                    output[outputPos++] = 64;
                    continue block231;
                }
                case '\uff3c': {
                    output[outputPos++] = 92;
                    continue block231;
                }
                case '\u2038': 
                case '\uff3e': {
                    output[outputPos++] = 94;
                    continue block231;
                }
                case '\uff3f': {
                    output[outputPos++] = 95;
                    continue block231;
                }
                case '\u2053': 
                case '\uff5e': {
                    output[outputPos++] = 126;
                    continue block231;
                }
                default: {
                    output[outputPos++] = c;
                }
            }
        }
        return outputPos;
    }
}

