/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtEndPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;

public interface CTSdtCell
extends XmlObject {
    public static final DocumentFactory<CTSdtCell> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtcell626dtype");
    public static final SchemaType type = Factory.getType();

    public CTSdtPr getSdtPr();

    public boolean isSetSdtPr();

    public void setSdtPr(CTSdtPr var1);

    public CTSdtPr addNewSdtPr();

    public void unsetSdtPr();

    public CTSdtEndPr getSdtEndPr();

    public boolean isSetSdtEndPr();

    public void setSdtEndPr(CTSdtEndPr var1);

    public CTSdtEndPr addNewSdtEndPr();

    public void unsetSdtEndPr();

    public CTSdtContentCell getSdtContent();

    public boolean isSetSdtContent();

    public void setSdtContent(CTSdtContentCell var1);

    public CTSdtContentCell addNewSdtContent();

    public void unsetSdtContent();
}

