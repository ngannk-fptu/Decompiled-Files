/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.input;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class BuilderErrorHandler
implements ErrorHandler {
    private static final String CVS_ID = "@(#) $RCSfile: BuilderErrorHandler.java,v $ $Revision: 1.13 $ $Date: 2007/11/10 05:29:00 $ $Name:  $";

    @Override
    public void warning(SAXParseException exception) throws SAXException {
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }
}

