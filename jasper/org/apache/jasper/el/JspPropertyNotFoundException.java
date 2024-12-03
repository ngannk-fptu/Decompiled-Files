/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.PropertyNotFoundException
 */
package org.apache.jasper.el;

import javax.el.PropertyNotFoundException;

public final class JspPropertyNotFoundException
extends PropertyNotFoundException {
    private static final long serialVersionUID = 1L;

    public JspPropertyNotFoundException(String mark, PropertyNotFoundException e) {
        super(mark + " " + e.getMessage(), e.getCause());
    }
}

