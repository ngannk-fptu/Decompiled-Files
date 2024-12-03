/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.caldav.server.soap.calws;

import ietf.params.xml.ns.icalendar_2.ArrayOfComponents;
import ietf.params.xml.ns.icalendar_2.ArrayOfProperties;
import ietf.params.xml.ns.icalendar_2.AttendeePropType;
import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.DtendPropType;
import ietf.params.xml.ns.icalendar_2.DtstartPropType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import ietf.params.xml.ns.icalendar_2.OrganizerPropType;
import ietf.params.xml.ns.icalendar_2.UidPropType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import ietf.params.xml.ns.icalendar_2.VfreebusyType;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.CaldavBwNode;
import org.bedework.caldav.server.CaldavCalNode;
import org.bedework.caldav.server.CaldavComponentNode;
import org.bedework.caldav.server.CaldavPrincipalNode;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.caldav.server.soap.SoapHandler;
import org.bedework.caldav.server.soap.calws.Report;
import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.ParseUtil;
import org.bedework.caldav.util.TimeRange;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNotFound;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavUnauthorized;
import org.oasis_open.docs.ns.xri.xrd_1.XRDType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.ArrayOfHrefs;
import org.oasis_open.docs.ws_calendar.ns.soap.ArrayOfResponses;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarDataResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarMultigetType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.ErrorResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemType;
import org.oasis_open.docs.ws_calendar.ns.soap.ForbiddenType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarCollectionLocationType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarDataType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidCalendarObjectResourceType;
import org.oasis_open.docs.ws_calendar.ns.soap.InvalidFilterType;
import org.oasis_open.docs.ws_calendar.ns.soap.MismatchedChangeTokenType;
import org.oasis_open.docs.ws_calendar.ns.soap.MissingChangeTokenType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultiOpResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultiOpType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatResponseElementType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatusPropElementType;
import org.oasis_open.docs.ws_calendar.ns.soap.ObjectFactory;
import org.oasis_open.docs.ws_calendar.ns.soap.PropstatType;
import org.oasis_open.docs.ws_calendar.ns.soap.StatusType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetDoesNotExistType;
import org.oasis_open.docs.ws_calendar.ns.soap.TargetNotEntityType;
import org.oasis_open.docs.ws_calendar.ns.soap.UTCTimeRangeType;
import org.oasis_open.docs.ws_calendar.ns.soap.UidConflictType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemType;

