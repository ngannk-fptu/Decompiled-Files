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
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideMasterId;

public interface CTSlideMasterIdListEntry
extends XmlObject {
    public static final DocumentFactory<CTSlideMasterIdListEntry> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslidemasteridlistentryae7ftype");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getId();

    public STSlideMasterId xgetId();

    public boolean isSetId();

    public void setId(long var1);

    public void xsetId(STSlideMasterId var1);

    public void unsetId();

    public String getId2();

    public STRelationshipId xgetId2();

    public void setId2(String var1);

    public void xsetId2(STRelationshipId var1);
}

