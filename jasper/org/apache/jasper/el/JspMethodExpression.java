/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.MethodExpression
 *  javax.el.MethodInfo
 *  javax.el.MethodNotFoundException
 *  javax.el.PropertyNotFoundException
 */
package org.apache.jasper.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import org.apache.jasper.el.JspELException;
import org.apache.jasper.el.JspMethodNotFoundException;
import org.apache.jasper.el.JspPropertyNotFoundException;

public final class JspMethodExpression
extends MethodExpression
implements Externalizable {
    private String mark;
    private MethodExpression target;

    public JspMethodExpression() {
    }

    public JspMethodExpression(String mark, MethodExpression target) {
        this.target = target;
        this.mark = mark;
    }

    public MethodInfo getMethodInfo(ELContext context) throws NullPointerException, PropertyNotFoundException, MethodNotFoundException, ELException {
        context.notifyBeforeEvaluation(this.getExpressionString());
        try {
            MethodInfo result = this.target.getMethodInfo(context);
            context.notifyAfterEvaluation(this.getExpressionString());
            return result;
        }
        catch (MethodNotFoundException e) {
            if (e instanceof JspMethodNotFoundException) {
                throw e;
            }
            throw new JspMethodNotFoundException(this.mark, e);
        }
        catch (PropertyNotFoundException e) {
            if (e instanceof JspPropertyNotFoundException) {
                throw e;
            }
            throw new JspPropertyNotFoundException(this.mark, e);
        }
        catch (ELException e) {
            if (e instanceof JspELException) {
                throw e;
            }
            throw new JspELException(this.mark, e);
        }
    }

    public Object invoke(ELContext context, Object[] params) throws NullPointerException, PropertyNotFoundException, MethodNotFoundException, ELException {
        context.notifyBeforeEvaluation(this.getExpressionString());
        try {
            Object result = this.target.invoke(context, params);
            context.notifyAfterEvaluation(this.getExpressionString());
            return result;
        }
        catch (MethodNotFoundException e) {
            if (e instanceof JspMethodNotFoundException) {
                throw e;
            }
            throw new JspMethodNotFoundException(this.mark, e);
        }
        catch (PropertyNotFoundException e) {
            if (e instanceof JspPropertyNotFoundException) {
                throw e;
            }
            throw new JspPropertyNotFoundException(this.mark, e);
        }
        catch (ELException e) {
            if (e instanceof JspELException) {
                throw e;
            }
            throw new JspELException(this.mark, e);
        }
    }

    public boolean isParametersProvided() {
        return this.target.isParametersProvided();
    }

    public boolean equals(Object obj) {
        return this.target.equals(obj);
    }

    public int hashCode() {
        return this.target.hashCode();
    }

    public String getExpressionString() {
        return this.target.getExpressionString();
    }

    public boolean isLiteralText() {
        return this.target.isLiteralText();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.mark);
        out.writeObject(this.target);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.mark = in.readUTF();
        this.target = (MethodExpression)in.readObject();
    }
}

