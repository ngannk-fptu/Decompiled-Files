/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTWorksheetSource
extends XmlObject {
    public static final DocumentFactory<CTWorksheetSource> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctworksheetsourced4c8type");
    public static final SchemaType type = Factory.getType();

    public String getRef();

    public STRef xgetRef();

    public boolean isSetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public void unsetRef();

    public String getName();

    public STXstring xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public void unsetName();

    public String getSheet();

    public STXstring xgetSheet();

    public boolean isSetSheet();

    public void setSheet(String var1);

    public void xsetSheet(STXstring var1);

    public void unsetSheet();

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();
}

