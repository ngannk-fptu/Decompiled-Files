/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmptyElement;

public interface CTColorMappingOverride
extends XmlObject {
    public static final DocumentFactory<CTColorMappingOverride> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolormappingoverridea2b2type");
    public static final SchemaType type = Factory.getType();

    public CTEmptyElement getMasterClrMapping();

    public boolean isSetMasterClrMapping();

    public void setMasterClrMapping(CTEmptyElement var1);

    public CTEmptyElement addNewMasterClrMapping();

    public void unsetMasterClrMapping();

    public CTColorMapping getOverrideClrMapping();

    public boolean isSetOverrideClrMapping();

    public void setOverrideClrMapping(CTColorMapping var1);

    public CTColorMapping addNewOverrideClrMapping();

    public void unsetOverrideClrMapping();
}

