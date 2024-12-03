/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.ExceptionSpec;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.SynPredBlock;
import groovyjarjarantlr.Token;

class Alternative {
    AlternativeElement head;
    AlternativeElement tail;
    protected SynPredBlock synPred;
    protected String semPred;
    protected ExceptionSpec exceptionSpec;
    protected Lookahead[] cache;
    protected int lookaheadDepth;
    protected Token treeSpecifier = null;
    private boolean doAutoGen;

    public Alternative() {
    }

    public Alternative(AlternativeElement alternativeElement) {
        this.addElement(alternativeElement);
    }

    public void addElement(AlternativeElement alternativeElement) {
        if (this.head == null) {
            this.head = this.tail = alternativeElement;
        } else {
            this.tail.next = alternativeElement;
            this.tail = alternativeElement;
        }
    }

    public boolean atStart() {
        return this.head == null;
    }

    public boolean getAutoGen() {
        return this.doAutoGen && this.treeSpecifier == null;
    }

    public Token getTreeSpecifier() {
        return this.treeSpecifier;
    }

    public void setAutoGen(boolean bl) {
        this.doAutoGen = bl;
    }
}

