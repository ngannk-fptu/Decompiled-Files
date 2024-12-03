/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrChange;

public interface CTPPrGeneral
extends CTPPrBase {
    public static final DocumentFactory<CTPPrGeneral> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpprgenerald6f2type");
    public static final SchemaType type = Factory.getType();

    public CTPPrChange getPPrChange();

    public boolean isSetPPrChange();

    public void setPPrChange(CTPPrChange var1);

    public CTPPrChange addNewPPrChange();

    public void unsetPPrChange();
}

