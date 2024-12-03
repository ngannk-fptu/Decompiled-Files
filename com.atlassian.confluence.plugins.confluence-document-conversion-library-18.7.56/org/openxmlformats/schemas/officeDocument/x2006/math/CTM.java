/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMPr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMR;

public interface CTM
extends XmlObject {
    public static final DocumentFactory<CTM> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctm3f8ftype");
    public static final SchemaType type = Factory.getType();

    public CTMPr getMPr();

    public boolean isSetMPr();

    public void setMPr(CTMPr var1);

    public CTMPr addNewMPr();

    public void unsetMPr();

    public List<CTMR> getMrList();

    public CTMR[] getMrArray();

    public CTMR getMrArray(int var1);

    public int sizeOfMrArray();

    public void setMrArray(CTMR[] var1);

    public void setMrArray(int var1, CTMR var2);

    public CTMR insertNewMr(int var1);

    public CTMR addNewMr();

    public void removeMr(int var1);
}

