/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedNames;

public class CTDefinedNamesImpl
extends XmlComplexContentImpl
implements CTDefinedNames {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "definedName")};

    public CTDefinedNamesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDefinedName> getDefinedNameList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDefinedName>(this::getDefinedNameArray, this::setDefinedNameArray, this::insertNewDefinedName, this::removeDefinedName, this::sizeOfDefinedNameArray);
        }
    }

    @Override
    public CTDefinedName[] getDefinedNameArray() {
        return (CTDefinedName[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTDefinedName[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDefinedName getDefinedNameArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDefinedName target = null;
            target = (CTDefinedName)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfDefinedNameArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setDefinedNameArray(CTDefinedName[] definedNameArray) {
        this.check_orphaned();
        this.arraySetterHelper(definedNameArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setDefinedNameArray(int i, CTDefinedName definedName) {
        this.generatedSetterHelperImpl(definedName, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDefinedName insertNewDefinedName(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDefinedName target = null;
            target = (CTDefinedName)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDefinedName addNewDefinedName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDefinedName target = null;
            target = (CTDefinedName)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDefinedName(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

