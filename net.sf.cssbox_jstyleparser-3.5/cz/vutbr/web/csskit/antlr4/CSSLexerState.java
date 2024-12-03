/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSToken;

public class CSSLexerState {
    public short curlyNest;
    public short parenNest;
    public short sqNest;
    public boolean quotOpen;
    public boolean aposOpen;

    public CSSLexerState() {
        this.curlyNest = 0;
        this.parenNest = 0;
        this.sqNest = 0;
        this.quotOpen = false;
        this.aposOpen = false;
    }

    public CSSLexerState(CSSLexerState clone) {
        this.curlyNest = clone.curlyNest;
        this.parenNest = clone.parenNest;
        this.sqNest = clone.sqNest;
        this.quotOpen = clone.quotOpen;
        this.aposOpen = clone.aposOpen;
    }

    public boolean equals(Object o) {
        if (o instanceof CSSLexerState) {
            CSSLexerState that = (CSSLexerState)o;
            return this.curlyNest == that.curlyNest && this.parenNest == that.parenNest && this.sqNest == that.sqNest && this.quotOpen == that.quotOpen && this.aposOpen == that.aposOpen;
        }
        return false;
    }

    public boolean isBalanced() {
        return !this.aposOpen && !this.quotOpen && this.curlyNest == 0 && this.parenNest == 0 && this.sqNest == 0;
    }

    public boolean isBalanced(RecoveryMode mode, CSSLexerState state, CSSToken t) {
        if (mode == RecoveryMode.BALANCED) {
            return !this.aposOpen && !this.quotOpen && this.curlyNest == 0 && this.parenNest == 0 && this.sqNest == 0;
        }
        if (mode == RecoveryMode.FUNCTION) {
            return this.parenNest == 0 && this.sqNest == 0;
        }
        if (mode == RecoveryMode.RULE) {
            return !this.aposOpen && !this.quotOpen && this.parenNest == 0 && this.sqNest == 0;
        }
        if (mode == RecoveryMode.DECL) {
            if (t.getType() == 63) {
                return !this.aposOpen && !this.quotOpen && this.parenNest == 0 && this.sqNest == 0 && this.curlyNest == state.curlyNest - 1;
            }
            return !this.aposOpen && !this.quotOpen && this.parenNest == 0 && this.sqNest == 0 && this.curlyNest == state.curlyNest;
        }
        return true;
    }

    public String toString() {
        return "{=" + this.curlyNest + ", (=" + this.parenNest + ", [=" + this.sqNest + ", '=" + (this.aposOpen ? "1" : "0") + ", \"=" + (this.quotOpen ? "1" : "0");
    }

    public static enum RecoveryMode {
        BALANCED,
        FUNCTION,
        RULE,
        DECL,
        NOBALANCE;

    }
}

