/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar;

import ietf.params.xml.ns.icalendar_2.ActionPropType;
import ietf.params.xml.ns.icalendar_2.AltrepParamType;
import ietf.params.xml.ns.icalendar_2.ArrayOfComponents;
import ietf.params.xml.ns.icalendar_2.ArrayOfParameters;
import ietf.params.xml.ns.icalendar_2.ArrayOfProperties;
import ietf.params.xml.ns.icalendar_2.AttendeePropType;
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.CategoriesPropType;
import ietf.params.xml.ns.icalendar_2.ClassPropType;
import ietf.params.xml.ns.icalendar_2.CnParamType;
import ietf.params.xml.ns.icalendar_2.CommentPropType;
import ietf.params.xml.ns.icalendar_2.CompletedPropType;
import ietf.params.xml.ns.icalendar_2.ContactPropType;
import ietf.params.xml.ns.icalendar_2.CreatedPropType;
import ietf.params.xml.ns.icalendar_2.CutypeParamType;
import ietf.params.xml.ns.icalendar_2.DateDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.DaylightType;
import ietf.params.xml.ns.icalendar_2.DelegatedFromParamType;
import ietf.params.xml.ns.icalendar_2.DelegatedToParamType;
import ietf.params.xml.ns.icalendar_2.DescriptionPropType;
import ietf.params.xml.ns.icalendar_2.DirParamType;
import ietf.params.xml.ns.icalendar_2.DtendPropType;
import ietf.params.xml.ns.icalendar_2.DtstampPropType;
import ietf.params.xml.ns.icalendar_2.DtstartPropType;
import ietf.params.xml.ns.icalendar_2.DuePropType;
import ietf.params.xml.ns.icalendar_2.DurationPropType;
import ietf.params.xml.ns.icalendar_2.ExrulePropType;
import ietf.params.xml.ns.icalendar_2.FbtypeParamType;
import ietf.params.xml.ns.icalendar_2.FreebusyPropType;
import ietf.params.xml.ns.icalendar_2.FreqRecurType;
import ietf.params.xml.ns.icalendar_2.GeoPropType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import ietf.params.xml.ns.icalendar_2.LanguageParamType;
import ietf.params.xml.ns.icalendar_2.LastModifiedPropType;
import ietf.params.xml.ns.icalendar_2.LocationPropType;
import ietf.params.xml.ns.icalendar_2.MemberParamType;
import ietf.params.xml.ns.icalendar_2.MethodPropType;
import ietf.params.xml.ns.icalendar_2.ObjectFactory;
import ietf.params.xml.ns.icalendar_2.OrganizerPropType;
import ietf.params.xml.ns.icalendar_2.PartstatParamType;
import ietf.params.xml.ns.icalendar_2.PercentCompletePropType;
import ietf.params.xml.ns.icalendar_2.PeriodType;
import ietf.params.xml.ns.icalendar_2.PriorityPropType;
import ietf.params.xml.ns.icalendar_2.ProdidPropType;
import ietf.params.xml.ns.icalendar_2.RdatePropType;
import ietf.params.xml.ns.icalendar_2.RecurType;
import ietf.params.xml.ns.icalendar_2.RecurrenceIdPropType;
import ietf.params.xml.ns.icalendar_2.RelatedParamType;
import ietf.params.xml.ns.icalendar_2.RelatedToPropType;
import ietf.params.xml.ns.icalendar_2.ReltypeParamType;
import ietf.params.xml.ns.icalendar_2.RepeatPropType;
import ietf.params.xml.ns.icalendar_2.ResourcesPropType;
import ietf.params.xml.ns.icalendar_2.RoleParamType;
import ietf.params.xml.ns.icalendar_2.RrulePropType;
import ietf.params.xml.ns.icalendar_2.ScheduleStatusParamType;
import ietf.params.xml.ns.icalendar_2.SentByParamType;
import ietf.params.xml.ns.icalendar_2.SequencePropType;
import ietf.params.xml.ns.icalendar_2.StandardType;
import ietf.params.xml.ns.icalendar_2.StatusPropType;
import ietf.params.xml.ns.icalendar_2.SummaryPropType;
import ietf.params.xml.ns.icalendar_2.TranspPropType;
import ietf.params.xml.ns.icalendar_2.TriggerPropType;
import ietf.params.xml.ns.icalendar_2.TzidParamType;
import ietf.params.xml.ns.icalendar_2.UidPropType;
import ietf.params.xml.ns.icalendar_2.UntilRecurType;
import ietf.params.xml.ns.icalendar_2.UrlPropType;
import ietf.params.xml.ns.icalendar_2.ValarmType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import ietf.params.xml.ns.icalendar_2.VersionPropType;
import ietf.params.xml.ns.icalendar_2.VeventType;
import ietf.params.xml.ns.icalendar_2.VfreebusyType;
import ietf.params.xml.ns.icalendar_2.VjournalType;
import ietf.params.xml.ns.icalendar_2.VtimezoneType;
import ietf.params.xml.ns.icalendar_2.VtodoType;
import ietf.params.xml.ns.icalendar_2.XBedeworkCostPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkWrappedNameParamType;
import ietf.params.xml.ns.icalendar_2.XBedeworkWrapperPropType;
import ietf.params.xml.ns.icalendar_2.XBwCategoriesPropType;
import ietf.params.xml.ns.icalendar_2.XBwContactPropType;
import ietf.params.xml.ns.icalendar_2.XBwLocationPropType;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TextList;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Resources;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.XProperty;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.misc.Util;

