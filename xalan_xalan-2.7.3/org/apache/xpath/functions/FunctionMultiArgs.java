/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.Vector;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.WrongNumberArgsException;

public class FunctionMultiArgs
extends Function3Args {
    static final long serialVersionUID = 7117257746138417181L;
    Expression[] m_args;

    public Expression[] getArgs() {
        return this.m_args;
    }

    @Override
    public void setArg(Expression arg, int argNum) throws WrongNumberArgsException {
        if (argNum < 3) {
            super.setArg(arg, argNum);
        } else {
            if (null == this.m_args) {
                this.m_args = new Expression[1];
                this.m_args[0] = arg;
            } else {
                Expression[] args = new Expression[this.m_args.length + 1];
                System.arraycopy(this.m_args, 0, args, 0, this.m_args.length);
                args[this.m_args.length] = arg;
                this.m_args = args;
            }
            arg.exprSetParent(this);
        }
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        if (null != this.m_args) {
            for (int i = 0; i < this.m_args.length; ++i) {
                this.m_args[i].fixupVariables(vars, globalsSize);
            }
        }
    }

    @Override
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
    }

    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        String fMsg = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[]{"Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called."});
        throw new RuntimeException(fMsg);
    }

    @Override
    public boolean canTraverseOutsideSubtree() {
        if (super.canTraverseOutsideSubtree()) {
            return true;
        }
        int n = this.m_args.length;
        for (int i = 0; i < n; ++i) {
            if (!this.m_args[i].canTraverseOutsideSubtree()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void callArgVisitors(XPathVisitor visitor) {
        super.callArgVisitors(visitor);
        if (null != this.m_args) {
            int n = this.m_args.length;
            for (int i = 0; i < n; ++i) {
                this.m_args[i].callVisitors(new ArgMultiOwner(i), visitor);
            }
        }
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        FunctionMultiArgs fma = (FunctionMultiArgs)expr;
        if (null != this.m_args) {
            int n = this.m_args.length;
            if (null == fma || fma.m_args.length != n) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (this.m_args[i].deepEquals(fma.m_args[i])) continue;
                return false;
            }
        } else if (null != fma.m_args) {
            return false;
        }
        return true;
    }

    class ArgMultiOwner
    implements ExpressionOwner {
        int m_argIndex;

        ArgMultiOwner(int index) {
            this.m_argIndex = index;
        }

        @Override
        public Expression getExpression() {
            return FunctionMultiArgs.this.m_args[this.m_argIndex];
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(FunctionMultiArgs.this);
            FunctionMultiArgs.this.m_args[this.m_argIndex] = exp;
        }
    }
}

