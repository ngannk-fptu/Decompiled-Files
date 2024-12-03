/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 */
package javax.servlet.jsp;

import javax.servlet.Servlet;

public interface JspPage
extends Servlet {
    public void jspInit();

    public void jspDestroy();
}

