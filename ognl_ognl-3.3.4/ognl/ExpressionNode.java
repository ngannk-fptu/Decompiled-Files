/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ASTChain;
import ognl.ASTMethod;
import ognl.ASTProperty;
import ognl.ASTSequence;
import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.OgnlParserTreeConstants;
import ognl.SimpleNode;
import ognl.enhance.ExpressionCompiler;

public abstract class ExpressionNode
extends SimpleNode {
    public ExpressionNode(int i) {
        super(i);
    }

    public ExpressionNode(OgnlParser p, int i) {
        super(p, i);
    }

    @Override
    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        return false;
    }

    @Override
    public boolean isConstant(OgnlContext context) throws OgnlException {
        boolean result = this.isNodeConstant(context);
        if (this._children != null && this._children.length > 0) {
            result = true;
            for (int i = 0; result && i < this._children.length; ++i) {
                result = this._children[i] instanceof SimpleNode ? ((SimpleNode)this._children[i]).isConstant(context) : false;
            }
        }
        return result;
    }

    public String getExpressionOperator(int index) {
        throw new RuntimeException("unknown operator for " + OgnlParserTreeConstants.jjtNodeName[this._id]);
    }

    @Override
    public String toString() {
        String result;
        String string = result = this._parent == null ? "" : "(";
        if (this._children != null && this._children.length > 0) {
            for (int i = 0; i < this._children.length; ++i) {
                if (i > 0) {
                    result = result + " " + this.getExpressionOperator(i) + " ";
                }
                result = result + this._children[i].toString();
            }
        }
        if (this._parent != null) {
            result = result + ")";
        }
        return result;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result;
        String string = result = this._parent == null || NumericExpression.class.isAssignableFrom(this._parent.getClass()) ? "" : "(";
        if (this._children != null && this._children.length > 0) {
            for (int i = 0; i < this._children.length; ++i) {
                if (i > 0) {
                    result = result + " " + this.getExpressionOperator(i) + " ";
                }
                String value = this._children[i].toGetSourceString(context, target);
                if ((ASTProperty.class.isInstance(this._children[i]) || ASTMethod.class.isInstance(this._children[i]) || ASTSequence.class.isInstance(this._children[i]) || ASTChain.class.isInstance(this._children[i])) && value != null && value.trim().length() > 0) {
                    String cast;
                    String pre = null;
                    if (ASTMethod.class.isInstance(this._children[i])) {
                        pre = (String)context.get("_currentChain");
                    }
                    if (pre == null) {
                        pre = "";
                    }
                    if ((cast = (String)context.remove("_preCast")) == null) {
                        cast = "";
                    }
                    value = cast + ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context) + pre + value;
                }
                result = result + value;
            }
        }
        if (this._parent != null && !NumericExpression.class.isAssignableFrom(this._parent.getClass())) {
            result = result + ")";
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        String result;
        String string = result = this._parent == null ? "" : "(";
        if (this._children != null && this._children.length > 0) {
            for (int i = 0; i < this._children.length; ++i) {
                if (i > 0) {
                    result = result + " " + this.getExpressionOperator(i) + " ";
                }
                result = result + this._children[i].toSetSourceString(context, target);
            }
        }
        if (this._parent != null) {
            result = result + ")";
        }
        return result;
    }

    @Override
    public boolean isOperation(OgnlContext context) throws OgnlException {
        return true;
    }
}

