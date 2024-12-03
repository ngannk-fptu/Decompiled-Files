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
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.DTD2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class DTDEventImpl
extends BaseEventImpl
implements DTD2 {
    protected final String mRootName;
    protected final String mSystemId;
    protected final String mPublicId;
    protected final String mInternalSubset;
    protected final Object mDTD;
    protected String mFullText = null;

    public DTDEventImpl(Location loc, String rootName, String sysId, String pubId, String intSubset, Object dtd) {
        super(loc);
        this.mRootName = rootName;
        this.mSystemId = sysId;
        this.mPublicId = pubId;
        this.mInternalSubset = intSubset;
        this.mFullText = null;
        this.mDTD = dtd;
    }

    public DTDEventImpl(Location loc, String rootName, String intSubset) {
        this(loc, rootName, null, null, intSubset, null);
    }

    public DTDEventImpl(Location loc, String fullText) {
        this(loc, null, null, null, null, null);
        this.mFullText = fullText;
    }

    @Override
    public String getDocumentTypeDeclaration() {
        try {
            return this.doGetDocumentTypeDeclaration();
        }
        catch (XMLStreamException sex) {
            throw new RuntimeException("Internal error: " + sex);
        }
    }

    @Override
    public List<EntityDeclaration> getEntities() {
        return null;
    }

    @Override
    public List<NotationDeclaration> getNotations() {
        return null;
    }

    @Override
    public Object getProcessedDTD() {
        return this.mDTD;
    }

    @Override
    public int getEventType() {
        return 11;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            if (this.mFullText != null) {
                w.write(this.mFullText);
                return;
            }
            w.write("<!DOCTYPE");
            if (this.mRootName != null) {
                w.write(32);
                w.write(this.mRootName);
            }
            if (this.mSystemId != null) {
                if (this.mPublicId != null) {
                    w.write(" PUBLIC \"");
                    w.write(this.mPublicId);
                    w.write(34);
                } else {
                    w.write(" SYSTEM");
                }
                w.write(" \"");
                w.write(this.mSystemId);
                w.write(34);
            }
            if (this.mInternalSubset != null) {
                w.write(" [");
                w.write(this.mInternalSubset);
                w.write(93);
            }
            w.write(">");
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        if (this.mRootName != null) {
            w.writeDTD(this.mRootName, this.mSystemId, this.mPublicId, this.mInternalSubset);
            return;
        }
        w.writeDTD(this.doGetDocumentTypeDeclaration());
    }

    @Override
    public String getRootName() {
        return this.mRootName;
    }

    @Override
    public String getSystemId() {
        return this.mSystemId;
    }

    @Override
    public String getPublicId() {
        return this.mPublicId;
    }

    @Override
    public String getInternalSubset() {
        return this.mInternalSubset;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof DTD)) {
            return false;
        }
        DTD other = (DTD)o;
        return DTDEventImpl.stringsWithNullsEqual(this.getDocumentTypeDeclaration(), other.getDocumentTypeDeclaration());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (this.mRootName != null) {
            hash ^= this.mRootName.hashCode();
        }
        if (this.mSystemId != null) {
            hash ^= this.mSystemId.hashCode();
        }
        if (this.mPublicId != null) {
            hash ^= this.mPublicId.hashCode();
        }
        if (this.mInternalSubset != null) {
            hash ^= this.mInternalSubset.hashCode();
        }
        if (this.mDTD != null) {
            hash ^= this.mDTD.hashCode();
        }
        if (hash == 0 && this.mFullText != null) {
            hash ^= this.mFullText.hashCode();
        }
        return hash;
    }

    protected String doGetDocumentTypeDeclaration() throws XMLStreamException {
        if (this.mFullText == null) {
            int len = 60;
            if (this.mInternalSubset != null) {
                len += this.mInternalSubset.length() + 4;
            }
            StringWriter sw = new StringWriter(len);
            this.writeAsEncodedUnicode(sw);
            this.mFullText = sw.toString();
        }
        return this.mFullText;
    }
}

