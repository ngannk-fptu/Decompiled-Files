/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartDocument;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class StartDocumentEventImpl
extends BaseEventImpl
implements StartDocument {
    private final boolean mStandaloneSet;
    private final boolean mIsStandalone;
    private final String mVersion;
    private final boolean mEncodingSet;
    private final String mEncodingScheme;
    private final String mSystemId;

    public StartDocumentEventImpl(Location loc, XMLStreamReader r) {
        super(loc);
        this.mStandaloneSet = r.standaloneSet();
        this.mIsStandalone = r.isStandalone();
        String version = r.getVersion();
        if (version == null || version.length() == 0) {
            version = "1.0";
        }
        this.mVersion = version;
        this.mEncodingScheme = r.getCharacterEncodingScheme();
        this.mEncodingSet = this.mEncodingScheme != null && this.mEncodingScheme.length() > 0;
        this.mSystemId = loc != null ? loc.getSystemId() : "";
    }

    public StartDocumentEventImpl(Location loc) {
        this(loc, (String)null);
    }

    public StartDocumentEventImpl(Location loc, String encoding) {
        this(loc, encoding, null);
    }

    public StartDocumentEventImpl(Location loc, String encoding, String version) {
        this(loc, encoding, version, false, false);
    }

    public StartDocumentEventImpl(Location loc, String encoding, String version, boolean standaloneSet, boolean isStandalone) {
        super(loc);
        this.mEncodingScheme = encoding;
        this.mEncodingSet = encoding != null && encoding.length() > 0;
        this.mVersion = version;
        this.mStandaloneSet = standaloneSet;
        this.mIsStandalone = isStandalone;
        this.mSystemId = "";
    }

    @Override
    public boolean encodingSet() {
        return this.mEncodingSet;
    }

    @Override
    public String getCharacterEncodingScheme() {
        return this.mEncodingScheme;
    }

    @Override
    public String getSystemId() {
        return this.mSystemId;
    }

    @Override
    public String getVersion() {
        return this.mVersion;
    }

    @Override
    public boolean isStandalone() {
        return this.mIsStandalone;
    }

    @Override
    public boolean standaloneSet() {
        return this.mStandaloneSet;
    }

    @Override
    public int getEventType() {
        return 7;
    }

    @Override
    public boolean isStartDocument() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<?xml version=\"");
            if (this.mVersion == null || this.mVersion.length() == 0) {
                w.write("1.0");
            } else {
                w.write(this.mVersion);
            }
            w.write(34);
            if (this.mEncodingSet) {
                w.write(" encoding=\"");
                w.write(this.mEncodingScheme);
                w.write(34);
            }
            if (this.mStandaloneSet) {
                if (this.mIsStandalone) {
                    w.write(" standalone=\"yes\"");
                } else {
                    w.write(" standalone=\"no\"");
                }
            }
            w.write(" ?>");
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        w.writeStartDocument();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof StartDocument)) {
            return false;
        }
        StartDocument other = (StartDocument)o;
        return this.encodingSet() == other.encodingSet() && this.isStandalone() == other.isStandalone() && this.standaloneSet() == other.standaloneSet() && StartDocumentEventImpl.stringsWithNullsEqual(this.getCharacterEncodingScheme(), other.getCharacterEncodingScheme()) && StartDocumentEventImpl.stringsWithNullsEqual(this.getSystemId(), other.getSystemId()) && StartDocumentEventImpl.stringsWithNullsEqual(this.getVersion(), other.getVersion());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (this.encodingSet()) {
            ++hash;
        }
        if (this.isStandalone()) {
            --hash;
        }
        if (this.standaloneSet()) {
            hash ^= 1;
        }
        if (this.mVersion != null) {
            hash ^= this.mVersion.hashCode();
        }
        if (this.mEncodingScheme != null) {
            hash ^= this.mEncodingScheme.hashCode();
        }
        if (this.mSystemId != null) {
            hash ^= this.mSystemId.hashCode();
        }
        return hash;
    }
}

