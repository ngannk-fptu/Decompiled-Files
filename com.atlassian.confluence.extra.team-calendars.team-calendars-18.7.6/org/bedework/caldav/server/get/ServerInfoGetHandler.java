/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server.get;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.get.GetHandler;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.bedework.webdav.servlet.shared.serverInfo.ServerInfo;

public class ServerInfoGetHandler
extends GetHandler {
    public ServerInfoGetHandler(CaldavBWIntf intf) {
        super(intf);
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp, RequestPars pars) throws WebdavException {
        try {
            ServerInfo serverInfo = this.intf.getServerInfo();
            if (serverInfo == null) {
                resp.sendError(404);
                return;
            }
            String name = pars.getNoPrefixResourceUri();
            if (!"/serverinfo.xml".equals(name)) {
                resp.sendError(404);
                return;
            }
            XmlEmit xml = this.intf.getXmlEmit();
            this.startEmit(resp);
            resp.setContentType("application/server-info+xml;charset=utf-8");
            serverInfo.toXml(xml);
            resp.setStatus(200);
        }
        catch (WebdavForbidden wdf) {
            resp.setStatus(403);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

