/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ASTVisitor;
import groovyjarjarantlr.collections.AST;

public class DumpASTVisitor
implements ASTVisitor {
    protected int level = 0;

    private void tabs() {
        for (int i = 0; i < this.level; ++i) {
            System.out.print("   ");
        }
    }

    public void visit(AST aST) {
        AST aST2;
        boolean bl = false;
        for (aST2 = aST; aST2 != null; aST2 = aST2.getNextSibling()) {
            if (aST2.getFirstChild() == null) continue;
            bl = false;
            break;
        }
        for (aST2 = aST; aST2 != null; aST2 = aST2.getNextSibling()) {
            if (!bl || aST2 == aST) {
                this.tabs();
            }
            if (aST2.getText() == null) {
                System.out.print("nil");
            } else {
                System.out.print(aST2.getText());
            }
            System.out.print(" [" + aST2.getType() + "] ");
            if (bl) {
                System.out.print(" ");
            } else {
                System.out.println("");
            }
            if (aST2.getFirstChild() == null) continue;
            ++this.level;
            this.visit(aST2.getFirstChild());
            --this.level;
        }
        if (bl) {
            System.out.println("");
        }
    }
}

