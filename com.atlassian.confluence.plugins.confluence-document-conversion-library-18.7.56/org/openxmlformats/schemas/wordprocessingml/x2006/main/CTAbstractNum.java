/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLongHexNumber
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMultiLevelType
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLongHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMultiLevelType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTAbstractNum
extends XmlObject {
    public static final DocumentFactory<CTAbstractNum> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctabstractnum588etype");
    public static final SchemaType type = Factory.getType();

    public CTLongHexNumber getNsid();

    public boolean isSetNsid();

    public void setNsid(CTLongHexNumber var1);

    public CTLongHexNumber addNewNsid();

    public void unsetNsid();

    public CTMultiLevelType getMultiLevelType();

    public boolean isSetMultiLevelType();

    public void setMultiLevelType(CTMultiLevelType var1);

    public CTMultiLevelType addNewMultiLevelType();

    public void unsetMultiLevelType();

    public CTLongHexNumber getTmpl();

    public boolean isSetTmpl();

    public void setTmpl(CTLongHexNumber var1);

    public CTLongHexNumber addNewTmpl();

    public void unsetTmpl();

    public CTString getName();

    public boolean isSetName();

    public void setName(CTString var1);

    public CTString addNewName();

    public void unsetName();

    public CTString getStyleLink();

    public boolean isSetStyleLink();

    public void setStyleLink(CTString var1);

    public CTString addNewStyleLink();

    public void unsetStyleLink();

    public CTString getNumStyleLink();

    public boolean isSetNumStyleLink();

    public void setNumStyleLink(CTString var1);

    public CTString addNewNumStyleLink();

    public void unsetNumStyleLink();

    public List<CTLvl> getLvlList();

    public CTLvl[] getLvlArray();

    public CTLvl getLvlArray(int var1);

    public int sizeOfLvlArray();

    public void setLvlArray(CTLvl[] var1);

    public void setLvlArray(int var1, CTLvl var2);

    public CTLvl insertNewLvl(int var1);

    public CTLvl addNewLvl();

    public void removeLvl(int var1);

    public BigInteger getAbstractNumId();

    public STDecimalNumber xgetAbstractNumId();

    public void setAbstractNumId(BigInteger var1);

    public void xsetAbstractNumId(STDecimalNumber var1);
}

