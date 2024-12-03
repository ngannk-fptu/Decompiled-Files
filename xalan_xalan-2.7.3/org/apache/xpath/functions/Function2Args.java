/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.Vector;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.WrongNumberArgsException;

public class Function2Args
extends FunctionOneArg {
    static final long serialVersionUID = 5574294996842710641L;
    Expression m_arg1;

    public Expression getArg1() {
        return this.m_arg1;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        if (null != this.m_arg1) {
            this.m_arg1.fixupVariables(vars, globalsSize);
        }
    }

    @Override
    public void setArg(Expression arg, int argNum) throws WrongNumberArgsException {
        if (argNum == 0) {
            super.setArg(arg, argNum);
        } else if (1 == argNum) {
            this.m_arg1 = arg;
            arg.exprSetParent(this);
        } else {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
        if (argNum != 2) {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("two", null));
    }

    @Override
    public boolean canTraverseOutsideSubtree() {
        return super.canTraverseOutsideSubtree() ? true : this.m_arg1.canTraverseOutsideSubtree();
    }

    @Override
    public void callArgVisitors(XPathVisitor visitor) {
        super.callArgVisitors(visitor);
        if (null != this.m_arg1) {
            this.m_arg1.callVisitors(new Arg1Owner(), visitor);
        }
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        if (null != this.m_arg1) {
            if (null == ((Function2Args)expr).m_arg1) {
                return false;
            }
            if (!this.m_arg1.deepEquals(((Function2Args)expr).m_arg1)) {
                return false;
            }
        } else if (null != ((Function2Args)expr).m_arg1) {
            return false;
        }
        return true;
    }

    class Arg1Owner
    implements ExpressionOwner {
        Arg1Owner() {
        }

        @Override
        public Expression getExpression() {
            return Function2Args.this.m_arg1;
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(Function2Args.this);
            Function2Args.this.m_arg1 = exp;
        }
    }
}

