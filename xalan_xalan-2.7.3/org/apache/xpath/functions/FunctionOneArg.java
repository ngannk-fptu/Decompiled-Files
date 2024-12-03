/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.Vector;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.WrongNumberArgsException;

public class FunctionOneArg
extends Function
implements ExpressionOwner {
    static final long serialVersionUID = -5180174180765609758L;
    Expression m_arg0;

    public Expression getArg0() {
        return this.m_arg0;
    }

    @Override
    public void setArg(Expression arg, int argNum) throws WrongNumberArgsException {
        if (0 == argNum) {
            this.m_arg0 = arg;
            arg.exprSetParent(this);
        } else {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
        if (argNum != 1) {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("one", null));
    }

    @Override
    public boolean canTraverseOutsideSubtree() {
        return this.m_arg0.canTraverseOutsideSubtree();
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        if (null != this.m_arg0) {
            this.m_arg0.fixupVariables(vars, globalsSize);
        }
    }

    @Override
    public void callArgVisitors(XPathVisitor visitor) {
        if (null != this.m_arg0) {
            this.m_arg0.callVisitors(this, visitor);
        }
    }

    @Override
    public Expression getExpression() {
        return this.m_arg0;
    }

    @Override
    public void setExpression(Expression exp) {
        exp.exprSetParent(this);
        this.m_arg0 = exp;
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        if (null != this.m_arg0) {
            if (null == ((FunctionOneArg)expr).m_arg0) {
                return false;
            }
            if (!this.m_arg0.deepEquals(((FunctionOneArg)expr).m_arg0)) {
                return false;
            }
        } else if (null != ((FunctionOneArg)expr).m_arg0) {
            return false;
        }
        return true;
    }
}

