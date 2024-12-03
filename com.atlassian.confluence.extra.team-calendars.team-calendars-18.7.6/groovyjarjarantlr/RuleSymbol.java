/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.GrammarSymbol;
import groovyjarjarantlr.RuleBlock;
import groovyjarjarantlr.RuleRefElement;
import groovyjarjarantlr.collections.impl.Vector;

class RuleSymbol
extends GrammarSymbol {
    RuleBlock block;
    boolean defined;
    Vector references = new Vector();
    String access;
    String comment;

    public RuleSymbol(String string) {
        super(string);
    }

    public void addReference(RuleRefElement ruleRefElement) {
        this.references.appendElement(ruleRefElement);
    }

    public RuleBlock getBlock() {
        return this.block;
    }

    public RuleRefElement getReference(int n) {
        return (RuleRefElement)this.references.elementAt(n);
    }

    public boolean isDefined() {
        return this.defined;
    }

    public int numReferences() {
        return this.references.size();
    }

    public void setBlock(RuleBlock ruleBlock) {
        this.block = ruleBlock;
    }

    public void setDefined() {
        this.defined = true;
    }
}

