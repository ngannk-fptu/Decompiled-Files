/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASElementDeclaration;
import org.apache.xerces.dom3.as.ASModel;
import org.apache.xerces.dom3.as.ASObjectList;
import org.apache.xerces.dom3.as.DOMASException;
import org.w3c.dom.DOMException;

public interface DocumentAS {
    public ASModel getActiveASModel();

    public void setActiveASModel(ASModel var1);

    public ASObjectList getBoundASModels();

    public void setBoundASModels(ASObjectList var1);

    public ASModel getInternalAS();

    public void setInternalAS(ASModel var1);

    public void addAS(ASModel var1);

    public void removeAS(ASModel var1);

    public ASElementDeclaration getElementDeclaration() throws DOMException;

    public void validate() throws DOMASException;
}

