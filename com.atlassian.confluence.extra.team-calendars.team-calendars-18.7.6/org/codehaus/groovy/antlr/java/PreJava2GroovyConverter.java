/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

import java.util.Stack;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

public class PreJava2GroovyConverter
extends VisitorAdapter {
    private Stack stack = new Stack();

    public PreJava2GroovyConverter(String[] tokenNames) {
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        if (visit == 1) {
            if (t.getType() == 114) {
                this.visitJavaLiteralDo(t);
            } else if (t.getType() == 28) {
                this.visitJavaArrayInit(t);
            }
        }
    }

    private void visitJavaLiteralDo(GroovySourceAST t) {
        this.swapTwoChildren(t);
    }

    private void visitJavaArrayInit(GroovySourceAST t) {
        GroovySourceAST grandParent;
        if (this.stack.size() > 2 && (grandParent = this.getGrandParentNode()).getType() == 27) {
            grandParent.setType(28);
            grandParent.setFirstChild(t);
            t.setType(33);
        }
    }

    public void swapTwoChildren(GroovySourceAST t) {
        GroovySourceAST a = (GroovySourceAST)t.getFirstChild();
        GroovySourceAST b = (GroovySourceAST)a.getNextSibling();
        t.setFirstChild(b);
        a.setNextSibling(null);
        b.setNextSibling(a);
    }

    @Override
    public void push(GroovySourceAST t) {
        this.stack.push(t);
    }

    @Override
    public GroovySourceAST pop() {
        if (!this.stack.empty()) {
            return (GroovySourceAST)this.stack.pop();
        }
        return null;
    }

    private GroovySourceAST getGrandParentNode() {
        Object currentNode = this.stack.pop();
        Object parentNode = this.stack.pop();
        Object grandParentNode = this.stack.peek();
        this.stack.push(parentNode);
        this.stack.push(currentNode);
        return (GroovySourceAST)grandParentNode;
    }
}

