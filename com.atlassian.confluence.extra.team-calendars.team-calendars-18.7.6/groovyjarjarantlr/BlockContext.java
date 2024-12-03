/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Alternative;
import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.BlockEndElement;

class BlockContext {
    AlternativeBlock block;
    int altNum;
    BlockEndElement blockEnd;

    BlockContext() {
    }

    public void addAlternativeElement(AlternativeElement alternativeElement) {
        this.currentAlt().addElement(alternativeElement);
    }

    public Alternative currentAlt() {
        return (Alternative)this.block.alternatives.elementAt(this.altNum);
    }

    public AlternativeElement currentElement() {
        return this.currentAlt().tail;
    }
}

