/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav;

import java.io.IOException;
import java.util.Properties;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DavException
extends Exception
implements XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(DavException.class);
    private static Properties statusPhrases = new Properties();
    public static final String XML_ERROR = "error";
    private int errorCode = 500;
    private Element errorCondition;

    public DavException(int errorCode, String message) {
        this(errorCode, message, null, null);
    }

    public DavException(int errorCode, Throwable cause) {
        this(errorCode, null, cause, null);
    }

    public DavException(int errorCode) {
        this(errorCode, statusPhrases.getProperty(String.valueOf(errorCode)), null, null);
    }

    public DavException(int errorCode, String message, Throwable cause, Element errorCondition) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorCondition = errorCondition;
        log.debug("DavException: (" + errorCode + ") " + message);
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getStatusPhrase() {
        return DavException.getStatusPhrase(this.errorCode);
    }

    public static String getStatusPhrase(int errorCode) {
        return statusPhrases.getProperty(errorCode + "");
    }

    public boolean hasErrorCondition() {
        return this.errorCondition != null;
    }

    public Element getErrorCondition() {
        return this.errorCondition;
    }

    @Override
    public Element toXml(Document document) {
        if (this.hasErrorCondition()) {
            Element error;
            if (DomUtil.matches(this.errorCondition, XML_ERROR, DavConstants.NAMESPACE)) {
                error = (Element)document.importNode(this.errorCondition, true);
            } else {
                error = DomUtil.createElement(document, XML_ERROR, DavConstants.NAMESPACE);
                error.appendChild(document.importNode(this.errorCondition, true));
            }
            return error;
        }
        return null;
    }

    static {
        try {
            statusPhrases.load(DavException.class.getResourceAsStream("statuscode.properties"));
        }
        catch (IOException e) {
            log.error("Failed to load status properties: " + e.getMessage());
        }
    }
}

