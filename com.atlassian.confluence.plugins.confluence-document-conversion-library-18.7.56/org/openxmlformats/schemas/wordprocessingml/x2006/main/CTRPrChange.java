/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrOriginal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;

public interface CTRPrChange
extends CTTrackChange {
    public static final DocumentFactory<CTRPrChange> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrprchangeeaeetype");
    public static final SchemaType type = Factory.getType();

    public CTRPrOriginal getRPr();

    public void setRPr(CTRPrOriginal var1);

    public CTRPrOriginal addNewRPr();
}