public class IcalToXcal {
    static ObjectFactory of = new ObjectFactory();

    public static IcalendarType fromIcal(Calendar cal, BaseComponentType pattern, boolean wrapXprops) throws Throwable {
        return IcalToXcal.fromIcal(cal, pattern, false, wrapXprops);
    }

    public static IcalendarType fromIcal(Calendar cal, BaseComponentType pattern, boolean doTimezones, boolean wrapXprops) throws Throwable {
        IcalendarType ical = new IcalendarType();
        VcalendarType vcal = new VcalendarType();
        ical.getVcalendar().add(vcal);
        IcalToXcal.processProperties(cal.getProperties(), vcal, pattern, wrapXprops);
        ComponentList<CalendarComponent> icComps = cal.getComponents();
        if (icComps == null) {
            return ical;
        }
        ArrayOfComponents aoc = new ArrayOfComponents();
        vcal.setComponents(aoc);
        for (Object e : icComps) {
            if (!doTimezones && e instanceof VTimeZone) continue;
            aoc.getBaseComponent().add((JAXBElement<? extends BaseComponentType>)IcalToXcal.toComponent((CalendarComponent)e, pattern, wrapXprops));
        }
        return ical;
    }

    public static JAXBElement toComponent(Component val, BaseComponentType pattern, boolean wrapXprops) throws Throwable {
        Object el;
        if (val == null) {
            return null;
        }
        PropertyList<Property> icprops = val.getProperties();
        ComponentList<Component> icComps = null;
        if (icprops == null) {
            return null;
        }
        if (val instanceof VEvent) {
            el = of.createVevent(new VeventType());
            icComps = ((VEvent)val).getAlarms();
        } else if (val instanceof VToDo) {
            el = of.createVtodo(new VtodoType());
            icComps = ((VToDo)val).getAlarms();
        } else if (val instanceof VJournal) {
            el = of.createVjournal(new VjournalType());
        } else if (val instanceof VFreeBusy) {
            el = of.createVfreebusy(new VfreebusyType());
        } else if (val instanceof VAlarm) {
            el = of.createValarm(new ValarmType());
        } else if (val instanceof VTimeZone) {
            el = of.createVtimezone(new VtimezoneType());
            icComps = ((VTimeZone)val).getObservances();
        } else if (val instanceof Daylight) {
            el = of.createDaylight(new DaylightType());
        } else if (val instanceof Standard) {
            el = of.createStandard(new StandardType());
        } else {
            throw new Exception("org.bedework.invalid.entity.type" + val.getClass().getName());
        }
        BaseComponentType comp = (BaseComponentType)el.getValue();
        IcalToXcal.processProperties(val.getProperties(), comp, pattern, wrapXprops);
        if (Util.isEmpty(icComps)) {
            return el;
        }
        ArrayOfComponents aoc = new ArrayOfComponents();
        comp.setComponents(aoc);
        for (Object e : icComps) {
            JAXBElement subel = IcalToXcal.toComponent((Component)e, pattern, wrapXprops);
            aoc.getBaseComponent().add((JAXBElement<? extends BaseComponentType>)subel);
        }
        return el;
    }

