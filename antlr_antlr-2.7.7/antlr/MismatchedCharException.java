/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CharScanner;
import antlr.RecognitionException;
import antlr.collections.impl.BitSet;

public class MismatchedCharException
extends RecognitionException {
    public static final int CHAR = 1;
    public static final int NOT_CHAR = 2;
    public static final int RANGE = 3;
    public static final int NOT_RANGE = 4;
    public static final int SET = 5;
    public static final int NOT_SET = 6;
    public int mismatchType;
    public int foundChar;
    public int expecting;
    public int upper;
    public BitSet set;
    public CharScanner scanner;

    public MismatchedCharException() {
        super("Mismatched char");
    }

    public MismatchedCharException(char c, char c2, char c3, boolean bl, CharScanner charScanner) {
        super("Mismatched char", charScanner.getFilename(), charScanner.getLine(), charScanner.getColumn());
        this.mismatchType = bl ? 4 : 3;
        this.foundChar = c;
        this.expecting = c2;
        this.upper = c3;
        this.scanner = charScanner;
    }

    public MismatchedCharException(char c, char c2, boolean bl, CharScanner charScanner) {
        super("Mismatched char", charScanner.getFilename(), charScanner.getLine(), charScanner.getColumn());
        this.mismatchType = bl ? 2 : 1;
        this.foundChar = c;
        this.expecting = c2;
        this.scanner = charScanner;
    }

    public MismatchedCharException(char c, BitSet bitSet, boolean bl, CharScanner charScanner) {
        super("Mismatched char", charScanner.getFilename(), charScanner.getLine(), charScanner.getColumn());
        this.mismatchType = bl ? 6 : 5;
        this.foundChar = c;
        this.set = bitSet;
        this.scanner = charScanner;
    }

    public String getMessage() {
        StringBuffer stringBuffer = new StringBuffer();
        switch (this.mismatchType) {
            case 1: {
                stringBuffer.append("expecting ");
                this.appendCharName(stringBuffer, this.expecting);
                stringBuffer.append(", found ");
                this.appendCharName(stringBuffer, this.foundChar);
                break;
            }
            case 2: {
                stringBuffer.append("expecting anything but '");
                this.appendCharName(stringBuffer, this.expecting);
                stringBuffer.append("'; got it anyway");
                break;
            }
            case 3: 
            case 4: {
                stringBuffer.append("expecting token ");
                if (this.mismatchType == 4) {
                    stringBuffer.append("NOT ");
                }
                stringBuffer.append("in range: ");
                this.appendCharName(stringBuffer, this.expecting);
                stringBuffer.append("..");
                this.appendCharName(stringBuffer, this.upper);
                stringBuffer.append(", found ");
                this.appendCharName(stringBuffer, this.foundChar);
                break;
            }
            case 5: 
            case 6: {
                stringBuffer.append("expecting " + (this.mismatchType == 6 ? "NOT " : "") + "one of (");
                int[] nArray = this.set.toArray();
                for (int i = 0; i < nArray.length; ++i) {
                    this.appendCharName(stringBuffer, nArray[i]);
                }
                stringBuffer.append("), found ");
                this.appendCharName(stringBuffer, this.foundChar);
                break;
            }
            default: {
                stringBuffer.append(super.getMessage());
            }
        }
        return stringBuffer.toString();
    }

    private void appendCharName(StringBuffer stringBuffer, int n) {
        switch (n) {
            case 65535: {
                stringBuffer.append("'<EOF>'");
                break;
            }
            case 10: {
                stringBuffer.append("'\\n'");
                break;
            }
            case 13: {
                stringBuffer.append("'\\r'");
                break;
            }
            case 9: {
                stringBuffer.append("'\\t'");
                break;
            }
            default: {
                stringBuffer.append('\'');
                stringBuffer.append((char)n);
                stringBuffer.append('\'');
            }
        }
    }
}

