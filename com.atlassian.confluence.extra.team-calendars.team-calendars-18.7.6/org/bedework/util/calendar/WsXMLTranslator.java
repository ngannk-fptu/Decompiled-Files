/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar;

import ietf.params.xml.ns.icalendar_2.ArrayOfComponents;
import ietf.params.xml.ns.icalendar_2.ArrayOfParameters;
import ietf.params.xml.ns.icalendar_2.ArrayOfProperties;
import ietf.params.xml.ns.icalendar_2.AttachPropType;
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.CalAddressListParamType;
import ietf.params.xml.ns.icalendar_2.CalAddressParamType;
import ietf.params.xml.ns.icalendar_2.CalAddressPropertyType;
import ietf.params.xml.ns.icalendar_2.CalscalePropType;
import ietf.params.xml.ns.icalendar_2.DateDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DurationParameterType;
import ietf.params.xml.ns.icalendar_2.DurationPropType;
import ietf.params.xml.ns.icalendar_2.FreebusyPropType;
import ietf.params.xml.ns.icalendar_2.GeoPropType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import ietf.params.xml.ns.icalendar_2.IntegerPropertyType;
import ietf.params.xml.ns.icalendar_2.RangeParamType;
import ietf.params.xml.ns.icalendar_2.RecurPropertyType;
import ietf.params.xml.ns.icalendar_2.RecurType;
import ietf.params.xml.ns.icalendar_2.RequestStatusPropType;
import ietf.params.xml.ns.icalendar_2.TextListPropertyType;
import ietf.params.xml.ns.icalendar_2.TextParameterType;
import ietf.params.xml.ns.icalendar_2.TextPropertyType;
import ietf.params.xml.ns.icalendar_2.TriggerPropType;
import ietf.params.xml.ns.icalendar_2.UntilRecurType;
import ietf.params.xml.ns.icalendar_2.UriParameterType;
import ietf.params.xml.ns.icalendar_2.UriPropertyType;
import ietf.params.xml.ns.icalendar_2.UtcDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.UtcOffsetPropertyType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.parameter.Value;
import org.apache.log4j.Logger;
import org.bedework.util.calendar.BuildState;
import org.bedework.util.calendar.ContentHandlerImpl;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.util.calendar.XcalUtil;

public class WsXMLTranslator {
    private final TimeZoneRegistry tzRegistry;

    public WsXMLTranslator(TimeZoneRegistry tzRegistry) {
        this.tzRegistry = tzRegistry;
    }

    public Calendar fromXcal(IcalendarType ical) throws Throwable {
        BuildState bs = new BuildState(this.tzRegistry);
        bs.setContentHandler(new ContentHandlerImpl(bs));
        List<VcalendarType> vcts = ical.getVcalendar();
        if (vcts.size() == 0) {
            return null;
        }
        if (vcts.size() > 1) {
            throw new Exception("More than one vcalendar");
        }
        this.processVcalendar(vcts.get(0), bs);
        return bs.getCalendar();
    }

    public Calendar fromXcomp(JAXBElement<? extends BaseComponentType> comp) throws Throwable {
        IcalendarType ical = new IcalendarType();
        List<VcalendarType> vcts = ical.getVcalendar();
        VcalendarType vcal = new VcalendarType();
        vcts.add(vcal);
        ArrayOfComponents aop = new ArrayOfComponents();
        vcal.setComponents(aop);
        aop.getBaseComponent().add(comp);
        return this.fromXcal(ical);
    }

    private void processVcalendar(VcalendarType vcal, BuildState bs) throws Throwable {
        bs.getContentHandler().startCalendar();
        this.processProperties(vcal.getProperties(), bs);
        this.processCalcomps(vcal, bs);
    }

    private void processProperties(ArrayOfProperties aop, BuildState bs) throws Throwable {
        if (aop == null || aop.getBasePropertyOrTzid().size() == 0) {
            return;
        }
        for (JAXBElement<? extends BasePropertyType> e : aop.getBasePropertyOrTzid()) {
            this.processProperty((BasePropertyType)e.getValue(), e.getName(), bs);
        }
    }

    private void processCalcomps(BaseComponentType c, BuildState bs) throws Throwable {
        List<JAXBElement<? extends BaseComponentType>> comps = XcalUtil.getComponents(c);
        if (comps == null) {
            return;
        }
        for (JAXBElement<? extends BaseComponentType> el : comps) {
            this.processComponent((BaseComponentType)el.getValue(), bs);
        }
    }

