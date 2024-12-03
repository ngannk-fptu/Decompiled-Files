/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.ObjectPropertyAccessor
 *  ognl.OgnlException
 */
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.util.Map;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

public class ObjectAccessor
extends ObjectPropertyAccessor {
    public Object getProperty(Map map, Object o, Object o1) throws OgnlException {
        Object obj = super.getProperty(map, o, o1);
        map.put("last.bean.accessed", o.getClass());
        map.put("last.property.accessed", o1.toString());
        ReflectionContextState.updateCurrentPropertyPath(map, o1);
        return obj;
    }

    public void setProperty(Map map, Object o, Object o1, Object o2) throws OgnlException {
        super.setProperty(map, o, o1, o2);
    }
}

