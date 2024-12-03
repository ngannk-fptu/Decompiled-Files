/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import java.util.ArrayList;
import java.util.Vector;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLDTDDescription
extends XMLResourceIdentifierImpl
implements org.apache.xerces.xni.grammars.XMLDTDDescription {
    protected String fRootName = null;
    protected ArrayList fPossibleRoots = null;

    public XMLDTDDescription(XMLResourceIdentifier xMLResourceIdentifier, String string) {
        this.setValues(xMLResourceIdentifier.getPublicId(), xMLResourceIdentifier.getLiteralSystemId(), xMLResourceIdentifier.getBaseSystemId(), xMLResourceIdentifier.getExpandedSystemId());
        this.fRootName = string;
        this.fPossibleRoots = null;
    }

    public XMLDTDDescription(String string, String string2, String string3, String string4, String string5) {
        this.setValues(string, string2, string3, string4);
        this.fRootName = string5;
        this.fPossibleRoots = null;
    }

    public XMLDTDDescription(XMLInputSource xMLInputSource) {
        this.setValues(xMLInputSource.getPublicId(), null, xMLInputSource.getBaseSystemId(), xMLInputSource.getSystemId());
        this.fRootName = null;
        this.fPossibleRoots = null;
    }

    @Override
    public String getGrammarType() {
        return "http://www.w3.org/TR/REC-xml";
    }

    @Override
    public String getRootName() {
        return this.fRootName;
    }

    public void setRootName(String string) {
        this.fRootName = string;
        this.fPossibleRoots = null;
    }

    public void setPossibleRoots(ArrayList arrayList) {
        this.fPossibleRoots = arrayList;
    }

    public void setPossibleRoots(Vector vector) {
        this.fPossibleRoots = vector != null ? new ArrayList(vector) : null;
    }

    public boolean equals(Object object) {
        if (!(object instanceof XMLGrammarDescription)) {
            return false;
        }
        if (!this.getGrammarType().equals(((XMLGrammarDescription)object).getGrammarType())) {
            return false;
        }
        XMLDTDDescription xMLDTDDescription = (XMLDTDDescription)object;
        if (this.fRootName != null) {
            if (xMLDTDDescription.fRootName != null && !xMLDTDDescription.fRootName.equals(this.fRootName)) {
                return false;
            }
            if (xMLDTDDescription.fPossibleRoots != null && !xMLDTDDescription.fPossibleRoots.contains(this.fRootName)) {
                return false;
            }
        } else if (this.fPossibleRoots != null) {
            if (xMLDTDDescription.fRootName != null) {
                if (!this.fPossibleRoots.contains(xMLDTDDescription.fRootName)) {
                    return false;
                }
            } else {
                String string;
                if (xMLDTDDescription.fPossibleRoots == null) {
                    return false;
                }
                boolean bl = false;
                int n = this.fPossibleRoots.size();
                for (int i = 0; i < n && !(bl = xMLDTDDescription.fPossibleRoots.contains(string = (String)this.fPossibleRoots.get(i))); ++i) {
                }
                if (!bl) {
                    return false;
                }
            }
        }
        if (this.fExpandedSystemId != null ? !this.fExpandedSystemId.equals(xMLDTDDescription.fExpandedSystemId) : xMLDTDDescription.fExpandedSystemId != null) {
            return false;
        }
        return !(this.fPublicId != null ? !this.fPublicId.equals(xMLDTDDescription.fPublicId) : xMLDTDDescription.fPublicId != null);
    }

    @Override
    public int hashCode() {
        if (this.fExpandedSystemId != null) {
            return this.fExpandedSystemId.hashCode();
        }
        if (this.fPublicId != null) {
            return this.fPublicId.hashCode();
        }
        return 0;
    }
}

