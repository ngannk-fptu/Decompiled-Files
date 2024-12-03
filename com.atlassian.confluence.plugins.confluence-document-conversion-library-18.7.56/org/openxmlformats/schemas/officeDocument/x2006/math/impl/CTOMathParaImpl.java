/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathParaPr
 */
package org.openxmlformats.schemas.officeDocument.x2006.math.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathParaPr;

public class CTOMathParaImpl
extends XmlComplexContentImpl
implements CTOMathPara {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathParaPr"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath")};

    public CTOMathParaImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathParaPr getOMathParaPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathParaPr target = null;
            target = (CTOMathParaPr)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOMathParaPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setOMathParaPr(CTOMathParaPr oMathParaPr) {
        this.generatedSetterHelperImpl((XmlObject)oMathParaPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathParaPr addNewOMathParaPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathParaPr target = null;
            target = (CTOMathParaPr)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOMathParaPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOMath> getOMathList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOMath>(this::getOMathArray, this::setOMathArray, this::insertNewOMath, this::removeOMath, this::sizeOfOMathArray);
        }
    }

    @Override
    public CTOMath[] getOMathArray() {
        return (CTOMath[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTOMath[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMath getOMathArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMath target = null;
            target = (CTOMath)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfOMathArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setOMathArray(CTOMath[] oMathArray) {
        this.check_orphaned();
        this.arraySetterHelper(oMathArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setOMathArray(int i, CTOMath oMath) {
        this.generatedSetterHelperImpl(oMath, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMath insertNewOMath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMath target = null;
            target = (CTOMath)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMath addNewOMath() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMath target = null;
            target = (CTOMath)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOMath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }
}

