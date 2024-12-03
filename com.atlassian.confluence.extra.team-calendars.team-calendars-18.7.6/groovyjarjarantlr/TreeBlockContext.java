/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.BlockContext;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.TreeElement;

class TreeBlockContext
extends BlockContext {
    protected boolean nextElementIsRoot = true;

    TreeBlockContext() {
    }

    public void addAlternativeElement(AlternativeElement alternativeElement) {
        TreeElement treeElement = (TreeElement)this.block;
        if (this.nextElementIsRoot) {
            treeElement.root = (GrammarAtom)alternativeElement;
            this.nextElementIsRoot = false;
        } else {
            super.addAlternativeElement(alternativeElement);
        }
    }
}

