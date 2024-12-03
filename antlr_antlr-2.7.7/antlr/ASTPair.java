/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.collections.AST;

public class ASTPair {
    public AST root;
    public AST child;

    public final void advanceChildToEnd() {
        if (this.child != null) {
            while (this.child.getNextSibling() != null) {
                this.child = this.child.getNextSibling();
            }
        }
    }

    public ASTPair copy() {
        ASTPair aSTPair = new ASTPair();
        aSTPair.root = this.root;
        aSTPair.child = this.child;
        return aSTPair;
    }

    public String toString() {
        String string = this.root == null ? "null" : this.root.getText();
        String string2 = this.child == null ? "null" : this.child.getText();
        return "[" + string + "," + string2 + "]";
    }
}

