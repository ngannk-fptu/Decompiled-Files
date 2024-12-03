/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.StyleSheetType;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetsType;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class StyleSheetsTypeImpl
extends XmlComplexContentImpl
implements StyleSheetsType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "StyleSheet")};

    public StyleSheetsTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<StyleSheetType> getStyleSheetList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<StyleSheetType>(this::getStyleSheetArray, this::setStyleSheetArray, this::insertNewStyleSheet, this::removeStyleSheet, this::sizeOfStyleSheetArray);
        }
    }

    @Override
    public StyleSheetType[] getStyleSheetArray() {
        return (StyleSheetType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new StyleSheetType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StyleSheetType getStyleSheetArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            StyleSheetType target = null;
            target = (StyleSheetType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfStyleSheetArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setStyleSheetArray(StyleSheetType[] styleSheetArray) {
        this.check_orphaned();
        this.arraySetterHelper(styleSheetArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setStyleSheetArray(int i, StyleSheetType styleSheet) {
        this.generatedSetterHelperImpl(styleSheet, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StyleSheetType insertNewStyleSheet(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            StyleSheetType target = null;
            target = (StyleSheetType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StyleSheetType addNewStyleSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            StyleSheetType target = null;
            target = (StyleSheetType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeStyleSheet(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

