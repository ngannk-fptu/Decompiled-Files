/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SOAPBody
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPMessage
 */
package org.bedework.caldav.server.soap;

import ietf.params.xml.ns.icalendar_2.ArrayOfParameters;
import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.DateDatetimePropertyType;
import ietf.params.xml.ns.icalendar_2.TzidParamType;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import org.bedework.caldav.server.CaldavBWIntf;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.bedework.webdav.servlet.common.MethodBase;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class SoapHandler
extends MethodBase {
    private MessageFactory soapMsgFactory;
    protected JAXBContext jc;
    protected static final Object monitor = new Object();

    public SoapHandler(CaldavBWIntf intf) throws WebdavException {
        this.nsIntf = intf;
        try {
            if (this.soapMsgFactory == null) {
                this.soapMsgFactory = MessageFactory.newInstance();
            }
            if (this.jc == null) {
                this.jc = JAXBContext.newInstance((String)this.getJaxbContextPath());
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected abstract String getJaxbContextPath();

    @Override
    public void init() {
    }

    protected void initResponse(HttpServletResponse resp) throws WebdavException {
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        resp.setContentType("text/xml;charset=utf-8");
    }

    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
    }

    protected UnmarshalResult unmarshal(HttpServletRequest req) throws WebdavException {
        try {
            UnmarshalResult res = new UnmarshalResult();
            SOAPMessage msg = this.soapMsgFactory.createMessage(null, (InputStream)req.getInputStream());
            SOAPBody body = msg.getSOAPBody();
            SOAPHeader hdrMsg = msg.getSOAPHeader();
            Unmarshaller u = this.jc.createUnmarshaller();
            if (hdrMsg != null && hdrMsg.hasChildNodes()) {
                res.hdrs = new Object[1];
                res.hdrs[0] = u.unmarshal(hdrMsg.getFirstChild());
            }
            res.body = u.unmarshal(body.getFirstChild());
            return res;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected String getAccount() {
        return this.getNsIntf().getAccount();
    }

    protected SysIntf getSysi() {
        return this.getIntf().getSysi();
    }

    protected CaldavBWIntf getIntf() {
        return (CaldavBWIntf)this.getNsIntf();
    }

    protected Document makeDoc(QName name, Object o) throws WebdavException {
        try {
            Marshaller marshaller = this.jc.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", (Object)Boolean.TRUE);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().newDocument();
            marshaller.marshal((Object)this.makeJAXBElement(name, o.getClass(), o), (Node)doc);
            return doc;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected void marshal(Object o, OutputStream out) throws WebdavException {
        try {
            Marshaller marshaller = this.jc.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", (Object)Boolean.TRUE);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().newDocument();
            SOAPMessage msg = this.soapMsgFactory.createMessage();
            msg.getSOAPBody().addDocument(doc);
            marshaller.marshal(o, (Node)msg.getSOAPBody());
            msg.writeTo(out);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    protected JAXBElement makeJAXBElement(QName name, Class cl, Object o) {
        return new JAXBElement(name, cl, o);
    }

    protected void removeNode(Node nd) throws WebdavException {
        Node parent = nd.getParentNode();
        parent.removeChild(nd);
    }

    protected String findTzid(BasePropertyType bp) {
        ArrayOfParameters aop = bp.getParameters();
        for (JAXBElement<? extends BaseParameterType> el : aop.getBaseParameter()) {
            if (!el.getName().equals(XcalTags.tzid)) continue;
            TzidParamType tzid = (TzidParamType)el.getValue();
            return tzid.getText();
        }
        return null;
    }

    protected String checkUTC(BasePropertyType bp) {
        if (this.findTzid(bp) != null) {
            return null;
        }
        if (!(bp instanceof DateDatetimePropertyType)) {
            return null;
        }
        DateDatetimePropertyType d = (DateDatetimePropertyType)bp;
        if (d.getDate() != null) {
            return null;
        }
        if (d.getDateTime() == null) {
            return null;
        }
        String dt = XcalUtil.getIcalFormatDateTime(d.getDateTime().toString());
        if (dt.length() == 18 && dt.charAt(17) == 'Z') {
            return dt;
        }
        return null;
    }

    public static class UnmarshalResult {
        public Object[] hdrs;
        public Object body;
    }
}

