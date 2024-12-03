/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspTagException
 */
package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspTagException;
import org.springframework.lang.Nullable;

public interface ArgumentAware {
    public void addArgument(@Nullable Object var1) throws JspTagException;
}

