/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTBuildList
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBuildList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTimeNodeList;

public interface CTSlideTiming
extends XmlObject {
    public static final DocumentFactory<CTSlideTiming> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslidetiming4214type");
    public static final SchemaType type = Factory.getType();

    public CTTimeNodeList getTnLst();

    public boolean isSetTnLst();

    public void setTnLst(CTTimeNodeList var1);

    public CTTimeNodeList addNewTnLst();

    public void unsetTnLst();

    public CTBuildList getBldLst();

    public boolean isSetBldLst();

    public void setBldLst(CTBuildList var1);

    public CTBuildList addNewBldLst();

    public void unsetBldLst();

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();
}