public class CalwsHandler
extends SoapHandler {
    static String calwsNs = "http://docs.oasis-open.org/ns/wscal/calws-soap";
    static ObjectFactory of = new ObjectFactory();

    public CalwsHandler(CaldavBWIntf intf) throws WebdavException {
        super(intf);
    }

    @Override
    protected String getJaxbContextPath() {
        return "org.oasis_open.docs.ws_calendar.ns.soap:" + XRDType.class.getPackage().getName();
    }

    public void processPost(HttpServletRequest req, HttpServletResponse resp, RequestPars pars) throws WebdavException {
        try {
            this.initResponse(resp);
            SoapHandler.UnmarshalResult ur = this.unmarshal(req);
            Object body = ur.body;
            if (body instanceof JAXBElement) {
                body = ((JAXBElement)body).getValue();
            }
            this.processRequest(req, resp, (BaseRequestType)body, pars, false);
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected JAXBElement<? extends BaseResponseType> processRequest(HttpServletRequest req, HttpServletResponse resp, BaseRequestType breq, RequestPars pars, boolean multi) throws WebdavException {
        try {
            if (breq instanceof MultiOpType) {
                return this.doMultiOp((MultiOpType)breq, req, resp, pars);
            }
            if (breq instanceof GetPropertiesType) {
                return this.doGetProperties((GetPropertiesType)breq, resp, multi);
            }
            if (breq instanceof FreebusyReportType) {
                return this.doFreebusyReport((FreebusyReportType)breq, resp, multi);
            }
            if (breq instanceof CalendarMultigetType) {
                return this.doCalendarMultiget((CalendarMultigetType)breq, resp, multi);
            }
            if (breq instanceof CalendarQueryType) {
                return this.doCalendarQuery((CalendarQueryType)breq, resp, multi);
            }
            if (breq instanceof AddItemType) {
                return this.doAddItem((AddItemType)breq, req, resp, multi);
            }
            if (breq instanceof FetchItemType) {
                return this.doFetchItem((FetchItemType)breq, req, resp, multi);
            }
            if (breq instanceof DeleteItemType) {
                return this.doDeleteItem((DeleteItemType)breq, req, resp, multi);
            }
            if (breq instanceof UpdateItemType) {
                return this.doUpdateItem((UpdateItemType)breq, req, resp, multi);
            }
            throw new WebdavException("Unhandled request");
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private JAXBElement<MultiOpResponseType> doMultiOp(MultiOpType mo, HttpServletRequest req, HttpServletResponse resp, RequestPars pars) throws WebdavException {
        if (this.debug) {
            this.debug("MultiOpType: ");
        }
        try {
            MultiOpResponseType mor = new MultiOpResponseType();
            JAXBElement<MultiOpResponseType> jax = of.createMultiOpResponse(mor);
            ArrayOfResponses aor = new ArrayOfResponses();
            mor.setResponses(aor);
            for (BaseRequestType breq : mo.getOperations().getGetPropertiesOrFreebusyReportOrCalendarQuery()) {
                aor.getBaseResponse().add(this.processRequest(req, resp, breq, pars, true));
            }
            this.marshal(jax, (OutputStream)resp.getOutputStream());
            return jax;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private JAXBElement<GetPropertiesResponseType> doGetProperties(GetPropertiesType gp, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("GetProperties: ");
        }
        try {
            String url = gp.getHref();
            GetPropertiesResponseType gpr = new GetPropertiesResponseType();
            JAXBElement<GetPropertiesResponseType> jax = of.createGetPropertiesResponse(gpr);
            gpr.setId(gp.getId());
            gpr.setHref(url);
            if (url != null) {
                WebdavNsNode calNode = this.getNsIntf().getNode(url, 1, 0, false);
                if (calNode != null) {
                    CaldavBwNode nd = (CaldavBwNode)calNode;
                    ((CaldavBWIntf)this.getNsIntf()).getCalWSProperties(nd, gpr.getChildCollectionOrCreationDateTimeOrDisplayName());
                }
                if (!multi) {
                    this.marshal(jax, (OutputStream)resp.getOutputStream());
                }
            }
            return jax;
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private JAXBElement<FreebusyReportResponseType> doFreebusyReport(FreebusyReportType fr, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("FreebusyReport: ");
        }
        FreebusyReportResponseType frr = new FreebusyReportResponseType();
        frr.setId(fr.getId());
        JAXBElement<FreebusyReportResponseType> jax = of.createFreebusyReportResponse(frr);
        try {
            String url = fr.getHref();
            if (url == null) {
                frr.setStatus(StatusType.ERROR);
                frr.setMessage("No href supplied");
            } else {
                WebdavNsNode elNode = this.getNsIntf().getNode(url, 1, 3, false);
                if (!(elNode instanceof CaldavPrincipalNode)) {
                    frr.setStatus(StatusType.ERROR);
                    frr.setMessage("Only principal href supported");
                } else {
                    String cua = this.getSysi().principalToCaladdr(this.getSysi().getPrincipal(url));
                    IcalendarType ical = new IcalendarType();
                    VcalendarType vcal = new VcalendarType();
                    ical.getVcalendar().add(vcal);
                    VfreebusyType vfb = new VfreebusyType();
                    JAXBElement compel = new JAXBElement(XcalTags.vfreebusy, VfreebusyType.class, (Object)vfb);
                    ArrayOfComponents aoc = new ArrayOfComponents();
                    vcal.setComponents(aoc);
                    aoc.getBaseComponent().add((JAXBElement<? extends BaseComponentType>)compel);
                    CalDAVAuthProperties authp = this.getSysi().getAuthProperties();
                    UTCTimeRangeType utr = fr.getTimeRange();
                    TimeRange tr = ParseUtil.getPeriod(XcalUtil.getIcalFormatDateTime(utr.getStart().toString()), XcalUtil.getIcalFormatDateTime(utr.getEnd().toString()), 5, authp.getDefaultFBPeriod(), 5, authp.getMaxFBPeriod());
                    ArrayOfProperties aop = new ArrayOfProperties();
                    vfb.setProperties(aop);
                    DtstartPropType dtstart = new DtstartPropType();
                    XcalUtil.initDt(dtstart, tr.getStart().toString(), null);
                    JAXBElement dtstartProp = new JAXBElement(XcalTags.dtstart, DtstartPropType.class, (Object)dtstart);
                    aop.getBasePropertyOrTzid().add((JAXBElement<? extends BasePropertyType>)dtstartProp);
                    DtendPropType dtend = new DtendPropType();
                    XcalUtil.initDt(dtend, tr.getEnd().toString(), null);
                    JAXBElement dtendProp = new JAXBElement(XcalTags.dtend, DtendPropType.class, (Object)dtend);
                    aop.getBasePropertyOrTzid().add((JAXBElement<? extends BasePropertyType>)dtendProp);
                    UidPropType uid = new UidPropType();
                    uid.setText(Util.makeRandomString(30, 35));
                    JAXBElement uidProp = new JAXBElement(XcalTags.uid, UidPropType.class, (Object)uid);
                    aop.getBasePropertyOrTzid().add((JAXBElement<? extends BasePropertyType>)uidProp);
                    OrganizerPropType org = new OrganizerPropType();
                    org.setCalAddress(cua);
                    JAXBElement orgProp = new JAXBElement(XcalTags.organizer, OrganizerPropType.class, (Object)org);
                    aop.getBasePropertyOrTzid().add((JAXBElement<? extends BasePropertyType>)orgProp);
                    AttendeePropType att = new AttendeePropType();
                    att.setCalAddress(this.getSysi().principalToCaladdr(this.getSysi().getPrincipal()));
                    JAXBElement attProp = new JAXBElement(XcalTags.attendee, AttendeePropType.class, (Object)att);
                    aop.getBasePropertyOrTzid().add((JAXBElement<? extends BasePropertyType>)attProp);
                    SysiIcalendar sical = this.getSysi().fromIcal(null, ical, SysIntf.IcalResultType.OneComponent);
                    CalDAVEvent ev = sical.getEvent();
                    ev.setScheduleMethod(2);
                    TreeSet<String> recipients = new TreeSet<String>();
                    recipients.add(cua);
                    ev.setRecipients(recipients);
                    Collection<SysIntf.SchedRecipientResult> srrs = this.getSysi().requestFreeBusy(ev, false);
                    if (srrs.size() != 1) {
                        frr.setStatus(StatusType.ERROR);
                        frr.setMessage("No data returned");
                    } else {
                        SysIntf.SchedRecipientResult sr = srrs.iterator().next();
                        frr.setIcalendar(this.getSysi().toIcalendar(sr.freeBusy, false, null));
                        frr.setStatus(StatusType.OK);
                    }
                }
            }
            if (!multi) {
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
            return jax;
        }
        catch (WebdavException we) {
            frr.setStatus(StatusType.ERROR);
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private JAXBElement<CalendarQueryResponseType> doCalendarMultiget(CalendarMultigetType cm, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("CalendarMultiget: ");
        }
        CalendarQueryResponseType cqr = new CalendarQueryResponseType();
        JAXBElement<CalendarQueryResponseType> jax = of.createCalendarQueryResponse(cqr);
        cqr.setId(cm.getId());
        try {
            String url = cm.getHref();
            if (url == null) {
                cqr.setStatus(StatusType.ERROR);
                cqr.setMessage("No href supplied");
            } else {
                ArrayOfHrefs hrefs = cm.getHrefs();
                if (hrefs != null) {
                    Report rpt = new Report(this.getNsIntf());
                    ArrayList<String> badHrefs = new ArrayList<String>();
                    this.buildQueryResponse(cqr, rpt.getMgetNodes(hrefs.getHref(), badHrefs), cm.getIcalendar());
                    if (!badHrefs.isEmpty()) {
                        for (String bh : badHrefs) {
                            MultistatResponseElementType mre = new MultistatResponseElementType();
                            mre.setHref(bh);
                            cqr.getResponse().add(mre);
                            PropstatType ps = new PropstatType();
                            mre.getPropstat().add(ps);
                            ps.setStatus(StatusType.NOT_FOUND);
                        }
                    }
                }
            }
        }
        catch (WebdavException we) {
            cqr.getResponse().clear();
            this.errorResponse(cqr, we);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        if (!multi) {
            try {
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
            catch (Throwable t) {
                if (this.debug) {
                    this.error(t);
                }
                throw new WebdavException(t);
            }
        }
        return jax;
    }

    private JAXBElement<CalendarQueryResponseType> doCalendarQuery(CalendarQueryType cq, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("CalendarQuery: ");
        }
        resp.setHeader("Content-Type", "application/soap+xml");
        CalendarQueryResponseType cqr = new CalendarQueryResponseType();
        JAXBElement<CalendarQueryResponseType> jax = of.createCalendarQueryResponse(cqr);
        cqr.setId(cq.getId());
        try {
            String url = cq.getHref();
            if (url == null) {
                cqr.setStatus(StatusType.ERROR);
                cqr.setMessage("No href supplied");
            } else {
                Report rpt = new Report(this.getNsIntf());
                this.buildQueryResponse(cqr, rpt.query(url, cq), cq.getIcalendar());
                cqr.setStatus(StatusType.OK);
            }
        }
        catch (WebdavException we) {
            cqr.getResponse().clear();
            this.errorResponse(cqr, we);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        if (!multi) {
            try {
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
            catch (Throwable t) {
                if (this.debug) {
                    this.error(t);
                }
                throw new WebdavException(t);
            }
        }
        return jax;
    }

    private JAXBElement<AddItemResponseType> doAddItem(AddItemType ai, HttpServletRequest req, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("AddItem: cal=" + ai.getHref());
        }
        AddItemResponseType air = new AddItemResponseType();
        JAXBElement<AddItemResponseType> jax = of.createAddItemResponse(air);
        air.setId(ai.getId());
        UidPropType uidp = (UidPropType)XcalUtil.findProperty(XcalUtil.findEntity(ai.getIcalendar()), XcalTags.uid);
        if (uidp == null || uidp.getText() == null) {
            air.setStatus(StatusType.ERROR);
        } else {
            String entityPath = Util.buildPath(false, ai.getHref(), "/", this.getIntf().makeName(uidp.getText()) + ".ics");
            WebdavNsNode elNode = this.getNsIntf().getNode(entityPath, 0, 1, false);
            try {
                if (elNode != null && this.getIntf().putEvent(resp, (CaldavComponentNode)elNode, ai.getIcalendar(), true, false, null, null)) {
                    air.setStatus(StatusType.OK);
                    air.setHref(elNode.getUri());
                    air.setChangeToken(((CaldavBwNode)elNode).getEtokenValue());
                } else {
                    air.setStatus(StatusType.ERROR);
                }
            }
            catch (WebdavException we) {
                this.errorResponse(air, we);
            }
            catch (Throwable t) {
                if (this.debug) {
                    this.error(t);
                }
                this.errorResponse(air, new WebdavException(t));
            }
        }
        if (!multi) {
            try {
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
            catch (WebdavException we) {
                throw we;
            }
            catch (Throwable t) {
                throw new WebdavException(t);
            }
        }
        return jax;
    }

    private JAXBElement<FetchItemResponseType> doFetchItem(FetchItemType fi, HttpServletRequest req, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("FetchItem:       cal=" + fi.getHref());
        }
        FetchItemResponseType fir = new FetchItemResponseType();
        JAXBElement<FetchItemResponseType> jax = of.createFetchItemResponse(fir);
        fir.setId(fi.getId());
        try {
            WebdavNsNode elNode = this.getNsIntf().getNode(fi.getHref(), 1, 1, false);
            if (elNode == null) {
                this.errorResponse(fir, new WebdavNotFound());
            } else {
                CaldavComponentNode comp = (CaldavComponentNode)elNode;
                fir.setStatus(StatusType.OK);
                fir.setChangeToken(comp.getEtokenValue());
                CalDAVEvent ev = comp.getEvent();
                fir.setIcalendar(this.getIntf().getSysi().toIcalendar(ev, false, null));
            }
        }
        catch (WebdavException we) {
            this.errorResponse(fir, we);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            this.errorResponse(fir, new WebdavException(t));
        }
        if (!multi) {
            try {
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
            catch (WebdavException we) {
                throw we;
            }
            catch (Throwable t) {
                throw new WebdavException(t);
            }
        }
        return jax;
    }

    private JAXBElement<DeleteItemResponseType> doDeleteItem(DeleteItemType di, HttpServletRequest req, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("DeleteItem:       cal=" + di.getHref());
        }
        DeleteItemResponseType dir = new DeleteItemResponseType();
        JAXBElement<DeleteItemResponseType> jax = of.createDeleteItemResponse(dir);
        dir.setId(di.getId());
        try {
            WebdavNsNode node = this.getNsIntf().getNode(di.getHref(), 1, 3, false);
            if (node == null) {
                this.errorResponse(dir, new WebdavNotFound());
            } else if (node instanceof CaldavCalNode) {
                this.errorResponse(dir, new WebdavUnauthorized());
            } else {
                this.getNsIntf().delete(node);
                dir.setStatus(StatusType.OK);
            }
        }
        catch (WebdavException we) {
            this.errorResponse(dir, we);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            this.errorResponse(dir, new WebdavException(t));
        }
        if (!multi) {
            try {
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
            catch (WebdavException we) {
                throw we;
            }
            catch (Throwable t) {
                throw new WebdavException(t);
            }
        }
        return jax;
    }

    private JAXBElement<UpdateItemResponseType> doUpdateItem(UpdateItemType ui, HttpServletRequest req, HttpServletResponse resp, boolean multi) throws WebdavException {
        if (this.debug) {
            this.debug("UpdateItem:       cal=" + ui.getHref());
        }
        UpdateItemResponseType uir = new UpdateItemResponseType();
        JAXBElement<UpdateItemResponseType> jax = of.createUpdateItemResponse(uir);
        uir.setId(ui.getId());
        try {
            WebdavNsNode elNode = this.getNsIntf().getNode(ui.getHref(), 1, 1, false);
            if (elNode == null) {
                uir.setStatus(StatusType.ERROR);
                uir.setMessage("Href not found");
            } else {
                CaldavComponentNode compNode = (CaldavComponentNode)elNode;
                String changeToken = ui.getChangeToken();
                if (changeToken == null) {
                    uir.setStatus(StatusType.ERROR);
                    ErrorResponseType er = new ErrorResponseType();
                    MissingChangeTokenType ec = new MissingChangeTokenType();
                    er.setError(of.createMissingChangeToken(ec));
                    uir.setErrorResponse(er);
                    uir.setMessage("Missing token");
                } else {
                    String compEtoken = compNode.getEtokenValue();
                    if (!changeToken.equals(compEtoken)) {
                        uir.setStatus(StatusType.ERROR);
                        ErrorResponseType er = new ErrorResponseType();
                        MismatchedChangeTokenType ec = new MismatchedChangeTokenType();
                        er.setError(of.createMismatchedChangeToken(ec));
                        uir.setErrorResponse(er);
                        uir.setMessage("Token mismatch");
                        if (this.debug) {
                            this.debug("Try reindex for " + compNode.getEvent().getUid());
                        }
                        this.getSysi().reindexEvent(compNode.getEvent());
                    } else {
                        SysIntf.UpdateResult ur;
                        CalDAVEvent ev = compNode.getEvent();
                        if (this.debug) {
                            this.debug("event: " + ev);
                        }
                        if ((ur = this.getSysi().updateEvent(ev, ui.getSelect())).getOk()) {
                            uir.setStatus(StatusType.OK);
                        } else {
                            uir.setStatus(StatusType.ERROR);
                            uir.setMessage(ur.getReason());
                        }
                    }
                }
            }
        }
        catch (WebdavException we) {
            this.errorResponse(uir, we);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            this.errorResponse(uir, new WebdavException(t));
        }
        if (!multi) {
            try {
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
            catch (WebdavException we) {
                throw we;
            }
            catch (Throwable t) {
                throw new WebdavException(t);
            }
        }
        return jax;
    }

    private void buildQueryResponse(CalendarQueryResponseType cqr, Collection<WebdavNsNode> nodes, IcalendarType pattern) throws WebdavException {
        if (nodes == null) {
            return;
        }
        for (WebdavNsNode curnode : nodes) {
            MultistatResponseElementType mre = new MultistatResponseElementType();
            mre.setHref(curnode.getUri());
            mre.setChangeToken(((CaldavBwNode)curnode).getEtokenValue());
            cqr.getResponse().add(mre);
            PropstatType ps = new PropstatType();
            mre.getPropstat().add(ps);
            ps.setStatus(StatusType.OK);
            ps.setMessage(this.getStatus(curnode.getStatus(), null));
            if (!curnode.getExists() || !(curnode instanceof CaldavComponentNode)) continue;
            MultistatusPropElementType mpe = new MultistatusPropElementType();
            ps.getProp().add(mpe);
            CalendarDataResponseType cdr = new CalendarDataResponseType();
            mpe.setCalendarData(cdr);
            CalDAVEvent ev = ((CaldavComponentNode)curnode).getEvent();
            cdr.setIcalendar(this.getIntf().getSysi().toIcalendar(ev, false, pattern));
            cdr.setContentType("application/calendar+xml");
            cdr.setVersion("2.0");
        }
    }

    private void errorResponse(BaseResponseType br, WebdavException we) {
        br.setStatus(StatusType.ERROR);
        br.setMessage(we.getMessage());
        ErrorResponseType er = new ErrorResponseType();
        if (we instanceof WebdavForbidden) {
            ForbiddenType ec = new ForbiddenType();
            er.setError(of.createForbidden(ec));
        } else if (we instanceof WebdavNotFound) {
            TargetDoesNotExistType ec = new TargetDoesNotExistType();
            er.setError(of.createTargetDoesNotExist(ec));
        } else if (we instanceof WebdavUnauthorized) {
            TargetNotEntityType ec = new TargetNotEntityType();
            er.setError(of.createTargetNotEntity(ec));
        } else {
            QName etag = we.getErrorTag();
            if (etag != null) {
                if (etag.equals(CaldavTags.validFilter)) {
                    InvalidFilterType invf = new InvalidFilterType();
                    er.setError(of.createInvalidFilter(invf));
                } else if (etag.equals(CaldavTags.calendarCollectionLocationOk)) {
                    InvalidCalendarCollectionLocationType ec = new InvalidCalendarCollectionLocationType();
                    er.setError(of.createInvalidCalendarCollectionLocation(ec));
                } else if (etag.equals(CaldavTags.noUidConflict)) {
                    UidConflictType uc = new UidConflictType();
                    uc.setHref(we.getMessage());
                    er.setError(of.createUidConflict(uc));
                } else if (etag.equals(CaldavTags.validCalendarData)) {
                    InvalidCalendarDataType ec = new InvalidCalendarDataType();
                    er.setError(of.createInvalidCalendarData(ec));
                } else if (etag.equals(CaldavTags.validCalendarObjectResource)) {
                    InvalidCalendarObjectResourceType ec = new InvalidCalendarObjectResourceType();
                    er.setError(of.createInvalidCalendarObjectResource(ec));
                } else if (etag.equals(CaldavTags.validFilter)) {
                    InvalidFilterType iv = new InvalidFilterType();
                    iv.setDetail(we.getMessage());
                    er.setError(of.createInvalidFilter(iv));
                }
            }
        }
        if (er.getError() != null) {
            br.setErrorResponse(er);
        }
    }
}