    public static void processProperties(PropertyList icprops, BaseComponentType comp, BaseComponentType pattern, boolean wrapXprops) throws Throwable {
        if (icprops == null || icprops.isEmpty()) {
            return;
        }
        comp.setProperties(new ArrayOfProperties());
        List<JAXBElement<? extends BasePropertyType>> pl = comp.getProperties().getBasePropertyOrTzid();
        for (Object icprop : icprops) {
            JAXBElement<? extends BasePropertyType> xmlprop;
            Property prop = (Property)icprop;
            PropertyIndex.PropertyInfoIndex pii = PropertyIndex.PropertyInfoIndex.fromName(prop.getName());
            if (pii != null && !IcalToXcal.emit(pattern, comp.getClass(), pii.getXmlClass()) || (xmlprop = IcalToXcal.doProperty(prop, pii, wrapXprops)) == null) continue;
            IcalToXcal.processParameters(prop.getParameters(), (BasePropertyType)xmlprop.getValue());
            pl.add(xmlprop);
        }
    }

    static JAXBElement<? extends BasePropertyType> doProperty(Property prop, PropertyIndex.PropertyInfoIndex pii, boolean wrapXprops) throws Throwable {
        if (prop instanceof XProperty) {
            if (!wrapXprops) {
                return null;
            }
            XBedeworkWrapperPropType wrapper = new XBedeworkWrapperPropType();
            wrapper.setText(prop.getValue());
            XBedeworkWrappedNameParamType wnp = new XBedeworkWrappedNameParamType();
            wnp.setText(prop.getName());
            if (wrapper.getParameters() == null) {
                wrapper.setParameters(new ArrayOfParameters());
            }
            wrapper.getParameters().getBaseParameter().add(of.createXBedeworkWrappedName(wnp));
            return of.createXBedeworkWrapper(wrapper);
        }
        switch (pii) {
            case ACTION: {
                ActionPropType a = new ActionPropType();
                a.setText(prop.getValue());
                return of.createAction(a);
            }
            case ATTACH: {
                return null;
            }
            case ATTENDEE: {
                AttendeePropType att = new AttendeePropType();
                att.setCalAddress(prop.getValue());
                return of.createAttendee(att);
            }
            case BUSYTYPE: {
                return null;
            }
            case CATEGORIES: {
                CategoriesPropType c = new CategoriesPropType();
                TextList cats = ((Categories)prop).getCategories();
                Iterator<String> pit = cats.iterator();
                while (pit.hasNext()) {
                    c.getText().add(pit.next());
                }
                return of.createCategories(c);
            }
            case CLASS: {
                ClassPropType cl = new ClassPropType();
                cl.setText(prop.getValue());
                return of.createClass(cl);
            }
            case COMMENT: {
                CommentPropType cm = new CommentPropType();
                cm.setText(prop.getValue());
                return of.createComment(cm);
            }
            case COMPLETED: {
                CompletedPropType cmp = new CompletedPropType();
                cmp.setUtcDateTime(XcalUtil.getXMlUTCCal(prop.getValue()));
                return of.createCompleted(cmp);
            }
            case CONTACT: {
                ContactPropType ct = new ContactPropType();
                ct.setText(prop.getValue());
                return of.createContact(ct);
            }
            case CREATED: {
                CreatedPropType created = new CreatedPropType();
                created.setUtcDateTime(XcalUtil.getXMlUTCCal(prop.getValue()));
                return of.createCreated(created);
            }
            case DESCRIPTION: {
                DescriptionPropType desc = new DescriptionPropType();
                desc.setText(prop.getValue());
                return of.createDescription(desc);
            }
            case DTEND: {
                DtendPropType dtend = (DtendPropType)IcalToXcal.makeDateDatetime(new DtendPropType(), prop);
                return of.createDtend(dtend);
            }
            case DTSTAMP: {
                DtstampPropType dtstamp = new DtstampPropType();
                dtstamp.setUtcDateTime(XcalUtil.getXMlUTCCal(prop.getValue()));
                return of.createDtstamp(dtstamp);
            }
            case DTSTART: {
                DtstartPropType dtstart = (DtstartPropType)IcalToXcal.makeDateDatetime(new DtstartPropType(), prop);
                return of.createDtstart(dtstart);
            }
            case DUE: {
                DuePropType due = (DuePropType)IcalToXcal.makeDateDatetime(new DuePropType(), prop);
                return of.createDue(due);
            }
            case DURATION: {
                DurationPropType dur = new DurationPropType();
                dur.setDuration(prop.getValue());
                return of.createDuration(dur);
            }
            case EXDATE: {
                return null;
            }
            case EXRULE: {
                ExrulePropType er = new ExrulePropType();
                er.setRecur(IcalToXcal.doRecur(((RRule)prop).getRecur()));
                return of.createExrule(er);
            }
            case FREEBUSY: {
                FreeBusy icfb = (FreeBusy)prop;
                PeriodList fbps = icfb.getPeriods();
                if (Util.isEmpty(fbps)) {
                    return null;
                }
                FreebusyPropType fb = new FreebusyPropType();
                String fbtype = IcalToXcal.paramVal(prop, "FBTYPE");
                if (fbtype != null) {
                    ArrayOfParameters pars = IcalToXcal.getAop(fb);
                    FbtypeParamType f = new FbtypeParamType();
                    f.setText(fbtype);
                    JAXBElement<FbtypeParamType> param = of.createFbtype(f);
                    pars.getBaseParameter().add(param);
                }
                List<PeriodType> pdl = fb.getPeriod();
                for (Object o : fbps) {
                    Period p = (Period)o;
                    PeriodType np = new PeriodType();
                    np.setStart(XcalUtil.getXMlUTCCal(p.getStart().toString()));
                    np.setEnd(XcalUtil.getXMlUTCCal(p.getEnd().toString()));
                    pdl.add(np);
                }
                return of.createFreebusy(fb);
            }
            case GEO: {
                Geo geo = (Geo)prop;
                GeoPropType g = new GeoPropType();
                g.setLatitude(geo.getLatitude().floatValue());
                g.setLatitude(geo.getLongitude().floatValue());
                return of.createGeo(g);
            }
            case LAST_MODIFIED: {
                LastModifiedPropType lm = new LastModifiedPropType();
                lm.setUtcDateTime(XcalUtil.getXMlUTCCal(prop.getValue()));
                return of.createLastModified(lm);
            }
            case LOCATION: {
                LocationPropType l = new LocationPropType();
                l.setText(prop.getValue());
                return of.createLocation(l);
            }
            case METHOD: {
                MethodPropType m = new MethodPropType();
                m.setText(prop.getValue());
                return of.createMethod(m);
            }
            case ORGANIZER: {
                OrganizerPropType org = new OrganizerPropType();
                org.setCalAddress(prop.getValue());
                return of.createOrganizer(org);
            }
            case PERCENT_COMPLETE: {
                PercentCompletePropType p = new PercentCompletePropType();
                p.setInteger(BigInteger.valueOf(((PercentComplete)prop).getPercentage()));
                return of.createPercentComplete(p);
            }
            case PRIORITY: {
                PriorityPropType pr = new PriorityPropType();
                pr.setInteger(BigInteger.valueOf(((Priority)prop).getLevel()));
                return of.createPriority(pr);
            }
            case PRODID: {
                ProdidPropType prod = new ProdidPropType();
                prod.setText(prop.getValue());
                return of.createProdid(prod);
            }
            case RDATE: {
                RdatePropType rdate = (RdatePropType)IcalToXcal.makeDateDatetime(new RdatePropType(), prop);
                return of.createRdate(rdate);
            }
            case RECURRENCE_ID: {
                RecurrenceIdPropType ri = new RecurrenceIdPropType();
                String strval = prop.getValue();
                if (IcalToXcal.dateOnly(prop)) {
                    if (strval.length() > 8) {
                        strval = strval.substring(0, 8);
                    }
                    ri.setDate(XcalUtil.fromDtval(strval));
                } else {
                    XcalUtil.initDt(ri, strval, IcalToXcal.getTzid(prop));
                }
                return of.createRecurrenceId(ri);
            }
            case RELATED_TO: {
                RelatedToPropType rt = new RelatedToPropType();
                String relType = IcalToXcal.paramVal(prop, "RELTYPE");
                String value = IcalToXcal.paramVal(prop, "VALUE");
                if (value == null || "uid".equalsIgnoreCase(value)) {
                    rt.setUid(prop.getValue());
                } else if ("uri".equalsIgnoreCase(value)) {
                    rt.setUri(prop.getValue());
                } else {
                    rt.setText(prop.getValue());
                }
                if (relType != null) {
                    ArrayOfParameters pars = IcalToXcal.getAop(rt);
                    ReltypeParamType r = new ReltypeParamType();
                    r.setText(relType);
                    JAXBElement<ReltypeParamType> param = of.createReltype(r);
                    pars.getBaseParameter().add(param);
                }
                return of.createRelatedTo(rt);
            }
            case REPEAT: {
                Repeat rept = (Repeat)prop;
                RepeatPropType rep = new RepeatPropType();
                rep.setInteger(BigInteger.valueOf(rept.getCount()));
                return of.createRepeat(rep);
            }
            case REQUEST_STATUS: {
                return null;
            }
            case RESOURCES: {
                ResourcesPropType r = new ResourcesPropType();
                List<String> rl = r.getText();
                TextList rlist = ((Resources)prop).getResources();
                Iterator<String> rlit = rlist.iterator();
                while (rlit.hasNext()) {
                    rl.add(rlit.next());
                }
                return of.createResources(r);
            }
            case RRULE: {
                RrulePropType rrp = new RrulePropType();
                rrp.setRecur(IcalToXcal.doRecur(((RRule)prop).getRecur()));
                return of.createRrule(rrp);
            }
            case SEQUENCE: {
                SequencePropType s = new SequencePropType();
                s.setInteger(BigInteger.valueOf(((Sequence)prop).getSequenceNo()));
                return of.createSequence(s);
            }
            case STATUS: {
                StatusPropType st = new StatusPropType();
                st.setText(prop.getValue());
                return of.createStatus(st);
            }
            case SUMMARY: {
                SummaryPropType sum = new SummaryPropType();
                sum.setText(prop.getValue());
                return of.createSummary(sum);
            }
            case TRIGGER: {
                TriggerPropType trig = new TriggerPropType();
                String valType = IcalToXcal.paramVal(prop, "VALUE");
                if (valType == null || valType.equalsIgnoreCase(Value.DURATION.getValue())) {
                    trig.setDuration(prop.getValue());
                    String rel = IcalToXcal.paramVal(prop, "RELATED");
                    if (rel != null) {
                        ArrayOfParameters pars = IcalToXcal.getAop(trig);
                        RelatedParamType rpar = new RelatedParamType();
                        rpar.setText("END");
                        JAXBElement<RelatedParamType> param = of.createRelated(rpar);
                        pars.getBaseParameter().add(param);
                    }
                } else if (valType.equalsIgnoreCase(Value.DATE_TIME.getValue())) {
                    trig.setDateTime(XcalUtil.getXMlUTCCal(prop.getValue()));
                }
                return of.createTrigger(trig);
            }
            case TRANSP: {
                TranspPropType t = new TranspPropType();
                t.setText(prop.getValue());
                return of.createTransp(t);
            }
            case TZID: 
            case TZNAME: 
            case TZOFFSETFROM: 
            case TZOFFSETTO: 
            case TZURL: {
                return null;
            }
            case UID: {
                UidPropType uid = new UidPropType();
                uid.setText(prop.getValue());
                return of.createUid(uid);
            }
            case URL: {
                UrlPropType u = new UrlPropType();
                u.setUri(prop.getValue());
                return of.createUrl(u);
            }
            case VERSION: {
                VersionPropType vers = new VersionPropType();
                vers.setText(prop.getValue());
                return of.createVersion(vers);
            }
            case XBEDEWORK_COST: {
                XBedeworkCostPropType cst = new XBedeworkCostPropType();
                cst.setText(prop.getValue());
                return of.createXBedeworkCost(cst);
            }
            case X_BEDEWORK_CATEGORIES: {
                XBwCategoriesPropType xpcat = new XBwCategoriesPropType();
                xpcat.getText().add(prop.getValue());
                return of.createXBedeworkCategories(xpcat);
            }
            case X_BEDEWORK_CONTACT: {
                XBwContactPropType xpcon = new XBwContactPropType();
                xpcon.setText(prop.getValue());
                return of.createXBedeworkContact(xpcon);
            }
            case X_BEDEWORK_LOCATION: {
                XBwLocationPropType xploc = new XBwLocationPropType();
                xploc.setText(prop.getValue());
                return of.createXBedeworkLocation(xploc);
            }
        }
        if (prop instanceof XProperty) {
            if (!wrapXprops) {
                return null;
            }
            XBedeworkWrapperPropType wrapper = new XBedeworkWrapperPropType();
            IcalToXcal.processParameters(prop.getParameters(), wrapper);
            return of.createXBedeworkWrapper(wrapper);
        }
        return null;
    }

