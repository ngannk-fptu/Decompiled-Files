/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPerm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEdGrp;

public interface CTPermStart
extends CTPerm {
    public static final DocumentFactory<CTPermStart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpermstart0140type");
    public static final SchemaType type = Factory.getType();

    public STEdGrp.Enum getEdGrp();

    public STEdGrp xgetEdGrp();

    public boolean isSetEdGrp();

    public void setEdGrp(STEdGrp.Enum var1);

    public void xsetEdGrp(STEdGrp var1);

    public void unsetEdGrp();

    public String getEd();

    public STString xgetEd();

    public boolean isSetEd();

    public void setEd(String var1);

    public void xsetEd(STString var1);

    public void unsetEd();

    public BigInteger getColFirst();

    public STDecimalNumber xgetColFirst();

    public boolean isSetColFirst();

    public void setColFirst(BigInteger var1);

    public void xsetColFirst(STDecimalNumber var1);

    public void unsetColFirst();

    public BigInteger getColLast();

    public STDecimalNumber xgetColLast();

    public boolean isSetColLast();

    public void setColLast(BigInteger var1);

    public void xsetColLast(STDecimalNumber var1);

    public void unsetColLast();
}

