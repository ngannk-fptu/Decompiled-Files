/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.CTRegroupTable
 *  com.microsoft.schemas.office.office.CTRules
 */
package com.microsoft.schemas.office.office;

import com.microsoft.schemas.office.office.CTIdMap;
import com.microsoft.schemas.office.office.CTRegroupTable;
import com.microsoft.schemas.office.office.CTRules;
import com.microsoft.schemas.vml.STExt;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTShapeLayout
extends XmlObject {
    public static final DocumentFactory<CTShapeLayout> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshapelayoutbda4type");
    public static final SchemaType type = Factory.getType();

    public CTIdMap getIdmap();

    public boolean isSetIdmap();

    public void setIdmap(CTIdMap var1);

    public CTIdMap addNewIdmap();

    public void unsetIdmap();

    public CTRegroupTable getRegrouptable();

    public boolean isSetRegrouptable();

    public void setRegrouptable(CTRegroupTable var1);

    public CTRegroupTable addNewRegrouptable();

    public void unsetRegrouptable();

    public CTRules getRules();

    public boolean isSetRules();

    public void setRules(CTRules var1);

    public CTRules addNewRules();

    public void unsetRules();

    public STExt.Enum getExt();

    public STExt xgetExt();

    public boolean isSetExt();

    public void setExt(STExt.Enum var1);

    public void xsetExt(STExt var1);

    public void unsetExt();
}