    static void processParameters(ParameterList icparams, BasePropertyType prop) throws Throwable {
        if (icparams == null || icparams.isEmpty()) {
            return;
        }
        for (Parameter param : icparams) {
            JAXBElement<? extends BaseParameterType> xmlprop;
            PropertyIndex.ParameterInfoIndex pii = PropertyIndex.ParameterInfoIndex.lookupPname(param.getName());
            if (pii == null || (xmlprop = IcalToXcal.doParameter(param, pii)) == null) continue;
            if (prop.getParameters() == null) {
                prop.setParameters(new ArrayOfParameters());
            }
            prop.getParameters().getBaseParameter().add(xmlprop);
        }
    }

    static JAXBElement<? extends BaseParameterType> doParameter(Parameter param, PropertyIndex.ParameterInfoIndex pii) throws Throwable {
        switch (pii) {
            case ALTREP: {
                AltrepParamType ar = new AltrepParamType();
                ar.setUri(param.getValue());
                return of.createAltrep(ar);
            }
            case CN: {
                CnParamType cn = new CnParamType();
                cn.setText(param.getValue());
                return of.createCn(cn);
            }
            case CUTYPE: {
                CutypeParamType c = new CutypeParamType();
                c.setText(param.getValue());
                return of.createCutype(c);
            }
            case DELEGATED_FROM: {
                DelegatedFromParamType df = new DelegatedFromParamType();
                df.getCalAddress().add(param.getValue());
                return of.createDelegatedFrom(df);
            }
            case DELEGATED_TO: {
                DelegatedToParamType dt = new DelegatedToParamType();
                dt.getCalAddress().add(param.getValue());
                return of.createDelegatedTo(dt);
            }
            case DIR: {
                DirParamType d = new DirParamType();
                d.setUri(param.getValue());
                return of.createDir(d);
            }
            case ENCODING: {
                return null;
            }
            case FMTTYPE: {
                return null;
            }
            case FBTYPE: {
                return null;
            }
            case LANGUAGE: {
                LanguageParamType l = new LanguageParamType();
                l.setText(param.getValue());
                return of.createLanguage(l);
            }
            case MEMBER: {
                MemberParamType m = new MemberParamType();
                m.getCalAddress().add(param.getValue());
                return of.createMember(m);
            }
            case PARTSTAT: {
                PartstatParamType partstat = new PartstatParamType();
                partstat.setText(param.getValue());
                return of.createPartstat(partstat);
            }
            case RANGE: {
                return null;
            }
            case RELATED: {
                return null;
            }
            case RELTYPE: {
                return null;
            }
            case ROLE: {
                RoleParamType r = new RoleParamType();
                r.setText(param.getValue());
                return of.createRole(r);
            }
            case RSVP: {
                return null;
            }
            case SCHEDULE_AGENT: {
                return null;
            }
            case SCHEDULE_STATUS: {
                ScheduleStatusParamType ss = new ScheduleStatusParamType();
                ss.setText(param.getValue());
                return of.createScheduleStatus(ss);
            }
            case SENT_BY: {
                SentByParamType sb = new SentByParamType();
                sb.setCalAddress(param.getValue());
                return of.createSentBy(sb);
            }
            case TYPE: {
                return null;
            }
            case TZID: {
                TzidParamType tzid = new TzidParamType();
                tzid.setText(param.getValue());
                return of.createTzid(tzid);
            }
            case VALUE: {
                return null;
            }
        }
        return null;
    }

