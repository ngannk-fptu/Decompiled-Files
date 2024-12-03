/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.WrappedRuntimeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ListingErrorHandler
implements ErrorHandler,
ErrorListener {
    protected PrintWriter m_pw = null;
    protected boolean throwOnWarning = false;
    protected boolean throwOnError = true;
    protected boolean throwOnFatalError = true;

    public ListingErrorHandler(PrintWriter pw) {
        if (null == pw) {
            throw new NullPointerException(XMLMessages.createXMLMessage("ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER", null));
        }
        this.m_pw = pw;
    }

    public ListingErrorHandler() {
        this.m_pw = new PrintWriter(System.err, true);
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        ListingErrorHandler.logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("warning: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnWarning()) {
            throw exception;
        }
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        ListingErrorHandler.logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("error: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnError()) {
            throw exception;
        }
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        ListingErrorHandler.logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("fatalError: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnFatalError()) {
            throw exception;
        }
    }

    @Override
    public void warning(TransformerException exception) throws TransformerException {
        ListingErrorHandler.logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("warning: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnWarning()) {
            throw exception;
        }
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        ListingErrorHandler.logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("error: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnError()) {
            throw exception;
        }
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        ListingErrorHandler.logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("error: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnError()) {
            throw exception;
        }
    }

    public static void logExceptionLocation(PrintWriter pw, Throwable exception) {
        if (null == pw) {
            pw = new PrintWriter(System.err, true);
        }
        SourceLocator locator = null;
        Throwable cause = exception;
        do {
            SourceLocator causeLocator;
            if (cause instanceof SAXParseException) {
                locator = new SAXSourceLocator((SAXParseException)cause);
                continue;
            }
            if (!(cause instanceof TransformerException) || null == (causeLocator = ((TransformerException)cause).getLocator())) continue;
            locator = causeLocator;
        } while (null != (cause = cause instanceof TransformerException ? ((TransformerException)cause).getCause() : (cause instanceof WrappedRuntimeException ? ((WrappedRuntimeException)cause).getException() : (cause instanceof SAXException ? ((SAXException)cause).getException() : null))));
        if (null != locator) {
            String id = locator.getPublicId() != locator.getPublicId() ? locator.getPublicId() : (null != locator.getSystemId() ? locator.getSystemId() : "SystemId-Unknown");
            pw.print(id + ":Line=" + locator.getLineNumber() + ";Column=" + locator.getColumnNumber() + ": ");
            pw.println("exception:" + exception.getMessage());
            pw.println("root-cause:" + (null != cause ? cause.getMessage() : "null"));
            ListingErrorHandler.logSourceLine(pw, locator);
        } else {
            pw.print("SystemId-Unknown:locator-unavailable: ");
            pw.println("exception:" + exception.getMessage());
            pw.println("root-cause:" + (null != cause ? cause.getMessage() : "null"));
        }
    }

    public static void logSourceLine(PrintWriter pw, SourceLocator locator) {
        String url;
        if (null == locator) {
            return;
        }
        if (null == pw) {
            pw = new PrintWriter(System.err, true);
        }
        if (null == (url = locator.getSystemId())) {
            pw.println("line: (No systemId; cannot read file)");
            pw.println();
            return;
        }
        try {
            int line = locator.getLineNumber();
            int column = locator.getColumnNumber();
            pw.println("line: " + ListingErrorHandler.getSourceLine(url, line));
            StringBuffer buf = new StringBuffer("line: ");
            for (int i = 1; i < column; ++i) {
                buf.append(' ');
            }
            buf.append('^');
            pw.println(buf.toString());
        }
        catch (Exception e) {
            pw.println("line: logSourceLine unavailable due to: " + e.getMessage());
            pw.println();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static String getSourceLine(String sourceUrl, int lineNum) throws Exception {
        URL url = null;
        try {
            url = new URL(sourceUrl);
        }
        catch (MalformedURLException mue) {
            int indexOfColon = sourceUrl.indexOf(58);
            int indexOfSlash = sourceUrl.indexOf(47);
            if (indexOfColon != -1 && indexOfSlash != -1 && indexOfColon < indexOfSlash) {
                throw mue;
            }
            url = new URL(SystemIDResolver.getAbsoluteURI(sourceUrl));
        }
        String line = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            URLConnection uc = url.openConnection();
            is = uc.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            for (int i = 1; i <= lineNum; ++i) {
                line = br.readLine();
            }
        }
        finally {
            br.close();
            is.close();
        }
        return line;
    }

    public void setThrowOnWarning(boolean b) {
        this.throwOnWarning = b;
    }

    public boolean getThrowOnWarning() {
        return this.throwOnWarning;
    }

    public void setThrowOnError(boolean b) {
        this.throwOnError = b;
    }

    public boolean getThrowOnError() {
        return this.throwOnError;
    }

    public void setThrowOnFatalError(boolean b) {
        this.throwOnFatalError = b;
    }

    public boolean getThrowOnFatalError() {
        return this.throwOnFatalError;
    }
}

