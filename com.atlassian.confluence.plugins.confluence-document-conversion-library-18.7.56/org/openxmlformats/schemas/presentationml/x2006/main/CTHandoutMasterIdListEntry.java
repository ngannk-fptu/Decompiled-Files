/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;

public interface CTHandoutMasterIdListEntry
extends XmlObject {
    public static final DocumentFactory<CTHandoutMasterIdListEntry> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthandoutmasteridlistentryad8dtype");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getId();

    public STRelationshipId xgetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);
}

