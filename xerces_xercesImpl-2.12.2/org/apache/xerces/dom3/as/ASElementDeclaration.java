/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASAttributeDeclaration;
import org.apache.xerces.dom3.as.ASContentModel;
import org.apache.xerces.dom3.as.ASDataType;
import org.apache.xerces.dom3.as.ASNamedObjectMap;
import org.apache.xerces.dom3.as.ASObject;

public interface ASElementDeclaration
extends ASObject {
    public static final short EMPTY_CONTENTTYPE = 1;
    public static final short ANY_CONTENTTYPE = 2;
    public static final short MIXED_CONTENTTYPE = 3;
    public static final short ELEMENTS_CONTENTTYPE = 4;

    public boolean getStrictMixedContent();

    public void setStrictMixedContent(boolean var1);

    public ASDataType getElementType();

    public void setElementType(ASDataType var1);

    public boolean getIsPCDataOnly();

    public void setIsPCDataOnly(boolean var1);

    public short getContentType();

    public void setContentType(short var1);

    public String getSystemId();

    public void setSystemId(String var1);

    public ASContentModel getAsCM();

    public void setAsCM(ASContentModel var1);

    public ASNamedObjectMap getASAttributeDecls();

    public void setASAttributeDecls(ASNamedObjectMap var1);

    public void addASAttributeDecl(ASAttributeDeclaration var1);

    public ASAttributeDeclaration removeASAttributeDecl(ASAttributeDeclaration var1);
}

