/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFieldGroup
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTX
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFieldGroup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSharedItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTX;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;

public interface CTCacheField
extends XmlObject {
    public static final DocumentFactory<CTCacheField> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcachefieldae21type");
    public static final SchemaType type = Factory.getType();

    public CTSharedItems getSharedItems();

    public boolean isSetSharedItems();

    public void setSharedItems(CTSharedItems var1);

    public CTSharedItems addNewSharedItems();

    public void unsetSharedItems();

    public CTFieldGroup getFieldGroup();

    public boolean isSetFieldGroup();

    public void setFieldGroup(CTFieldGroup var1);

    public CTFieldGroup addNewFieldGroup();

    public void unsetFieldGroup();

    public List<CTX> getMpMapList();

    public CTX[] getMpMapArray();

    public CTX getMpMapArray(int var1);

    public int sizeOfMpMapArray();

    public void setMpMapArray(CTX[] var1);

    public void setMpMapArray(int var1, CTX var2);

    public CTX insertNewMpMap(int var1);

    public CTX addNewMpMap();

    public void removeMpMap(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getName();

    public STXstring xgetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public String getCaption();

    public STXstring xgetCaption();

    public boolean isSetCaption();

    public void setCaption(String var1);

    public void xsetCaption(STXstring var1);

    public void unsetCaption();

    public String getPropertyName();

    public STXstring xgetPropertyName();

    public boolean isSetPropertyName();

    public void setPropertyName(String var1);

    public void xsetPropertyName(STXstring var1);

    public void unsetPropertyName();

    public boolean getServerField();

    public XmlBoolean xgetServerField();

    public boolean isSetServerField();

    public void setServerField(boolean var1);

    public void xsetServerField(XmlBoolean var1);

    public void unsetServerField();

    public boolean getUniqueList();

    public XmlBoolean xgetUniqueList();

    public boolean isSetUniqueList();

    public void setUniqueList(boolean var1);

    public void xsetUniqueList(XmlBoolean var1);

    public void unsetUniqueList();

    public long getNumFmtId();

    public STNumFmtId xgetNumFmtId();

    public boolean isSetNumFmtId();

    public void setNumFmtId(long var1);

    public void xsetNumFmtId(STNumFmtId var1);

    public void unsetNumFmtId();

    public String getFormula();

    public STXstring xgetFormula();

    public boolean isSetFormula();

    public void setFormula(String var1);

    public void xsetFormula(STXstring var1);

    public void unsetFormula();

    public int getSqlType();

    public XmlInt xgetSqlType();

    public boolean isSetSqlType();

    public void setSqlType(int var1);

    public void xsetSqlType(XmlInt var1);

    public void unsetSqlType();

    public int getHierarchy();

    public XmlInt xgetHierarchy();

    public boolean isSetHierarchy();

    public void setHierarchy(int var1);

    public void xsetHierarchy(XmlInt var1);

    public void unsetHierarchy();

    public long getLevel();

    public XmlUnsignedInt xgetLevel();

    public boolean isSetLevel();

    public void setLevel(long var1);

    public void xsetLevel(XmlUnsignedInt var1);

    public void unsetLevel();

    public boolean getDatabaseField();

    public XmlBoolean xgetDatabaseField();

    public boolean isSetDatabaseField();

    public void setDatabaseField(boolean var1);

    public void xsetDatabaseField(XmlBoolean var1);

    public void unsetDatabaseField();

    public long getMappingCount();

    public XmlUnsignedInt xgetMappingCount();

    public boolean isSetMappingCount();

    public void setMappingCount(long var1);

    public void xsetMappingCount(XmlUnsignedInt var1);

    public void unsetMappingCount();

    public boolean getMemberPropertyField();

    public XmlBoolean xgetMemberPropertyField();

    public boolean isSetMemberPropertyField();

    public void setMemberPropertyField(boolean var1);

    public void xsetMemberPropertyField(XmlBoolean var1);

    public void unsetMemberPropertyField();
}

