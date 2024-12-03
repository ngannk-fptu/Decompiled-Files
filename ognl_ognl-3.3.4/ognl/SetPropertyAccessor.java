/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Map;
import java.util.Set;
import ognl.NoSuchPropertyException;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;

public class SetPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Set set = (Set)target;
        if (name instanceof String) {
            Object result = name.equals("size") ? new Integer(set.size()) : (name.equals("iterator") ? set.iterator() : (name.equals("isEmpty") ? (set.isEmpty() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name)));
            return result;
        }
        throw new NoSuchPropertyException(target, name);
    }
}

