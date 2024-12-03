/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.IteratorPropertyAccessor
 *  ognl.ObjectPropertyAccessor
 *  ognl.OgnlException
 */
package com.opensymphony.xwork2.ognl.accessor;

import java.util.Map;
import ognl.IteratorPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

public class XWorkIteratorPropertyAccessor
extends IteratorPropertyAccessor {
    private final ObjectPropertyAccessor opa = new ObjectPropertyAccessor();

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        this.opa.setProperty(context, target, name, value);
    }
}

