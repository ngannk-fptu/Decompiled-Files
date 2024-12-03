/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlPr;

public interface CTXmlCellPr
extends XmlObject {
    public static final DocumentFactory<CTXmlCellPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctxmlcellprf1datype");
    public static final SchemaType type = Factory.getType();

    public CTXmlPr getXmlPr();

    public void setXmlPr(CTXmlPr var1);

    public CTXmlPr addNewXmlPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getId();

    public XmlUnsignedInt xgetId();

    public void setId(long var1);

    public void xsetId(XmlUnsignedInt var1);

    public String getUniqueName();

    public STXstring xgetUniqueName();

    public boolean isSetUniqueName();

    public void setUniqueName(String var1);

    public void xsetUniqueName(STXstring var1);

    public void unsetUniqueName();
}

