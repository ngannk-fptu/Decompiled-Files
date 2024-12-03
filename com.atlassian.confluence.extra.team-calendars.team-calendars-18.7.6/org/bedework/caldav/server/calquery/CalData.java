/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.calquery;

import ietf.params.xml.ns.caldav.AllcompType;
import ietf.params.xml.ns.caldav.AllpropType;
import ietf.params.xml.ns.caldav.CalendarDataType;
import ietf.params.xml.ns.caldav.CompType;
import ietf.params.xml.ns.caldav.ExpandType;
import ietf.params.xml.ns.caldav.LimitFreebusySetType;
import ietf.params.xml.ns.caldav.LimitRecurrenceSetType;
import ietf.params.xml.ns.caldav.PropType;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.log4j.Logger;
import org.bedework.caldav.server.CaldavComponentNode;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.DumpUtil;
import org.bedework.caldav.util.ParseUtil;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavProperty;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class CalData
extends WebdavProperty {
    private final boolean debug = this.getLogger().isDebugEnabled();
    protected transient Logger log;
    private CalendarDataType calendarData;

    public CalData(QName tag) {
        super(tag, null);
    }

    public CalendarDataType getCalendarData() {
        return this.calendarData;
    }

    public void parse(Node nd) throws WebdavException {
        CalendarDataType cd;
        NamedNodeMap nnm = nd.getAttributes();
        this.calendarData = cd = new CalendarDataType();
        if (nnm != null) {
            block13: for (int nnmi = 0; nnmi < nnm.getLength(); ++nnmi) {
                String attrName;
                Node attr = nnm.item(nnmi);
                switch (attrName = attr.getNodeName()) {
                    case "content-type": {
                        cd.setContentType(attr.getNodeValue());
                        if (cd.getContentType() != null) continue block13;
                        throw new WebdavBadRequest();
                    }
                    case "xmlns": {
                        continue block13;
                    }
                    case "version": {
                        if ("2.0".equals(attr.getNodeValue())) continue block13;
                        throw new WebdavForbidden(CaldavTags.validFilter, "Invalid attribute: " + attrName);
                    }
                    default: {
                        throw new WebdavForbidden(CaldavTags.validFilter, "Invalid attribute: " + attrName);
                    }
                }
            }
        }
        Element[] children = this.getChildren(nd);
        try {
            for (Element curnode : children) {
                if (this.debug) {
                    this.trace("calendar-data node type: " + curnode.getNodeType() + " name:" + curnode.getNodeName());
                }
                if (XmlUtil.nodeMatches(curnode, CaldavTags.comp)) {
                    if (cd.getComp() != null) {
                        throw new WebdavBadRequest();
                    }
                    cd.setComp(this.parseComp(curnode));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, CaldavTags.expand)) {
                    if (cd.getExpand() != null) {
                        throw new WebdavBadRequest();
                    }
                    cd.setExpand((ExpandType)ParseUtil.parseUTCTimeRange(new ExpandType(), curnode, true));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, CaldavTags.limitRecurrenceSet)) {
                    if (cd.getLimitRecurrenceSet() != null) {
                        throw new WebdavBadRequest();
                    }
                    cd.setLimitRecurrenceSet((LimitRecurrenceSetType)ParseUtil.parseUTCTimeRange(new LimitRecurrenceSetType(), curnode, true));
                    continue;
                }
                if (XmlUtil.nodeMatches(curnode, CaldavTags.limitFreebusySet)) {
                    if (cd.getLimitFreebusySet() != null) {
                        throw new WebdavBadRequest();
                    }
                    cd.setLimitFreebusySet((LimitFreebusySetType)ParseUtil.parseUTCTimeRange(new LimitFreebusySetType(), curnode, true));
                    continue;
                }
                throw new WebdavBadRequest();
            }
        }
        catch (WebdavBadRequest wbr) {
            throw wbr;
        }
        catch (Throwable t) {
            throw new WebdavBadRequest();
        }
        if (this.debug) {
            DumpUtil.dumpCalendarData(cd, this.getLogger());
        }
    }

    public void process(WebdavNsNode wdnode, XmlEmit xml, String contentType) throws WebdavException {
        if (!(wdnode instanceof CaldavComponentNode)) {
            return;
        }
        CaldavComponentNode node = (CaldavComponentNode)wdnode;
        CompType comp = this.getCalendarData().getComp();
        if (comp == null) {
            node.writeContent(xml, null, contentType);
            return;
        }
        node.init(true);
        if (!node.getExists()) {
            throw new WebdavException(404);
        }
        if (!"VCALENDAR".equals(comp.getName().toUpperCase())) {
            throw new WebdavBadRequest();
        }
        if (comp.getAllcomp() != null) {
            node.writeContent(xml, null, contentType);
            return;
        }
        for (CompType subcomp : comp.getComp()) {
            String nm = subcomp.getName().toUpperCase();
            if (!"VEVENT".equals(nm) && !"VTODO".equals(nm)) continue;
            if (subcomp.getAllprop() != null) {
                node.writeContent(xml, null, contentType);
                return;
            }
            try {
                if (contentType != null && contentType.equals("application/calendar+xml")) {
                    node.writeContent(xml, null, contentType);
                } else {
                    xml.cdataValue(this.transformVevent(node.getIntf(), node.getIcal(), subcomp.getProp(), contentType));
                }
            }
            catch (IOException ioe) {
                throw new WebdavException(ioe);
            }
            return;
        }
        node.writeContent(xml, null, contentType);
    }

    private String transformVevent(SysIntf intf, Calendar ical, Collection<PropType> props, String contentType) throws WebdavException {
        try {
            Calendar nical = new Calendar();
            PropertyList<Property> pl = ical.getProperties();
            PropertyList<Property> npl = nical.getProperties();
            Iterator it = pl.iterator();
            while (it.hasNext()) {
                npl.add((Object)((Property)it.next()));
            }
            ComponentList<CalendarComponent> cl = ical.getComponents();
            ComponentList<CalendarComponent> ncl = nical.getComponents();
            for (Component component : cl) {
                if (!(component instanceof VEvent)) {
                    ncl.add(component);
                    continue;
                }
                VEvent v = new VEvent();
                PropertyList<Property> vpl = component.getProperties();
                PropertyList<Property> nvpl = v.getProperties();
                nvpl.clear();
                for (PropType pr : props) {
                    Object p = vpl.getProperty(pr.getName());
                    if (p == null) continue;
                    nvpl.add(p);
                }
                ncl.add(v);
            }
            return intf.toIcalString(nical, contentType);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error("transformVevent exception: ", t);
            }
            throw new WebdavBadRequest();
        }
    }

    private CompType parseComp(Node nd) throws WebdavException {
        String name = this.getOnlyAttrVal(nd, "name");
        if (name == null) {
            throw new WebdavBadRequest();
        }
        CompType c = new CompType();
        c.setName(name);
        Element[] children = this.getChildren(nd);
        boolean hadComps = false;
        boolean hadProps = false;
        for (Element curnode : children) {
            if (this.debug) {
                this.trace("comp node type: " + curnode.getNodeType() + " name:" + curnode.getNodeName());
            }
            if (XmlUtil.nodeMatches(curnode, CaldavTags.allcomp)) {
                if (hadComps) {
                    throw new WebdavBadRequest();
                }
                c.setAllcomp(new AllcompType());
                continue;
            }
            if (XmlUtil.nodeMatches(curnode, CaldavTags.comp)) {
                if (c.getAllcomp() != null) {
                    throw new WebdavBadRequest();
                }
                c.getComp().add(this.parseComp(curnode));
                hadComps = true;
                continue;
            }
            if (XmlUtil.nodeMatches(curnode, CaldavTags.allprop)) {
                if (hadProps) {
                    throw new WebdavBadRequest();
                }
                c.setAllprop(new AllpropType());
                continue;
            }
            if (XmlUtil.nodeMatches(curnode, CaldavTags.prop)) {
                if (c.getAllprop() != null) {
                    throw new WebdavBadRequest();
                }
                c.getProp().add(this.parseProp(curnode));
                hadProps = true;
                continue;
            }
            throw new WebdavBadRequest();
        }
        return c;
    }

    private PropType parseProp(Node nd) throws WebdavException {
        Boolean val;
        NamedNodeMap nnm = nd.getAttributes();
        if (nnm == null || nnm.getLength() == 0) {
            throw new WebdavBadRequest();
        }
        String name = XmlUtil.getAttrVal(nnm, "name");
        if (name == null) {
            throw new WebdavBadRequest();
        }
        try {
            val = XmlUtil.getYesNoAttrVal(nnm, "novalue");
        }
        catch (Throwable t) {
            throw new WebdavBadRequest();
        }
        PropType pr = new PropType();
        pr.setName(name);
        if (val != null && val.booleanValue()) {
            pr.setNovalue("yes");
        }
        return pr;
    }

    private Element[] getChildren(Node nd) throws WebdavException {
        try {
            return XmlUtil.getElementsArray(nd);
        }
        catch (Throwable t) {
            if (this.debug) {
                this.getLogger().error("<filter>: parse exception: ", t);
            }
            throw new WebdavBadRequest();
        }
    }

    private String getOnlyAttrVal(Node nd, String name) throws WebdavException {
        NamedNodeMap nnm = nd.getAttributes();
        if (nnm == null || nnm.getLength() != 1) {
            throw new WebdavBadRequest();
        }
        String res = XmlUtil.getAttrVal(nnm, name);
        if (res == null) {
            throw new WebdavBadRequest();
        }
        return res;
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }

    protected void debugMsg(String msg) {
        this.getLogger().debug(msg);
    }

    protected void logIt(String msg) {
        this.getLogger().info(msg);
    }

    protected void trace(String msg) {
        this.getLogger().debug(msg);
    }
}

