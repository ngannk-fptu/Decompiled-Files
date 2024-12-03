/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontListEntry;

public class CTEmbeddedFontListImpl
extends XmlComplexContentImpl
implements CTEmbeddedFontList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "embeddedFont")};

    public CTEmbeddedFontListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTEmbeddedFontListEntry> getEmbeddedFontList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmbeddedFontListEntry>(this::getEmbeddedFontArray, this::setEmbeddedFontArray, this::insertNewEmbeddedFont, this::removeEmbeddedFont, this::sizeOfEmbeddedFontArray);
        }
    }

    @Override
    public CTEmbeddedFontListEntry[] getEmbeddedFontArray() {
        return (CTEmbeddedFontListEntry[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTEmbeddedFontListEntry[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmbeddedFontListEntry getEmbeddedFontArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmbeddedFontListEntry target = null;
            target = (CTEmbeddedFontListEntry)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfEmbeddedFontArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setEmbeddedFontArray(CTEmbeddedFontListEntry[] embeddedFontArray) {
        this.check_orphaned();
        this.arraySetterHelper(embeddedFontArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setEmbeddedFontArray(int i, CTEmbeddedFontListEntry embeddedFont) {
        this.generatedSetterHelperImpl(embeddedFont, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmbeddedFontListEntry insertNewEmbeddedFont(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmbeddedFontListEntry target = null;
            target = (CTEmbeddedFontListEntry)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmbeddedFontListEntry addNewEmbeddedFont() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmbeddedFontListEntry target = null;
            target = (CTEmbeddedFontListEntry)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEmbeddedFont(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

