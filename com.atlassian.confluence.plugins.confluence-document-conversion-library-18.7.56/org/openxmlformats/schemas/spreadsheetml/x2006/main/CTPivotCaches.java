/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCache;

public interface CTPivotCaches
extends XmlObject {
    public static final DocumentFactory<CTPivotCaches> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpivotcaches4f32type");
    public static final SchemaType type = Factory.getType();

    public List<CTPivotCache> getPivotCacheList();

    public CTPivotCache[] getPivotCacheArray();

    public CTPivotCache getPivotCacheArray(int var1);

    public int sizeOfPivotCacheArray();

    public void setPivotCacheArray(CTPivotCache[] var1);

    public void setPivotCacheArray(int var1, CTPivotCache var2);

    public CTPivotCache insertNewPivotCache(int var1);

    public CTPivotCache addNewPivotCache();

    public void removePivotCache(int var1);
}