    public static RecurType doRecur(Recur r) throws Throwable {
        Date until;
        RecurType rt = new RecurType();
        rt.setFreq(FreqRecurType.fromValue(r.getFrequency()));
        if (r.getCount() > 0) {
            rt.setCount(BigInteger.valueOf(r.getCount()));
        }
        if ((until = r.getUntil()) != null) {
            UntilRecurType u = new UntilRecurType();
            XcalUtil.initUntilRecur(u, until.toString());
        }
        if (r.getInterval() > 0) {
            rt.setInterval(String.valueOf(r.getInterval()));
        }
        IcalToXcal.listFromNumberList(rt.getBysecond(), r.getSecondList());
        IcalToXcal.listFromNumberList(rt.getByminute(), r.getMinuteList());
        IcalToXcal.listFromNumberList(rt.getByhour(), r.getHourList());
        if (r.getDayList() != null) {
            List<String> l = rt.getByday();
            for (Object o : r.getDayList()) {
                l.add(((WeekDay)o).getDay().name());
            }
        }
        IcalToXcal.listFromNumberList(rt.getByyearday(), r.getYearDayList());
        IcalToXcal.intlistFromNumberList(rt.getBymonthday(), r.getMonthDayList());
        IcalToXcal.listFromNumberList(rt.getByweekno(), r.getWeekNoList());
        IcalToXcal.intlistFromNumberList(rt.getBymonth(), r.getMonthList());
        IcalToXcal.bigintlistFromNumberList(rt.getBysetpos(), r.getSetPosList());
        return rt;
    }

