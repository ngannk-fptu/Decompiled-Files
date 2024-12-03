/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.ConnectType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface ConnectsType
extends XmlObject {
    public static final DocumentFactory<ConnectsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "connectstype8750type");
    public static final SchemaType type = Factory.getType();

    public List<ConnectType> getConnectList();

    public ConnectType[] getConnectArray();

    public ConnectType getConnectArray(int var1);

    public int sizeOfConnectArray();

    public void setConnectArray(ConnectType[] var1);

    public void setConnectArray(int var1, ConnectType var2);

    public ConnectType insertNewConnect(int var1);

    public ConnectType addNewConnect();

    public void removeConnect(int var1);
}

