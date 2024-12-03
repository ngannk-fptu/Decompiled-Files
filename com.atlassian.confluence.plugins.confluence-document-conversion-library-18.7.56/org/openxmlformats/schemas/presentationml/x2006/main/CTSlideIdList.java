/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;

public interface CTSlideIdList
extends XmlObject {
    public static final DocumentFactory<CTSlideIdList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslideidlist70a5type");
    public static final SchemaType type = Factory.getType();

    public List<CTSlideIdListEntry> getSldIdList();

    public CTSlideIdListEntry[] getSldIdArray();

    public CTSlideIdListEntry getSldIdArray(int var1);

    public int sizeOfSldIdArray();

    public void setSldIdArray(CTSlideIdListEntry[] var1);

    public void setSldIdArray(int var1, CTSlideIdListEntry var2);

    public CTSlideIdListEntry insertNewSldId(int var1);

    public CTSlideIdListEntry addNewSldId();

    public void removeSldId(int var1);
}

