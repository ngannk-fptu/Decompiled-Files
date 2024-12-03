/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASAttributeDeclaration;
import org.apache.xerces.dom3.as.ASContentModel;
import org.apache.xerces.dom3.as.ASElementDeclaration;
import org.apache.xerces.dom3.as.ASEntityDeclaration;
import org.apache.xerces.dom3.as.ASNamedObjectMap;
import org.apache.xerces.dom3.as.ASNotationDeclaration;
import org.apache.xerces.dom3.as.ASObject;
import org.apache.xerces.dom3.as.ASObjectList;
import org.apache.xerces.dom3.as.DOMASException;
import org.w3c.dom.DOMException;

public interface ASModel
extends ASObject {
    public boolean getIsNamespaceAware();

    public short getUsageLocation();

    public String getAsLocation();

    public void setAsLocation(String var1);

    public String getAsHint();

    public void setAsHint(String var1);

    public ASNamedObjectMap getElementDeclarations();

    public ASNamedObjectMap getAttributeDeclarations();

    public ASNamedObjectMap getNotationDeclarations();

    public ASNamedObjectMap getEntityDeclarations();

    public ASNamedObjectMap getContentModelDeclarations();

    public void addASModel(ASModel var1);

    public ASObjectList getASModels();

    public void removeAS(ASModel var1);

    public boolean validate();

    public ASElementDeclaration createASElementDeclaration(String var1, String var2) throws DOMException;

    public ASAttributeDeclaration createASAttributeDeclaration(String var1, String var2) throws DOMException;

    public ASNotationDeclaration createASNotationDeclaration(String var1, String var2, String var3, String var4) throws DOMException;

    public ASEntityDeclaration createASEntityDeclaration(String var1) throws DOMException;

    public ASContentModel createASContentModel(int var1, int var2, short var3) throws DOMASException;
}

