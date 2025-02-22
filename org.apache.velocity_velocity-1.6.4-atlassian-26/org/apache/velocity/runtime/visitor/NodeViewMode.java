/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.visitor;

import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.ASTAddNode;
import org.apache.velocity.runtime.parser.node.ASTAndNode;
import org.apache.velocity.runtime.parser.node.ASTAssignment;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTDivNode;
import org.apache.velocity.runtime.parser.node.ASTEQNode;
import org.apache.velocity.runtime.parser.node.ASTElseIfStatement;
import org.apache.velocity.runtime.parser.node.ASTElseStatement;
import org.apache.velocity.runtime.parser.node.ASTEscape;
import org.apache.velocity.runtime.parser.node.ASTEscapedDirective;
import org.apache.velocity.runtime.parser.node.ASTExpression;
import org.apache.velocity.runtime.parser.node.ASTFalse;
import org.apache.velocity.runtime.parser.node.ASTFloatingPointLiteral;
import org.apache.velocity.runtime.parser.node.ASTGENode;
import org.apache.velocity.runtime.parser.node.ASTGTNode;
import org.apache.velocity.runtime.parser.node.ASTIdentifier;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTIntegerLiteral;
import org.apache.velocity.runtime.parser.node.ASTIntegerRange;
import org.apache.velocity.runtime.parser.node.ASTLENode;
import org.apache.velocity.runtime.parser.node.ASTLTNode;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.ASTModNode;
import org.apache.velocity.runtime.parser.node.ASTMulNode;
import org.apache.velocity.runtime.parser.node.ASTNENode;
import org.apache.velocity.runtime.parser.node.ASTNotNode;
import org.apache.velocity.runtime.parser.node.ASTObjectArray;
import org.apache.velocity.runtime.parser.node.ASTOrNode;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.ASTStop;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.ASTSubtractNode;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTTrue;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.visitor.BaseVisitor;

public class NodeViewMode
extends BaseVisitor {
    private int indent = 0;
    private boolean showTokens = true;

    private String indentString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.indent; ++i) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private Object showNode(Node node, Object data) {
        String tokens = "";
        String special = "";
        if (this.showTokens) {
            Token t = node.getFirstToken();
            if (t.specialToken != null && !t.specialToken.image.startsWith("##")) {
                special = t.specialToken.image;
            }
            tokens = " -> " + special + t.image;
        }
        System.out.println(this.indentString() + node + tokens);
        ++this.indent;
        data = node.childrenAccept(this, data);
        --this.indent;
        return data;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTprocess node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTAssignment node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTOrNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTAndNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTEQNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTNENode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTLTNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTGTNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTLENode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTGENode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTAddNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTSubtractNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTMulNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTDivNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTModNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTNotNode node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTFloatingPointLiteral node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTIntegerLiteral node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTReference node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTTrue node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTFalse node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTText node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTElseStatement node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTElseIfStatement node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTObjectArray node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTDirective node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTWord node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTSetDirective node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTEscapedDirective node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTEscape node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTMap node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTIntegerRange node, Object data) {
        return this.showNode(node, data);
    }

    @Override
    public Object visit(ASTStop node, Object data) {
        return this.showNode(node, data);
    }
}

