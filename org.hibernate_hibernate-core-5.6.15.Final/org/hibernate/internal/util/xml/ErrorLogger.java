/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.internal.util.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class ErrorLogger
implements ErrorHandler,
Serializable {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)ErrorLogger.class.getName());
    private List<SAXParseException> errors;
    private String file;

    public ErrorLogger() {
    }

    public ErrorLogger(String file) {
        this.file = file;
    }

    @Override
    public void error(SAXParseException error) {
        if (this.errors == null) {
            this.errors = new ArrayList<SAXParseException>();
        }
        this.errors.add(error);
    }

    @Override
    public void fatalError(SAXParseException error) {
        this.error(error);
    }

    @Override
    public void warning(SAXParseException warn) {
        LOG.parsingXmlWarning(warn.getLineNumber(), warn.getMessage());
    }

    public List<SAXParseException> getErrors() {
        return this.errors;
    }

    public void reset() {
        this.errors = null;
    }

    public boolean hasErrors() {
        return this.errors != null && this.errors.size() > 0;
    }

    public void logErrors() {
        if (this.errors != null) {
            for (SAXParseException e : this.errors) {
                if (this.file == null) {
                    LOG.parsingXmlError(e.getLineNumber(), e.getMessage());
                    continue;
                }
                LOG.parsingXmlErrorForFile(this.file, e.getLineNumber(), e.getMessage());
            }
        }
    }
}

