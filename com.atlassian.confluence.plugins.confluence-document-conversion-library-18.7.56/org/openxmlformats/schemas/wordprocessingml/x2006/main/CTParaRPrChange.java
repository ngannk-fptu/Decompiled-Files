/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPrOriginal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;

public interface CTParaRPrChange
extends CTTrackChange {
    public static final DocumentFactory<CTParaRPrChange> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpararprchange986etype");
    public static final SchemaType type = Factory.getType();

    public CTParaRPrOriginal getRPr();

    public void setRPr(CTParaRPrOriginal var1);

    public CTParaRPrOriginal addNewRPr();
}

