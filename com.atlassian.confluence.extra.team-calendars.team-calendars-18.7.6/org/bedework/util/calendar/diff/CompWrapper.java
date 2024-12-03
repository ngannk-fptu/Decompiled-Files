/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.ActionPropType;
import ietf.params.xml.ns.icalendar_2.ArrayOfProperties;
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.UidPropType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.calendar.diff.BaseEntityWrapper;
import org.bedework.util.calendar.diff.CompsWrapper;
import org.bedework.util.calendar.diff.PropWrapper;
import org.bedework.util.calendar.diff.PropsWrapper;
import org.bedework.util.calendar.diff.XmlIcalCompare;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentsSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertiesSelectionType;

class CompWrapper
extends BaseEntityWrapper<CompWrapper, CompsWrapper, BaseComponentType>
implements Comparable<CompWrapper> {
    private PropsWrapper props;
    private CompsWrapper comps;
    private int kind;

    CompWrapper(CompsWrapper parent, QName name, BaseComponentType c) {
        super(parent, name, c);
        if (c.getProperties() != null) {
            this.props = new PropsWrapper(this, c.getProperties().getBasePropertyOrTzid());
        }
        this.comps = new CompsWrapper(this, XcalUtil.getComponents(c));
        this.kind = XcalUtil.getCompKind(name);
    }

    CompWrapper(XmlIcalCompare.Globals globals, QName name, BaseComponentType c) {
        super(null, name, c);
        this.setGlobals(globals);
        if (c.getProperties() != null) {
            this.props = new PropsWrapper(this, c.getProperties().getBasePropertyOrTzid());
        }
        this.comps = new CompsWrapper(this, XcalUtil.getComponents(c));
        this.kind = XcalUtil.getCompKind(name);
    }

    @Override
    QName getMappedName(QName name) {
        return null;
    }

    ComponentReferenceType makeRef(boolean forRemove) {
        boolean wholeComponent;
        ComponentReferenceType r = new ComponentReferenceType();
        boolean bl = wholeComponent = !forRemove;
        if (this.kind == XcalUtil.AlarmKind) {
            wholeComponent = true;
        }
        if (wholeComponent) {
            r.setBaseComponent(this.getJaxbElement());
            return r;
        }
        Class<?> cl = ((BaseComponentType)this.getEntity()).getClass();
        try {
            BaseComponentType copy = (BaseComponentType)cl.newInstance();
            copy.setProperties(new ArrayOfProperties());
            r.setBaseComponent((JAXBElement<? extends BaseComponentType>)new JAXBElement(this.getName(), copy.getClass(), (Object)copy));
            if (this.kind == XcalUtil.TzDaylight || this.kind == XcalUtil.TzStandard) {
                PropWrapper dts = (PropWrapper)this.props.find(XcalTags.dtstart);
                if (dts == null) {
                    throw new RuntimeException("No DTSTART for reference");
                }
                copy.getProperties().getBasePropertyOrTzid().add(dts.getJaxbElement());
                return r;
            }
            if (this.kind == XcalUtil.TzKind) {
                PropWrapper tzidw = (PropWrapper)this.props.find(XcalTags.tzid);
                if (tzidw == null) {
                    throw new RuntimeException("No tzid for reference");
                }
                copy.getProperties().getBasePropertyOrTzid().add(tzidw.getJaxbElement());
                return r;
            }
            PropWrapper uidw = (PropWrapper)this.props.find(XcalTags.uid);
            if (uidw == null) {
                throw new RuntimeException("No uid for reference");
            }
            copy.getProperties().getBasePropertyOrTzid().add(uidw.getJaxbElement());
            if (this.kind == XcalUtil.UidKind) {
                return r;
            }
            PropWrapper ridw = (PropWrapper)this.props.find(XcalTags.recurrenceId);
            if (ridw != null) {
                copy.getProperties().getBasePropertyOrTzid().add(ridw.getJaxbElement());
            }
            return r;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    boolean sameEntity(BaseEntityWrapper val) {
        String thisUid;
        int res = super.compareNameClass(val);
        if (res != 0) {
            return false;
        }
        CompWrapper that = (CompWrapper)val;
        if (this.kind != that.kind) {
            return false;
        }
        if (this.kind == XcalUtil.OuterKind) {
            return true;
        }
        if (this.kind == XcalUtil.TzKind || this.kind == XcalUtil.TzDaylight || this.kind == XcalUtil.TzStandard) {
            return true;
        }
        if (this.kind == XcalUtil.AlarmKind) {
            String thisAction;
            PropWrapper thatw = (PropWrapper)that.props.find(XcalTags.action);
            PropWrapper thisw = (PropWrapper)this.props.find(XcalTags.action);
            String thatAction = ((ActionPropType)thatw.getEntity()).getText();
            return thatAction.equals(thisAction = ((ActionPropType)thisw.getEntity()).getText());
        }
        PropWrapper thatUidw = (PropWrapper)that.props.find(XcalTags.uid);
        PropWrapper thisUidw = (PropWrapper)this.props.find(XcalTags.uid);
        String thatUid = ((UidPropType)thatUidw.getEntity()).getText();
        if (!thatUid.equals(thisUid = ((UidPropType)thisUidw.getEntity()).getText())) {
            return false;
        }
        if (this.kind == XcalUtil.UidKind) {
            return true;
        }
        return this.cmpRids(that) == 0;
    }

    private int cmpRids(CompWrapper that) {
        PropWrapper thatRidw = (PropWrapper)that.props.find(XcalTags.recurrenceId);
        PropWrapper thisRidw = (PropWrapper)this.props.find(XcalTags.recurrenceId);
        if (thisRidw == null && thatRidw == null) {
            return 0;
        }
        if (thisRidw == null) {
            return -1;
        }
        if (thatRidw == null) {
            return 1;
        }
        return thatRidw.compareTo(thisRidw);
    }

    public PropsWrapper getProps() {
        return this.props;
    }

    public CompsWrapper getComps() {
        return this.comps;
    }

    public ComponentSelectionType diff(CompWrapper that) {
        ComponentsSelectionType csel;
        PropertiesSelectionType psel;
        ComponentSelectionType sel = null;
        if (this.props != null && (psel = this.props.diff(that.props)) != null) {
            sel = that.getSelect(sel);
            sel.setProperties(psel);
        }
        if ((csel = this.comps.diff(that.comps)) != null) {
            sel = that.getSelect(sel);
            sel.setComponents(csel);
        }
        return sel;
    }

    @Override
    JAXBElement<? extends BaseComponentType> getJaxbElement() {
        if (this.kind != XcalUtil.OuterKind) {
            return super.getJaxbElement();
        }
        VcalendarType bct = new VcalendarType();
        return new JAXBElement(this.getName(), bct.getClass(), (Object)bct);
    }

    ComponentSelectionType getSelect(ComponentSelectionType val) {
        if (val != null) {
            return val;
        }
        ComponentSelectionType sel = new ComponentSelectionType();
        sel.setBaseComponent(this.getJaxbElement());
        if (this.kind == XcalUtil.OuterKind || this.kind == XcalUtil.TzKind || this.kind == XcalUtil.TzDaylight || this.kind == XcalUtil.TzStandard) {
            return sel;
        }
        BaseComponentType bct = (BaseComponentType)sel.getBaseComponent().getValue();
        ArrayOfProperties bprops = new ArrayOfProperties();
        bct.setProperties(bprops);
        if (this.kind == XcalUtil.AlarmKind) {
            PropWrapper pw = (PropWrapper)this.props.find(XcalTags.action);
            bprops.getBasePropertyOrTzid().add(pw.getJaxbElement());
            bprops.getBasePropertyOrTzid().add(pw.getJaxbElement());
            return sel;
        }
        PropWrapper pw = (PropWrapper)this.props.find(XcalTags.uid);
        bprops.getBasePropertyOrTzid().add(pw.getJaxbElement());
        if (this.kind == XcalUtil.UidKind) {
            return sel;
        }
        pw = (PropWrapper)this.props.find(XcalTags.recurrenceId);
        if (pw != null) {
            bprops.getBasePropertyOrTzid().add(pw.getJaxbElement());
        }
        return sel;
    }

    @Override
    public int compareTo(CompWrapper o) {
        int res = super.compareTo(o);
        if (res != 0) {
            return res;
        }
        res = ((BaseComponentType)this.getEntity()).getClass().getName().compareTo(((BaseComponentType)o.getEntity()).getClass().getName());
        if (res != 0) {
            return res;
        }
        if (this.kind > o.kind) {
            return 1;
        }
        if (this.kind < o.kind) {
            return -1;
        }
        if (this.kind == XcalUtil.OuterKind || this.kind == XcalUtil.TzKind || this.kind == XcalUtil.TzDaylight || this.kind == XcalUtil.TzStandard) {
            return this.props.compareTo(o.props);
        }
        if (this.kind == XcalUtil.AlarmKind) {
            res = ((PropWrapper)o.props.find(XcalTags.action)).compareTo((PropWrapper)this.props.find(XcalTags.action));
            if (res != 0) {
                return res;
            }
            res = ((PropWrapper)o.props.find(XcalTags.trigger)).compareTo((PropWrapper)this.props.find(XcalTags.trigger));
            if (res != 0) {
                return res;
            }
            return this.props.compareTo(o.props);
        }
        res = ((PropWrapper)o.props.find(XcalTags.uid)).compareTo((PropWrapper)this.props.find(XcalTags.uid));
        if (res != 0) {
            return res;
        }
        if (this.kind == XcalUtil.UidKind) {
            return this.props.compareTo(o.props);
        }
        res = this.cmpRids(o);
        if (res != 0) {
            return res;
        }
        return this.props.compareTo(o.props);
    }

    public int hashCode() {
        return this.getName().hashCode() * this.props.hashCode();
    }

    public boolean equals(Object o) {
        return this.compareTo((CompWrapper)o) == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CompWrapper{");
        super.toStringSegment(sb);
        sb.append(", props=");
        sb.append(this.props);
        sb.append("\n, comps=");
        sb.append(this.comps);
        sb.append("}");
        return sb.toString();
    }
}

