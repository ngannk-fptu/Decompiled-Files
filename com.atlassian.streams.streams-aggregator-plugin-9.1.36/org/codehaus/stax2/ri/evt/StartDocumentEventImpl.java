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

    public StartDocumentEventImpl(Location location, XMLStreamReader xMLStreamReader) {
        super(location);
        this.mStandaloneSet = xMLStreamReader.standaloneSet();
        this.mIsStandalone = xMLStreamReader.isStandalone();
        String string = xMLStreamReader.getVersion();
        if (string == null || string.length() == 0) {
            string = "1.0";
        }
        this.mVersion = string;
        this.mEncodingScheme = xMLStreamReader.getCharacterEncodingScheme();
        this.mEncodingSet = this.mEncodingScheme != null && this.mEncodingScheme.length() > 0;
        this.mSystemId = location != null ? location.getSystemId() : "";
    }

    public StartDocumentEventImpl(Location location) {
        this(location, (String)null);
    }

    public StartDocumentEventImpl(Location location, String string) {
        this(location, string, null);
    }

    public StartDocumentEventImpl(Location location, String string, String string2) {
        this(location, string, string2, false, false);
    }

    public StartDocumentEventImpl(Location location, String string, String string2, boolean bl, boolean bl2) {
        super(location);
        this.mEncodingScheme = string;
        this.mEncodingSet = string != null && string.length() > 0;
        this.mVersion = string2;
        this.mStandaloneSet = bl;
        this.mIsStandalone = bl2;
        this.mSystemId = "";
    }

    public boolean encodingSet() {
        return this.mEncodingSet;
    }

    public String getCharacterEncodingScheme() {
        return this.mEncodingScheme;
    }

    public String getSystemId() {
        return this.mSystemId;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public boolean isStandalone() {
        return this.mIsStandalone;
    }

    public boolean standaloneSet() {
        return this.mStandaloneSet;
    }

    public int getEventType() {
        return 7;
    }

    public boolean isStartDocument() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?xml version=\"");
            if (this.mVersion == null || this.mVersion.length() == 0) {
                writer.write("1.0");
            } else {
                writer.write(this.mVersion);
            }
            writer.write(34);
            if (this.mEncodingSet) {
                writer.write(" encoding=\"");
                writer.write(this.mEncodingScheme);
                writer.write(34);
            }
            if (this.mStandaloneSet) {
                if (this.mIsStandalone) {
                    writer.write(" standalone=\"yes\"");
                } else {
                    writer.write(" standalone=\"no\"");
                }
            }
            writer.write(" ?>");
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        xMLStreamWriter2.writeStartDocument();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof StartDocument)) {
            return false;
        }
        StartDocument startDocument = (StartDocument)object;
        return this.encodingSet() == startDocument.encodingSet() && this.isStandalone() == startDocument.isStandalone() && this.standaloneSet() == startDocument.standaloneSet() && StartDocumentEventImpl.stringsWithNullsEqual(this.getCharacterEncodingScheme(), startDocument.getCharacterEncodingScheme()) && StartDocumentEventImpl.stringsWithNullsEqual(this.getSystemId(), startDocument.getSystemId()) && StartDocumentEventImpl.stringsWithNullsEqual(this.getVersion(), startDocument.getVersion());
    }

    public int hashCode() {
        int n = 0;
        if (this.encodingSet()) {
            ++n;
        }
        if (this.isStandalone()) {
            --n;
        }
        if (this.standaloneSet()) {
            n ^= 1;
        }
        if (this.mVersion != null) {
            n ^= this.mVersion.hashCode();
        }
        if (this.mEncodingScheme != null) {
            n ^= this.mEncodingScheme.hashCode();
        }
        if (this.mSystemId != null) {
            n ^= this.mSystemId.hashCode();
        }
        return n;
    }
}

