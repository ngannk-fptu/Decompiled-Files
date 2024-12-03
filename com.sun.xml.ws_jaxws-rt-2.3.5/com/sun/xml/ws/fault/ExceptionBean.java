/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.marshaller.NamespacePrefixMapper
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.PropertyException
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.sun.xml.ws.fault;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.ws.developer.ServerSideException;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(namespace="http://jax-ws.dev.java.net/", name="exception")
final class ExceptionBean {
    @XmlAttribute(name="class")
    public String className;
    @XmlElement
    public String message;
    @XmlElementWrapper(namespace="http://jax-ws.dev.java.net/", name="stackTrace")
    @XmlElement(namespace="http://jax-ws.dev.java.net/", name="frame")
    public List<StackFrame> stackTrace = new ArrayList<StackFrame>();
    @XmlElement(namespace="http://jax-ws.dev.java.net/", name="cause")
    public ExceptionBean cause;
    @XmlAttribute
    public String note = "To disable this feature, set " + SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY + " system property to false";
    private static final JAXBContext JAXB_CONTEXT;
    static final String NS = "http://jax-ws.dev.java.net/";
    static final String LOCAL_NAME = "exception";
    private static final NamespacePrefixMapper nsp;

    public static void marshal(Throwable t, Node parent) throws JAXBException {
        Marshaller m = JAXB_CONTEXT.createMarshaller();
        try {
            m.setProperty("com.sun.xml.bind.namespacePrefixMapper", (Object)nsp);
        }
        catch (PropertyException propertyException) {
            // empty catch block
        }
        m.marshal((Object)new ExceptionBean(t), parent);
    }

    public static ServerSideException unmarshal(Node xml) throws JAXBException {
        ExceptionBean e = (ExceptionBean)JAXB_CONTEXT.createUnmarshaller().unmarshal(xml);
        return e.toException();
    }

    ExceptionBean() {
    }

    private ExceptionBean(Throwable t) {
        this.className = t.getClass().getName();
        this.message = t.getMessage();
        for (StackTraceElement f : t.getStackTrace()) {
            this.stackTrace.add(new StackFrame(f));
        }
        Throwable cause = t.getCause();
        if (t != cause && cause != null) {
            this.cause = new ExceptionBean(cause);
        }
    }

    private ServerSideException toException() {
        ServerSideException e = new ServerSideException(this.className, this.message);
        if (this.stackTrace != null) {
            StackTraceElement[] ste = new StackTraceElement[this.stackTrace.size()];
            for (int i = 0; i < this.stackTrace.size(); ++i) {
                ste[i] = this.stackTrace.get(i).toStackTraceElement();
            }
            e.setStackTrace(ste);
        }
        if (this.cause != null) {
            e.initCause(this.cause.toException());
        }
        return e;
    }

    public static boolean isStackTraceXml(Element n) {
        return LOCAL_NAME.equals(n.getLocalName()) && NS.equals(n.getNamespaceURI());
    }

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance((Class[])new Class[]{ExceptionBean.class});
        }
        catch (JAXBException e) {
            throw new Error(e);
        }
        nsp = new NamespacePrefixMapper(){

            public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
                if (ExceptionBean.NS.equals(namespaceUri)) {
                    return "";
                }
                return suggestion;
            }
        };
    }

    static final class StackFrame {
        @XmlAttribute(name="class")
        public String declaringClass;
        @XmlAttribute(name="method")
        public String methodName;
        @XmlAttribute(name="file")
        public String fileName;
        @XmlAttribute(name="line")
        public String lineNumber;

        StackFrame() {
        }

        public StackFrame(StackTraceElement ste) {
            this.declaringClass = ste.getClassName();
            this.methodName = ste.getMethodName();
            this.fileName = ste.getFileName();
            this.lineNumber = this.box(ste.getLineNumber());
        }

        private String box(int i) {
            if (i >= 0) {
                return String.valueOf(i);
            }
            if (i == -2) {
                return "native";
            }
            return "unknown";
        }

        private int unbox(String v) {
            try {
                return Integer.parseInt(v);
            }
            catch (NumberFormatException e) {
                if ("native".equals(v)) {
                    return -2;
                }
                return -1;
            }
        }

        private StackTraceElement toStackTraceElement() {
            return new StackTraceElement(this.declaringClass, this.methodName, this.fileName, this.unbox(this.lineNumber));
        }
    }
}

