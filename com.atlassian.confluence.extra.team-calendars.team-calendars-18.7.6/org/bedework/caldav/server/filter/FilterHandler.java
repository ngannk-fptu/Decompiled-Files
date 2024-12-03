/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.filter;

import ietf.params.xml.ns.caldav.CompFilterType;
import ietf.params.xml.ns.caldav.FilterType;
import ietf.params.xml.ns.caldav.PropFilterType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.fortuna.ical4j.model.Component;
import org.apache.log4j.Logger;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CaldavBwNode;
import org.bedework.caldav.server.CaldavComponentNode;
import org.bedework.caldav.server.sysinterface.RetrievalMode;
import org.bedework.caldav.util.filter.FilterUtil;
import org.bedework.caldav.util.filter.parse.EventQuery;
import org.bedework.caldav.util.filter.parse.Filters;
import org.bedework.webdav.servlet.common.WebdavUtils;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public class FilterHandler {
    private EventQuery eventq;
    private FilterType f;
    private boolean debug;
    protected transient Logger log;

    public FilterHandler(FilterType f) {
        this.f = f;
        this.debug = this.getLogger().isDebugEnabled();
    }

    public Collection<CalDAVEvent> query(CaldavBwNode wdnode, List<String> retrieveList, RetrievalMode retrieveRecur) throws WebdavException {
        try {
            this.eventq = Filters.getQuery(this.f);
            CalDAVCollection c = (CalDAVCollection)wdnode.getCollection(false);
            if (c == null) {
                return null;
            }
            Collection<CalDAVEvent> events = wdnode.getSysi().getEvents(c, this.eventq.filter, retrieveList, retrieveRecur);
            if (this.debug) {
                this.trace("Query returned " + events.size());
            }
            return events;
        }
        catch (WebdavBadRequest | WebdavForbidden wd) {
            throw wd;
        }
        catch (Throwable t) {
            this.error(t);
            throw new WebdavException(500);
        }
    }

    public Collection<WebdavNsNode> postFilter(Collection<WebdavNsNode> nodes) throws WebdavException {
        CompFilterType cfltr;
        if (!this.eventq.postFilter) {
            return nodes;
        }
        if (this.debug) {
            this.trace("post filtering needed");
        }
        if (!"VCALENDAR".equals((cfltr = this.f.getCompFilter()).getName())) {
            return new ArrayList<WebdavNsNode>();
        }
        ArrayList<WebdavNsNode> filtered = new ArrayList<WebdavNsNode>();
        block0: for (WebdavNsNode node : nodes) {
            CaldavComponentNode curnode = null;
            if (!(node instanceof CaldavComponentNode)) continue;
            curnode = (CaldavComponentNode)node;
            int entityType = curnode.getEvent().getEntityType();
            List<PropFilterType> pfs = null;
            if (entityType == 0) {
                pfs = this.eventq.eventFilters;
            } else if (entityType == 2) {
                pfs = this.eventq.todoFilters;
            } else if (entityType == 3) {
                pfs = this.eventq.journalFilters;
            }
            if (WebdavUtils.emptyCollection(pfs)) continue;
            Component comp = curnode.getComponent();
            for (PropFilterType pf : pfs) {
                if (!FilterUtil.filter(pf, comp)) continue;
                filtered.add(curnode);
                continue block0;
            }
        }
        return filtered;
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

    protected void error(Throwable t) {
        this.getLogger().error(this, t);
    }

    protected void logIt(String msg) {
        this.getLogger().info(msg);
    }

    protected void trace(String msg) {
        this.getLogger().debug(msg);
    }
}

