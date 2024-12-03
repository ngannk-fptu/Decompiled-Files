/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;

public class CTOleObjectsImpl
extends XmlComplexContentImpl
implements CTOleObjects {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleObject")};

    public CTOleObjectsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOleObject> getOleObjectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOleObject>(this::getOleObjectArray, this::setOleObjectArray, this::insertNewOleObject, this::removeOleObject, this::sizeOfOleObjectArray);
        }
    }

    @Override
    public CTOleObject[] getOleObjectArray() {
        return (CTOleObject[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTOleObject[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOleObject getOleObjectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOleObject target = null;
            target = (CTOleObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfOleObjectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setOleObjectArray(CTOleObject[] oleObjectArray) {
        this.check_orphaned();
        this.arraySetterHelper(oleObjectArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setOleObjectArray(int i, CTOleObject oleObject) {
        this.generatedSetterHelperImpl(oleObject, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOleObject insertNewOleObject(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOleObject target = null;
            target = (CTOleObject)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOleObject addNewOleObject() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOleObject target = null;
            target = (CTOleObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOleObject(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

