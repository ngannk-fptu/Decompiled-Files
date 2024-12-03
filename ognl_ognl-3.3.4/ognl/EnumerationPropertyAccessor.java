/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import java.util.Map;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;

public class EnumerationPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Enumeration e = (Enumeration)target;
        Object result = name instanceof String ? (name.equals("next") || name.equals("nextElement") ? e.nextElement() : (name.equals("hasNext") || name.equals("hasMoreElements") ? (e.hasMoreElements() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name))) : super.getProperty(context, target, name);
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        throw new IllegalArgumentException("can't set property " + name + " on Enumeration");
    }
}

