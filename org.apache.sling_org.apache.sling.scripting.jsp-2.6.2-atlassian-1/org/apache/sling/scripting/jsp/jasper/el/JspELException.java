/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.sling.scripting.jsp.jasper.el;

import javax.el.ELException;

public class JspELException
extends ELException {
    public JspELException(String mark, ELException e) {
        super(mark + " " + e.getMessage(), e.getCause());
    }
}

