/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTLsdException
extends XmlObject {
    public static final DocumentFactory<CTLsdException> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlsdexceptiona296type");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public STString xgetName();

    public void setName(String var1);

    public void xsetName(STString var1);

    public Object getLocked();

    public STOnOff xgetLocked();

    public boolean isSetLocked();

    public void setLocked(Object var1);

    public void xsetLocked(STOnOff var1);

    public void unsetLocked();

    public BigInteger getUiPriority();

    public STDecimalNumber xgetUiPriority();

    public boolean isSetUiPriority();

    public void setUiPriority(BigInteger var1);

    public void xsetUiPriority(STDecimalNumber var1);

    public void unsetUiPriority();

    public Object getSemiHidden();

    public STOnOff xgetSemiHidden();

    public boolean isSetSemiHidden();

    public void setSemiHidden(Object var1);

    public void xsetSemiHidden(STOnOff var1);

    public void unsetSemiHidden();

    public Object getUnhideWhenUsed();

    public STOnOff xgetUnhideWhenUsed();

    public boolean isSetUnhideWhenUsed();

    public void setUnhideWhenUsed(Object var1);

    public void xsetUnhideWhenUsed(STOnOff var1);

    public void unsetUnhideWhenUsed();

    public Object getQFormat();

    public STOnOff xgetQFormat();

    public boolean isSetQFormat();

    public void setQFormat(Object var1);

    public void xsetQFormat(STOnOff var1);

    public void unsetQFormat();
}

