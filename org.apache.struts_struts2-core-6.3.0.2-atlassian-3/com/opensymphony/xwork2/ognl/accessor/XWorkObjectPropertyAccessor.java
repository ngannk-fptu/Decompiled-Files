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

public class XWorkObjectPropertyAccessor
extends ObjectPropertyAccessor {
    public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
        context.put("last.bean.accessed", target.getClass());
        context.put("last.property.accessed", oname.toString());
        ReflectionContextState.updateCurrentPropertyPath(context, oname);
        return super.getProperty(context, target, oname);
    }
}

