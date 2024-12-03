/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.caldav.server.get;

import java.util.Collection;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.RequestPars;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsNode;

public abstract class GetHandler {
    protected CaldavBWIntf intf;
    protected XmlEmit xml;

    public GetHandler(CaldavBWIntf intf) {
        this.intf = intf;
        this.xml = intf.getXmlEmit();
    }

    public abstract void process(HttpServletRequest var1, HttpServletResponse var2, RequestPars var3) throws WebdavException;

    public String getAccount() {
        return this.intf.getAccount();
    }

    public SysIntf getSysi() {
        return this.intf.getSysi();
    }

    protected void startEmit(HttpServletResponse resp) throws WebdavException {
        try {
            this.xml.startEmit(resp.getWriter());
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public Collection<WebdavNsNode> getChildren(WebdavNsNode node, Supplier<Object> filterGetter) throws WebdavException {
        return this.intf.getChildren(node, filterGetter);
    }

    public WebdavNsNode getNode(String uri, int existance, int nodeType) throws WebdavException {
        return this.intf.getNode(uri, existance, nodeType, false);
    }

    protected void openTag(QName tag) throws WebdavException {
        try {
            this.xml.openTag(tag);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void openTag(QName tag, String attrName, String attrVal) throws WebdavException {
        try {
            this.xml.openTag(tag, attrName, attrVal);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void emptyTag(QName tag) throws WebdavException {
        try {
            this.xml.emptyTag(tag);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void property(QName tag, String val) throws WebdavException {
        try {
            this.xml.property(tag, val);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected void closeTag(QName tag) throws WebdavException {
        try {
            this.xml.closeTag(tag);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }
}

