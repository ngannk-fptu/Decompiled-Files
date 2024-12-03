/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCalcMode;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRefMode;

public interface CTCalcPr
extends XmlObject {
    public static final DocumentFactory<CTCalcPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcalcprd480type");
    public static final SchemaType type = Factory.getType();

    public long getCalcId();

    public XmlUnsignedInt xgetCalcId();

    public boolean isSetCalcId();

    public void setCalcId(long var1);

    public void xsetCalcId(XmlUnsignedInt var1);

    public void unsetCalcId();

    public STCalcMode.Enum getCalcMode();

    public STCalcMode xgetCalcMode();

    public boolean isSetCalcMode();

    public void setCalcMode(STCalcMode.Enum var1);

    public void xsetCalcMode(STCalcMode var1);

    public void unsetCalcMode();

    public boolean getFullCalcOnLoad();

    public XmlBoolean xgetFullCalcOnLoad();

    public boolean isSetFullCalcOnLoad();

    public void setFullCalcOnLoad(boolean var1);

    public void xsetFullCalcOnLoad(XmlBoolean var1);

    public void unsetFullCalcOnLoad();

    public STRefMode.Enum getRefMode();

    public STRefMode xgetRefMode();

    public boolean isSetRefMode();

    public void setRefMode(STRefMode.Enum var1);

    public void xsetRefMode(STRefMode var1);

    public void unsetRefMode();

    public boolean getIterate();

    public XmlBoolean xgetIterate();

    public boolean isSetIterate();

    public void setIterate(boolean var1);

    public void xsetIterate(XmlBoolean var1);

    public void unsetIterate();

    public long getIterateCount();

    public XmlUnsignedInt xgetIterateCount();

    public boolean isSetIterateCount();

    public void setIterateCount(long var1);

    public void xsetIterateCount(XmlUnsignedInt var1);

    public void unsetIterateCount();

    public double getIterateDelta();

    public XmlDouble xgetIterateDelta();

    public boolean isSetIterateDelta();

    public void setIterateDelta(double var1);

    public void xsetIterateDelta(XmlDouble var1);

    public void unsetIterateDelta();

    public boolean getFullPrecision();

    public XmlBoolean xgetFullPrecision();

    public boolean isSetFullPrecision();

    public void setFullPrecision(boolean var1);

    public void xsetFullPrecision(XmlBoolean var1);

    public void unsetFullPrecision();

    public boolean getCalcCompleted();

    public XmlBoolean xgetCalcCompleted();

    public boolean isSetCalcCompleted();

    public void setCalcCompleted(boolean var1);

    public void xsetCalcCompleted(XmlBoolean var1);

    public void unsetCalcCompleted();

    public boolean getCalcOnSave();

    public XmlBoolean xgetCalcOnSave();

    public boolean isSetCalcOnSave();

    public void setCalcOnSave(boolean var1);

    public void xsetCalcOnSave(XmlBoolean var1);

    public void unsetCalcOnSave();

    public boolean getConcurrentCalc();

    public XmlBoolean xgetConcurrentCalc();

    public boolean isSetConcurrentCalc();

    public void setConcurrentCalc(boolean var1);

    public void xsetConcurrentCalc(XmlBoolean var1);

    public void unsetConcurrentCalc();

    public long getConcurrentManualCount();

    public XmlUnsignedInt xgetConcurrentManualCount();

    public boolean isSetConcurrentManualCount();

    public void setConcurrentManualCount(long var1);

    public void xsetConcurrentManualCount(XmlUnsignedInt var1);

    public void unsetConcurrentManualCount();

    public boolean getForceFullCalc();

    public XmlBoolean xgetForceFullCalc();

    public boolean isSetForceFullCalc();

    public void setForceFullCalc(boolean var1);

    public void xsetForceFullCalc(XmlBoolean var1);

    public void unsetForceFullCalc();
}

