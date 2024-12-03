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
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.DateDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DaylightType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import ietf.params.xml.ns.icalendar_2.ObjectFactory;
import ietf.params.xml.ns.icalendar_2.StandardType;
import ietf.params.xml.ns.icalendar_2.TzidParamType;
import ietf.params.xml.ns.icalendar_2.UntilRecurType;
import ietf.params.xml.ns.icalendar_2.ValarmType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import ietf.params.xml.ns.icalendar_2.VeventType;
import ietf.params.xml.ns.icalendar_2.VfreebusyType;
import ietf.params.xml.ns.icalendar_2.VjournalType;
import ietf.params.xml.ns.icalendar_2.VtimezoneType;
import ietf.params.xml.ns.icalendar_2.VtodoType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import org.bedework.util.xml.tagdefs.XcalTags;

public class XcalUtil {
    private static final ObjectFactory icalOf = new ObjectFactory();
    static Map<Class, QName> compNames = new HashMap<Class, QName>();
    public static final Integer UnknownKind = -1;
    public static final Integer OuterKind = 0;
    public static final Integer RecurringKind = 1;
    public static final Integer UidKind = 2;
    public static final Integer AlarmKind = 3;
    public static final Integer TzKind = 4;
    public static final Integer TzDaylight = 5;
    public static final Integer TzStandard = 6;
    static Map<QName, Integer> compKinds = new HashMap<QName, Integer>();

    public static void initDt(DateDatetimePropertyType dt, String dtval, String tzid) throws Throwable {
        XMLGregorianCalendar xgc = XcalUtil.fromDtval(dtval);
        if (dtval.length() == 8) {
            dt.setDate(xgc);
            return;
        }
        dt.setDateTime(xgc);
        if (dtval.endsWith("Z") || tzid == null) {
            return;
        }
        TzidParamType tz = new TzidParamType();
        tz.setText(tzid);
        ArrayOfParameters aop = dt.getParameters();
        if (aop == null) {
            aop = new ArrayOfParameters();
            dt.setParameters(aop);
        }
        aop.getBaseParameter().add(icalOf.createTzid(tz));
        dt.setParameters(aop);
    }

    public static void initUntilRecur(UntilRecurType dt, String dtval) throws Throwable {
        XMLGregorianCalendar xgc = XcalUtil.fromDtval(dtval);
        if (dtval.length() == 8) {
            dt.setDate(xgc);
            return;
        }
        dt.setDateTime(xgc);
    }

    public static XMLGregorianCalendar fromDtval(String dtval) throws Throwable {
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        return dtf.newXMLGregorianCalendar(XcalUtil.getXmlFormatDateTime(dtval));
    }

    public static Duration makeXmlDuration(String dur) throws Throwable {
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        return dtf.newDuration(dur);
    }

    public static XMLGregorianCalendar getXMlUTCCal(String dtval) throws Throwable {
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        return dtf.newXMLGregorianCalendar(XcalUtil.getXmlFormatDateTime(dtval));
    }

    public static String getUTC(DateDatetimePropertyType dt, TzGetter tzs) throws Throwable {
        DtTzid dtz = XcalUtil.getDtTzid(dt);
        if (dtz.dt.length() == 18 && dtz.dt.charAt(17) == 'Z') {
            return dtz.dt;
        }
        TimeZone tz = null;
        if (dtz.tzid != null) {
            tz = tzs.getTz(dtz.tzid);
        }
        DateTime dtim = new DateTime(dtz.dt, tz);
        dtim.setUtc(true);
        return dtim.toString();
    }

    public static DtTzid getDtTzid(DateDatetimePropertyType dt) {
        DtTzid res = new DtTzid();
        ArrayOfParameters aop = dt.getParameters();
        if (aop != null) {
            for (JAXBElement<? extends BaseParameterType> e : aop.getBaseParameter()) {
                if (!e.getName().equals(XcalTags.tzid)) continue;
                res.tzid = ((TzidParamType)e.getValue()).getText();
                break;
            }
        }
        res.dateOnly = dt.getDate() != null;
        res.dt = res.dateOnly ? XcalUtil.getIcalFormatDateTime(dt.getDate().toString()) : XcalUtil.getIcalFormatDateTime(dt.getDateTime().toString());
        return res;
    }

