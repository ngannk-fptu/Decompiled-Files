/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.vml.CTH;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTHandles
extends XmlObject {
    public static final DocumentFactory<CTHandles> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthandles5c1ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTH> getHList();

    public CTH[] getHArray();

    public CTH getHArray(int var1);

    public int sizeOfHArray();

    public void setHArray(CTH[] var1);

    public void setHArray(int var1, CTH var2);

    public CTH insertNewH(int var1);

    public CTH addNewH();

    public void removeH(int var1);
}

