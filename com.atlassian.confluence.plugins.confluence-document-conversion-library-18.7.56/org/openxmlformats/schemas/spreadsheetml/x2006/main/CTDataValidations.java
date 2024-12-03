/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;

public interface CTDataValidations
extends XmlObject {
    public static final DocumentFactory<CTDataValidations> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdatavalidationse46ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTDataValidation> getDataValidationList();

    public CTDataValidation[] getDataValidationArray();

    public CTDataValidation getDataValidationArray(int var1);

    public int sizeOfDataValidationArray();

    public void setDataValidationArray(CTDataValidation[] var1);

    public void setDataValidationArray(int var1, CTDataValidation var2);

    public CTDataValidation insertNewDataValidation(int var1);

    public CTDataValidation addNewDataValidation();

    public void removeDataValidation(int var1);

    public boolean getDisablePrompts();

    public XmlBoolean xgetDisablePrompts();

    public boolean isSetDisablePrompts();

    public void setDisablePrompts(boolean var1);

    public void xsetDisablePrompts(XmlBoolean var1);

    public void unsetDisablePrompts();

    public long getXWindow();

    public XmlUnsignedInt xgetXWindow();

    public boolean isSetXWindow();

    public void setXWindow(long var1);

    public void xsetXWindow(XmlUnsignedInt var1);

    public void unsetXWindow();

    public long getYWindow();

    public XmlUnsignedInt xgetYWindow();

    public boolean isSetYWindow();

    public void setYWindow(long var1);

    public void xsetYWindow(XmlUnsignedInt var1);

    public void unsetYWindow();

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}

