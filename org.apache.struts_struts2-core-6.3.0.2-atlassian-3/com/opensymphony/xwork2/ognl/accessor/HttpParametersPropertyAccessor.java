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
import org.apache.struts2.dispatcher.HttpParameters;

public class HttpParametersPropertyAccessor
extends ObjectPropertyAccessor {
    public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
        HttpParameters parameters = (HttpParameters)target;
        return parameters.get(String.valueOf(oname)).getObject();
    }

    public void setProperty(Map context, Object target, Object oname, Object value) throws OgnlException {
        throw new OgnlException("Access to " + target.getClass().getName() + " is read-only!");
    }
}

