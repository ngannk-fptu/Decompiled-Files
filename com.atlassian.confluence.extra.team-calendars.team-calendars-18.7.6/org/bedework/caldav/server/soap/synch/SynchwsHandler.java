/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.caldav.server.soap.synch;

import java.io.OutputStream;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.soap.SoapHandler;
import org.bedework.caldav.server.soap.calws.CalwsHandler;
import org.bedework.caldav.server.soap.synch.SynchConnection;
import org.bedework.caldav.server.soap.synch.SynchConnectionsMBean;
import org.bedework.synch.wsmessages.KeepAliveNotificationType;
import org.bedework.synch.wsmessages.KeepAliveResponseType;
import org.bedework.synch.wsmessages.ObjectFactory;
import org.bedework.synch.wsmessages.StartServiceNotificationType;
import org.bedework.synch.wsmessages.StartServiceResponseType;
import org.bedework.synch.wsmessages.SynchIdTokenType;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;
import org.oasis_open.docs.ws_calendar.ns.soap.StatusType;

public class SynchwsHandler
extends CalwsHandler {
    private final ObjectFactory of = new ObjectFactory();
    private SynchConnectionsMBean conns;

    public SynchwsHandler(CaldavBWIntf intf) throws WebdavException {
        super(intf);
    }

    @Override
    protected String getJaxbContextPath() {
        return "org.bedework.synch.wsmessages";
    }

    @Override
    public void processPost(HttpServletRequest req, HttpServletResponse resp, RequestPars pars) throws WebdavException {
        try {
            this.initResponse(resp);
            SoapHandler.UnmarshalResult ur = this.unmarshal(req);
            Object body = ur.body;
            if (body instanceof JAXBElement) {
                body = ((JAXBElement)body).getValue();
            }
            if (body instanceof StartServiceNotificationType) {
                this.doStartService((StartServiceNotificationType)body, resp);
                return;
            }
            if (body instanceof KeepAliveNotificationType) {
                this.doKeepAlive((KeepAliveNotificationType)body, resp);
                return;
            }
            SynchIdTokenType idToken = null;
            if (ur.hdrs != null && ur.hdrs.length == 1) {
                Object o = ur.hdrs[0];
                if (o instanceof JAXBElement) {
                    o = ((JAXBElement)o).getValue();
                }
                if (o instanceof SynchIdTokenType) {
                    idToken = (SynchIdTokenType)o;
                }
            }
            if (idToken != null) {
                this.handleIdToken(req, idToken);
            }
            if (body instanceof BaseRequestType) {
                this.processRequest(req, resp, (BaseRequestType)body, pars, false);
                return;
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

    private void doStartService(StartServiceNotificationType ssn, HttpServletResponse resp) throws WebdavException {
        try {
            SynchConnection sc;
            if (this.debug) {
                this.debug("StartServiceNotification: url=" + ssn.getSubscribeUrl());
            }
            if ((sc = this.getActiveConnection(ssn.getSubscribeUrl())) == null) {
                sc = new SynchConnection(ssn.getConnectorId(), ssn.getSubscribeUrl(), UUID.randomUUID().toString());
            } else {
                sc.setSynchToken(UUID.randomUUID().toString());
            }
            sc.setLastPing(System.currentTimeMillis());
            this.setActiveConnection(sc);
            this.startServiceResponse(resp, sc, true);
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doKeepAlive(KeepAliveNotificationType kan, HttpServletResponse resp) throws WebdavException {
        if (this.debug) {
            this.debug("KeepAliveNotification: url=" + kan.getSubscribeUrl() + "\n                token=" + kan.getToken());
        }
        try {
            Object object = monitor;
            synchronized (object) {
                KeepAliveResponseType kar = this.of.createKeepAliveResponseType();
                SynchConnection sc = this.getActiveConnection(kan.getSubscribeUrl());
                if (sc == null) {
                    kar.setStatus(StatusType.NOT_FOUND);
                } else if (!sc.getSynchToken().equals(kan.getToken())) {
                    kar.setStatus(StatusType.ERROR);
                } else {
                    kar.setStatus(StatusType.OK);
                    sc.setLastPing(System.currentTimeMillis());
                    this.setActiveConnection(sc);
                }
                JAXBElement<KeepAliveResponseType> jax = this.of.createKeepAliveResponse(kar);
                this.marshal(jax, (OutputStream)resp.getOutputStream());
            }
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void handleIdToken(HttpServletRequest req, SynchIdTokenType idToken) throws WebdavException {
        try {
            SynchConnection sc;
            if (idToken.getPrincipalHref() != null) {
                this.getIntf().reAuth(req, idToken.getPrincipalHref(), false, idToken.getOpaqueData());
            }
            if ((sc = this.getActiveConnection(idToken.getSubscribeUrl())) != null && idToken.getSynchToken().equals(sc.getSynchToken())) {
                return;
            }
            throw new WebdavException(500, "Invalid synch token");
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private void startServiceResponse(HttpServletResponse resp, SynchConnection sc, boolean ok) throws WebdavException {
        try {
            StartServiceResponseType ssr = this.of.createStartServiceResponseType();
            if (ok) {
                ssr.setStatus(StatusType.OK);
                ssr.setToken(sc.getSynchToken());
            } else {
                ssr.setStatus(StatusType.ERROR);
            }
            JAXBElement<StartServiceResponseType> jax = this.of.createStartServiceResponse(ssr);
            this.marshal(jax, (OutputStream)resp.getOutputStream());
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private SynchConnectionsMBean getActiveConnections() throws Throwable {
        if (this.conns == null) {
            this.conns = this.getIntf().getActiveConnections();
        }
        return this.conns;
    }

    private void setActiveConnection(SynchConnection val) throws Throwable {
        this.getActiveConnections().setConnection(val);
    }

    private SynchConnection getActiveConnection(String url) throws Throwable {
        return this.getActiveConnections().getConnection(url);
    }
}

