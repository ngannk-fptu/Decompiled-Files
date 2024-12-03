/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTSheetDimension
extends XmlObject {
    public static final DocumentFactory<CTSheetDimension> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetdimensiond310type");
    public static final SchemaType type = Factory.getType();

    public String getRef();

    public STRef xgetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);
}

