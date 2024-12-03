/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTHyperlink
extends XmlObject {
    public static final DocumentFactory<CTHyperlink> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthyperlink0c85type");
    public static final SchemaType type = Factory.getType();

    public String getRef();

    public STRef xgetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();

    public String getLocation();

    public STXstring xgetLocation();

    public boolean isSetLocation();

    public void setLocation(String var1);

    public void xsetLocation(STXstring var1);

    public void unsetLocation();

    public String getTooltip();

    public STXstring xgetTooltip();

    public boolean isSetTooltip();

    public void setTooltip(String var1);

    public void xsetTooltip(STXstring var1);

    public void unsetTooltip();

    public String getDisplay();

    public STXstring xgetDisplay();

    public boolean isSetDisplay();

    public void setDisplay(String var1);

    public void xsetDisplay(STXstring var1);

    public void unsetDisplay();
}

