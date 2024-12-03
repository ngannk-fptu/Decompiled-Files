/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

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
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.ASTSubtractNode;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTTrue;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public interface ParserVisitor {
    public Object visit(SimpleNode var1, Object var2);

    public Object visit(ASTprocess var1, Object var2);

    public Object visit(ASTEscapedDirective var1, Object var2);

    public Object visit(ASTEscape var1, Object var2);

    public Object visit(ASTComment var1, Object var2);

    public Object visit(ASTFloatingPointLiteral var1, Object var2);

    public Object visit(ASTIntegerLiteral var1, Object var2);

    public Object visit(ASTStringLiteral var1, Object var2);

    public Object visit(ASTIdentifier var1, Object var2);

    public Object visit(ASTWord var1, Object var2);

    public Object visit(ASTDirective var1, Object var2);

    public Object visit(ASTBlock var1, Object var2);

    public Object visit(ASTMap var1, Object var2);

    public Object visit(ASTObjectArray var1, Object var2);

    public Object visit(ASTIntegerRange var1, Object var2);

    public Object visit(ASTMethod var1, Object var2);

    public Object visit(ASTReference var1, Object var2);

    public Object visit(ASTTrue var1, Object var2);

    public Object visit(ASTFalse var1, Object var2);

    public Object visit(ASTText var1, Object var2);

    public Object visit(ASTIfStatement var1, Object var2);

    public Object visit(ASTElseStatement var1, Object var2);

    public Object visit(ASTElseIfStatement var1, Object var2);

    public Object visit(ASTSetDirective var1, Object var2);

    public Object visit(ASTExpression var1, Object var2);

    public Object visit(ASTAssignment var1, Object var2);

    public Object visit(ASTOrNode var1, Object var2);

    public Object visit(ASTAndNode var1, Object var2);

    public Object visit(ASTEQNode var1, Object var2);

    public Object visit(ASTNENode var1, Object var2);

    public Object visit(ASTLTNode var1, Object var2);

    public Object visit(ASTGTNode var1, Object var2);

    public Object visit(ASTLENode var1, Object var2);

    public Object visit(ASTGENode var1, Object var2);

    public Object visit(ASTAddNode var1, Object var2);

    public Object visit(ASTSubtractNode var1, Object var2);

    public Object visit(ASTMulNode var1, Object var2);

    public Object visit(ASTDivNode var1, Object var2);

    public Object visit(ASTModNode var1, Object var2);

    public Object visit(ASTNotNode var1, Object var2);
}

