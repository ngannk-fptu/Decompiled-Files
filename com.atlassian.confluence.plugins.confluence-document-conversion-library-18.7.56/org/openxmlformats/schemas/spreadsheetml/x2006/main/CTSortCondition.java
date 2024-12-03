/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STSortBy
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STIconSetType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSortBy;

public interface CTSortCondition
extends XmlObject {
    public static final DocumentFactory<CTSortCondition> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsortconditionc4fctype");
    public static final SchemaType type = Factory.getType();

    public boolean getDescending();

    public XmlBoolean xgetDescending();

    public boolean isSetDescending();

    public void setDescending(boolean var1);

    public void xsetDescending(XmlBoolean var1);

    public void unsetDescending();

    public STSortBy.Enum getSortBy();

    public STSortBy xgetSortBy();

    public boolean isSetSortBy();

    public void setSortBy(STSortBy.Enum var1);

    public void xsetSortBy(STSortBy var1);

    public void unsetSortBy();

    public String getRef();

    public STRef xgetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public String getCustomList();

    public STXstring xgetCustomList();

    public boolean isSetCustomList();

    public void setCustomList(String var1);

    public void xsetCustomList(STXstring var1);

    public void unsetCustomList();

    public long getDxfId();

    public STDxfId xgetDxfId();

    public boolean isSetDxfId();

    public void setDxfId(long var1);

    public void xsetDxfId(STDxfId var1);

    public void unsetDxfId();

    public STIconSetType.Enum getIconSet();

    public STIconSetType xgetIconSet();

    public boolean isSetIconSet();

    public void setIconSet(STIconSetType.Enum var1);

    public void xsetIconSet(STIconSetType var1);

    public void unsetIconSet();

    public long getIconId();

    public XmlUnsignedInt xgetIconId();

    public boolean isSetIconId();

    public void setIconId(long var1);

    public void xsetIconId(XmlUnsignedInt var1);

    public void unsetIconId();
}

