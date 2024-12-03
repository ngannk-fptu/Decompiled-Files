/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerData
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTagsData;

public interface CTCustomerDataList
extends XmlObject {
    public static final DocumentFactory<CTCustomerDataList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomerdatalist8b7ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTCustomerData> getCustDataList();

    public CTCustomerData[] getCustDataArray();

    public CTCustomerData getCustDataArray(int var1);

    public int sizeOfCustDataArray();

    public void setCustDataArray(CTCustomerData[] var1);

    public void setCustDataArray(int var1, CTCustomerData var2);

    public CTCustomerData insertNewCustData(int var1);

    public CTCustomerData addNewCustData();

    public void removeCustData(int var1);

    public CTTagsData getTags();

    public boolean isSetTags();

    public void setTags(CTTagsData var1);

    public CTTagsData addNewTags();

    public void unsetTags();
}

