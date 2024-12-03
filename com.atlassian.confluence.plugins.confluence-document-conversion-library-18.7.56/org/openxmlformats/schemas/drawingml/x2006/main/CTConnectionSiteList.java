/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;

public interface CTConnectionSiteList
extends XmlObject {
    public static final DocumentFactory<CTConnectionSiteList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctconnectionsitelistab9etype");
    public static final SchemaType type = Factory.getType();

    public List<CTConnectionSite> getCxnList();

    public CTConnectionSite[] getCxnArray();

    public CTConnectionSite getCxnArray(int var1);

    public int sizeOfCxnArray();

    public void setCxnArray(CTConnectionSite[] var1);

    public void setCxnArray(int var1, CTConnectionSite var2);

    public CTConnectionSite insertNewCxn(int var1);

    public CTConnectionSite addNewCxn();

    public void removeCxn(int var1);
}

