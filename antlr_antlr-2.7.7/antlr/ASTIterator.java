/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.collections.AST;

public class ASTIterator {
    protected AST cursor = null;
    protected AST original = null;

    public ASTIterator(AST aST) {
        this.original = this.cursor = aST;
    }

    public boolean isSubtree(AST aST, AST aST2) {
        if (aST2 == null) {
            return true;
        }
        if (aST == null) {
            return aST2 == null;
        }
        for (AST aST3 = aST; aST3 != null && aST2 != null; aST3 = aST3.getNextSibling(), aST2 = aST2.getNextSibling()) {
            if (aST3.getType() != aST2.getType()) {
                return false;
            }
            if (aST3.getFirstChild() == null || this.isSubtree(aST3.getFirstChild(), aST2.getFirstChild())) continue;
            return false;
        }
        return true;
    }

    public AST next(AST aST) {
        AST aST2 = null;
        Object var3_3 = null;
        if (this.cursor == null) {
            return null;
        }
        while (this.cursor != null) {
            if (this.cursor.getType() == aST.getType() && this.cursor.getFirstChild() != null && this.isSubtree(this.cursor.getFirstChild(), aST.getFirstChild())) {
                return this.cursor;
            }
            this.cursor = this.cursor.getNextSibling();
        }
        return aST2;
    }
}

