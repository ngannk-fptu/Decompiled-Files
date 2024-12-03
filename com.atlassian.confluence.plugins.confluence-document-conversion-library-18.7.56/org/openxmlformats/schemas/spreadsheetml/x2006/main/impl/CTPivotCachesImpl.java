/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCache;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCaches;

public class CTPivotCachesImpl
extends XmlComplexContentImpl
implements CTPivotCaches {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotCache")};

    public CTPivotCachesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPivotCache> getPivotCacheList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPivotCache>(this::getPivotCacheArray, this::setPivotCacheArray, this::insertNewPivotCache, this::removePivotCache, this::sizeOfPivotCacheArray);
        }
    }

    @Override
    public CTPivotCache[] getPivotCacheArray() {
        return (CTPivotCache[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTPivotCache[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPivotCache getPivotCacheArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPivotCache target = null;
            target = (CTPivotCache)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfPivotCacheArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setPivotCacheArray(CTPivotCache[] pivotCacheArray) {
        this.check_orphaned();
        this.arraySetterHelper(pivotCacheArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setPivotCacheArray(int i, CTPivotCache pivotCache) {
        this.generatedSetterHelperImpl(pivotCache, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPivotCache insertNewPivotCache(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPivotCache target = null;
            target = (CTPivotCache)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPivotCache addNewPivotCache() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPivotCache target = null;
            target = (CTPivotCache)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePivotCache(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

