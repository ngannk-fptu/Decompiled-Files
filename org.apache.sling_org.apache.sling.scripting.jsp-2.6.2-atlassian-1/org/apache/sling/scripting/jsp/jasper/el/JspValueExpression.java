/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.PropertyNotFoundException
 *  javax.el.PropertyNotWritableException
 *  javax.el.ValueExpression
 */
package org.apache.sling.scripting.jsp.jasper.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import org.apache.sling.scripting.jsp.jasper.el.JspELException;
import org.apache.sling.scripting.jsp.jasper.el.JspPropertyNotFoundException;
import org.apache.sling.scripting.jsp.jasper.el.JspPropertyNotWritableException;

public final class JspValueExpression
extends ValueExpression
implements Externalizable {
    private ValueExpression target;
    private String mark;

    public JspValueExpression() {
    }

    public JspValueExpression(String mark, ValueExpression target) {
        this.target = target;
        this.mark = mark;
    }

    public Class<?> getExpectedType() {
        return this.target.getExpectedType();
    }

    public Class<?> getType(ELContext context) throws NullPointerException, PropertyNotFoundException, ELException {
        try {
            return this.target.getType(context);
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

    public boolean isReadOnly(ELContext context) throws NullPointerException, PropertyNotFoundException, ELException {
        try {
            return this.target.isReadOnly(context);
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

    public void setValue(ELContext context, Object value) throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
        try {
            this.target.setValue(context, value);
        }
        catch (PropertyNotWritableException e) {
            if (e instanceof JspPropertyNotWritableException) {
                throw e;
            }
            throw new JspPropertyNotWritableException(this.mark, e);
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

    public Object getValue(ELContext context) throws NullPointerException, PropertyNotFoundException, ELException {
        try {
            return this.target.getValue(context);
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
        this.target = (ValueExpression)in.readObject();
    }
}

