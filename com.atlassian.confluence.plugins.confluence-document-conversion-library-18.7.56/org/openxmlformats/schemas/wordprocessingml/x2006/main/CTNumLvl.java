/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTNumLvl
extends XmlObject {
    public static final DocumentFactory<CTNumLvl> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumlvl416ctype");
    public static final SchemaType type = Factory.getType();

    public CTDecimalNumber getStartOverride();

    public boolean isSetStartOverride();

    public void setStartOverride(CTDecimalNumber var1);

    public CTDecimalNumber addNewStartOverride();

    public void unsetStartOverride();

    public CTLvl getLvl();

    public boolean isSetLvl();

    public void setLvl(CTLvl var1);

    public CTLvl addNewLvl();

    public void unsetLvl();

    public BigInteger getIlvl();

    public STDecimalNumber xgetIlvl();

    public void setIlvl(BigInteger var1);

    public void xsetIlvl(STDecimalNumber var1);
}

