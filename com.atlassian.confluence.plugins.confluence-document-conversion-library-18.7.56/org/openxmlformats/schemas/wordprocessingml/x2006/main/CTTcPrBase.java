/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeaders
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCnf;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeaders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextDirection;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;

public interface CTTcPrBase
extends XmlObject {
    public static final DocumentFactory<CTTcPrBase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttcprbase93e6type");
    public static final SchemaType type = Factory.getType();

    public CTCnf getCnfStyle();

    public boolean isSetCnfStyle();

    public void setCnfStyle(CTCnf var1);

    public CTCnf addNewCnfStyle();

    public void unsetCnfStyle();

    public CTTblWidth getTcW();

    public boolean isSetTcW();

    public void setTcW(CTTblWidth var1);

    public CTTblWidth addNewTcW();

    public void unsetTcW();

    public CTDecimalNumber getGridSpan();

    public boolean isSetGridSpan();

    public void setGridSpan(CTDecimalNumber var1);

    public CTDecimalNumber addNewGridSpan();

    public void unsetGridSpan();

    public CTHMerge getHMerge();

    public boolean isSetHMerge();

    public void setHMerge(CTHMerge var1);

    public CTHMerge addNewHMerge();

    public void unsetHMerge();

    public CTVMerge getVMerge();

    public boolean isSetVMerge();

    public void setVMerge(CTVMerge var1);

    public CTVMerge addNewVMerge();

    public void unsetVMerge();

    public CTTcBorders getTcBorders();

    public boolean isSetTcBorders();

    public void setTcBorders(CTTcBorders var1);

    public CTTcBorders addNewTcBorders();

    public void unsetTcBorders();

    public CTShd getShd();

    public boolean isSetShd();

    public void setShd(CTShd var1);

    public CTShd addNewShd();

    public void unsetShd();

    public CTOnOff getNoWrap();

    public boolean isSetNoWrap();

    public void setNoWrap(CTOnOff var1);

    public CTOnOff addNewNoWrap();

    public void unsetNoWrap();

    public CTTcMar getTcMar();

    public boolean isSetTcMar();

    public void setTcMar(CTTcMar var1);

    public CTTcMar addNewTcMar();

    public void unsetTcMar();

    public CTTextDirection getTextDirection();

    public boolean isSetTextDirection();

    public void setTextDirection(CTTextDirection var1);

    public CTTextDirection addNewTextDirection();

    public void unsetTextDirection();

    public CTOnOff getTcFitText();

    public boolean isSetTcFitText();

    public void setTcFitText(CTOnOff var1);

    public CTOnOff addNewTcFitText();

    public void unsetTcFitText();

    public CTVerticalJc getVAlign();

    public boolean isSetVAlign();

    public void setVAlign(CTVerticalJc var1);

    public CTVerticalJc addNewVAlign();

    public void unsetVAlign();

    public CTOnOff getHideMark();

    public boolean isSetHideMark();

    public void setHideMark(CTOnOff var1);

    public CTOnOff addNewHideMark();

    public void unsetHideMark();

    public CTHeaders getHeaders();

    public boolean isSetHeaders();

    public void setHeaders(CTHeaders var1);

    public CTHeaders addNewHeaders();

    public void unsetHeaders();
}

