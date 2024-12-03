/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTTableBackgroundStyle
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableBackgroundStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTablePartStyle;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;

public interface CTTableStyle
extends XmlObject {
    public static final DocumentFactory<CTTableStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablestyled59etype");
    public static final SchemaType type = Factory.getType();

    public CTTableBackgroundStyle getTblBg();

    public boolean isSetTblBg();

    public void setTblBg(CTTableBackgroundStyle var1);

    public CTTableBackgroundStyle addNewTblBg();

    public void unsetTblBg();

    public CTTablePartStyle getWholeTbl();

    public boolean isSetWholeTbl();

    public void setWholeTbl(CTTablePartStyle var1);

    public CTTablePartStyle addNewWholeTbl();

    public void unsetWholeTbl();

    public CTTablePartStyle getBand1H();

    public boolean isSetBand1H();

    public void setBand1H(CTTablePartStyle var1);

    public CTTablePartStyle addNewBand1H();

    public void unsetBand1H();

    public CTTablePartStyle getBand2H();

    public boolean isSetBand2H();

    public void setBand2H(CTTablePartStyle var1);

    public CTTablePartStyle addNewBand2H();

    public void unsetBand2H();

    public CTTablePartStyle getBand1V();

    public boolean isSetBand1V();

    public void setBand1V(CTTablePartStyle var1);

    public CTTablePartStyle addNewBand1V();

    public void unsetBand1V();

    public CTTablePartStyle getBand2V();

    public boolean isSetBand2V();

    public void setBand2V(CTTablePartStyle var1);

    public CTTablePartStyle addNewBand2V();

    public void unsetBand2V();

    public CTTablePartStyle getLastCol();

    public boolean isSetLastCol();

    public void setLastCol(CTTablePartStyle var1);

    public CTTablePartStyle addNewLastCol();

    public void unsetLastCol();

    public CTTablePartStyle getFirstCol();

    public boolean isSetFirstCol();

    public void setFirstCol(CTTablePartStyle var1);

    public CTTablePartStyle addNewFirstCol();

    public void unsetFirstCol();

    public CTTablePartStyle getLastRow();

    public boolean isSetLastRow();

    public void setLastRow(CTTablePartStyle var1);

    public CTTablePartStyle addNewLastRow();

    public void unsetLastRow();

    public CTTablePartStyle getSeCell();

    public boolean isSetSeCell();

    public void setSeCell(CTTablePartStyle var1);

    public CTTablePartStyle addNewSeCell();

    public void unsetSeCell();

    public CTTablePartStyle getSwCell();

    public boolean isSetSwCell();

    public void setSwCell(CTTablePartStyle var1);

    public CTTablePartStyle addNewSwCell();

    public void unsetSwCell();

    public CTTablePartStyle getFirstRow();

    public boolean isSetFirstRow();

    public void setFirstRow(CTTablePartStyle var1);

    public CTTablePartStyle addNewFirstRow();

    public void unsetFirstRow();

    public CTTablePartStyle getNeCell();

    public boolean isSetNeCell();

    public void setNeCell(CTTablePartStyle var1);

    public CTTablePartStyle addNewNeCell();

    public void unsetNeCell();

    public CTTablePartStyle getNwCell();

    public boolean isSetNwCell();

    public void setNwCell(CTTablePartStyle var1);

    public CTTablePartStyle addNewNwCell();

    public void unsetNwCell();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getStyleId();

    public STGuid xgetStyleId();

    public void setStyleId(String var1);

    public void xsetStyleId(STGuid var1);

    public String getStyleName();

    public XmlString xgetStyleName();

    public void setStyleName(String var1);

    public void xsetStyleName(XmlString var1);
}

