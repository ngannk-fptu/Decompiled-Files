/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.vml.CTF;
import com.microsoft.schemas.vml.CTFormulas;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFormulasImpl
extends XmlComplexContentImpl
implements CTFormulas {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("urn:schemas-microsoft-com:vml", "f")};

    public CTFormulasImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTF> getFList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTF>(this::getFArray, this::setFArray, this::insertNewF, this::removeF, this::sizeOfFArray);
        }
    }

    @Override
    public CTF[] getFArray() {
        return (CTF[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTF[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTF getFArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTF target = null;
            target = (CTF)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfFArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setFArray(CTF[] fArray) {
        this.check_orphaned();
        this.arraySetterHelper(fArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setFArray(int i, CTF f) {
        this.generatedSetterHelperImpl(f, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTF insertNewF(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTF target = null;
            target = (CTF)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTF addNewF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTF target = null;
            target = (CTF)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeF(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

