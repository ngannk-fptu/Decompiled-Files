/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import javax.xml.namespace.QName;
import org.bedework.util.calendar.diff.BaseEntityWrapper;
import org.bedework.util.calendar.diff.ParamsWrapper;
import org.bedework.util.calendar.diff.ValueComparator;
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterSelectionType;

class ParamWrapper
extends BaseEntityWrapper<ParamWrapper, ParamsWrapper, BaseParameterType>
implements Comparable<ParamWrapper> {
    private ValueComparator comparator;

    ParamWrapper(ParamsWrapper parent, QName name, BaseParameterType p) {
        super(parent, name, p);
    }

    @Override
    QName getMappedName(QName name) {
        return null;
    }

    @Override
    boolean sameEntity(BaseEntityWrapper val) {
        int res = super.compareNameClass(val);
        return res == 0;
    }

    ParameterReferenceType makeRef() {
        ParameterReferenceType r = new ParameterReferenceType();
        r.setBaseParameter(this.getJaxbElement());
        return r;
    }

    ParameterSelectionType getSelect(ParameterSelectionType val) {
        if (val != null) {
            return val;
        }
        ParameterSelectionType sel = new ParameterSelectionType();
        sel.setBaseParameter(this.getJaxbElement());
        return sel;
    }

    public ParameterSelectionType diff(ParamWrapper that) {
        ParameterSelectionType sel = null;
        if (!this.equalValue(that)) {
            sel = that.getSelect(sel);
            ParameterReferenceType ct = new ParameterReferenceType();
            ct.setBaseParameter(this.getJaxbElement());
            sel.setChange(ct);
        }
        return sel;
    }

    public boolean equalValue(ParamWrapper that) {
        return this.getComparator().equals(that.getComparator());
    }

    public int compareValue(ParamWrapper that) {
        return this.getComparator().compareTo(that.getComparator());
    }

    ValueComparator getComparator() {
        if (this.comparator == null) {
            this.comparator = this.globals.matcher.getComparator(this.getEntity());
        }
        return this.comparator;
    }

    @Override
    public int compareTo(ParamWrapper o) {
        int res = super.compareTo(o);
        if (res != 0) {
            return res;
        }
        res = ((BaseParameterType)this.getEntity()).getClass().getName().compareTo(((BaseParameterType)o.getEntity()).getClass().getName());
        if (res != 0) {
            return res;
        }
        return this.compareValue(o);
    }

    public int hashCode() {
        return this.getName().hashCode() * this.getComparator().hashCode();
    }

    public boolean equals(Object o) {
        return this.compareTo((ParamWrapper)o) == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ParamWrapper{");
        super.toStringSegment(sb);
        sb.append(", matcher=\"");
        sb.append(this.getComparator());
        sb.append("\"");
        sb.append("}");
        return sb.toString();
    }
}

