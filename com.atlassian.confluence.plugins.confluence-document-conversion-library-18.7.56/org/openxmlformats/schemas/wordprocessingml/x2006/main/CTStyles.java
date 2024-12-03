/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLatentStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;

public interface CTStyles
extends XmlObject {
    public static final DocumentFactory<CTStyles> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstyles8506type");
    public static final SchemaType type = Factory.getType();

    public CTDocDefaults getDocDefaults();

    public boolean isSetDocDefaults();

    public void setDocDefaults(CTDocDefaults var1);

    public CTDocDefaults addNewDocDefaults();

    public void unsetDocDefaults();

    public CTLatentStyles getLatentStyles();

    public boolean isSetLatentStyles();

    public void setLatentStyles(CTLatentStyles var1);

    public CTLatentStyles addNewLatentStyles();

    public void unsetLatentStyles();

    public List<CTStyle> getStyleList();

    public CTStyle[] getStyleArray();

    public CTStyle getStyleArray(int var1);

    public int sizeOfStyleArray();

    public void setStyleArray(CTStyle[] var1);

    public void setStyleArray(int var1, CTStyle var2);

    public CTStyle insertNewStyle(int var1);

    public CTStyle addNewStyle();

    public void removeStyle(int var1);
}

