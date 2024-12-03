/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHandoutMasterIdListEntry;

public interface CTHandoutMasterIdList
extends XmlObject {
    public static final DocumentFactory<CTHandoutMasterIdList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthandoutmasteridlist5b95type");
    public static final SchemaType type = Factory.getType();

    public CTHandoutMasterIdListEntry getHandoutMasterId();

    public boolean isSetHandoutMasterId();

    public void setHandoutMasterId(CTHandoutMasterIdListEntry var1);

    public CTHandoutMasterIdListEntry addNewHandoutMasterId();

    public void unsetHandoutMasterId();
}

