/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.STFormat;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.STValue;

public interface CTSignatureTime
extends XmlObject {
    public static final DocumentFactory<CTSignatureTime> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsignaturetime461dtype");
    public static final SchemaType type = Factory.getType();

    public String getFormat();

    public STFormat xgetFormat();

    public void setFormat(String var1);

    public void xsetFormat(STFormat var1);

    public String getValue();

    public STValue xgetValue();

    public void setValue(String var1);

    public void xsetValue(STValue var1);
}

