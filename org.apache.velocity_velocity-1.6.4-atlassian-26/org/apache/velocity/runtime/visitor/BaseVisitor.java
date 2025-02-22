/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.visitor;

import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.ASTAddNode;
import org.apache.velocity.runtime.parser.node.ASTAndNode;
import org.apache.velocity.runtime.parser.node.ASTAssignment;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTComment;
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
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public abstract class BaseVisitor
implements ParserVisitor {
    protected InternalContextAdapter context;
    protected Writer writer;

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void setContext(InternalContextAdapter context) {
        this.context = context;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTprocess node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTAssignment node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTOrNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTAndNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTEQNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTNENode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTLTNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTGTNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTLENode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTGENode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTAddNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTSubtractNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTMulNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTDivNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTModNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTNotNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTIntegerLiteral node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTFloatingPointLiteral node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTReference node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTTrue node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTFalse node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTText node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTElseStatement node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTElseIfStatement node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTComment node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTObjectArray node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTWord node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTSetDirective node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTDirective node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTEscapedDirective node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTEscape node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTMap node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTIntegerRange node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTStop node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }
}

