/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.calquery;

import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.ParseUtil;
import org.bedework.caldav.util.TimeRange;
import org.bedework.util.misc.Logged;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Node;

public class FreeBusyQuery
extends Logged {
    private TimeRange timeRange;

    public FreeBusyQuery() {
        this.debug = this.getLogger().isDebugEnabled();
    }

    public void parse(Node nd) throws WebdavException {
        try {
            if (this.timeRange != null) {
                throw new WebdavBadRequest();
            }
            if (!XmlUtil.nodeMatches(nd, CaldavTags.timeRange)) {
                throw new WebdavBadRequest();
            }
            this.timeRange = ParseUtil.parseTimeRange(nd, false);
            if (this.debug) {
                this.debug("Parsed time range " + this.timeRange);
            }
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavBadRequest();
        }
    }

    public CalDAVEvent getFreeBusy(SysIntf sysi, CalDAVCollection col, int depth) throws WebdavException {
        try {
            return sysi.getFreeBusy(col, depth, this.timeRange);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void dump() {
        this.debug("<free-busy-query>");
        this.debug(this.timeRange.toString());
        this.debug("</free-busy-query>");
    }
}

