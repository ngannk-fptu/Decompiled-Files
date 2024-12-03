/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheHierarchies
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedItems
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedMembers
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDimensions
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureDimensionMaps
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureGroups
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPCDKPIs
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTupleCache
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheHierarchies;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheSource;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedMembers;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDimensions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureDimensionMaps;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureGroups;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPCDKPIs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTupleCache;

public interface CTPivotCacheDefinition
extends XmlObject {
    public static final DocumentFactory<CTPivotCacheDefinition> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpivotcachedefinition575ctype");
    public static final SchemaType type = Factory.getType();

    public CTCacheSource getCacheSource();

    public void setCacheSource(CTCacheSource var1);

    public CTCacheSource addNewCacheSource();

    public CTCacheFields getCacheFields();

    public void setCacheFields(CTCacheFields var1);

    public CTCacheFields addNewCacheFields();

    public CTCacheHierarchies getCacheHierarchies();

    public boolean isSetCacheHierarchies();

    public void setCacheHierarchies(CTCacheHierarchies var1);

    public CTCacheHierarchies addNewCacheHierarchies();

    public void unsetCacheHierarchies();

    public CTPCDKPIs getKpis();

    public boolean isSetKpis();

    public void setKpis(CTPCDKPIs var1);

    public CTPCDKPIs addNewKpis();

    public void unsetKpis();

    public CTTupleCache getTupleCache();

    public boolean isSetTupleCache();

    public void setTupleCache(CTTupleCache var1);

    public CTTupleCache addNewTupleCache();

    public void unsetTupleCache();

    public CTCalculatedItems getCalculatedItems();

    public boolean isSetCalculatedItems();

    public void setCalculatedItems(CTCalculatedItems var1);

    public CTCalculatedItems addNewCalculatedItems();

    public void unsetCalculatedItems();

    public CTCalculatedMembers getCalculatedMembers();

    public boolean isSetCalculatedMembers();

    public void setCalculatedMembers(CTCalculatedMembers var1);

    public CTCalculatedMembers addNewCalculatedMembers();

    public void unsetCalculatedMembers();

    public CTDimensions getDimensions();

    public boolean isSetDimensions();

    public void setDimensions(CTDimensions var1);

    public CTDimensions addNewDimensions();

    public void unsetDimensions();

    public CTMeasureGroups getMeasureGroups();

    public boolean isSetMeasureGroups();

    public void setMeasureGroups(CTMeasureGroups var1);

    public CTMeasureGroups addNewMeasureGroups();

    public void unsetMeasureGroups();

    public CTMeasureDimensionMaps getMaps();

    public boolean isSetMaps();

    public void setMaps(CTMeasureDimensionMaps var1);

    public CTMeasureDimensionMaps addNewMaps();

    public void unsetMaps();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();

    public boolean getInvalid();

    public XmlBoolean xgetInvalid();

    public boolean isSetInvalid();

    public void setInvalid(boolean var1);

    public void xsetInvalid(XmlBoolean var1);

    public void unsetInvalid();

    public boolean getSaveData();

    public XmlBoolean xgetSaveData();

    public boolean isSetSaveData();

    public void setSaveData(boolean var1);

    public void xsetSaveData(XmlBoolean var1);

    public void unsetSaveData();

    public boolean getRefreshOnLoad();

    public XmlBoolean xgetRefreshOnLoad();

    public boolean isSetRefreshOnLoad();

    public void setRefreshOnLoad(boolean var1);

    public void xsetRefreshOnLoad(XmlBoolean var1);

    public void unsetRefreshOnLoad();

    public boolean getOptimizeMemory();

    public XmlBoolean xgetOptimizeMemory();

    public boolean isSetOptimizeMemory();

    public void setOptimizeMemory(boolean var1);

    public void xsetOptimizeMemory(XmlBoolean var1);

    public void unsetOptimizeMemory();

    public boolean getEnableRefresh();

    public XmlBoolean xgetEnableRefresh();

    public boolean isSetEnableRefresh();

    public void setEnableRefresh(boolean var1);

    public void xsetEnableRefresh(XmlBoolean var1);

    public void unsetEnableRefresh();

    public String getRefreshedBy();

    public STXstring xgetRefreshedBy();

    public boolean isSetRefreshedBy();

    public void setRefreshedBy(String var1);

    public void xsetRefreshedBy(STXstring var1);

    public void unsetRefreshedBy();

    public double getRefreshedDate();

    public XmlDouble xgetRefreshedDate();

    public boolean isSetRefreshedDate();

    public void setRefreshedDate(double var1);

    public void xsetRefreshedDate(XmlDouble var1);

    public void unsetRefreshedDate();

    public Calendar getRefreshedDateIso();

    public XmlDateTime xgetRefreshedDateIso();

    public boolean isSetRefreshedDateIso();

    public void setRefreshedDateIso(Calendar var1);

    public void xsetRefreshedDateIso(XmlDateTime var1);

    public void unsetRefreshedDateIso();

    public boolean getBackgroundQuery();

    public XmlBoolean xgetBackgroundQuery();

    public boolean isSetBackgroundQuery();

    public void setBackgroundQuery(boolean var1);

    public void xsetBackgroundQuery(XmlBoolean var1);

    public void unsetBackgroundQuery();

    public long getMissingItemsLimit();

    public XmlUnsignedInt xgetMissingItemsLimit();

    public boolean isSetMissingItemsLimit();

    public void setMissingItemsLimit(long var1);

    public void xsetMissingItemsLimit(XmlUnsignedInt var1);

    public void unsetMissingItemsLimit();

    public short getCreatedVersion();

    public XmlUnsignedByte xgetCreatedVersion();

    public boolean isSetCreatedVersion();

    public void setCreatedVersion(short var1);

    public void xsetCreatedVersion(XmlUnsignedByte var1);

    public void unsetCreatedVersion();

    public short getRefreshedVersion();

    public XmlUnsignedByte xgetRefreshedVersion();

    public boolean isSetRefreshedVersion();

    public void setRefreshedVersion(short var1);

    public void xsetRefreshedVersion(XmlUnsignedByte var1);

    public void unsetRefreshedVersion();

    public short getMinRefreshableVersion();

    public XmlUnsignedByte xgetMinRefreshableVersion();

    public boolean isSetMinRefreshableVersion();

    public void setMinRefreshableVersion(short var1);

    public void xsetMinRefreshableVersion(XmlUnsignedByte var1);

    public void unsetMinRefreshableVersion();

    public long getRecordCount();

    public XmlUnsignedInt xgetRecordCount();

    public boolean isSetRecordCount();

    public void setRecordCount(long var1);

    public void xsetRecordCount(XmlUnsignedInt var1);

    public void unsetRecordCount();

    public boolean getUpgradeOnRefresh();

    public XmlBoolean xgetUpgradeOnRefresh();

    public boolean isSetUpgradeOnRefresh();

    public void setUpgradeOnRefresh(boolean var1);

    public void xsetUpgradeOnRefresh(XmlBoolean var1);

    public void unsetUpgradeOnRefresh();

    public boolean getTupleCache2();

    public XmlBoolean xgetTupleCache2();

    public boolean isSetTupleCache2();

    public void setTupleCache2(boolean var1);

    public void xsetTupleCache2(XmlBoolean var1);

    public void unsetTupleCache2();

    public boolean getSupportSubquery();

    public XmlBoolean xgetSupportSubquery();

    public boolean isSetSupportSubquery();

    public void setSupportSubquery(boolean var1);

    public void xsetSupportSubquery(XmlBoolean var1);

    public void unsetSupportSubquery();

    public boolean getSupportAdvancedDrill();

    public XmlBoolean xgetSupportAdvancedDrill();

    public boolean isSetSupportAdvancedDrill();

    public void setSupportAdvancedDrill(boolean var1);

    public void xsetSupportAdvancedDrill(XmlBoolean var1);

    public void unsetSupportAdvancedDrill();
}

