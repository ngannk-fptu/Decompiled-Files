/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.DTD2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class DTDEventImpl
extends BaseEventImpl
implements DTD2 {
    final String mRootName;
    final String mSystemId;
    final String mPublicId;
    final String mInternalSubset;
    final Object mDTD;
    String mFullText = null;

    public DTDEventImpl(Location location, String string, String string2, String string3, String string4, Object object) {
        super(location);
        this.mRootName = string;
        this.mSystemId = string2;
        this.mPublicId = string3;
        this.mInternalSubset = string4;
        this.mFullText = null;
        this.mDTD = object;
    }

    public DTDEventImpl(Location location, String string, String string2) {
        this(location, string, null, null, string2, null);
    }

    public DTDEventImpl(Location location, String string) {
        this(location, null, null, null, null, null);
        this.mFullText = string;
    }

    public String getDocumentTypeDeclaration() {
        try {
            return this.doGetDocumentTypeDeclaration();
        }
        catch (XMLStreamException xMLStreamException) {
            throw new RuntimeException("Internal error: " + xMLStreamException);
        }
    }

    public List getEntities() {
        return null;
    }

    public List getNotations() {
        return null;
    }

    public Object getProcessedDTD() {
        return this.mDTD;
    }

    public int getEventType() {
        return 11;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            if (this.mFullText != null) {
                writer.write(this.mFullText);
                return;
            }
            writer.write("<!DOCTYPE");
            if (this.mRootName != null) {
                writer.write(32);
                writer.write(this.mRootName);
            }
            if (this.mSystemId != null) {
                if (this.mPublicId != null) {
                    writer.write(" PUBLIC \"");
                    writer.write(this.mPublicId);
                    writer.write(34);
                } else {
                    writer.write(" SYSTEM");
                }
                writer.write(" \"");
                writer.write(this.mSystemId);
                writer.write(34);
            }
            if (this.mInternalSubset != null) {
                writer.write(" [");
                writer.write(this.mInternalSubset);
                writer.write(93);
            }
            writer.write(">");
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        if (this.mRootName != null) {
            xMLStreamWriter2.writeDTD(this.mRootName, this.mSystemId, this.mPublicId, this.mInternalSubset);
            return;
        }
        xMLStreamWriter2.writeDTD(this.doGetDocumentTypeDeclaration());
    }

    public String getRootName() {
        return this.mRootName;
    }

    public String getSystemId() {
        return this.mSystemId;
    }

    public String getPublicId() {
        return this.mPublicId;
    }

    public String getInternalSubset() {
        return this.mInternalSubset;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof DTD)) {
            return false;
        }
        DTD dTD = (DTD)object;
        return DTDEventImpl.stringsWithNullsEqual(this.getDocumentTypeDeclaration(), dTD.getDocumentTypeDeclaration());
    }

    public int hashCode() {
        int n = 0;
        if (this.mRootName != null) {
            n ^= this.mRootName.hashCode();
        }
        if (this.mSystemId != null) {
            n ^= this.mSystemId.hashCode();
        }
        if (this.mPublicId != null) {
            n ^= this.mPublicId.hashCode();
        }
        if (this.mInternalSubset != null) {
            n ^= this.mInternalSubset.hashCode();
        }
        if (this.mDTD != null) {
            n ^= this.mDTD.hashCode();
        }
        if (n == 0 && this.mFullText != null) {
            n ^= this.mFullText.hashCode();
        }
        return n;
    }

    protected String doGetDocumentTypeDeclaration() throws XMLStreamException {
        if (this.mFullText == null) {
            int n = 60;
            if (this.mInternalSubset != null) {
                n += this.mInternalSubset.length() + 4;
            }
            StringWriter stringWriter = new StringWriter(n);
            this.writeAsEncodedUnicode(stringWriter);
            this.mFullText = stringWriter.toString();
        }
        return this.mFullText;
    }
}