    private static void listFromNumberList(List<String> l, NumberList nl) {
        if (nl == null) {
            return;
        }
        for (Object o : nl) {
            l.add(String.valueOf(o));
        }
    }

    private static void intlistFromNumberList(List<Integer> l, NumberList nl) {
        if (nl == null) {
            return;
        }
        for (Object o : nl) {
            l.add((Integer)o);
        }
    }

    private static void bigintlistFromNumberList(List<BigInteger> l, NumberList nl) {
        if (nl == null) {
            return;
        }
        for (Object o : nl) {
            l.add(BigInteger.valueOf(((Integer)o).intValue()));
        }
    }

    private static String getTzid(Property p) {
        TzId tzidParam = (TzId)p.getParameter("TZID");
        if (tzidParam == null) {
            return null;
        }
        return tzidParam.getValue();
    }

    private static boolean dateOnly(Property p) {
        Value valParam = (Value)p.getParameter("VALUE");
        if (valParam == null || valParam.getValue() == null) {
            return false;
        }
        return valParam.getValue().toUpperCase().equals(Value.DATE);
    }

    private static String paramVal(Property p, String paramName) {
        Object param = p.getParameter(paramName);
        if (param == null || ((Content)param).getValue() == null) {
            return null;
        }
        return ((Content)param).getValue();
    }

