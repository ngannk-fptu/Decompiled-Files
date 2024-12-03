/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.descriptor;

import java.util.ArrayList;
import java.util.List;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.descriptor.Constants;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlErrorHandler
implements ErrorHandler {
    private static final StringManager sm = StringManager.getManager((String)Constants.PACKAGE_NAME);
    private final List<SAXParseException> errors = new ArrayList<SAXParseException>();
    private final List<SAXParseException> warnings = new ArrayList<SAXParseException>();

    @Override
    public void error(SAXParseException exception) throws SAXException {
        this.errors.add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        this.warnings.add(exception);
    }

    public List<SAXParseException> getErrors() {
        return this.errors;
    }

    public List<SAXParseException> getWarnings() {
        return this.warnings;
    }

    public void logFindings(Log log, String source) {
        for (SAXParseException e : this.getWarnings()) {
            log.warn((Object)sm.getString("xmlErrorHandler.warning", new Object[]{e.getMessage(), source}));
        }
        for (SAXParseException e : this.getErrors()) {
            log.warn((Object)sm.getString("xmlErrorHandler.error", new Object[]{e.getMessage(), source}));
        }
    }
}

