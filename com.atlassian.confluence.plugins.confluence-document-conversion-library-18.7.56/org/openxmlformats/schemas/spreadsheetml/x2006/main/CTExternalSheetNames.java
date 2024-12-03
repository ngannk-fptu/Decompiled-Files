/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetName;

public interface CTExternalSheetNames
extends XmlObject {
    public static final DocumentFactory<CTExternalSheetNames> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternalsheetnames7eddtype");
    public static final SchemaType type = Factory.getType();

    public List<CTExternalSheetName> getSheetNameList();

    public CTExternalSheetName[] getSheetNameArray();

    public CTExternalSheetName getSheetNameArray(int var1);

    public int sizeOfSheetNameArray();

    public void setSheetNameArray(CTExternalSheetName[] var1);

    public void setSheetNameArray(int var1, CTExternalSheetName var2);

    public CTExternalSheetName insertNewSheetName(int var1);

    public CTExternalSheetName addNewSheetName();

    public void removeSheetName(int var1);
}

