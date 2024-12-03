/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellSpans;

public interface CTRow
extends XmlObject {
    public static final DocumentFactory<CTRow> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrowdd39type");
    public static final SchemaType type = Factory.getType();

    public List<CTCell> getCList();

    public CTCell[] getCArray();

    public CTCell getCArray(int var1);

    public int sizeOfCArray();

    public void setCArray(CTCell[] var1);

    public void setCArray(int var1, CTCell var2);

    public CTCell insertNewC(int var1);

    public CTCell addNewC();

    public void removeC(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getR();

    public XmlUnsignedInt xgetR();

    public boolean isSetR();

    public void setR(long var1);

    public void xsetR(XmlUnsignedInt var1);

    public void unsetR();

    public List getSpans();

    public STCellSpans xgetSpans();

    public boolean isSetSpans();

    public void setSpans(List var1);

    public void xsetSpans(STCellSpans var1);

    public void unsetSpans();

    public long getS();

    public XmlUnsignedInt xgetS();

    public boolean isSetS();

    public void setS(long var1);

    public void xsetS(XmlUnsignedInt var1);

    public void unsetS();

    public boolean getCustomFormat();

    public XmlBoolean xgetCustomFormat();

    public boolean isSetCustomFormat();

    public void setCustomFormat(boolean var1);

    public void xsetCustomFormat(XmlBoolean var1);

    public void unsetCustomFormat();

    public double getHt();

    public XmlDouble xgetHt();

    public boolean isSetHt();

    public void setHt(double var1);

    public void xsetHt(XmlDouble var1);

    public void unsetHt();

    public boolean getHidden();

    public XmlBoolean xgetHidden();

    public boolean isSetHidden();

    public void setHidden(boolean var1);

    public void xsetHidden(XmlBoolean var1);

    public void unsetHidden();

    public boolean getCustomHeight();

    public XmlBoolean xgetCustomHeight();

    public boolean isSetCustomHeight();

    public void setCustomHeight(boolean var1);

    public void xsetCustomHeight(XmlBoolean var1);

    public void unsetCustomHeight();

    public short getOutlineLevel();

    public XmlUnsignedByte xgetOutlineLevel();

    public boolean isSetOutlineLevel();

    public void setOutlineLevel(short var1);

    public void xsetOutlineLevel(XmlUnsignedByte var1);

    public void unsetOutlineLevel();

    public boolean getCollapsed();

    public XmlBoolean xgetCollapsed();

    public boolean isSetCollapsed();

    public void setCollapsed(boolean var1);

    public void xsetCollapsed(XmlBoolean var1);

    public void unsetCollapsed();

    public boolean getThickTop();

    public XmlBoolean xgetThickTop();

    public boolean isSetThickTop();

    public void setThickTop(boolean var1);

    public void xsetThickTop(XmlBoolean var1);

    public void unsetThickTop();

    public boolean getThickBot();

    public XmlBoolean xgetThickBot();

    public boolean isSetThickBot();

    public void setThickBot(boolean var1);

    public void xsetThickBot(XmlBoolean var1);

    public void unsetThickBot();

    public boolean getPh();

    public XmlBoolean xgetPh();

    public boolean isSetPh();

    public void setPh(boolean var1);

    public void xsetPh(XmlBoolean var1);

    public void unsetPh();
}

