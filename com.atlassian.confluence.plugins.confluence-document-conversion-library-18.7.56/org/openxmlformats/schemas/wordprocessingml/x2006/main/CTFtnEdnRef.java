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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTFtnEdnRef
extends XmlObject {
    public static final DocumentFactory<CTFtnEdnRef> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctftnednref89eetype");
    public static final SchemaType type = Factory.getType();

    public Object getCustomMarkFollows();

    public STOnOff xgetCustomMarkFollows();

    public boolean isSetCustomMarkFollows();

    public void setCustomMarkFollows(Object var1);

    public void xsetCustomMarkFollows(STOnOff var1);

    public void unsetCustomMarkFollows();

    public BigInteger getId();

    public STDecimalNumber xgetId();

    public void setId(BigInteger var1);

    public void xsetId(STDecimalNumber var1);
}

