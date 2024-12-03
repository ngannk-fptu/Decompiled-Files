/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;

public interface DOMConfiguration {
    public void setParameter(String var1, Object var2) throws DOMException;

    public Object getParameter(String var1) throws DOMException;

    public boolean canSetParameter(String var1, Object var2);

    public DOMStringList getParameterNames();
}

