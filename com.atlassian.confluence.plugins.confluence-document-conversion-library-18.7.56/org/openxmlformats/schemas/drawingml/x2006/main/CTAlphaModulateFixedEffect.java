/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;

public interface CTAlphaModulateFixedEffect
extends XmlObject {
    public static final DocumentFactory<CTAlphaModulateFixedEffect> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctalphamodulatefixedeffect9769type");
    public static final SchemaType type = Factory.getType();

    public Object getAmt();

    public STPositivePercentage xgetAmt();

    public boolean isSetAmt();

    public void setAmt(Object var1);

    public void xsetAmt(STPositivePercentage var1);

    public void unsetAmt();
}

