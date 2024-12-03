/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.TextParameterType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.bedework.util.calendar.diff.BaseWrapper;
import org.bedework.util.calendar.diff.ParamWrapper;
import org.bedework.util.calendar.diff.PropWrapper;
import org.bedework.util.misc.Util;

abstract class BaseEntityWrapper<T extends BaseEntityWrapper, ParentT extends BaseWrapper, EntityT>
extends BaseWrapper<ParentT> {
    private QName mappedName;
    private EntityT entity;
    public static final QName XBedeworkWrapperQNAME = new QName("urn:ietf:params:xml:ns:icalendar-2.0", "x-bedework-wrapper");
    public static final QName XBedeworkWrappedNameQNAME = new QName("urn:ietf:params:xml:ns:icalendar-2.0", "x-bedework-wrapped-name");

    BaseEntityWrapper(ParentT parent, QName name, EntityT entity) {
        super(parent, name);
        this.entity = entity;
        this.mappedName = this.getMappedName(name);
        if (this.mappedName == null) {
            this.mappedName = name;
        }
    }

    QName getMappedName() {
        return this.mappedName;
    }

    EntityT getEntity() {
        return this.entity;
    }

    JAXBElement<? extends EntityT> getJaxbElement() {
        return new JAXBElement(this.getName(), this.entity.getClass(), this.getEntity());
    }

    abstract QName getMappedName(QName var1);

    abstract boolean sameEntity(BaseEntityWrapper var1);

    public int compareNames(BaseEntityWrapper that) {
        QName thatN = that.getMappedName();
        int res = this.getMappedName().getNamespaceURI().compareTo(thatN.getNamespaceURI());
        if (res != 0) {
            return res;
        }
        res = this.getMappedName().getLocalPart().compareTo(thatN.getLocalPart());
        if (res != 0) {
            return res;
        }
        if (!this.getMappedName().equals(XBedeworkWrapperQNAME)) {
            return 0;
        }
        PropWrapper thatProp = (PropWrapper)that;
        PropWrapper thisProp = (PropWrapper)this;
        String thatname = null;
        String thisname = null;
        for (ParamWrapper prop : thatProp.params.getEls()) {
            if (!prop.getMappedName().equals(XBedeworkWrappedNameQNAME)) continue;
            thatname = ((TextParameterType)prop.getEntity()).getText();
            break;
        }
        for (ParamWrapper prop : thisProp.params.getEls()) {
            if (!prop.getMappedName().equals(XBedeworkWrappedNameQNAME)) continue;
            thisname = ((TextParameterType)prop.getEntity()).getText();
            break;
        }
        return Util.compareStrings(thisname, thatname);
    }

    public int compareNameClass(BaseEntityWrapper that) {
        int res = this.compareNames(that);
        if (res != 0) {
            return res;
        }
        return this.getEntity().getClass().getName().compareTo(that.getEntity().getClass().getName());
    }

    public int compareTo(BaseEntityWrapper o) {
        int res = Util.compareStrings(this.getName().getLocalPart(), o.getName().getLocalPart());
        if (res != 0) {
            return res;
        }
        return Util.compareStrings(this.getName().getNamespaceURI(), o.getName().getNamespaceURI());
    }

    @Override
    protected void toStringSegment(StringBuilder sb) {
        super.toStringSegment(sb);
        if (!this.mappedName.equals(this.getName())) {
            sb.append(", mappedName=");
            sb.append(this.mappedName);
        }
    }
}

