/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFunctionGroup
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFunctionGroup;

public interface CTFunctionGroups
extends XmlObject {
    public static final DocumentFactory<CTFunctionGroups> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfunctiongroupsbfd5type");
    public static final SchemaType type = Factory.getType();

    public List<CTFunctionGroup> getFunctionGroupList();

    public CTFunctionGroup[] getFunctionGroupArray();

    public CTFunctionGroup getFunctionGroupArray(int var1);

    public int sizeOfFunctionGroupArray();

    public void setFunctionGroupArray(CTFunctionGroup[] var1);

    public void setFunctionGroupArray(int var1, CTFunctionGroup var2);

    public CTFunctionGroup insertNewFunctionGroup(int var1);

    public CTFunctionGroup addNewFunctionGroup();

    public void removeFunctionGroup(int var1);

    public long getBuiltInGroupCount();

    public XmlUnsignedInt xgetBuiltInGroupCount();

    public boolean isSetBuiltInGroupCount();

    public void setBuiltInGroupCount(long var1);

    public void xsetBuiltInGroupCount(XmlUnsignedInt var1);

    public void unsetBuiltInGroupCount();
}

