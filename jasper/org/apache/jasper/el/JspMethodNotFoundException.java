/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.MethodNotFoundException
 */
package org.apache.jasper.el;

import javax.el.MethodNotFoundException;

public class JspMethodNotFoundException
extends MethodNotFoundException {
    private static final long serialVersionUID = 1L;

    public JspMethodNotFoundException(String mark, MethodNotFoundException e) {
        super(mark + " " + e.getMessage(), e.getCause());
    }
}

