/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPrChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPrInner;

public interface CTTcPr
extends CTTcPrInner {
    public static final DocumentFactory<CTTcPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttcpree37type");
    public static final SchemaType type = Factory.getType();

    public CTTcPrChange getTcPrChange();

    public boolean isSetTcPrChange();

    public void setTcPrChange(CTTcPrChange var1);

    public CTTcPrChange addNewTcPrChange();

    public void unsetTcPrChange();
}

