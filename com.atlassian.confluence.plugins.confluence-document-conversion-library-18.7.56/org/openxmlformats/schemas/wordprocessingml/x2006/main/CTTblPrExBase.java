/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJcTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblCellMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLook;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;

public interface CTTblPrExBase
extends XmlObject {
    public static final DocumentFactory<CTTblPrExBase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblprexbasee7eetype");
    public static final SchemaType type = Factory.getType();

    public CTTblWidth getTblW();

    public boolean isSetTblW();

    public void setTblW(CTTblWidth var1);

    public CTTblWidth addNewTblW();

    public void unsetTblW();

    public CTJcTable getJc();

    public boolean isSetJc();

    public void setJc(CTJcTable var1);

    public CTJcTable addNewJc();

    public void unsetJc();

    public CTTblWidth getTblCellSpacing();

    public boolean isSetTblCellSpacing();

    public void setTblCellSpacing(CTTblWidth var1);

    public CTTblWidth addNewTblCellSpacing();

    public void unsetTblCellSpacing();

    public CTTblWidth getTblInd();

    public boolean isSetTblInd();

    public void setTblInd(CTTblWidth var1);

    public CTTblWidth addNewTblInd();

    public void unsetTblInd();

    public CTTblBorders getTblBorders();

    public boolean isSetTblBorders();

    public void setTblBorders(CTTblBorders var1);

    public CTTblBorders addNewTblBorders();

    public void unsetTblBorders();

    public CTShd getShd();

    public boolean isSetShd();

    public void setShd(CTShd var1);

    public CTShd addNewShd();

    public void unsetShd();

    public CTTblLayoutType getTblLayout();

    public boolean isSetTblLayout();

    public void setTblLayout(CTTblLayoutType var1);

    public CTTblLayoutType addNewTblLayout();

    public void unsetTblLayout();

    public CTTblCellMar getTblCellMar();

    public boolean isSetTblCellMar();

    public void setTblCellMar(CTTblCellMar var1);

    public CTTblCellMar addNewTblCellMar();

    public void unsetTblCellMar();

    public CTTblLook getTblLook();

    public boolean isSetTblLook();

    public void setTblLook(CTTblLook var1);

    public CTTblLook addNewTblLook();

    public void unsetTblLook();
}

