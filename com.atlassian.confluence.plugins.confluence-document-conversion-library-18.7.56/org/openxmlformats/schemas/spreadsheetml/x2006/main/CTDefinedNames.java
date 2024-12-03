/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;

public interface CTDefinedNames
extends XmlObject {
    public static final DocumentFactory<CTDefinedNames> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdefinednamesce48type");
    public static final SchemaType type = Factory.getType();

    public List<CTDefinedName> getDefinedNameList();

    public CTDefinedName[] getDefinedNameArray();

    public CTDefinedName getDefinedNameArray(int var1);

    public int sizeOfDefinedNameArray();

    public void setDefinedNameArray(CTDefinedName[] var1);

    public void setDefinedNameArray(int var1, CTDefinedName var2);

    public CTDefinedName insertNewDefinedName(int var1);

    public CTDefinedName addNewDefinedName();

    public void removeDefinedName(int var1);
}

