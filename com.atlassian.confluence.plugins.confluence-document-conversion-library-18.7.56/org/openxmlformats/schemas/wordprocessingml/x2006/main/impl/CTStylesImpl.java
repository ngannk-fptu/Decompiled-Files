/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLatentStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;

public class CTStylesImpl
extends XmlComplexContentImpl
implements CTStyles {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docDefaults"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "latentStyles"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "style")};

    public CTStylesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocDefaults getDocDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocDefaults target = null;
            target = (CTDocDefaults)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDocDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setDocDefaults(CTDocDefaults docDefaults) {
        this.generatedSetterHelperImpl(docDefaults, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocDefaults addNewDocDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocDefaults target = null;
            target = (CTDocDefaults)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDocDefaults() {
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
    public CTLatentStyles getLatentStyles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLatentStyles target = null;
            target = (CTLatentStyles)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLatentStyles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setLatentStyles(CTLatentStyles latentStyles) {
        this.generatedSetterHelperImpl(latentStyles, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLatentStyles addNewLatentStyles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLatentStyles target = null;
            target = (CTLatentStyles)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLatentStyles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTStyle> getStyleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTStyle>(this::getStyleArray, this::setStyleArray, this::insertNewStyle, this::removeStyle, this::sizeOfStyleArray);
        }
    }

    @Override
    public CTStyle[] getStyleArray() {
        return (CTStyle[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTStyle[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyle getStyleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyle target = null;
            target = (CTStyle)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfStyleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setStyleArray(CTStyle[] styleArray) {
        this.check_orphaned();
        this.arraySetterHelper(styleArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setStyleArray(int i, CTStyle style) {
        this.generatedSetterHelperImpl(style, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyle insertNewStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyle target = null;
            target = (CTStyle)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyle addNewStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyle target = null;
            target = (CTStyle)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }
}

