/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.StartDocument;

public class StartDocumentEvent
extends AbstractXMLEvent
implements StartDocument {
    public static final String DEFAULT_VERSION = "1.0";
    public static final String DEFAULT_SYSTEM_ID = "";
    public static final String DEFAULT_ENCODING = "UTF-8";
    protected String encoding;
    protected Boolean standalone;
    protected String version;

    public StartDocumentEvent() {
    }

    public StartDocumentEvent(Location location) {
        super(location);
    }

    public StartDocumentEvent(String encoding, Location location) {
        super(location);
        this.encoding = encoding;
    }

    public StartDocumentEvent(String encoding, Boolean standalone, String version, Location location) {
        super(location);
        this.encoding = encoding;
        this.standalone = standalone;
        this.version = version;
    }

    public StartDocumentEvent(String encoding, Boolean standalone, String version, Location location, QName schemaType) {
        super(location, schemaType);
        this.encoding = encoding;
        this.standalone = standalone;
        this.version = version;
    }

    public StartDocumentEvent(StartDocument that) {
        super(that);
        if (that.encodingSet()) {
            this.encoding = that.getCharacterEncodingScheme();
        }
        if (this.standaloneSet()) {
            this.standalone = that.isStandalone() ? Boolean.TRUE : Boolean.FALSE;
        }
        this.version = DEFAULT_VERSION.equals(that.getVersion()) ? null : that.getVersion();
    }

    public int getEventType() {
        return 7;
    }

    public boolean encodingSet() {
        return this.encoding != null;
    }

    public String getCharacterEncodingScheme() {
        return this.encoding == null ? DEFAULT_ENCODING : this.encoding;
    }

    public String getSystemId() {
        String systemId;
        Location location = this.getLocation();
        if (location != null && (systemId = location.getSystemId()) != null) {
            return systemId;
        }
        return DEFAULT_SYSTEM_ID;
    }

    public String getVersion() {
        return this.version == null ? DEFAULT_VERSION : this.version;
    }

    public boolean isStandalone() {
        return this.standalone == null ? false : this.standalone;
    }

    public boolean standaloneSet() {
        return this.standalone != null;
    }
}

