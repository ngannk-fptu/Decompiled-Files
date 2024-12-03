/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;

public interface CTDataBinding
extends XmlObject {
    public static final DocumentFactory<CTDataBinding> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdatabinding9077type");
    public static final SchemaType type = Factory.getType();

    public String getPrefixMappings();

    public STString xgetPrefixMappings();

    public boolean isSetPrefixMappings();

    public void setPrefixMappings(String var1);

    public void xsetPrefixMappings(STString var1);

    public void unsetPrefixMappings();

    public String getXpath();

    public STString xgetXpath();

    public void setXpath(String var1);

    public void xsetXpath(STString var1);

    public String getStoreItemID();

    public STString xgetStoreItemID();

    public void setStoreItemID(String var1);

    public void xsetStoreItemID(STString var1);
}

