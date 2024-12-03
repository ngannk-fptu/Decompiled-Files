/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;

public interface CTIgnoredError
extends XmlObject {
    public static final DocumentFactory<CTIgnoredError> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctignorederrorc51ftype");
    public static final SchemaType type = Factory.getType();

    public List getSqref();

    public STSqref xgetSqref();

    public void setSqref(List var1);

    public void xsetSqref(STSqref var1);

    public boolean getEvalError();

    public XmlBoolean xgetEvalError();

    public boolean isSetEvalError();

    public void setEvalError(boolean var1);

    public void xsetEvalError(XmlBoolean var1);

    public void unsetEvalError();

    public boolean getTwoDigitTextYear();

    public XmlBoolean xgetTwoDigitTextYear();

    public boolean isSetTwoDigitTextYear();

    public void setTwoDigitTextYear(boolean var1);

    public void xsetTwoDigitTextYear(XmlBoolean var1);

    public void unsetTwoDigitTextYear();

    public boolean getNumberStoredAsText();

    public XmlBoolean xgetNumberStoredAsText();

    public boolean isSetNumberStoredAsText();

    public void setNumberStoredAsText(boolean var1);

    public void xsetNumberStoredAsText(XmlBoolean var1);

    public void unsetNumberStoredAsText();

    public boolean getFormula();

    public XmlBoolean xgetFormula();

    public boolean isSetFormula();

    public void setFormula(boolean var1);

    public void xsetFormula(XmlBoolean var1);

    public void unsetFormula();

    public boolean getFormulaRange();

    public XmlBoolean xgetFormulaRange();

    public boolean isSetFormulaRange();

    public void setFormulaRange(boolean var1);

    public void xsetFormulaRange(XmlBoolean var1);

    public void unsetFormulaRange();

    public boolean getUnlockedFormula();

    public XmlBoolean xgetUnlockedFormula();

    public boolean isSetUnlockedFormula();

    public void setUnlockedFormula(boolean var1);

    public void xsetUnlockedFormula(XmlBoolean var1);

    public void unsetUnlockedFormula();

    public boolean getEmptyCellReference();

    public XmlBoolean xgetEmptyCellReference();

    public boolean isSetEmptyCellReference();

    public void setEmptyCellReference(boolean var1);

    public void xsetEmptyCellReference(XmlBoolean var1);

    public void unsetEmptyCellReference();

    public boolean getListDataValidation();

    public XmlBoolean xgetListDataValidation();

    public boolean isSetListDataValidation();

    public void setListDataValidation(boolean var1);

    public void xsetListDataValidation(XmlBoolean var1);

    public void unsetListDataValidation();

    public boolean getCalculatedColumn();

    public XmlBoolean xgetCalculatedColumn();

    public boolean isSetCalculatedColumn();

    public void setCalculatedColumn(boolean var1);

    public void xsetCalculatedColumn(XmlBoolean var1);

    public void unsetCalculatedColumn();
}