    private void processComponent(BaseComponentType comp, BuildState bs) throws Throwable {
        PropertyIndex.ComponentInfoIndex cii = PropertyIndex.ComponentInfoIndex.fromXmlClass(comp.getClass());
        if (cii == null) {
            throw new Exception("Unknown component " + comp.getClass());
        }
        String name = cii.getPname();
        bs.getContentHandler().startComponent(name);
        this.processProperties(comp.getProperties(), bs);
        this.processCalcomps(comp, bs);
        bs.getContentHandler().endComponent(name);
    }

    private void processProperty(BasePropertyType prop, QName elname, BuildState bs) throws Throwable {
        String parName;
        PropertyIndex.PropertyInfoIndex pii = PropertyIndex.PropertyInfoIndex.fromXmlClass(prop.getClass());
        String name = elname.getLocalPart().toUpperCase();
        ArrayOfParameters aop = prop.getParameters();
        boolean wrapper = name.equals("X-BEDEWORK-WRAPPER");
        if (wrapper) {
            for (JAXBElement<? extends BaseParameterType> e : aop.getBaseParameter()) {
                parName = e.getName().getLocalPart().toUpperCase();
                if (!parName.equals("X-BEDEWORK-WRAPPED-NAME")) continue;
                name = this.getParValue((BaseParameterType)e.getValue());
            }
        }
        bs.getContentHandler().startProperty(name);
        if (aop != null) {
            for (JAXBElement<? extends BaseParameterType> e : aop.getBaseParameter()) {
                parName = e.getName().getLocalPart().toUpperCase();
                if (parName.equals("X-BEDEWORK-WRAPPED-NAME")) continue;
                bs.getContentHandler().parameter(parName, this.getParValue((BaseParameterType)e.getValue()));
            }
        }
        if (!this.processValue(prop, bs)) {
            throw new Exception("Bad property " + prop);
        }
        bs.getContentHandler().endProperty(name);
    }

    public String fromRecurProperty(RecurPropertyType rp) {
        RecurType r = rp.getRecur();
        ArrayList<String> rels = new ArrayList<String>();
        this.addRecurEl(rels, "FREQ", (Object)r.getFreq());
        if (r.getUntil() != null) {
            UntilRecurType until = r.getUntil();
            if (until.getDate() != null) {
                rels.add("UNTIL=" + until.getDate());
            } else {
                rels.add("UNTIL=" + until.getDateTime());
            }
        }
        this.addRecurEl(rels, "COUNT", r.getCount());
        this.addRecurEl(rels, "INTERVAL", r.getInterval());
        this.addRecurEl(rels, "BYSECOND", r.getBysecond());
        this.addRecurEl(rels, "BYMINUTE", r.getByminute());
        this.addRecurEl(rels, "BYHOUR", r.getByhour());
        this.addRecurEl(rels, "BYDAY", r.getByday());
        this.addRecurEl(rels, "BYMONTHDAY", r.getBymonthday());
        this.addRecurEl(rels, "BYYEARDAY", r.getByyearday());
        this.addRecurEl(rels, "BYWEEKNO", r.getByweekno());
        this.addRecurEl(rels, "BYMONTH", r.getBymonth());
        this.addRecurEl(rels, "BYSETPOS", r.getBysetpos());
        this.addRecurEl(rels, "WKST", (Object)r.getWkst());
        return this.fromList(rels, false, ";");
    }

