/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReference;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReferences;

public class CTExternalReferencesImpl
extends XmlComplexContentImpl
implements CTExternalReferences {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "externalReference")};

    public CTExternalReferencesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTExternalReference> getExternalReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTExternalReference>(this::getExternalReferenceArray, this::setExternalReferenceArray, this::insertNewExternalReference, this::removeExternalReference, this::sizeOfExternalReferenceArray);
        }
    }

    @Override
    public CTExternalReference[] getExternalReferenceArray() {
        return (CTExternalReference[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTExternalReference[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalReference getExternalReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalReference target = null;
            target = (CTExternalReference)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfExternalReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setExternalReferenceArray(CTExternalReference[] externalReferenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(externalReferenceArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setExternalReferenceArray(int i, CTExternalReference externalReference) {
        this.generatedSetterHelperImpl(externalReference, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalReference insertNewExternalReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalReference target = null;
            target = (CTExternalReference)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalReference addNewExternalReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalReference target = null;
            target = (CTExternalReference)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExternalReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

