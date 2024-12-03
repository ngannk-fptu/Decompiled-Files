/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

public interface CTSdtEndPr
extends XmlObject {
    public static final DocumentFactory<CTSdtEndPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtendprbc6etype");
    public static final SchemaType type = Factory.getType();

    public List<CTRPr> getRPrList();

    public CTRPr[] getRPrArray();

    public CTRPr getRPrArray(int var1);

    public int sizeOfRPrArray();

    public void setRPrArray(CTRPr[] var1);

    public void setRPrArray(int var1, CTRPr var2);

    public CTRPr insertNewRPr(int var1);

    public CTRPr addNewRPr();

    public void removeRPr(int var1);
}

