/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import javax.xml.transform.TransformerException;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XObject;

public class XBoolean
extends XObject {
    static final long serialVersionUID = -2964933058866100881L;
    public static final XBoolean S_TRUE = new XBooleanStatic(true);
    public static final XBoolean S_FALSE = new XBooleanStatic(false);
    private final boolean m_val;

    public XBoolean(boolean b) {
        this.m_val = b;
    }

    public XBoolean(Boolean b) {
        this.m_val = b;
        this.setObject(b);
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public String getTypeString() {
        return "#BOOLEAN";
    }

    @Override
    public double num() {
        return this.m_val ? 1.0 : 0.0;
    }

    @Override
    public boolean bool() {
        return this.m_val;
    }

    @Override
    public String str() {
        return this.m_val ? "true" : "false";
    }

    @Override
    public Object object() {
        if (null == this.m_obj) {
            this.setObject(this.m_val ? Boolean.TRUE : Boolean.FALSE);
        }
        return this.m_obj;
    }

    @Override
    public boolean equals(XObject obj2) {
        if (obj2.getType() == 4) {
            return obj2.equals(this);
        }
        try {
            return this.m_val == obj2.bool();
        }
        catch (TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
    }
}

