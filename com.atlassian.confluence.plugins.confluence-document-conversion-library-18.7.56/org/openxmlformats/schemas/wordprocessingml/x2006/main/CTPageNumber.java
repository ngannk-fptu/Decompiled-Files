/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STChapterSep
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STChapterSep;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

public interface CTPageNumber
extends XmlObject {
    public static final DocumentFactory<CTPageNumber> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagenumber7570type");
    public static final SchemaType type = Factory.getType();

    public STNumberFormat.Enum getFmt();

    public STNumberFormat xgetFmt();

    public boolean isSetFmt();

    public void setFmt(STNumberFormat.Enum var1);

    public void xsetFmt(STNumberFormat var1);

    public void unsetFmt();

    public BigInteger getStart();

    public STDecimalNumber xgetStart();

    public boolean isSetStart();

    public void setStart(BigInteger var1);

    public void xsetStart(STDecimalNumber var1);

    public void unsetStart();

    public BigInteger getChapStyle();

    public STDecimalNumber xgetChapStyle();

    public boolean isSetChapStyle();

    public void setChapStyle(BigInteger var1);

    public void xsetChapStyle(STDecimalNumber var1);

    public void unsetChapStyle();

    public STChapterSep.Enum getChapSep();

    public STChapterSep xgetChapSep();

    public boolean isSetChapSep();

    public void setChapSep(STChapterSep.Enum var1);

    public void xsetChapSep(STChapterSep var1);

    public void unsetChapSep();
}

