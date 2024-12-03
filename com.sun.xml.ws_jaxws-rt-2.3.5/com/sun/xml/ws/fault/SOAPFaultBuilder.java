/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.annotation.XmlTransient
 *  javax.xml.soap.Detail
 *  javax.xml.soap.DetailEntry
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.ProtocolException
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.SOAPFaultException
 */
package com.sun.xml.ws.fault;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.model.ExceptionType;
import com.sun.xml.ws.encoding.soap.SerializationException;
import com.sun.xml.ws.fault.CodeType;
import com.sun.xml.ws.fault.DetailType;
import com.sun.xml.ws.fault.ExceptionBean;
import com.sun.xml.ws.fault.ReasonType;
import com.sun.xml.ws.fault.SOAP11Fault;
import com.sun.xml.ws.fault.SOAP12Fault;
import com.sun.xml.ws.fault.SubcodeType;
import com.sun.xml.ws.message.FaultMessage;
import com.sun.xml.ws.message.jaxb.JAXBMessage;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.util.DOMUtil;
import com.sun.xml.ws.util.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class SOAPFaultBuilder {
    private static final JAXBContext JAXB_CONTEXT;
    private static final Logger logger;
    public static final boolean captureStackTrace;
    static final String CAPTURE_STACK_TRACE_PROPERTY;

    abstract DetailType getDetail();

    abstract void setDetail(DetailType var1);

    @XmlTransient
    @Nullable
    public QName getFirstDetailEntryName() {
        Node entry;
        DetailType dt = this.getDetail();
        if (dt != null && (entry = dt.getDetail(0)) != null) {
            return new QName(entry.getNamespaceURI(), entry.getLocalName());
        }
        return null;
    }

    abstract String getFaultString();

    public Throwable createException(Map<QName, CheckedExceptionImpl> exceptions) throws JAXBException {
        DetailType dt = this.getDetail();
        Node detail = null;
        if (dt != null) {
            detail = dt.getDetail(0);
        }
        if (detail == null || exceptions == null) {
            return this.attachServerException(this.getProtocolException());
        }
        QName detailName = new QName(detail.getNamespaceURI(), detail.getLocalName());
        CheckedExceptionImpl ce = exceptions.get(detailName);
        if (ce == null) {
            return this.attachServerException(this.getProtocolException());
        }
        if (ce.getExceptionType().equals((Object)ExceptionType.UserDefined)) {
            return this.attachServerException(this.createUserDefinedException(ce));
        }
        Class exceptionClass = ce.getExceptionClass();
        try {
            Constructor constructor = exceptionClass.getConstructor(String.class, (Class)ce.getDetailType().type);
            Exception exception = (Exception)constructor.newInstance(this.getFaultString(), this.getJAXBObject(detail, ce));
            return this.attachServerException(exception);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @NotNull
    public static Message createSOAPFaultMessage(@NotNull SOAPVersion soapVersion, @NotNull ProtocolException ex, @Nullable QName faultcode) {
        Object detail = SOAPFaultBuilder.getFaultDetail(null, ex);
        if (soapVersion == SOAPVersion.SOAP_12) {
            return SOAPFaultBuilder.createSOAP12Fault(soapVersion, ex, detail, null, faultcode);
        }
        return SOAPFaultBuilder.createSOAP11Fault(soapVersion, ex, detail, null, faultcode);
    }

    public static Message createSOAPFaultMessage(SOAPVersion soapVersion, CheckedExceptionImpl ceModel, Throwable ex) {
        Throwable t = ex instanceof InvocationTargetException ? ((InvocationTargetException)ex).getTargetException() : ex;
        return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, ceModel, t, null);
    }

    public static Message createSOAPFaultMessage(SOAPVersion soapVersion, CheckedExceptionImpl ceModel, Throwable ex, QName faultCode) {
        Object detail = SOAPFaultBuilder.getFaultDetail(ceModel, ex);
        if (soapVersion == SOAPVersion.SOAP_12) {
            return SOAPFaultBuilder.createSOAP12Fault(soapVersion, ex, detail, ceModel, faultCode);
        }
        return SOAPFaultBuilder.createSOAP11Fault(soapVersion, ex, detail, ceModel, faultCode);
    }

    public static Message createSOAPFaultMessage(SOAPVersion soapVersion, String faultString, QName faultCode) {
        if (faultCode == null) {
            faultCode = SOAPFaultBuilder.getDefaultFaultCode(soapVersion);
        }
        return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, faultString, faultCode, null);
    }

    public static Message createSOAPFaultMessage(SOAPVersion soapVersion, SOAPFault fault) {
        switch (soapVersion) {
            case SOAP_11: {
                return JAXBMessage.create(JAXB_CONTEXT, (Object)new SOAP11Fault(fault), soapVersion);
            }
            case SOAP_12: {
                return JAXBMessage.create(JAXB_CONTEXT, (Object)new SOAP12Fault(fault), soapVersion);
            }
        }
        throw new AssertionError();
    }

    private static Message createSOAPFaultMessage(SOAPVersion soapVersion, String faultString, QName faultCode, Element detail) {
        switch (soapVersion) {
            case SOAP_11: {
                return JAXBMessage.create(JAXB_CONTEXT, (Object)new SOAP11Fault(faultCode, faultString, null, detail), soapVersion);
            }
            case SOAP_12: {
                return JAXBMessage.create(JAXB_CONTEXT, (Object)new SOAP12Fault(faultCode, faultString, detail), soapVersion);
            }
        }
        throw new AssertionError();
    }

    final void captureStackTrace(@Nullable Throwable t) {
        if (t == null) {
            return;
        }
        if (!captureStackTrace) {
            return;
        }
        try {
            Document d = DOMUtil.createDom();
            ExceptionBean.marshal(t, d);
            DetailType detail = this.getDetail();
            if (detail == null) {
                detail = new DetailType();
                this.setDetail(detail);
            }
            detail.getDetails().add(d.getDocumentElement());
        }
        catch (JAXBException e) {
            logger.log(Level.WARNING, "Unable to capture the stack trace into XML", e);
        }
    }

    private <T extends Throwable> T attachServerException(T t) {
        DetailType detail = this.getDetail();
        if (detail == null) {
            return t;
        }
        for (Element n : detail.getDetails()) {
            if (!ExceptionBean.isStackTraceXml(n)) continue;
            try {
                t.initCause(ExceptionBean.unmarshal(n));
            }
            catch (JAXBException e) {
                logger.log(Level.WARNING, "Unable to read the capture stack trace in the fault", e);
            }
            return t;
        }
        return t;
    }

    protected abstract Throwable getProtocolException();

    private Object getJAXBObject(Node jaxbBean, CheckedExceptionImpl ce) throws JAXBException {
        XMLBridge bridge = ce.getBond();
        return bridge.unmarshal(jaxbBean, null);
    }

    private Exception createUserDefinedException(CheckedExceptionImpl ce) {
        Class exceptionClass = ce.getExceptionClass();
        Class detailBean = ce.getDetailBean();
        try {
            Node detailNode = this.getDetail().getDetails().get(0);
            Object jaxbDetail = this.getJAXBObject(detailNode, ce);
            if (jaxbDetail instanceof Exception) {
                return (Exception)jaxbDetail;
            }
            try {
                Constructor exConstructor = exceptionClass.getConstructor(String.class, detailBean);
                return (Exception)exConstructor.newInstance(this.getFaultString(), jaxbDetail);
            }
            catch (NoSuchMethodException e) {
                Constructor exConstructor = exceptionClass.getConstructor(String.class);
                return (Exception)exConstructor.newInstance(this.getFaultString());
            }
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static String getWriteMethod(Field f) {
        return "set" + StringUtils.capitalize(f.getName());
    }

    private static Object getFaultDetail(CheckedExceptionImpl ce, Throwable exception) {
        if (ce == null) {
            return null;
        }
        if (ce.getExceptionType().equals((Object)ExceptionType.UserDefined)) {
            return SOAPFaultBuilder.createDetailFromUserDefinedException(ce, exception);
        }
        try {
            return ce.getFaultInfoGetter().invoke((Object)exception, new Object[0]);
        }
        catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    private static Object createDetailFromUserDefinedException(CheckedExceptionImpl ce, Object exception) {
        Class detailBean = ce.getDetailBean();
        if (ce.getExceptionClass().equals(detailBean)) {
            return exception;
        }
        Field[] fields = detailBean.getDeclaredFields();
        try {
            Object detail = detailBean.newInstance();
            for (Field f : fields) {
                Method em = exception.getClass().getMethod(SOAPFaultBuilder.getReadMethod(f), new Class[0]);
                try {
                    Method sm = detailBean.getMethod(SOAPFaultBuilder.getWriteMethod(f), em.getReturnType());
                    sm.invoke(detail, em.invoke(exception, new Object[0]));
                }
                catch (NoSuchMethodException ne) {
                    Field sf = detailBean.getField(f.getName());
                    sf.set(detail, em.invoke(exception, new Object[0]));
                }
            }
            return detail;
        }
        catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    private static String getReadMethod(Field f) {
        if (f.getType().isAssignableFrom(Boolean.TYPE)) {
            return "is" + StringUtils.capitalize(f.getName());
        }
        return "get" + StringUtils.capitalize(f.getName());
    }

    private static Message createSOAP11Fault(SOAPVersion soapVersion, Throwable e, Object detail, CheckedExceptionImpl ce, QName faultCode) {
        SOAPFaultException soapFaultException = null;
        String faultString = null;
        String faultActor = null;
        Throwable cause = e.getCause();
        if (e instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e;
        } else if (cause != null && cause instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e.getCause();
        }
        if (soapFaultException != null) {
            QName soapFaultCode = soapFaultException.getFault().getFaultCodeAsQName();
            if (soapFaultCode != null) {
                faultCode = soapFaultCode;
            }
            faultString = soapFaultException.getFault().getFaultString();
            faultActor = soapFaultException.getFault().getFaultActor();
        }
        if (faultCode == null) {
            faultCode = SOAPFaultBuilder.getDefaultFaultCode(soapVersion);
        }
        if (faultString == null && (faultString = e.getMessage()) == null) {
            faultString = e.toString();
        }
        Object detailNode = null;
        QName firstEntry = null;
        if (detail == null && soapFaultException != null) {
            detailNode = soapFaultException.getFault().getDetail();
            firstEntry = SOAPFaultBuilder.getFirstDetailEntryName(detailNode);
        } else if (ce != null) {
            try {
                DOMResult dr = new DOMResult();
                ce.getBond().marshal(detail, dr);
                detailNode = (Element)dr.getNode().getFirstChild();
                firstEntry = SOAPFaultBuilder.getFirstDetailEntryName((Element)detailNode);
            }
            catch (JAXBException e1) {
                faultString = e.getMessage();
                faultCode = SOAPFaultBuilder.getDefaultFaultCode(soapVersion);
            }
        }
        SOAP11Fault soap11Fault = new SOAP11Fault(faultCode, faultString, faultActor, (Element)detailNode);
        if (ce == null) {
            soap11Fault.captureStackTrace(e);
        }
        Message msg = JAXBMessage.create(JAXB_CONTEXT, (Object)soap11Fault, soapVersion);
        return new FaultMessage(msg, firstEntry);
    }

    @Nullable
    private static QName getFirstDetailEntryName(@Nullable Detail detail) {
        Iterator it;
        if (detail != null && (it = detail.getDetailEntries()).hasNext()) {
            DetailEntry entry = (DetailEntry)it.next();
            return SOAPFaultBuilder.getFirstDetailEntryName((Element)entry);
        }
        return null;
    }

    @NotNull
    private static QName getFirstDetailEntryName(@NotNull Element entry) {
        return new QName(entry.getNamespaceURI(), entry.getLocalName());
    }

    private static Message createSOAP12Fault(SOAPVersion soapVersion, Throwable e, Object detail, CheckedExceptionImpl ce, QName faultCode) {
        SOAPFaultException soapFaultException = null;
        CodeType code = null;
        String faultString = null;
        String faultRole = null;
        String faultNode = null;
        Throwable cause = e.getCause();
        if (e instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e;
        } else if (cause != null && cause instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e.getCause();
        }
        if (soapFaultException != null) {
            SOAPFault fault = soapFaultException.getFault();
            QName soapFaultCode = fault.getFaultCodeAsQName();
            if (soapFaultCode != null) {
                faultCode = soapFaultCode;
                code = new CodeType(faultCode);
                Iterator iter = fault.getFaultSubcodes();
                boolean first = true;
                SubcodeType subcode = null;
                while (iter.hasNext()) {
                    QName value = (QName)iter.next();
                    if (first) {
                        SubcodeType sct = new SubcodeType(value);
                        code.setSubcode(sct);
                        subcode = sct;
                        first = false;
                        continue;
                    }
                    subcode = SOAPFaultBuilder.fillSubcodes(subcode, value);
                }
            }
            faultString = soapFaultException.getFault().getFaultString();
            faultRole = soapFaultException.getFault().getFaultActor();
            faultNode = soapFaultException.getFault().getFaultNode();
        }
        if (faultCode == null) {
            faultCode = SOAPFaultBuilder.getDefaultFaultCode(soapVersion);
            code = new CodeType(faultCode);
        } else if (code == null) {
            code = new CodeType(faultCode);
        }
        if (faultString == null && (faultString = e.getMessage()) == null) {
            faultString = e.toString();
        }
        ReasonType reason = new ReasonType(faultString);
        Object detailNode = null;
        QName firstEntry = null;
        if (detail == null && soapFaultException != null) {
            detailNode = soapFaultException.getFault().getDetail();
            firstEntry = SOAPFaultBuilder.getFirstDetailEntryName(detailNode);
        } else if (detail != null) {
            try {
                DOMResult dr = new DOMResult();
                ce.getBond().marshal(detail, dr);
                detailNode = (Element)dr.getNode().getFirstChild();
                firstEntry = SOAPFaultBuilder.getFirstDetailEntryName((Element)detailNode);
            }
            catch (JAXBException e1) {
                faultString = e.getMessage();
            }
        }
        SOAP12Fault soap12Fault = new SOAP12Fault(code, reason, faultNode, faultRole, (Element)detailNode);
        if (ce == null) {
            soap12Fault.captureStackTrace(e);
        }
        Message msg = JAXBMessage.create(JAXB_CONTEXT, (Object)soap12Fault, soapVersion);
        return new FaultMessage(msg, firstEntry);
    }

    private static SubcodeType fillSubcodes(SubcodeType parent, QName value) {
        SubcodeType newCode = new SubcodeType(value);
        parent.setSubcode(newCode);
        return newCode;
    }

    private static QName getDefaultFaultCode(SOAPVersion soapVersion) {
        return soapVersion.faultCodeServer;
    }

    public static SOAPFaultBuilder create(Message msg) throws JAXBException {
        return (SOAPFaultBuilder)msg.readPayloadAsJAXB(JAXB_CONTEXT.createUnmarshaller());
    }

    private static JAXBContext createJAXBContext() {
        try {
            return JAXBContext.newInstance((Class[])new Class[]{SOAP11Fault.class, SOAP12Fault.class});
        }
        catch (JAXBException e) {
            throw new Error(e);
        }
    }

    static {
        logger = Logger.getLogger(SOAPFaultBuilder.class.getName());
        CAPTURE_STACK_TRACE_PROPERTY = SOAPFaultBuilder.class.getName() + ".captureStackTrace";
        boolean tmpVal = false;
        try {
            tmpVal = Boolean.getBoolean(CAPTURE_STACK_TRACE_PROPERTY);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        captureStackTrace = tmpVal;
        JAXB_CONTEXT = SOAPFaultBuilder.createJAXBContext();
    }
}