    private static ArrayOfParameters getAop(BasePropertyType prop) {
        ArrayOfParameters pars = prop.getParameters();
        if (pars == null) {
            pars = new ArrayOfParameters();
            prop.setParameters(pars);
        }
        return pars;
    }

    private static DateDatetimePropertyType makeDateDatetime(DateDatetimePropertyType p, Property prop) throws Throwable {
        XcalUtil.initDt(p, prop.getValue(), IcalToXcal.getTzid(prop));
        return p;
    }

    private static boolean emit(BaseComponentType pattern, Class compCl, Class ... cl) {
        if (pattern == null) {
            return true;
        }
        if (!compCl.getName().equals(pattern.getClass().getName())) {
            return false;
        }
        if (cl == null | cl.length == 0) {
            return true;
        }
        String className = cl[0].getName();
        if (BasePropertyType.class.isAssignableFrom(cl[0])) {
            if (pattern.getProperties() == null) {
                return false;
            }
            List<JAXBElement<? extends BasePropertyType>> patternProps = pattern.getProperties().getBasePropertyOrTzid();
            for (JAXBElement<? extends BasePropertyType> jp : patternProps) {
                if (!((BasePropertyType)jp.getValue()).getClass().getName().equals(className)) continue;
                return true;
            }
            return false;
        }
        List<JAXBElement<? extends BaseComponentType>> patternComps = XcalUtil.getComponents(pattern);
        if (patternComps == null) {
            return false;
        }
        for (JAXBElement<? extends BaseComponentType> jp : patternComps) {
            if (!((BaseComponentType)jp.getValue()).getClass().getName().equals(className)) continue;
            return IcalToXcal.emit(pattern, cl[0], Arrays.copyOfRange(cl, 1, cl.length - 1));
        }
        return false;
    }
}

