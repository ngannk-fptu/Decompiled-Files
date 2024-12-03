/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.XSObject;

public interface XSAnnotation
extends XSObject {
    public static final short W3C_DOM_ELEMENT = 1;
    public static final short SAX_CONTENTHANDLER = 2;
    public static final short W3C_DOM_DOCUMENT = 3;

    public boolean writeAnnotation(Object var1, short var2);

    public String getAnnotationString();
}

