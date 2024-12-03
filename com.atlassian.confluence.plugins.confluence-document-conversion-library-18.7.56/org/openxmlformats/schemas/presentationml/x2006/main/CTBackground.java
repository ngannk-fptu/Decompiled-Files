/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlackWhiteMode;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;

public interface CTBackground
extends XmlObject {
    public static final DocumentFactory<CTBackground> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbackground36f7type");
    public static final SchemaType type = Factory.getType();

    public CTBackgroundProperties getBgPr();

    public boolean isSetBgPr();

    public void setBgPr(CTBackgroundProperties var1);

    public CTBackgroundProperties addNewBgPr();

    public void unsetBgPr();

    public CTStyleMatrixReference getBgRef();

    public boolean isSetBgRef();

    public void setBgRef(CTStyleMatrixReference var1);

    public CTStyleMatrixReference addNewBgRef();

    public void unsetBgRef();

    public STBlackWhiteMode.Enum getBwMode();

    public STBlackWhiteMode xgetBwMode();

    public boolean isSetBwMode();

    public void setBwMode(STBlackWhiteMode.Enum var1);

    public void xsetBwMode(STBlackWhiteMode var1);

    public void unsetBwMode();
}

