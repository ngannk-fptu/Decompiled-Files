/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Iterator;
import java.util.Map;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;

public class IteratorPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Iterator iterator = (Iterator)target;
        Object result = name instanceof String ? (name.equals("next") ? iterator.next() : (name.equals("hasNext") ? (iterator.hasNext() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name))) : super.getProperty(context, target, name);
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        throw new IllegalArgumentException("can't set property " + name + " on Iterator");
    }
}

