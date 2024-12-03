/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedNames;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetDataSet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetNames;

public interface CTExternalBook
extends XmlObject {
    public static final DocumentFactory<CTExternalBook> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternalbookc89dtype");
    public static final SchemaType type = Factory.getType();

    public CTExternalSheetNames getSheetNames();

    public boolean isSetSheetNames();

    public void setSheetNames(CTExternalSheetNames var1);

    public CTExternalSheetNames addNewSheetNames();

    public void unsetSheetNames();

    public CTExternalDefinedNames getDefinedNames();

    public boolean isSetDefinedNames();

    public void setDefinedNames(CTExternalDefinedNames var1);

    public CTExternalDefinedNames addNewDefinedNames();

    public void unsetDefinedNames();

    public CTExternalSheetDataSet getSheetDataSet();

    public boolean isSetSheetDataSet();

    public void setSheetDataSet(CTExternalSheetDataSet var1);

    public CTExternalSheetDataSet addNewSheetDataSet();

    public void unsetSheetDataSet();

    public String getId();

    public STRelationshipId xgetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);
}

