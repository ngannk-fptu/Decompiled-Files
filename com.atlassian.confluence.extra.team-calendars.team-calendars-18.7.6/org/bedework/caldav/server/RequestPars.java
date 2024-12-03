/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.caldav.server;

import javax.servlet.http.HttpServletRequest;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.IscheduleIn;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.webdav.servlet.common.PostRequestPars;
import org.bedework.webdav.servlet.shared.WebdavException;

public class RequestPars
extends PostRequestPars {
    public static final int iScheduleSerialNumber = 2;
    private IscheduleIn ischedRequest;
    private SysiIcalendar ic;
    private CalDAVCollection col;
    private boolean share;
    private boolean serverInfo;
    private boolean iSchedule;
    private boolean freeBusy;
    private boolean webcal;
    private boolean webcalGetAccept;
    private boolean synchws;
    private boolean notifyws;
    private boolean calwsSoap;

    /*
     * Unable to fully structure code
     */
    public RequestPars(HttpServletRequest req, CaldavBWIntf intf, String resourceUri) throws WebdavException {
        block3: {
            block9: {
                block8: {
                    block7: {
                        block6: {
                            block5: {
                                block4: {
                                    super(req, intf, resourceUri);
                                    sysi = intf.getSysi();
                                    sp = sysi.getSystemProperties();
                                    if (this.processRequest()) break block3;
                                    this.serverInfo = this.checkUri("/serverinfo");
                                    if (!this.serverInfo) break block4;
                                    this.getTheReader = false;
                                    break block3;
                                }
                                this.iSchedule = this.checkUri(sp.getIscheduleURI());
                                if (!this.iSchedule) break block5;
                                this.ischedRequest = new IscheduleIn(req, sysi.getUrlHandler());
                                this.getTheReader = false;
                                break block3;
                            }
                            this.freeBusy = this.checkUri(sp.getFburlServiceURI());
                            if (!this.freeBusy) break block6;
                            this.getTheReader = false;
                            break block3;
                        }
                        this.webcal = this.checkUri(sp.getWebcalServiceURI());
                        if (!this.webcal) break block7;
                        this.getTheReader = false;
                        break block3;
                    }
                    if (!intf.getCalWS()) break block8;
                    if ("create".equals(req.getParameter("action"))) {
                        this.addMember = true;
                    }
                    break block3;
                }
                this.notifyws = intf.getNotifyWs();
                if (this.notifyws) break block3;
                this.synchws = intf.getSynchWs();
                if (!this.synchws) break block9;
                this.getTheReader = false;
                break block3;
            }
            if (sp.getCalSoapWsURI() == null) ** GOTO lbl-1000
            this.calwsSoap = sp.getCalSoapWsURI().equals(resourceUri);
            if (this.calwsSoap) {
                this.getTheReader = false;
            } else lbl-1000:
            // 2 sources

            {
                this.processXml();
            }
        }
        this.getReader();
    }

    public IscheduleIn getIschedRequest() {
        return this.ischedRequest;
    }

    public boolean isCalwsSoap() {
        return this.calwsSoap;
    }

    public boolean isSynchws() {
        return this.synchws;
    }

    public boolean isNotifyws() {
        return this.notifyws;
    }

    public boolean isWebcal() {
        return this.webcal;
    }

    public void setWebcalGetAccept(boolean val) {
        this.webcalGetAccept = val;
    }

    public boolean isWebcalGetAccept() {
        return this.webcalGetAccept;
    }

    public boolean isServerInfo() {
        return this.serverInfo;
    }

    public boolean isFreeBusy() {
        return this.freeBusy;
    }

    public boolean isiSchedule() {
        return this.iSchedule;
    }

    public boolean isShare() {
        return this.share;
    }

    public void setCol(CalDAVCollection val) {
        this.col = val;
    }

    public CalDAVCollection getCol() {
        return this.col;
    }

    public void setIcalendar(SysiIcalendar val) {
        this.ic = val;
    }

    public SysiIcalendar getIcalendar() {
        return this.ic;
    }
}

