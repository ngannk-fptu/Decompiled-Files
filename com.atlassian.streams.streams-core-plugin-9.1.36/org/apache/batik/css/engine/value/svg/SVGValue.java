/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;

public interface SVGValue
extends Value {
    public short getPaintType() throws DOMException;

    public String getUri() throws DOMException;

    public short getColorType() throws DOMException;

    public String getColorProfile() throws DOMException;

    public int getNumberOfColors() throws DOMException;

    public float getColor(int var1) throws DOMException;
}

