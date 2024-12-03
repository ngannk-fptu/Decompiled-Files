/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangeNumbering
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFData;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangeNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;

public interface CTFldChar
extends XmlObject {
    public static final DocumentFactory<CTFldChar> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfldchare83etype");
    public static final SchemaType type = Factory.getType();

    public CTText getFldData();

    public boolean isSetFldData();

    public void setFldData(CTText var1);

    public CTText addNewFldData();

    public void unsetFldData();

    public CTFFData getFfData();

    public boolean isSetFfData();

    public void setFfData(CTFFData var1);

    public CTFFData addNewFfData();

    public void unsetFfData();

    public CTTrackChangeNumbering getNumberingChange();

    public boolean isSetNumberingChange();

    public void setNumberingChange(CTTrackChangeNumbering var1);

    public CTTrackChangeNumbering addNewNumberingChange();

    public void unsetNumberingChange();

    public STFldCharType.Enum getFldCharType();

    public STFldCharType xgetFldCharType();

    public void setFldCharType(STFldCharType.Enum var1);

    public void xsetFldCharType(STFldCharType var1);

    public Object getFldLock();

    public STOnOff xgetFldLock();

    public boolean isSetFldLock();

    public void setFldLock(Object var1);

    public void xsetFldLock(STOnOff var1);

    public void unsetFldLock();

    public Object getDirty();

    public STOnOff xgetDirty();

    public boolean isSetDirty();

    public void setDirty(Object var1);

    public void xsetDirty(STOnOff var1);

    public void unsetDirty();
}

