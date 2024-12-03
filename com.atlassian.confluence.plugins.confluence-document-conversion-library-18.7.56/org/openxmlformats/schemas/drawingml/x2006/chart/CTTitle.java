/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTTitle
extends XmlObject {
    public static final DocumentFactory<CTTitle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttitleb54etype");
    public static final SchemaType type = Factory.getType();

    public CTTx getTx();

    public boolean isSetTx();

    public void setTx(CTTx var1);

    public CTTx addNewTx();

    public void unsetTx();

    public CTLayout getLayout();

    public boolean isSetLayout();

    public void setLayout(CTLayout var1);

    public CTLayout addNewLayout();

    public void unsetLayout();

    public CTBoolean getOverlay();

    public boolean isSetOverlay();

    public void setOverlay(CTBoolean var1);

    public CTBoolean addNewOverlay();

    public void unsetOverlay();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTTextBody getTxPr();

    public boolean isSetTxPr();

    public void setTxPr(CTTextBody var1);

    public CTTextBody addNewTxPr();

    public void unsetTxPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

