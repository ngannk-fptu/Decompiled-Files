/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.xslt.util.XslTransformErrorLocatorHelper;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.WrappedRuntimeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultErrorHandler
implements ErrorHandler,
ErrorListener {
    PrintWriter m_pw;
    boolean m_throwExceptionOnError = true;

    public DefaultErrorHandler(PrintWriter pw) {
        this.m_pw = pw;
    }

    public DefaultErrorHandler(PrintStream pw) {
        this.m_pw = new PrintWriter(pw, true);
    }

    public DefaultErrorHandler() {
        this(true);
    }

    public DefaultErrorHandler(boolean throwExceptionOnError) {
        this.m_throwExceptionOnError = throwExceptionOnError;
    }

    public PrintWriter getErrorWriter() {
        if (this.m_pw == null) {
            this.m_pw = new PrintWriter(System.err, true);
        }
        return this.m_pw;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        PrintWriter pw = this.getErrorWriter();
        DefaultErrorHandler.printLocation(pw, (Throwable)exception);
        pw.println("Parser warning: " + exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

    @Override
    public void warning(TransformerException exception) throws TransformerException {
        PrintWriter pw = this.getErrorWriter();
        DefaultErrorHandler.printLocation(pw, (Throwable)exception);
        pw.println(exception.getMessage());
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        if (this.m_throwExceptionOnError) {
            throw exception;
        }
        PrintWriter pw = this.getErrorWriter();
        DefaultErrorHandler.printLocation(pw, (Throwable)exception);
        pw.println(exception.getMessage());
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        if (this.m_throwExceptionOnError) {
            throw exception;
        }
        PrintWriter pw = this.getErrorWriter();
        DefaultErrorHandler.printLocation(pw, (Throwable)exception);
        pw.println(exception.getMessage());
    }

    public static void ensureLocationSet(TransformerException exception) {
        SourceLocator locator = null;
        Throwable cause = exception;
        do {
            SourceLocator causeLocator;
            if (cause instanceof SAXParseException) {
                locator = new SAXSourceLocator((SAXParseException)cause);
                continue;
            }
            if (!(cause instanceof TransformerException) || null == (causeLocator = cause.getLocator())) continue;
            locator = causeLocator;
        } while (null != (cause = cause instanceof TransformerException ? cause.getCause() : (cause instanceof SAXException ? ((SAXException)cause).getException() : null)));
        exception.setLocator(locator);
    }

    public static void printLocation(PrintStream pw, TransformerException exception) {
        DefaultErrorHandler.printLocation(new PrintWriter(pw), (Throwable)exception);
    }

    public static void printLocation(PrintStream pw, SAXParseException exception) {
        DefaultErrorHandler.printLocation(new PrintWriter(pw), (Throwable)exception);
    }

    public static void printLocation(PrintWriter pw, Throwable exception) {
        SourceLocator locator = null;
        Throwable cause = exception;
        String xslSystemId = null;
        do {
            SourceLocator causeLocator;
            if (cause instanceof SAXParseException) {
                locator = new SAXSourceLocator((SAXParseException)cause);
                continue;
            }
            if (!(cause instanceof TransformerException) || null == (causeLocator = ((TransformerException)cause).getLocator())) continue;
            if (causeLocator.getSystemId() == null) {
                xslSystemId = XslTransformErrorLocatorHelper.systemId;
            }
            locator = causeLocator;
        } while (null != (cause = cause instanceof TransformerException ? ((TransformerException)cause).getCause() : (cause instanceof WrappedRuntimeException ? ((WrappedRuntimeException)cause).getException() : (cause instanceof SAXException ? ((SAXException)cause).getException() : null))));
        if (null != locator) {
            String id = null != locator.getPublicId() ? locator.getPublicId() : (null != locator.getSystemId() ? locator.getSystemId() : (null != xslSystemId ? xslSystemId : XMLMessages.createXMLMessage("ER_SYSTEMID_UNKNOWN", null)));
            pw.print(id + "; " + XMLMessages.createXMLMessage("line", null) + locator.getLineNumber() + "; " + XMLMessages.createXMLMessage("column", null) + locator.getColumnNumber() + "; ");
        } else {
            pw.print("(" + XMLMessages.createXMLMessage("ER_LOCATION_UNKNOWN", null) + ")");
        }
    }
}

