/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTGradientFill
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTGradientFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;

public interface CTFill
extends XmlObject {
    public static final DocumentFactory<CTFill> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfill550ctype");
    public static final SchemaType type = Factory.getType();

    public CTPatternFill getPatternFill();

    public boolean isSetPatternFill();

    public void setPatternFill(CTPatternFill var1);

    public CTPatternFill addNewPatternFill();

    public void unsetPatternFill();

    public CTGradientFill getGradientFill();

    public boolean isSetGradientFill();

    public void setGradientFill(CTGradientFill var1);

    public CTGradientFill addNewGradientFill();

    public void unsetGradientFill();
}