    public static String getXmlFormatDateTime(String val) {
        if (val.charAt(4) == '-') {
            return val;
        }
        if (val.length() < 8) {
            throw new RuntimeException("Bad date: " + val);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(val.substring(0, 4));
        sb.append("-");
        sb.append(val.substring(4, 6));
        sb.append("-");
        sb.append(val.substring(6, 8));
        if (val.length() > 8) {
            sb.append("T");
            sb.append(val.substring(9, 11));
            sb.append(":");
            sb.append(val.substring(11, 13));
            sb.append(":");
            sb.append(val.substring(13));
        }
        return sb.toString();
    }

    public static String getIcalFormatDateTime(XMLGregorianCalendar dt) {
        if (dt == null) {
            return null;
        }
        return XcalUtil.getIcalFormatDateTime(dt.toXMLFormat());
    }

    public static String getIcalFormatDateTime(String dt) {
        if (dt == null) {
            return null;
        }
        if (dt.charAt(4) != '-') {
            return dt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(dt.substring(0, 4));
        sb.append(dt.substring(5, 7));
        sb.append(dt.substring(8, 10));
        if (dt.length() > 10) {
            sb.append("T");
            sb.append(dt.substring(11, 13));
            sb.append(dt.substring(14, 16));
            sb.append(dt.substring(17, 19));
            if (dt.endsWith("Z")) {
                sb.append("Z");
            }
        }
        return sb.toString();
    }

    public static String getXmlFormatTime(String val) {
        if (val.charAt(2) == ':') {
            return val;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(val.substring(0, 2));
        sb.append(":");
        sb.append(val.substring(2, 4));
        sb.append(":");
        sb.append(val.substring(4));
        return sb.toString();
    }

    public static String getIcalFormatTime(String tm) {
        if (tm == null) {
            return null;
        }
        if (tm.charAt(2) != ':') {
            return tm;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tm.substring(0, 2));
        sb.append(tm.substring(3, 5));
        sb.append(tm.substring(6));
        return sb.toString();
    }

    public static String getIcalUtcOffset(String tm) {
        if (tm == null) {
            return null;
        }
        if (tm.charAt(3) != ':') {
            return tm;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tm.substring(0, 3));
        sb.append(tm.substring(4));
        return sb.toString();
    }

    public static String getXmlFormatUtcOffset(String val) {
        if (val.charAt(3) == ':') {
            return val;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(val.substring(0, 3));
        sb.append(":");
        sb.append(val.substring(3));
        return sb.toString();
    }

    public static BaseComponentType cloneComponent(BaseComponentType comp) throws Throwable {
        return (BaseComponentType)comp.getClass().newInstance();
    }

    public static BasePropertyType cloneProperty(BasePropertyType prop) throws Throwable {
        return (BasePropertyType)prop.getClass().newInstance();
    }

    public static BaseParameterType cloneProperty(BaseParameterType param) throws Throwable {
        return (BaseParameterType)param.getClass().newInstance();
    }

    public static BaseComponentType findComponent(IcalendarType ical, QName name) {
        for (VcalendarType v : ical.getVcalendar()) {
            if (name.equals(XcalTags.vcalendar)) {
                return v;
            }
            BaseComponentType bc = XcalUtil.findComponent(v, name);
            if (bc == null) continue;
            return bc;
        }
        return null;
    }

    public static List<JAXBElement<? extends BaseComponentType>> getComponents(BaseComponentType c) {
        if (c.getComponents() == null) {
            return null;
        }
        return new ArrayList<JAXBElement<? extends BaseComponentType>>(c.getComponents().getBaseComponent());
    }

    public static BaseComponentType findComponent(BaseComponentType bcPar, QName name) {
        List<JAXBElement<? extends BaseComponentType>> cs = XcalUtil.getComponents(bcPar);
        if (cs == null) {
            return null;
        }
        for (JAXBElement<? extends BaseComponentType> bcel : cs) {
            if (bcel.getName().equals(name)) {
                return (BaseComponentType)bcel.getValue();
            }
            BaseComponentType bc = XcalUtil.findComponent((BaseComponentType)bcel.getValue(), name);
            if (bc == null) continue;
            return bc;
        }
        return null;
    }

    public static BaseComponentType findEntity(IcalendarType ical) {
        if (ical == null) {
            return null;
        }
        for (VcalendarType v : ical.getVcalendar()) {
            Iterator<JAXBElement<? extends BaseComponentType>> iterator;
            ArrayOfComponents cs = v.getComponents();
            if (cs == null || !(iterator = cs.getBaseComponent().iterator()).hasNext()) continue;
            JAXBElement<? extends BaseComponentType> bcel = iterator.next();
            return (BaseComponentType)bcel.getValue();
        }
        return null;
    }

    public static BasePropertyType findProperty(BaseComponentType bcPar, QName name) {
        if (bcPar == null) {
            return null;
        }
        ArrayOfProperties ps = bcPar.getProperties();
        if (ps == null) {
            return null;
        }
        for (JAXBElement<? extends BasePropertyType> bpel : ps.getBasePropertyOrTzid()) {
            if (!bpel.getName().equals(name)) continue;
            return (BasePropertyType)bpel.getValue();
        }
        return null;
    }

    public static BaseParameterType findParam(BasePropertyType prop, QName name) {
        if (prop == null) {
            return null;
        }
        ArrayOfParameters ps = prop.getParameters();
        if (ps == null) {
            return null;
        }
        for (JAXBElement<? extends BaseParameterType> bpel : ps.getBaseParameter()) {
            if (!bpel.getName().equals(name)) continue;
            return (BaseParameterType)bpel.getValue();
        }
        return null;
    }

    public static QName getCompName(Class cl) {
        return compNames.get(cl);
    }

    public static int getCompKind(QName name) {
        return compKinds.get(name);
    }

    private static void addInfo(QName nm, Integer kind, Class cl) {
        compNames.put(cl, nm);
        compKinds.put(nm, kind);
    }

    static {
        XcalUtil.addInfo(XcalTags.vcalendar, OuterKind, VcalendarType.class);
        XcalUtil.addInfo(XcalTags.vtodo, RecurringKind, VtodoType.class);
        XcalUtil.addInfo(XcalTags.vjournal, RecurringKind, VjournalType.class);
        XcalUtil.addInfo(XcalTags.vevent, RecurringKind, VeventType.class);
        XcalUtil.addInfo(XcalTags.vfreebusy, UidKind, VfreebusyType.class);
        XcalUtil.addInfo(XcalTags.valarm, AlarmKind, ValarmType.class);
        XcalUtil.addInfo(XcalTags.standard, TzStandard, StandardType.class);
        XcalUtil.addInfo(XcalTags.vtimezone, TzKind, VtimezoneType.class);
        XcalUtil.addInfo(XcalTags.daylight, TzDaylight, DaylightType.class);
    }

    public static interface TzGetter {
        public TimeZone getTz(String var1) throws Throwable;
    }

    public static class DtTzid {
        public String dt;
        public boolean dateOnly;
        public String tzid;
    }
}

