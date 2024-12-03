/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class MyErrorHandler
implements ErrorHandler {
    private Log log = LogFactory.getLog(MyErrorHandler.class);

    MyErrorHandler() {
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("ParserUtils: warning ", ex);
        }
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        throw ex;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }
}

