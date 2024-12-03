/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTControlList
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.openxmlformats.schemas.presentationml.x2006.main.CTControlList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;

public interface CTCommonSlideData
extends XmlObject {
    public static final DocumentFactory<CTCommonSlideData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcommonslidedata8c7ftype");
    public static final SchemaType type = Factory.getType();

    public CTBackground getBg();

    public boolean isSetBg();

    public void setBg(CTBackground var1);

    public CTBackground addNewBg();

    public void unsetBg();

    public CTGroupShape getSpTree();

    public void setSpTree(CTGroupShape var1);

    public CTGroupShape addNewSpTree();

    public CTCustomerDataList getCustDataLst();

    public boolean isSetCustDataLst();

    public void setCustDataLst(CTCustomerDataList var1);

    public CTCustomerDataList addNewCustDataLst();

    public void unsetCustDataLst();

    public CTControlList getControls();

    public boolean isSetControls();

    public void setControls(CTControlList var1);

    public CTControlList addNewControls();

    public void unsetControls();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();
}

