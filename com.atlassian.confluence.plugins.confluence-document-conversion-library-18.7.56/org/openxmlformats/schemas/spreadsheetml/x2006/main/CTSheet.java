/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetState;

public interface CTSheet
extends XmlObject {
    public static final DocumentFactory<CTSheet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheet4dbetype");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public STXstring xgetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public long getSheetId();

    public XmlUnsignedInt xgetSheetId();

    public void setSheetId(long var1);

    public void xsetSheetId(XmlUnsignedInt var1);

    public STSheetState.Enum getState();

    public STSheetState xgetState();

    public boolean isSetState();

    public void setState(STSheetState.Enum var1);

    public void xsetState(STSheetState var1);

    public void unsetState();

    public String getId();

    public STRelationshipId xgetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);
}

