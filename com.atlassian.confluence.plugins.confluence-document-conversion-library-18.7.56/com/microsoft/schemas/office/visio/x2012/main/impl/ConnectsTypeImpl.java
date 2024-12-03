/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.ConnectType;
import com.microsoft.schemas.office.visio.x2012.main.ConnectsType;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ConnectsTypeImpl
extends XmlComplexContentImpl
implements ConnectsType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "Connect")};

    public ConnectsTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ConnectType> getConnectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<ConnectType>(this::getConnectArray, this::setConnectArray, this::insertNewConnect, this::removeConnect, this::sizeOfConnectArray);
        }
    }

    @Override
    public ConnectType[] getConnectArray() {
        return (ConnectType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new ConnectType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ConnectType getConnectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ConnectType target = null;
            target = (ConnectType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfConnectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setConnectArray(ConnectType[] connectArray) {
        this.check_orphaned();
        this.arraySetterHelper(connectArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setConnectArray(int i, ConnectType connect) {
        this.generatedSetterHelperImpl(connect, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ConnectType insertNewConnect(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ConnectType target = null;
            target = (ConnectType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ConnectType addNewConnect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ConnectType target = null;
            target = (ConnectType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeConnect(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

