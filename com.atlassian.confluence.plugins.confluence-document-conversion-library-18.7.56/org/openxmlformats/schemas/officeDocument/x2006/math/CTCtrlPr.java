/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMathCtrlDel
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMathCtrlIns
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMathCtrlDel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMathCtrlIns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

public interface CTCtrlPr
extends XmlObject {
    public static final DocumentFactory<CTCtrlPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctctrlprea05type");
    public static final SchemaType type = Factory.getType();

    public CTRPr getRPr();

    public boolean isSetRPr();

    public void setRPr(CTRPr var1);

    public CTRPr addNewRPr();

    public void unsetRPr();

    public CTMathCtrlIns getIns();

    public boolean isSetIns();

    public void setIns(CTMathCtrlIns var1);

    public CTMathCtrlIns addNewIns();

    public void unsetIns();

    public CTMathCtrlDel getDel();

    public boolean isSetDel();

    public void setDel(CTMathCtrlDel var1);

    public CTMathCtrlDel addNewDel();

    public void unsetDel();
}

