/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataRefs
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataRefs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction;

public interface CTDataConsolidate
extends XmlObject {
    public static final DocumentFactory<CTDataConsolidate> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdataconsolidate941etype");
    public static final SchemaType type = Factory.getType();

    public CTDataRefs getDataRefs();

    public boolean isSetDataRefs();

    public void setDataRefs(CTDataRefs var1);

    public CTDataRefs addNewDataRefs();

    public void unsetDataRefs();

    public STDataConsolidateFunction.Enum getFunction();

    public STDataConsolidateFunction xgetFunction();

    public boolean isSetFunction();

    public void setFunction(STDataConsolidateFunction.Enum var1);

    public void xsetFunction(STDataConsolidateFunction var1);

    public void unsetFunction();

    public boolean getStartLabels();

    public XmlBoolean xgetStartLabels();

    public boolean isSetStartLabels();

    public void setStartLabels(boolean var1);

    public void xsetStartLabels(XmlBoolean var1);

    public void unsetStartLabels();

    public boolean getLeftLabels();

    public XmlBoolean xgetLeftLabels();

    public boolean isSetLeftLabels();

    public void setLeftLabels(boolean var1);

    public void xsetLeftLabels(XmlBoolean var1);

    public void unsetLeftLabels();

    public boolean getTopLabels();

    public XmlBoolean xgetTopLabels();

    public boolean isSetTopLabels();

    public void setTopLabels(boolean var1);

    public void xsetTopLabels(XmlBoolean var1);

    public void unsetTopLabels();

    public boolean getLink();

    public XmlBoolean xgetLink();

    public boolean isSetLink();

    public void setLink(boolean var1);

    public void xsetLink(XmlBoolean var1);

    public void unsetLink();
}

