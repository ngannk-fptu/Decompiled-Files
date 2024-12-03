/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.MasterShortcutType
 */
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.MasterShortcutType;
import com.microsoft.schemas.office.visio.x2012.main.MasterType;
import com.microsoft.schemas.office.visio.x2012.main.MastersType;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MastersTypeImpl
extends XmlComplexContentImpl
implements MastersType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "Master"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "MasterShortcut")};

    public MastersTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<MasterType> getMasterList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<MasterType>(this::getMasterArray, this::setMasterArray, this::insertNewMaster, this::removeMaster, this::sizeOfMasterArray);
        }
    }

    @Override
    public MasterType[] getMasterArray() {
        return (MasterType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new MasterType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MasterType getMasterArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            MasterType target = null;
            target = (MasterType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfMasterArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setMasterArray(MasterType[] masterArray) {
        this.check_orphaned();
        this.arraySetterHelper(masterArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setMasterArray(int i, MasterType master) {
        this.generatedSetterHelperImpl(master, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MasterType insertNewMaster(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            MasterType target = null;
            target = (MasterType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MasterType addNewMaster() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            MasterType target = null;
            target = (MasterType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMaster(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<MasterShortcutType> getMasterShortcutList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<MasterShortcutType>(this::getMasterShortcutArray, this::setMasterShortcutArray, this::insertNewMasterShortcut, this::removeMasterShortcut, this::sizeOfMasterShortcutArray);
        }
    }

    @Override
    public MasterShortcutType[] getMasterShortcutArray() {
        return (MasterShortcutType[])this.getXmlObjectArray(PROPERTY_QNAME[1], (XmlObject[])new MasterShortcutType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MasterShortcutType getMasterShortcutArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            MasterShortcutType target = null;
            target = (MasterShortcutType)this.get_store().find_element_user(PROPERTY_QNAME[1], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfMasterShortcutArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setMasterShortcutArray(MasterShortcutType[] masterShortcutArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])masterShortcutArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setMasterShortcutArray(int i, MasterShortcutType masterShortcut) {
        this.generatedSetterHelperImpl((XmlObject)masterShortcut, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MasterShortcutType insertNewMasterShortcut(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            MasterShortcutType target = null;
            target = (MasterShortcutType)this.get_store().insert_element_user(PROPERTY_QNAME[1], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MasterShortcutType addNewMasterShortcut() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            MasterShortcutType target = null;
            target = (MasterShortcutType)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMasterShortcut(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }
}

