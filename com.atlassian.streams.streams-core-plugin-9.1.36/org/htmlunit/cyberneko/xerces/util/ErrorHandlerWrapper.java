/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLErrorHandler;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerWrapper
implements XMLErrorHandler {
    private ErrorHandler fErrorHandler_;

    public ErrorHandlerWrapper(ErrorHandler errorHandler) {
        this.setErrorHandler(errorHandler);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.fErrorHandler_ = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return this.fErrorHandler_;
    }

    @Override
    public void warning(String domain, String key, XMLParseException exception) throws XNIException {
        if (this.fErrorHandler_ != null) {
            SAXParseException saxException = ErrorHandlerWrapper.createSAXParseException(exception);
            try {
                this.fErrorHandler_.warning(saxException);
            }
            catch (SAXParseException e) {
                throw ErrorHandlerWrapper.createXMLParseException(e);
            }
            catch (SAXException e) {
                throw ErrorHandlerWrapper.createXNIException(e);
            }
        }
    }

    @Override
    public void error(String domain, String key, XMLParseException exception) throws XNIException {
        if (this.fErrorHandler_ != null) {
            SAXParseException saxException = ErrorHandlerWrapper.createSAXParseException(exception);
            try {
                this.fErrorHandler_.error(saxException);
            }
            catch (SAXParseException e) {
                throw ErrorHandlerWrapper.createXMLParseException(e);
            }
            catch (SAXException e) {
                throw ErrorHandlerWrapper.createXNIException(e);
            }
        }
    }

    @Override
    public void fatalError(String domain, String key, XMLParseException exception) throws XNIException {
        if (this.fErrorHandler_ != null) {
            SAXParseException saxException = ErrorHandlerWrapper.createSAXParseException(exception);
            try {
                this.fErrorHandler_.fatalError(saxException);
            }
            catch (SAXParseException e) {
                throw ErrorHandlerWrapper.createXMLParseException(e);
            }
            catch (SAXException e) {
                throw ErrorHandlerWrapper.createXNIException(e);
            }
        }
    }

    protected static SAXParseException createSAXParseException(XMLParseException exception) {
        return new SAXParseException(exception.getMessage(), exception.getPublicId(), exception.getExpandedSystemId(), exception.getLineNumber(), exception.getColumnNumber(), exception.getException());
    }

    protected static XMLParseException createXMLParseException(SAXParseException exception) {
        final String fPublicId = exception.getPublicId();
        final String fExpandedSystemId = exception.getSystemId();
        final int fLineNumber = exception.getLineNumber();
        final int fColumnNumber = exception.getColumnNumber();
        XMLLocator location = new XMLLocator(){

            @Override
            public String getPublicId() {
                return fPublicId;
            }

            @Override
            public String getExpandedSystemId() {
                return fExpandedSystemId;
            }

            @Override
            public String getBaseSystemId() {
                return null;
            }

            @Override
            public String getLiteralSystemId() {
                return null;
            }

            @Override
            public int getColumnNumber() {
                return fColumnNumber;
            }

            @Override
            public int getLineNumber() {
                return fLineNumber;
            }

            @Override
            public int getCharacterOffset() {
                return -1;
            }

            @Override
            public String getEncoding() {
                return null;
            }

            @Override
            public String getXMLVersion() {
                return null;
            }
        };
        return new XMLParseException(location, exception.getMessage(), exception);
    }

    protected static XNIException createXNIException(SAXException exception) {
        return new XNIException(exception.getMessage(), exception);
    }
}

