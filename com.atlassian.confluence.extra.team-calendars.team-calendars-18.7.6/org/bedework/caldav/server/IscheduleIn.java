/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.james.jdkim.tagvalue.SignatureRecordImpl
 */
package org.bedework.caldav.server;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.apache.james.jdkim.tagvalue.SignatureRecordImpl;
import org.bedework.caldav.server.IscheduleMessage;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;

public class IscheduleIn
extends IscheduleMessage {
    private HttpServletRequest req;

    public IscheduleIn(HttpServletRequest req, UrlHandler urlHandler) throws WebdavException {
        this.req = req;
        Enumeration e = req.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            String nameLc = name.toLowerCase();
            this.addField(nameLc);
            Enumeration hvals = req.getHeaders(name);
            while (hvals.hasMoreElements()) {
                String hval = (String)hvals.nextElement();
                this.addHeader(nameLc, hval);
                if ("originator".equals(nameLc)) {
                    if (this.originator != null) {
                        throw new WebdavBadRequest("Multiple originator headers");
                    }
                    this.originator = this.adjustPrincipal(hval, urlHandler);
                    continue;
                }
                if ("recipient".equals(nameLc)) {
                    String[] rlist = hval.split(",");
                    if (rlist == null) continue;
                    for (String r : rlist) {
                        this.recipients.add(this.adjustPrincipal(r.trim(), urlHandler));
                    }
                    continue;
                }
                if ("ischedule-version".equals(nameLc)) {
                    if (this.iScheduleVersion != null) {
                        throw new WebdavBadRequest("Multiple iSchedule-Version headers");
                    }
                    this.iScheduleVersion = hval;
                    continue;
                }
                if ("ischedule-message-id".equals(nameLc)) {
                    if (this.iScheduleMessageId != null) {
                        throw new WebdavBadRequest("Multiple iSchedule-Message-Id headers");
                    }
                    this.iScheduleMessageId = hval;
                    continue;
                }
                if (!"dkim-signature".equals(nameLc)) continue;
                if (this.dkimSignature != null) {
                    throw new WebdavBadRequest("Multiple dkim-signature headers");
                }
                this.dkimSignature = SignatureRecordImpl.forIschedule((String)hval);
            }
        }
    }

    public HttpServletRequest getReq() {
        return this.req;
    }

    private String adjustPrincipal(String val, UrlHandler urlHandler) throws WebdavException {
        if (val == null) {
            return null;
        }
        return urlHandler.unprefix(val);
    }
}

