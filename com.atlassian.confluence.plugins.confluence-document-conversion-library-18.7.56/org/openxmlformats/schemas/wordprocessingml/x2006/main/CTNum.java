/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTNum
extends XmlObject {
    public static final DocumentFactory<CTNum> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnume94ctype");
    public static final SchemaType type = Factory.getType();

    public CTDecimalNumber getAbstractNumId();

    public void setAbstractNumId(CTDecimalNumber var1);

    public CTDecimalNumber addNewAbstractNumId();

    public List<CTNumLvl> getLvlOverrideList();

    public CTNumLvl[] getLvlOverrideArray();

    public CTNumLvl getLvlOverrideArray(int var1);

    public int sizeOfLvlOverrideArray();

    public void setLvlOverrideArray(CTNumLvl[] var1);

    public void setLvlOverrideArray(int var1, CTNumLvl var2);

    public CTNumLvl insertNewLvlOverride(int var1);

    public CTNumLvl addNewLvlOverride();

    public void removeLvlOverride(int var1);

    public BigInteger getNumId();

    public STDecimalNumber xgetNumId();

    public void setNumId(BigInteger var1);

    public void xsetNumId(STDecimalNumber var1);
}

