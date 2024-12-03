/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNotationDeclaration;

public interface ElementPSVI
extends ItemPSVI {
    public XSElementDeclaration getElementDeclaration();

    public XSNotationDeclaration getNotation();

    public boolean getNil();

    public XSModel getSchemaInformation();
}

