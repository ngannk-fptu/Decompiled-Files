/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationImeMode
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationErrorStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationImeMode;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationOperator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;

public interface CTDataValidation
extends XmlObject {
    public static final DocumentFactory<CTDataValidation> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdatavalidation9d0ctype");
    public static final SchemaType type = Factory.getType();

    public String getFormula1();

    public STFormula xgetFormula1();

    public boolean isSetFormula1();

    public void setFormula1(String var1);

    public void xsetFormula1(STFormula var1);

    public void unsetFormula1();

    public String getFormula2();

    public STFormula xgetFormula2();

    public boolean isSetFormula2();

    public void setFormula2(String var1);

    public void xsetFormula2(STFormula var1);

    public void unsetFormula2();

    public STDataValidationType.Enum getType();

    public STDataValidationType xgetType();

    public boolean isSetType();

    public void setType(STDataValidationType.Enum var1);

    public void xsetType(STDataValidationType var1);

    public void unsetType();

    public STDataValidationErrorStyle.Enum getErrorStyle();

    public STDataValidationErrorStyle xgetErrorStyle();

    public boolean isSetErrorStyle();

    public void setErrorStyle(STDataValidationErrorStyle.Enum var1);

    public void xsetErrorStyle(STDataValidationErrorStyle var1);

    public void unsetErrorStyle();

    public STDataValidationImeMode.Enum getImeMode();

    public STDataValidationImeMode xgetImeMode();

    public boolean isSetImeMode();

    public void setImeMode(STDataValidationImeMode.Enum var1);

    public void xsetImeMode(STDataValidationImeMode var1);

    public void unsetImeMode();

    public STDataValidationOperator.Enum getOperator();

    public STDataValidationOperator xgetOperator();

    public boolean isSetOperator();

    public void setOperator(STDataValidationOperator.Enum var1);

    public void xsetOperator(STDataValidationOperator var1);

    public void unsetOperator();

    public boolean getAllowBlank();

    public XmlBoolean xgetAllowBlank();

    public boolean isSetAllowBlank();

    public void setAllowBlank(boolean var1);

    public void xsetAllowBlank(XmlBoolean var1);

    public void unsetAllowBlank();

    public boolean getShowDropDown();

    public XmlBoolean xgetShowDropDown();

    public boolean isSetShowDropDown();

    public void setShowDropDown(boolean var1);

    public void xsetShowDropDown(XmlBoolean var1);

    public void unsetShowDropDown();

    public boolean getShowInputMessage();

    public XmlBoolean xgetShowInputMessage();

    public boolean isSetShowInputMessage();

    public void setShowInputMessage(boolean var1);

    public void xsetShowInputMessage(XmlBoolean var1);

    public void unsetShowInputMessage();

    public boolean getShowErrorMessage();

    public XmlBoolean xgetShowErrorMessage();

    public boolean isSetShowErrorMessage();

    public void setShowErrorMessage(boolean var1);

    public void xsetShowErrorMessage(XmlBoolean var1);

    public void unsetShowErrorMessage();

    public String getErrorTitle();

    public STXstring xgetErrorTitle();

    public boolean isSetErrorTitle();

    public void setErrorTitle(String var1);

    public void xsetErrorTitle(STXstring var1);

    public void unsetErrorTitle();

    public String getError();

    public STXstring xgetError();

    public boolean isSetError();

    public void setError(String var1);

    public void xsetError(STXstring var1);

    public void unsetError();

    public String getPromptTitle();

    public STXstring xgetPromptTitle();

    public boolean isSetPromptTitle();

    public void setPromptTitle(String var1);

    public void xsetPromptTitle(STXstring var1);

    public void unsetPromptTitle();

    public String getPrompt();

    public STXstring xgetPrompt();

    public boolean isSetPrompt();

    public void setPrompt(String var1);

    public void xsetPrompt(STXstring var1);

    public void unsetPrompt();

    public List getSqref();

    public STSqref xgetSqref();

    public void setSqref(List var1);

    public void xsetSqref(STSqref var1);
}

