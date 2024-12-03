/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtEndPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;

public interface CTSdtRun
extends XmlObject {
    public static final DocumentFactory<CTSdtRun> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtrun5c60type");
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

    public CTSdtContentRun getSdtContent();

    public boolean isSetSdtContent();

    public void setSdtContent(CTSdtContentRun var1);

    public CTSdtContentRun addNewSdtContent();

    public void unsetSdtContent();
}

