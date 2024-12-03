/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.MasterShortcutType
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.MasterShortcutType;
import com.microsoft.schemas.office.visio.x2012.main.MasterType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface MastersType
extends XmlObject {
    public static final DocumentFactory<MastersType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "masterstypeaebatype");
    public static final SchemaType type = Factory.getType();

    public List<MasterType> getMasterList();

    public MasterType[] getMasterArray();

    public MasterType getMasterArray(int var1);

    public int sizeOfMasterArray();

    public void setMasterArray(MasterType[] var1);

    public void setMasterArray(int var1, MasterType var2);

    public MasterType insertNewMaster(int var1);

    public MasterType addNewMaster();

    public void removeMaster(int var1);

    public List<MasterShortcutType> getMasterShortcutList();

    public MasterShortcutType[] getMasterShortcutArray();

    public MasterShortcutType getMasterShortcutArray(int var1);

    public int sizeOfMasterShortcutArray();

    public void setMasterShortcutArray(MasterShortcutType[] var1);

    public void setMasterShortcutArray(int var1, MasterShortcutType var2);

    public MasterShortcutType insertNewMasterShortcut(int var1);

    public MasterShortcutType addNewMasterShortcut();

    public void removeMasterShortcut(int var1);
}

