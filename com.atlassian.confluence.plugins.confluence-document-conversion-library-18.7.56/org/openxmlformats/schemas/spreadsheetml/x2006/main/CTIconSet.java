/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STIconSetType;

public interface CTIconSet
extends XmlObject {
    public static final DocumentFactory<CTIconSet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cticonset2648type");
    public static final SchemaType type = Factory.getType();

    public List<CTCfvo> getCfvoList();

    public CTCfvo[] getCfvoArray();

    public CTCfvo getCfvoArray(int var1);

    public int sizeOfCfvoArray();

    public void setCfvoArray(CTCfvo[] var1);

    public void setCfvoArray(int var1, CTCfvo var2);

    public CTCfvo insertNewCfvo(int var1);

    public CTCfvo addNewCfvo();

    public void removeCfvo(int var1);

    public STIconSetType.Enum getIconSet();

    public STIconSetType xgetIconSet();

    public boolean isSetIconSet();

    public void setIconSet(STIconSetType.Enum var1);

    public void xsetIconSet(STIconSetType var1);

    public void unsetIconSet();

    public boolean getShowValue();

    public XmlBoolean xgetShowValue();

    public boolean isSetShowValue();

    public void setShowValue(boolean var1);

    public void xsetShowValue(XmlBoolean var1);

    public void unsetShowValue();

    public boolean getPercent();

    public XmlBoolean xgetPercent();

    public boolean isSetPercent();

    public void setPercent(boolean var1);

    public void xsetPercent(XmlBoolean var1);

    public void unsetPercent();

    public boolean getReverse();

    public XmlBoolean xgetReverse();

    public boolean isSetReverse();

    public void setReverse(boolean var1);

    public void xsetReverse(XmlBoolean var1);

    public void unsetReverse();
}

