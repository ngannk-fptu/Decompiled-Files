/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CharFormatter;
import antlr.Grammar;
import antlr.LexerGrammar;
import antlr.collections.impl.BitSet;
import antlr.collections.impl.Vector;

public class Lookahead
implements Cloneable {
    BitSet fset;
    String cycle;
    BitSet epsilonDepth;
    boolean hasEpsilon = false;

    public Lookahead() {
        this.fset = new BitSet();
    }

    public Lookahead(BitSet bitSet) {
        this.fset = bitSet;
    }

    public Lookahead(String string) {
        this();
        this.cycle = string;
    }

    public Object clone() {
        Lookahead lookahead = null;
        try {
            lookahead = (Lookahead)super.clone();
            lookahead.fset = (BitSet)this.fset.clone();
            lookahead.cycle = this.cycle;
            if (this.epsilonDepth != null) {
                lookahead.epsilonDepth = (BitSet)this.epsilonDepth.clone();
            }
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
        return lookahead;
    }

    public void combineWith(Lookahead lookahead) {
        if (this.cycle == null) {
            this.cycle = lookahead.cycle;
        }
        if (lookahead.containsEpsilon()) {
            this.hasEpsilon = true;
        }
        if (this.epsilonDepth != null) {
            if (lookahead.epsilonDepth != null) {
                this.epsilonDepth.orInPlace(lookahead.epsilonDepth);
            }
        } else if (lookahead.epsilonDepth != null) {
            this.epsilonDepth = (BitSet)lookahead.epsilonDepth.clone();
        }
        this.fset.orInPlace(lookahead.fset);
    }

    public boolean containsEpsilon() {
        return this.hasEpsilon;
    }

    public Lookahead intersection(Lookahead lookahead) {
        Lookahead lookahead2 = new Lookahead(this.fset.and(lookahead.fset));
        if (this.hasEpsilon && lookahead.hasEpsilon) {
            lookahead2.setEpsilon();
        }
        return lookahead2;
    }

    public boolean nil() {
        return this.fset.nil() && !this.hasEpsilon;
    }

    public static Lookahead of(int n) {
        Lookahead lookahead = new Lookahead();
        lookahead.fset.add(n);
        return lookahead;
    }

    public void resetEpsilon() {
        this.hasEpsilon = false;
    }

    public void setEpsilon() {
        this.hasEpsilon = true;
    }

    public String toString() {
        String string = "";
        String string2 = "";
        String string3 = "";
        String string4 = this.fset.toString(",");
        if (this.containsEpsilon()) {
            string = "+<epsilon>";
        }
        if (this.cycle != null) {
            string2 = "; FOLLOW(" + this.cycle + ")";
        }
        if (this.epsilonDepth != null) {
            string3 = "; depths=" + this.epsilonDepth.toString(",");
        }
        return string4 + string + string2 + string3;
    }

    public String toString(String string, CharFormatter charFormatter) {
        String string2 = "";
        String string3 = "";
        String string4 = "";
        String string5 = this.fset.toString(string, charFormatter);
        if (this.containsEpsilon()) {
            string2 = "+<epsilon>";
        }
        if (this.cycle != null) {
            string3 = "; FOLLOW(" + this.cycle + ")";
        }
        if (this.epsilonDepth != null) {
            string4 = "; depths=" + this.epsilonDepth.toString(",");
        }
        return string5 + string2 + string3 + string4;
    }

    public String toString(String string, CharFormatter charFormatter, Grammar grammar) {
        if (grammar instanceof LexerGrammar) {
            return this.toString(string, charFormatter);
        }
        return this.toString(string, grammar.tokenManager.getVocabulary());
    }

    public String toString(String string, Vector vector) {
        String string2 = "";
        String string3 = "";
        String string4 = this.fset.toString(string, vector);
        if (this.cycle != null) {
            string2 = "; FOLLOW(" + this.cycle + ")";
        }
        if (this.epsilonDepth != null) {
            string3 = "; depths=" + this.epsilonDepth.toString(",");
        }
        return string4 + string2 + string3;
    }
}

