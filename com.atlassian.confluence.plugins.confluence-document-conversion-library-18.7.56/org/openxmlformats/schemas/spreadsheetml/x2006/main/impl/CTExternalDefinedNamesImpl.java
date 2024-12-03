/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedNames;

public class CTExternalDefinedNamesImpl
extends XmlComplexContentImpl
implements CTExternalDefinedNames {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "definedName")};

    public CTExternalDefinedNamesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTExternalDefinedName> getDefinedNameList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTExternalDefinedName>(this::getDefinedNameArray, this::setDefinedNameArray, this::insertNewDefinedName, this::removeDefinedName, this::sizeOfDefinedNameArray);
        }
    }

    @Override
    public CTExternalDefinedName[] getDefinedNameArray() {
        return (CTExternalDefinedName[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTExternalDefinedName[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalDefinedName getDefinedNameArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalDefinedName target = null;
            target = (CTExternalDefinedName)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public void setDefinedNameArray(CTExternalDefinedName[] definedNameArray) {
        this.check_orphaned();
        this.arraySetterHelper(definedNameArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setDefinedNameArray(int i, CTExternalDefinedName definedName) {
        this.generatedSetterHelperImpl(definedName, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalDefinedName insertNewDefinedName(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalDefinedName target = null;
            target = (CTExternalDefinedName)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalDefinedName addNewDefinedName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalDefinedName target = null;
            target = (CTExternalDefinedName)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
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

