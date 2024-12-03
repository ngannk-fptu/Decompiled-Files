/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBinding
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBinding;

public interface CTMap
extends XmlObject {
    public static final DocumentFactory<CTMap> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmap023btype");
    public static final SchemaType type = Factory.getType();

    public CTDataBinding getDataBinding();

    public boolean isSetDataBinding();

    public void setDataBinding(CTDataBinding var1);

    public CTDataBinding addNewDataBinding();

    public void unsetDataBinding();

    public long getID();

    public XmlUnsignedInt xgetID();

    public void setID(long var1);

    public void xsetID(XmlUnsignedInt var1);

    public String getName();

    public XmlString xgetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public String getRootElement();

    public XmlString xgetRootElement();

    public void setRootElement(String var1);

    public void xsetRootElement(XmlString var1);

    public String getSchemaID();

    public XmlString xgetSchemaID();

    public void setSchemaID(String var1);

    public void xsetSchemaID(XmlString var1);

    public boolean getShowImportExportValidationErrors();

    public XmlBoolean xgetShowImportExportValidationErrors();

    public void setShowImportExportValidationErrors(boolean var1);

    public void xsetShowImportExportValidationErrors(XmlBoolean var1);

    public boolean getAutoFit();

    public XmlBoolean xgetAutoFit();

    public void setAutoFit(boolean var1);

    public void xsetAutoFit(XmlBoolean var1);

    public boolean getAppend();

    public XmlBoolean xgetAppend();

    public void setAppend(boolean var1);

    public void xsetAppend(XmlBoolean var1);

    public boolean getPreserveSortAFLayout();

    public XmlBoolean xgetPreserveSortAFLayout();

    public void setPreserveSortAFLayout(boolean var1);

    public void xsetPreserveSortAFLayout(XmlBoolean var1);

    public boolean getPreserveFormat();

    public XmlBoolean xgetPreserveFormat();

    public void setPreserveFormat(boolean var1);

    public void xsetPreserveFormat(XmlBoolean var1);
}

