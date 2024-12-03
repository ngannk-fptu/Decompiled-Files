/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;

public interface CTSlideMasterIdList
extends XmlObject {
    public static final DocumentFactory<CTSlideMasterIdList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslidemasteridlist0b63type");
    public static final SchemaType type = Factory.getType();

    public List<CTSlideMasterIdListEntry> getSldMasterIdList();

    public CTSlideMasterIdListEntry[] getSldMasterIdArray();

    public CTSlideMasterIdListEntry getSldMasterIdArray(int var1);

    public int sizeOfSldMasterIdArray();

    public void setSldMasterIdArray(CTSlideMasterIdListEntry[] var1);

    public void setSldMasterIdArray(int var1, CTSlideMasterIdListEntry var2);

    public CTSlideMasterIdListEntry insertNewSldMasterId(int var1);

    public CTSlideMasterIdListEntry addNewSldMasterId();

    public void removeSldMasterId(int var1);
}