    private boolean processValue(BasePropertyType prop, BuildState bs) throws Throwable {
        if (prop instanceof RecurPropertyType) {
            this.propVal(bs, this.fromRecurProperty((RecurPropertyType)prop));
            return true;
        }
        if (prop instanceof DurationPropType) {
            DurationPropType dp = (DurationPropType)prop;
            this.propVal(bs, dp.getDuration());
            return true;
        }
        if (prop instanceof TextPropertyType) {
            TextPropertyType tp = (TextPropertyType)prop;
            this.propVal(bs, tp.getText());
            return true;
        }
        if (prop instanceof TextListPropertyType) {
            TextListPropertyType p = (TextListPropertyType)prop;
            this.propVal(bs, this.fromList(p.getText(), false));
            return true;
        }
        if (prop instanceof CalAddressPropertyType) {
            CalAddressPropertyType cap = (CalAddressPropertyType)prop;
            this.propVal(bs, cap.getCalAddress());
            return true;
        }
        if (prop instanceof IntegerPropertyType) {
            IntegerPropertyType ip = (IntegerPropertyType)prop;
            this.propVal(bs, String.valueOf(ip.getInteger()));
            return true;
        }
        if (prop instanceof UriPropertyType) {
            UriPropertyType p = (UriPropertyType)prop;
            this.propVal(bs, p.getUri());
            return true;
        }
        if (prop instanceof UtcOffsetPropertyType) {
            UtcOffsetPropertyType p = (UtcOffsetPropertyType)prop;
            this.propVal(bs, p.getUtcOffset());
            return true;
        }
        if (prop instanceof UtcDatetimePropertyType) {
            UtcDatetimePropertyType p = (UtcDatetimePropertyType)prop;
            this.propVal(bs, XcalUtil.getIcalFormatDateTime(p.getUtcDateTime().toString()));
            return true;
        }
        if (prop instanceof DatetimePropertyType) {
            DatetimePropertyType p = (DatetimePropertyType)prop;
            this.propVal(bs, XcalUtil.getIcalFormatDateTime(p.getDateTime().toString()));
            return true;
        }
        if (prop instanceof DateDatetimePropertyType) {
            XcalUtil.DtTzid dtTzid = XcalUtil.getDtTzid((DateDatetimePropertyType)prop);
            if (dtTzid.dateOnly) {
                bs.getContentHandler().parameter("VALUE", Value.DATE.getValue());
            }
            this.propVal(bs, dtTzid.dt);
            return true;
        }
        if (prop instanceof CalscalePropType) {
            CalscalePropType p = (CalscalePropType)prop;
            this.propVal(bs, p.getText().name());
            return true;
        }
        if (prop instanceof AttachPropType) {
            AttachPropType p = (AttachPropType)prop;
            if (p.getUri() != null) {
                this.propVal(bs, p.getUri());
            } else {
                this.propVal(bs, p.getBinary());
            }
            return true;
        }
        if (prop instanceof GeoPropType) {
            GeoPropType p = (GeoPropType)prop;
            this.propVal(bs, p.getLatitude() + ";" + p.getLongitude());
            return true;
        }
        if (prop instanceof FreebusyPropType) {
            FreebusyPropType p = (FreebusyPropType)prop;
            this.propVal(bs, this.fromList(p.getPeriod(), false));
            return true;
        }
        if (prop instanceof TriggerPropType) {
            TriggerPropType p = (TriggerPropType)prop;
            if (p.getDuration() != null) {
                this.propVal(bs, p.getDuration());
            } else {
                this.propVal(bs, XcalUtil.getIcalFormatDateTime(p.getDateTime().toString()));
            }
            return true;
        }
        if (prop instanceof RequestStatusPropType) {
            RequestStatusPropType p = (RequestStatusPropType)prop;
            StringBuilder sb = new StringBuilder();
            sb.append(p.getCode());
            if (p.getDescription() != null) {
                sb.append(";");
                sb.append(p.getDescription());
            }
            if (p.getExtdata() != null) {
                sb.append(";");
                sb.append(p.getExtdata());
            }
            this.propVal(bs, sb.toString());
            return true;
        }
        if (WsXMLTranslator.getLog().isDebugEnabled()) {
            WsXMLTranslator.warn("Unhandled class " + prop.getClass());
        }
        return false;
    }

    private void addRecurEl(List<String> l, String name, Object o) {
        String val;
        if (o == null) {
            return;
        }
        if (o instanceof List) {
            val = this.fromList((List)o, false);
            if (val == null) {
                return;
            }
        } else {
            val = String.valueOf(o);
        }
        l.add(name + "=" + val);
    }

    private void propVal(BuildState bs, String val) throws Throwable {
        bs.getContentHandler().propertyValue(val);
    }

    private String getParValue(BaseParameterType bpt) throws Throwable {
        if (bpt instanceof TextParameterType) {
            return ((TextParameterType)bpt).getText();
        }
        if (bpt instanceof DurationParameterType) {
            return ((DurationParameterType)bpt).getDuration().toString();
        }
        if (bpt instanceof RangeParamType) {
            return ((RangeParamType)bpt).getText().value();
        }
        if (bpt instanceof CalAddressListParamType) {
            return this.fromList(((CalAddressListParamType)bpt).getCalAddress(), true);
        }
        if (bpt instanceof CalAddressParamType) {
            return ((CalAddressParamType)bpt).getCalAddress();
        }
        if (bpt instanceof UriParameterType) {
            return ((UriParameterType)bpt).getUri();
        }
        throw new Exception("Unsupported param type");
    }

    private String fromList(List<?> l, boolean quote) {
        return this.fromList(l, quote, ",");
    }

    private String fromList(List<?> l, boolean quote, String delimChar) {
        if (l == null || l.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        String qt = "";
        if (quote) {
            qt = "\"";
        }
        for (Object o : l) {
            sb.append(delim);
            delim = delimChar;
            sb.append(qt);
            sb.append(o);
            sb.append(qt);
        }
        return sb.toString();
    }

    public static Logger getLog() {
        return Logger.getLogger(WsXMLTranslator.class);
    }

    public static void error(Throwable t) {
        WsXMLTranslator.getLog().error(WsXMLTranslator.class, t);
    }

    public static void warn(String msg) {
        WsXMLTranslator.getLog().warn(msg);
    }
}

