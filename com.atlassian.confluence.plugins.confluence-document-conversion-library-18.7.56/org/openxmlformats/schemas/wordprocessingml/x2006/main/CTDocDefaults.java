/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrDefault;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrDefault;

public interface CTDocDefaults
extends XmlObject {
    public static final DocumentFactory<CTDocDefaults> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdocdefaults2ea8type");
    public static final SchemaType type = Factory.getType();

    public CTRPrDefault getRPrDefault();

    public boolean isSetRPrDefault();

    public void setRPrDefault(CTRPrDefault var1);

    public CTRPrDefault addNewRPrDefault();

    public void unsetRPrDefault();

    public CTPPrDefault getPPrDefault();

    public boolean isSetPPrDefault();

    public void setPPrDefault(CTPPrDefault var1);

    public CTPPrDefault addNewPPrDefault();

    public void unsetPPrDefault();
}

