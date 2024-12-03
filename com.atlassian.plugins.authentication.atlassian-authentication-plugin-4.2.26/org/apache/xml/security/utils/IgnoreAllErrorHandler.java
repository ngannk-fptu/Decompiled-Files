/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils;

import java.security.AccessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class IgnoreAllErrorHandler
implements ErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(IgnoreAllErrorHandler.class);
    private static final boolean warnOnExceptions = IgnoreAllErrorHandler.getProperty("org.apache.xml.security.test.warn.on.exceptions");
    private static final boolean throwExceptions = IgnoreAllErrorHandler.getProperty("org.apache.xml.security.test.throw.exceptions");

    private static boolean getProperty(String name) {
        return AccessController.doPrivileged(() -> Boolean.getBoolean(name));
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        if (warnOnExceptions) {
            LOG.warn("", (Throwable)ex);
        }
        if (throwExceptions) {
            throw ex;
        }
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        if (warnOnExceptions) {
            LOG.error("", (Throwable)ex);
        }
        if (throwExceptions) {
            throw ex;
        }
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        if (warnOnExceptions) {
            LOG.warn("", (Throwable)ex);
        }
        if (throwExceptions) {
            throw ex;
        }
    }
}

