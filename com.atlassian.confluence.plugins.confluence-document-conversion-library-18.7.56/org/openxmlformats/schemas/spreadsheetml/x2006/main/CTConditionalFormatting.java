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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;

public interface CTConditionalFormatting
extends XmlObject {
    public static final DocumentFactory<CTConditionalFormatting> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctconditionalformatting0deatype");
    public static final SchemaType type = Factory.getType();

    public List<CTCfRule> getCfRuleList();

    public CTCfRule[] getCfRuleArray();

    public CTCfRule getCfRuleArray(int var1);

    public int sizeOfCfRuleArray();

    public void setCfRuleArray(CTCfRule[] var1);

    public void setCfRuleArray(int var1, CTCfRule var2);

    public CTCfRule insertNewCfRule(int var1);

    public CTCfRule addNewCfRule();

    public void removeCfRule(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getPivot();

    public XmlBoolean xgetPivot();

    public boolean isSetPivot();

    public void setPivot(boolean var1);

    public void xsetPivot(XmlBoolean var1);

    public void unsetPivot();

    public List getSqref();

    public STSqref xgetSqref();

    public boolean isSetSqref();

    public void setSqref(List var1);

    public void xsetSqref(STSqref var1);

    public void unsetSqref();
}

