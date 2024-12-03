/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.smil;

import org.w3c.dom.DOMException;

public interface ElementTimeControl {
    public boolean beginElement() throws DOMException;

    public boolean beginElementAt(float var1) throws DOMException;

    public boolean endElement() throws DOMException;

    public boolean endElementAt(float var1) throws DOMException;
}

