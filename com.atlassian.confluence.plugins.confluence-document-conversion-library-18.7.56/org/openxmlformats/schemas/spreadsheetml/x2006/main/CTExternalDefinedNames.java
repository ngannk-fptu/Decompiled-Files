/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedName;

public interface CTExternalDefinedNames
extends XmlObject {
    public static final DocumentFactory<CTExternalDefinedNames> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternaldefinednamesccf3type");
    public static final SchemaType type = Factory.getType();

    public List<CTExternalDefinedName> getDefinedNameList();

    public CTExternalDefinedName[] getDefinedNameArray();

    public CTExternalDefinedName getDefinedNameArray(int var1);

    public int sizeOfDefinedNameArray();

    public void setDefinedNameArray(CTExternalDefinedName[] var1);

    public void setDefinedNameArray(int var1, CTExternalDefinedName var2);

    public CTExternalDefinedName insertNewDefinedName(int var1);

    public CTExternalDefinedName addNewDefinedName();

    public void removeDefinedName(int var1);
}

