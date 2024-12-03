/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.Vector;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;

public class Function3Args
extends Function2Args {
    static final long serialVersionUID = 7915240747161506646L;
    Expression m_arg2;

    public Expression getArg2() {
        return this.m_arg2;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        if (null != this.m_arg2) {
            this.m_arg2.fixupVariables(vars, globalsSize);
        }
    }

    @Override
    public void setArg(Expression arg, int argNum) throws WrongNumberArgsException {
        if (argNum < 2) {
            super.setArg(arg, argNum);
        } else if (2 == argNum) {
            this.m_arg2 = arg;
            arg.exprSetParent(this);
        } else {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
        if (argNum != 3) {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("three", null));
    }

    @Override
    public boolean canTraverseOutsideSubtree() {
        return super.canTraverseOutsideSubtree() ? true : this.m_arg2.canTraverseOutsideSubtree();
    }

    @Override
    public void callArgVisitors(XPathVisitor visitor) {
        super.callArgVisitors(visitor);
        if (null != this.m_arg2) {
            this.m_arg2.callVisitors(new Arg2Owner(), visitor);
        }
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        if (null != this.m_arg2) {
            if (null == ((Function3Args)expr).m_arg2) {
                return false;
            }
            if (!this.m_arg2.deepEquals(((Function3Args)expr).m_arg2)) {
                return false;
            }
        } else if (null != ((Function3Args)expr).m_arg2) {
            return false;
        }
        return true;
    }

    class Arg2Owner
    implements ExpressionOwner {
        Arg2Owner() {
        }

        @Override
        public Expression getExpression() {
            return Function3Args.this.m_arg2;
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(Function3Args.this);
            Function3Args.this.m_arg2 = exp;
        }
    }
}

