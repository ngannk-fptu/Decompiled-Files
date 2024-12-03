/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.RecognitionException;
import antlr.Token;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

public class MismatchedTokenException
extends RecognitionException {
    String[] tokenNames;
    public Token token;
    public AST node;
    String tokenText = null;
    public static final int TOKEN = 1;
    public static final int NOT_TOKEN = 2;
    public static final int RANGE = 3;
    public static final int NOT_RANGE = 4;
    public static final int SET = 5;
    public static final int NOT_SET = 6;
    public int mismatchType;
    public int expecting;
    public int upper;
    public BitSet set;

    public MismatchedTokenException() {
        super("Mismatched Token: expecting any AST node", "<AST>", -1, -1);
    }

    public MismatchedTokenException(String[] stringArray, AST aST, int n, int n2, boolean bl) {
        super("Mismatched Token", "<AST>", aST == null ? -1 : aST.getLine(), aST == null ? -1 : aST.getColumn());
        this.tokenNames = stringArray;
        this.node = aST;
        this.tokenText = aST == null ? "<empty tree>" : ((Object)aST).toString();
        this.mismatchType = bl ? 4 : 3;
        this.expecting = n;
        this.upper = n2;
    }

    public MismatchedTokenException(String[] stringArray, AST aST, int n, boolean bl) {
        super("Mismatched Token", "<AST>", aST == null ? -1 : aST.getLine(), aST == null ? -1 : aST.getColumn());
        this.tokenNames = stringArray;
        this.node = aST;
        this.tokenText = aST == null ? "<empty tree>" : ((Object)aST).toString();
        this.mismatchType = bl ? 2 : 1;
        this.expecting = n;
    }

    public MismatchedTokenException(String[] stringArray, AST aST, BitSet bitSet, boolean bl) {
        super("Mismatched Token", "<AST>", aST == null ? -1 : aST.getLine(), aST == null ? -1 : aST.getColumn());
        this.tokenNames = stringArray;
        this.node = aST;
        this.tokenText = aST == null ? "<empty tree>" : ((Object)aST).toString();
        this.mismatchType = bl ? 6 : 5;
        this.set = bitSet;
    }

    public MismatchedTokenException(String[] stringArray, Token token, int n, int n2, boolean bl, String string) {
        super("Mismatched Token", string, token.getLine(), token.getColumn());
        this.tokenNames = stringArray;
        this.token = token;
        this.tokenText = token.getText();
        this.mismatchType = bl ? 4 : 3;
        this.expecting = n;
        this.upper = n2;
    }

    public MismatchedTokenException(String[] stringArray, Token token, int n, boolean bl, String string) {
        super("Mismatched Token", string, token.getLine(), token.getColumn());
        this.tokenNames = stringArray;
        this.token = token;
        this.tokenText = token.getText();
        this.mismatchType = bl ? 2 : 1;
        this.expecting = n;
    }

    public MismatchedTokenException(String[] stringArray, Token token, BitSet bitSet, boolean bl, String string) {
        super("Mismatched Token", string, token.getLine(), token.getColumn());
        this.tokenNames = stringArray;
        this.token = token;
        this.tokenText = token.getText();
        this.mismatchType = bl ? 6 : 5;
        this.set = bitSet;
    }

    public String getMessage() {
        StringBuffer stringBuffer = new StringBuffer();
        switch (this.mismatchType) {
            case 1: {
                stringBuffer.append("expecting " + this.tokenName(this.expecting) + ", found '" + this.tokenText + "'");
                break;
            }
            case 2: {
                stringBuffer.append("expecting anything but " + this.tokenName(this.expecting) + "; got it anyway");
                break;
            }
            case 3: {
                stringBuffer.append("expecting token in range: " + this.tokenName(this.expecting) + ".." + this.tokenName(this.upper) + ", found '" + this.tokenText + "'");
                break;
            }
            case 4: {
                stringBuffer.append("expecting token NOT in range: " + this.tokenName(this.expecting) + ".." + this.tokenName(this.upper) + ", found '" + this.tokenText + "'");
                break;
            }
            case 5: 
            case 6: {
                stringBuffer.append("expecting " + (this.mismatchType == 6 ? "NOT " : "") + "one of (");
                int[] nArray = this.set.toArray();
                for (int i = 0; i < nArray.length; ++i) {
                    stringBuffer.append(" ");
                    stringBuffer.append(this.tokenName(nArray[i]));
                }
                stringBuffer.append("), found '" + this.tokenText + "'");
                break;
            }
            default: {
                stringBuffer.append(super.getMessage());
            }
        }
        return stringBuffer.toString();
    }

    private String tokenName(int n) {
        if (n == 0) {
            return "<Set of tokens>";
        }
        if (n < 0 || n >= this.tokenNames.length) {
            return "<" + String.valueOf(n) + ">";
        }
        return this.tokenNames[n];
    }
}

