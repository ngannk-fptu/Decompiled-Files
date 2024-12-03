/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;

public class CTLineStyleListImpl
extends XmlComplexContentImpl
implements CTLineStyleList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ln")};

    public CTLineStyleListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTLineProperties> getLnList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLineProperties>(this::getLnArray, this::setLnArray, this::insertNewLn, this::removeLn, this::sizeOfLnArray);
        }
    }

    @Override
    public CTLineProperties[] getLnArray() {
        return (CTLineProperties[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTLineProperties[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineProperties getLnArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineProperties target = null;
            target = (CTLineProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfLnArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setLnArray(CTLineProperties[] lnArray) {
        this.check_orphaned();
        this.arraySetterHelper(lnArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setLnArray(int i, CTLineProperties ln) {
        this.generatedSetterHelperImpl(ln, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineProperties insertNewLn(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineProperties target = null;
            target = (CTLineProperties)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineProperties addNewLn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineProperties target = null;
            target = (CTLineProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLn(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

