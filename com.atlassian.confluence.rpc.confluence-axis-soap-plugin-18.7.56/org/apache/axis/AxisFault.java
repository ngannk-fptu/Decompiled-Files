/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.soap.SOAPFaultException;
import org.apache.axis.Constants;
import org.apache.axis.InternalException;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.NetworkUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class AxisFault
extends RemoteException {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = AxisFault.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault).getName());
    protected QName faultCode;
    protected Vector faultSubCode;
    protected String faultString = "";
    protected String faultActor;
    protected Vector faultDetails;
    protected String faultNode;
    protected ArrayList faultHeaders = null;
    static /* synthetic */ Class class$org$apache$axis$AxisFault;

    public static AxisFault makeFault(Exception e) {
        Throwable t;
        if (e instanceof InvocationTargetException && (t = ((InvocationTargetException)e).getTargetException()) instanceof Exception) {
            e = (Exception)t;
        }
        if (e instanceof AxisFault) {
            return (AxisFault)e;
        }
        return new AxisFault(e);
    }

    public AxisFault(String code, String faultString, String actor, Element[] details) {
        this(new QName("http://xml.apache.org/axis/", code), faultString, actor, details);
    }

    public AxisFault(QName code, String faultString, String actor, Element[] details) {
        super(faultString);
        this.setFaultCode(code);
        this.setFaultString(faultString);
        this.setFaultActor(actor);
        this.setFaultDetail(details);
        if (details == null) {
            this.initFromException(this);
        }
    }

    public AxisFault(QName code, QName[] subcodes, String faultString, String actor, String node, Element[] details) {
        super(faultString);
        this.setFaultCode(code);
        if (subcodes != null) {
            for (int i = 0; i < subcodes.length; ++i) {
                this.addFaultSubCode(subcodes[i]);
            }
        }
        this.setFaultString(faultString);
        this.setFaultActor(actor);
        this.setFaultNode(node);
        this.setFaultDetail(details);
        if (details == null) {
            this.initFromException(this);
        }
    }

    protected AxisFault(Exception target) {
        super("", target);
        this.setFaultCodeAsString("Server.userException");
        this.initFromException(target);
        if (target instanceof SOAPFaultException) {
            this.removeHostname();
            this.initFromSOAPFaultException((SOAPFaultException)target);
            this.addHostnameIfNeeded();
        }
    }

    public AxisFault(String message) {
        super(message);
        this.setFaultCodeAsString("Server.generalException");
        this.setFaultString(message);
        this.initFromException(this);
    }

    public AxisFault() {
        this.setFaultCodeAsString("Server.generalException");
        this.initFromException(this);
    }

    public AxisFault(String message, Throwable t) {
        super(message, t);
        this.setFaultCodeAsString("Server.generalException");
        this.setFaultString(this.getMessage());
        this.addHostnameIfNeeded();
    }

    private void initFromException(Exception target) {
        Element oldStackTrace = this.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
        if (oldStackTrace != null) {
            return;
        }
        this.setFaultString(target.toString());
        if (target instanceof AxisFault && target.getClass() != (class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = AxisFault.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault)) {
            this.addFaultDetail(Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME, target.getClass().getName());
        }
        if (target == this) {
            this.addFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE, this.getPlainStackTrace());
        } else {
            this.addFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE, JavaUtils.stackToString(target));
        }
        this.addHostnameIfNeeded();
    }

    private void initFromSOAPFaultException(SOAPFaultException fault) {
        if (fault.getFaultCode() != null) {
            this.setFaultCode(fault.getFaultCode());
        }
        if (fault.getFaultString() != null) {
            this.setFaultString(fault.getFaultString());
        }
        if (fault.getFaultActor() != null) {
            this.setFaultActor(fault.getFaultActor());
        }
        if (null == fault.getDetail()) {
            return;
        }
        Vector details = new Vector();
        Iterator detailIter = fault.getDetail().getChildElements();
        while (detailIter.hasNext()) {
            details.add(detailIter.next());
        }
        this.setFaultDetail(XMLUtils.asElementArray(details));
    }

    private void initFaultDetails() {
        if (this.faultDetails == null) {
            this.faultDetails = new Vector();
        }
    }

    public void clearFaultDetails() {
        this.faultDetails = null;
    }

    public void dump() {
        log.debug((Object)this.dumpToString());
    }

    public String dumpToString() {
        int i;
        StringBuffer buf = new StringBuffer("AxisFault");
        buf.append(JavaUtils.LS);
        buf.append(" faultCode: ");
        buf.append(XMLUtils.xmlEncodeString(this.faultCode.toString()));
        buf.append(JavaUtils.LS);
        buf.append(" faultSubcode: ");
        if (this.faultSubCode != null) {
            for (i = 0; i < this.faultSubCode.size(); ++i) {
                buf.append(JavaUtils.LS);
                buf.append(this.faultSubCode.elementAt(i).toString());
            }
        }
        buf.append(JavaUtils.LS);
        buf.append(" faultString: ");
        buf.append(XMLUtils.xmlEncodeString(this.faultString));
        buf.append(JavaUtils.LS);
        buf.append(" faultActor: ");
        buf.append(XMLUtils.xmlEncodeString(this.faultActor));
        buf.append(JavaUtils.LS);
        buf.append(" faultNode: ");
        buf.append(XMLUtils.xmlEncodeString(this.faultNode));
        buf.append(JavaUtils.LS);
        buf.append(" faultDetail: ");
        if (this.faultDetails != null) {
            for (i = 0; i < this.faultDetails.size(); ++i) {
                Element e = (Element)this.faultDetails.get(i);
                buf.append(JavaUtils.LS);
                buf.append("\t{");
                buf.append(null == e.getNamespaceURI() ? "" : e.getNamespaceURI());
                buf.append("}");
                buf.append(null == e.getLocalName() ? "" : e.getLocalName());
                buf.append(":");
                buf.append(XMLUtils.getInnerXMLString(e));
            }
        }
        buf.append(JavaUtils.LS);
        return buf.toString();
    }

    public void setFaultCode(QName code) {
        this.faultCode = code;
    }

    public void setFaultCode(String code) {
        this.setFaultCodeAsString(code);
    }

    public void setFaultCodeAsString(String code) {
        SOAPConstants soapConstants = MessageContext.getCurrentContext() == null ? SOAPConstants.SOAP11_CONSTANTS : MessageContext.getCurrentContext().getSOAPConstants();
        this.faultCode = new QName(soapConstants.getEnvelopeURI(), code);
    }

    public QName getFaultCode() {
        return this.faultCode;
    }

    public void addFaultSubCodeAsString(String code) {
        this.initFaultSubCodes();
        this.faultSubCode.add(new QName("http://xml.apache.org/axis/", code));
    }

    protected void initFaultSubCodes() {
        if (this.faultSubCode == null) {
            this.faultSubCode = new Vector();
        }
    }

    public void addFaultSubCode(QName code) {
        this.initFaultSubCodes();
        this.faultSubCode.add(code);
    }

    public void clearFaultSubCodes() {
        this.faultSubCode = null;
    }

    public QName[] getFaultSubCodes() {
        if (this.faultSubCode == null) {
            return null;
        }
        QName[] q = new QName[this.faultSubCode.size()];
        return this.faultSubCode.toArray(q);
    }

    public void setFaultString(String str) {
        this.faultString = str != null ? str : "";
    }

    public String getFaultString() {
        return this.faultString;
    }

    public void setFaultReason(String str) {
        this.setFaultString(str);
    }

    public String getFaultReason() {
        return this.getFaultString();
    }

    public void setFaultActor(String actor) {
        this.faultActor = actor;
    }

    public String getFaultActor() {
        return this.faultActor;
    }

    public String getFaultRole() {
        return this.getFaultActor();
    }

    public void setFaultRole(String role) {
        this.setFaultActor(role);
    }

    public String getFaultNode() {
        return this.faultNode;
    }

    public void setFaultNode(String node) {
        this.faultNode = node;
    }

    public void setFaultDetail(Element[] details) {
        if (details == null) {
            this.faultDetails = null;
            return;
        }
        this.faultDetails = new Vector(details.length);
        for (int loop = 0; loop < details.length; ++loop) {
            this.faultDetails.add(details[loop]);
        }
    }

    public void setFaultDetailString(String details) {
        this.clearFaultDetails();
        this.addFaultDetailString(details);
    }

    public void addFaultDetailString(String detail) {
        this.initFaultDetails();
        try {
            Document doc = XMLUtils.newDocument();
            Element element = doc.createElement("string");
            Text text = doc.createTextNode(detail);
            element.appendChild(text);
            this.faultDetails.add(element);
        }
        catch (ParserConfigurationException e) {
            throw new InternalException(e);
        }
    }

    public void addFaultDetail(Element detail) {
        this.initFaultDetails();
        this.faultDetails.add(detail);
    }

    public void addFaultDetail(QName qname, String body) {
        Element detail = XMLUtils.StringToElement(qname.getNamespaceURI(), qname.getLocalPart(), body);
        this.addFaultDetail(detail);
    }

    public Element[] getFaultDetails() {
        if (this.faultDetails == null) {
            return null;
        }
        Element[] result = new Element[this.faultDetails.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (Element)this.faultDetails.elementAt(i);
        }
        return result;
    }

    public Element lookupFaultDetail(QName qname) {
        if (this.faultDetails != null) {
            String searchNamespace = qname.getNamespaceURI();
            String searchLocalpart = qname.getLocalPart();
            Iterator it = this.faultDetails.iterator();
            while (it.hasNext()) {
                String namespace;
                Element e = (Element)it.next();
                String localpart = e.getLocalName();
                if (localpart == null) {
                    localpart = e.getNodeName();
                }
                if ((namespace = e.getNamespaceURI()) == null) {
                    namespace = "";
                }
                if (!searchNamespace.equals(namespace) || !searchLocalpart.equals(localpart)) continue;
                return e;
            }
        }
        return null;
    }

    public boolean removeFaultDetail(QName qname) {
        Element elt = this.lookupFaultDetail(qname);
        if (elt == null) {
            return false;
        }
        return this.faultDetails.remove(elt);
    }

    public void output(SerializationContext context) throws Exception {
        SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
        if (context.getMessageContext() != null) {
            soapConstants = context.getMessageContext().getSOAPConstants();
        }
        SOAPEnvelope envelope = new SOAPEnvelope(soapConstants);
        SOAPFault fault = new SOAPFault(this);
        envelope.addBodyElement(fault);
        if (this.faultHeaders != null) {
            Iterator i = this.faultHeaders.iterator();
            while (i.hasNext()) {
                SOAPHeaderElement header = (SOAPHeaderElement)i.next();
                envelope.addHeader(header);
            }
        }
        envelope.output(context);
    }

    public String toString() {
        return this.faultString;
    }

    private String getPlainStackTrace() {
        StringWriter sw = new StringWriter(512);
        PrintWriter pw = new PrintWriter(sw);
        super.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public void printStackTrace(PrintStream ps) {
        ps.println(this.dumpToString());
        super.printStackTrace(ps);
    }

    public void printStackTrace(PrintWriter pw) {
        pw.println(this.dumpToString());
        super.printStackTrace(pw);
    }

    public void addHeader(SOAPHeaderElement header) {
        if (this.faultHeaders == null) {
            this.faultHeaders = new ArrayList();
        }
        this.faultHeaders.add(header);
    }

    public ArrayList getHeaders() {
        return this.faultHeaders;
    }

    public void clearHeaders() {
        this.faultHeaders = null;
    }

    public void writeDetails(QName qname, SerializationContext context) throws IOException {
        Throwable detailObject = this.detail;
        if (detailObject == null) {
            return;
        }
        boolean haveSerializer = false;
        try {
            if (context.getTypeMapping().getSerializer(detailObject.getClass()) != null) {
                haveSerializer = true;
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        if (haveSerializer) {
            boolean oldMR = context.getDoMultiRefs();
            context.setDoMultiRefs(false);
            context.serialize(qname, null, detailObject);
            context.setDoMultiRefs(oldMR);
        }
    }

    public void addHostnameIfNeeded() {
        if (this.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_HOSTNAME) != null) {
            return;
        }
        this.addHostname(NetworkUtils.getLocalHostname());
    }

    public void addHostname(String hostname) {
        this.removeHostname();
        this.addFaultDetail(Constants.QNAME_FAULTDETAIL_HOSTNAME, hostname);
    }

    public void removeHostname() {
        this.removeFaultDetail(Constants.QNAME_FAULTDETAIL_HOSTNAME);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

