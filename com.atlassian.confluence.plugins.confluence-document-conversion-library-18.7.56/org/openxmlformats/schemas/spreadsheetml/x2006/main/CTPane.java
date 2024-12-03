/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPaneState;

public interface CTPane
extends XmlObject {
    public static final DocumentFactory<CTPane> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpaneaab1type");
    public static final SchemaType type = Factory.getType();

    public double getXSplit();

    public XmlDouble xgetXSplit();

    public boolean isSetXSplit();

    public void setXSplit(double var1);

    public void xsetXSplit(XmlDouble var1);

    public void unsetXSplit();

    public double getYSplit();

    public XmlDouble xgetYSplit();

    public boolean isSetYSplit();

    public void setYSplit(double var1);

    public void xsetYSplit(XmlDouble var1);

    public void unsetYSplit();

    public String getTopLeftCell();

    public STCellRef xgetTopLeftCell();

    public boolean isSetTopLeftCell();

    public void setTopLeftCell(String var1);

    public void xsetTopLeftCell(STCellRef var1);

    public void unsetTopLeftCell();

    public STPane.Enum getActivePane();

    public STPane xgetActivePane();

    public boolean isSetActivePane();

    public void setActivePane(STPane.Enum var1);

    public void xsetActivePane(STPane var1);

    public void unsetActivePane();

    public STPaneState.Enum getState();

    public STPaneState xgetState();

    public boolean isSetState();

    public void setState(STPaneState.Enum var1);

    public void xsetState(STPaneState var1);

    public void unsetState();
}

