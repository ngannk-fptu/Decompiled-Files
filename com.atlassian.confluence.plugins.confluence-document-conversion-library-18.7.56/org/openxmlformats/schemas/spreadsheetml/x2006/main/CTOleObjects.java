/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;

public interface CTOleObjects
extends XmlObject {
    public static final DocumentFactory<CTOleObjects> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctoleobjects1455type");
    public static final SchemaType type = Factory.getType();

    public List<CTOleObject> getOleObjectList();

    public CTOleObject[] getOleObjectArray();

    public CTOleObject getOleObjectArray(int var1);

    public int sizeOfOleObjectArray();

    public void setOleObjectArray(CTOleObject[] var1);

    public void setOleObjectArray(int var1, CTOleObject var2);

    public CTOleObject insertNewOleObject(int var1);

    public CTOleObject addNewOleObject();

    public void removeOleObject(int var1);
}

