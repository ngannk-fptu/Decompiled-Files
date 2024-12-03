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
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLsdException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTLatentStyles
extends XmlObject {
    public static final DocumentFactory<CTLatentStyles> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlatentstyles2e3atype");
    public static final SchemaType type = Factory.getType();

    public List<CTLsdException> getLsdExceptionList();

    public CTLsdException[] getLsdExceptionArray();

    public CTLsdException getLsdExceptionArray(int var1);

    public int sizeOfLsdExceptionArray();

    public void setLsdExceptionArray(CTLsdException[] var1);

    public void setLsdExceptionArray(int var1, CTLsdException var2);

    public CTLsdException insertNewLsdException(int var1);

    public CTLsdException addNewLsdException();

    public void removeLsdException(int var1);

    public Object getDefLockedState();

    public STOnOff xgetDefLockedState();

    public boolean isSetDefLockedState();

    public void setDefLockedState(Object var1);

    public void xsetDefLockedState(STOnOff var1);

    public void unsetDefLockedState();

    public BigInteger getDefUIPriority();

    public STDecimalNumber xgetDefUIPriority();

    public boolean isSetDefUIPriority();

    public void setDefUIPriority(BigInteger var1);

    public void xsetDefUIPriority(STDecimalNumber var1);

    public void unsetDefUIPriority();

    public Object getDefSemiHidden();

    public STOnOff xgetDefSemiHidden();

    public boolean isSetDefSemiHidden();

    public void setDefSemiHidden(Object var1);

    public void xsetDefSemiHidden(STOnOff var1);

    public void unsetDefSemiHidden();

    public Object getDefUnhideWhenUsed();

    public STOnOff xgetDefUnhideWhenUsed();

    public boolean isSetDefUnhideWhenUsed();

    public void setDefUnhideWhenUsed(Object var1);

    public void xsetDefUnhideWhenUsed(STOnOff var1);

    public void unsetDefUnhideWhenUsed();

    public Object getDefQFormat();

    public STOnOff xgetDefQFormat();

    public boolean isSetDefQFormat();

    public void setDefQFormat(Object var1);

    public void xsetDefQFormat(STOnOff var1);

    public void unsetDefQFormat();

    public BigInteger getCount();

    public STDecimalNumber xgetCount();

    public boolean isSetCount();

    public void setCount(BigInteger var1);

    public void xsetCount(STDecimalNumber var1);

    public void unsetCount();
}

