/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class Jump
extends AstNode {
    public Node target;
    private Node target2;
    private Jump jumpNode;

    public Jump() {
        this.type = -1;
    }

    public Jump(int nodeType) {
        this.type = nodeType;
    }

    public Jump(int type, int lineno) {
        this(type);
        this.setLineno(lineno);
    }

    public Jump(int type, Node child) {
        this(type);
        this.addChildToBack(child);
    }

    public Jump(int type, Node child, int lineno) {
        this(type, child);
        this.setLineno(lineno);
    }

    public Jump getJumpStatement() {
        if (this.type != 124 && this.type != 125) {
            Jump.codeBug();
        }
        return this.jumpNode;
    }

    public void setJumpStatement(Jump jumpStatement) {
        if (this.type != 124 && this.type != 125) {
            Jump.codeBug();
        }
        if (jumpStatement == null) {
            Jump.codeBug();
        }
        if (this.jumpNode != null) {
            Jump.codeBug();
        }
        this.jumpNode = jumpStatement;
    }

    public Node getDefault() {
        if (this.type != 118) {
            Jump.codeBug();
        }
        return this.target2;
    }

    public void setDefault(Node defaultTarget) {
        if (this.type != 118) {
            Jump.codeBug();
        }
        if (defaultTarget.getType() != 135) {
            Jump.codeBug();
        }
        if (this.target2 != null) {
            Jump.codeBug();
        }
        this.target2 = defaultTarget;
    }

    public Node getFinally() {
        if (this.type != 84) {
            Jump.codeBug();
        }
        return this.target2;
    }

    public void setFinally(Node finallyTarget) {
        if (this.type != 84) {
            Jump.codeBug();
        }
        if (finallyTarget.getType() != 135) {
            Jump.codeBug();
        }
        if (this.target2 != null) {
            Jump.codeBug();
        }
        this.target2 = finallyTarget;
    }

    public Jump getLoop() {
        if (this.type != 134) {
            Jump.codeBug();
        }
        return this.jumpNode;
    }

    public void setLoop(Jump loop) {
        if (this.type != 134) {
            Jump.codeBug();
        }
        if (loop == null) {
            Jump.codeBug();
        }
        if (this.jumpNode != null) {
            Jump.codeBug();
        }
        this.jumpNode = loop;
    }

    public Node getContinue() {
        if (this.type != 136) {
            Jump.codeBug();
        }
        return this.target2;
    }

    public void setContinue(Node continueTarget) {
        if (this.type != 136) {
            Jump.codeBug();
        }
        if (continueTarget.getType() != 135) {
            Jump.codeBug();
        }
        if (this.target2 != null) {
            Jump.codeBug();
        }
        this.target2 = continueTarget;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public String toSource(int depth) {
        throw new UnsupportedOperationException(this.toString());
    }
}

