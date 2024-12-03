/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTConnectorLocking
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnection;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectorLocking;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTNonVisualConnectorProperties
extends XmlObject {
    public static final DocumentFactory<CTNonVisualConnectorProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnonvisualconnectorproperties6f8etype");
    public static final SchemaType type = Factory.getType();

    public CTConnectorLocking getCxnSpLocks();

    public boolean isSetCxnSpLocks();

    public void setCxnSpLocks(CTConnectorLocking var1);

    public CTConnectorLocking addNewCxnSpLocks();

    public void unsetCxnSpLocks();

    public CTConnection getStCxn();

    public boolean isSetStCxn();

    public void setStCxn(CTConnection var1);

    public CTConnection addNewStCxn();

    public void unsetStCxn();

    public CTConnection getEndCxn();

    public boolean isSetEndCxn();

    public void setEndCxn(CTConnection var1);

    public CTConnection addNewEndCxn();

    public void unsetEndCxn();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}

