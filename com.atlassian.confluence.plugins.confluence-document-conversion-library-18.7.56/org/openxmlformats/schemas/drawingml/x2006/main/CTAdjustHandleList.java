/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPolarAdjustHandle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTXYAdjustHandle;

public interface CTAdjustHandleList
extends XmlObject {
    public static final DocumentFactory<CTAdjustHandleList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctadjusthandlelistfdb0type");
    public static final SchemaType type = Factory.getType();

    public List<CTXYAdjustHandle> getAhXYList();

    public CTXYAdjustHandle[] getAhXYArray();

    public CTXYAdjustHandle getAhXYArray(int var1);

    public int sizeOfAhXYArray();

    public void setAhXYArray(CTXYAdjustHandle[] var1);

    public void setAhXYArray(int var1, CTXYAdjustHandle var2);

    public CTXYAdjustHandle insertNewAhXY(int var1);

    public CTXYAdjustHandle addNewAhXY();

    public void removeAhXY(int var1);

    public List<CTPolarAdjustHandle> getAhPolarList();

    public CTPolarAdjustHandle[] getAhPolarArray();

    public CTPolarAdjustHandle getAhPolarArray(int var1);

    public int sizeOfAhPolarArray();

    public void setAhPolarArray(CTPolarAdjustHandle[] var1);

    public void setAhPolarArray(int var1, CTPolarAdjustHandle var2);

    public CTPolarAdjustHandle insertNewAhPolar(int var1);

    public CTPolarAdjustHandle addNewAhPolar();

    public void removeAhPolar(int var1);
}

