/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.ObjectPropertyAccessor
 *  ognl.OgnlException
 */
package com.opensymphony.xwork2.ognl.accessor;

import java.util.Map;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import org.apache.struts2.dispatcher.Parameter;

public class ParameterPropertyAccessor
extends ObjectPropertyAccessor {
    public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
        if (target instanceof Parameter) {
            if ("value".equalsIgnoreCase(String.valueOf(oname))) {
                throw new OgnlException("Access to " + oname + " is not allowed! Call parameter name directly!");
            }
            return ((Parameter)target).getObject();
        }
        return super.getProperty(context, target, oname);
    }

    public void setProperty(Map context, Object target, Object oname, Object value) throws OgnlException {
        if (target instanceof Parameter) {
            throw new OgnlException("Access to " + target.getClass().getName() + " is read-only!");
        }
        super.setProperty(context, target, oname, value);
    }
}

