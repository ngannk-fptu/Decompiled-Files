/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBackgroundFillStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;

public interface CTStyleMatrix
extends XmlObject {
    public static final DocumentFactory<CTStyleMatrix> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstylematrix1903type");
    public static final SchemaType type = Factory.getType();

    public CTFillStyleList getFillStyleLst();

    public void setFillStyleLst(CTFillStyleList var1);

    public CTFillStyleList addNewFillStyleLst();

    public CTLineStyleList getLnStyleLst();

    public void setLnStyleLst(CTLineStyleList var1);

    public CTLineStyleList addNewLnStyleLst();

    public CTEffectStyleList getEffectStyleLst();

    public void setEffectStyleLst(CTEffectStyleList var1);

    public CTEffectStyleList addNewEffectStyleLst();

    public CTBackgroundFillStyleList getBgFillStyleLst();

    public void setBgFillStyleLst(CTBackgroundFillStyleList var1);

    public CTBackgroundFillStyleList addNewBgFillStyleLst();

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();
}

