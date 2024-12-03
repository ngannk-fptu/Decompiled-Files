/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.PropertyNotWritableException
 */
package org.apache.sling.scripting.jsp.jasper.el;

import javax.el.PropertyNotWritableException;

public class JspPropertyNotWritableException
extends PropertyNotWritableException {
    public JspPropertyNotWritableException(String mark, PropertyNotWritableException e) {
        super(mark + " " + e.getMessage(), e.getCause());
    }
}

