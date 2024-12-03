/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathArg;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSubPr;

public interface CTSSub
extends XmlObject {
    public static final DocumentFactory<CTSSub> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctssubfdc5type");
    public static final SchemaType type = Factory.getType();

    public CTSSubPr getSSubPr();

    public boolean isSetSSubPr();

    public void setSSubPr(CTSSubPr var1);

    public CTSSubPr addNewSSubPr();

    public void unsetSSubPr();

    public CTOMathArg getE();

    public void setE(CTOMathArg var1);

    public CTOMathArg addNewE();

    public CTOMathArg getSub();

    public void setSub(CTOMathArg var1);

    public CTOMathArg addNewSub();
}

