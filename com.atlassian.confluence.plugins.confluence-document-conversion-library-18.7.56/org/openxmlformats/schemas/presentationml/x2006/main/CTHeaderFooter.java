/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;

public interface CTHeaderFooter
extends XmlObject {
    public static final DocumentFactory<CTHeaderFooter> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctheaderfooterb29dtype");
    public static final SchemaType type = Factory.getType();

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();

    public boolean getSldNum();

    public XmlBoolean xgetSldNum();

    public boolean isSetSldNum();

    public void setSldNum(boolean var1);

    public void xsetSldNum(XmlBoolean var1);

    public void unsetSldNum();

    public boolean getHdr();

    public XmlBoolean xgetHdr();

    public boolean isSetHdr();

    public void setHdr(boolean var1);

    public void xsetHdr(XmlBoolean var1);

    public void unsetHdr();

    public boolean getFtr();

    public XmlBoolean xgetFtr();

    public boolean isSetFtr();

    public void setFtr(boolean var1);

    public void xsetFtr(XmlBoolean var1);

    public void unsetFtr();

    public boolean getDt();

    public XmlBoolean xgetDt();

    public boolean isSetDt();

    public void setDt(boolean var1);

    public void xsetDt(XmlBoolean var1);

    public void unsetDt();
}

