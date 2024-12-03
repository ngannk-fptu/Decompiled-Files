/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;

public interface CTPPrDefault
extends XmlObject {
    public static final DocumentFactory<CTPPrDefault> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpprdefaultf839type");
    public static final SchemaType type = Factory.getType();

    public CTPPrGeneral getPPr();

    public boolean isSetPPr();

    public void setPPr(CTPPrGeneral var1);

    public CTPPrGeneral addNewPPr();

    public void unsetPPr();
}

