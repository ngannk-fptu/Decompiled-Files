/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;

public interface CTColorScale
extends XmlObject {
    public static final DocumentFactory<CTColorScale> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolorscale1a70type");
    public static final SchemaType type = Factory.getType();

    public List<CTCfvo> getCfvoList();

    public CTCfvo[] getCfvoArray();

    public CTCfvo getCfvoArray(int var1);

    public int sizeOfCfvoArray();

    public void setCfvoArray(CTCfvo[] var1);

    public void setCfvoArray(int var1, CTCfvo var2);

    public CTCfvo insertNewCfvo(int var1);

    public CTCfvo addNewCfvo();

    public void removeCfvo(int var1);

    public List<CTColor> getColorList();

    public CTColor[] getColorArray();

    public CTColor getColorArray(int var1);

    public int sizeOfColorArray();

    public void setColorArray(CTColor[] var1);

    public void setColorArray(int var1, CTColor var2);

    public CTColor insertNewColor(int var1);

    public CTColor addNewColor();

    public void removeColor(int var1);
}

