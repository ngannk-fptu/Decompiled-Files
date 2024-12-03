/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import org.apache.axis.Constants;
import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;

public class Notation
implements Serializable {
    NCName name;
    URI publicURI;
    URI systemURI;
    private static TypeDesc typeDesc = new TypeDesc(class$org$apache$axis$types$Notation == null ? (class$org$apache$axis$types$Notation = Notation.class$("org.apache.axis.types.Notation")) : class$org$apache$axis$types$Notation);
    static /* synthetic */ Class class$org$apache$axis$types$Notation;

    public Notation() {
    }

    public Notation(NCName name, URI publicURI, URI systemURI) {
        this.name = name;
        this.publicURI = publicURI;
        this.systemURI = systemURI;
    }

    public NCName getName() {
        return this.name;
    }

    public void setName(NCName name) {
        this.name = name;
    }

    public URI getPublic() {
        return this.publicURI;
    }

    public void setPublic(URI publicURI) {
        this.publicURI = publicURI;
    }

    public URI getSystem() {
        return this.systemURI;
    }

    public void setSystem(URI systemURI) {
        this.systemURI = systemURI;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Notation)) {
            return false;
        }
        Notation other = (Notation)obj;
        if (this.name == null ? other.getName() != null : !this.name.equals(other.getName())) {
            return false;
        }
        if (this.publicURI == null ? other.getPublic() != null : !this.publicURI.equals(other.getPublic())) {
            return false;
        }
        return !(this.systemURI == null ? other.getSystem() != null : !this.systemURI.equals(other.getSystem()));
    }

    public int hashCode() {
        int hash = 0;
        if (null != this.name) {
            hash += this.name.hashCode();
        }
        if (null != this.publicURI) {
            hash += this.publicURI.hashCode();
        }
        if (null != this.systemURI) {
            hash += this.systemURI.hashCode();
        }
        return hash;
    }

    public static TypeDesc getTypeDesc() {
        return typeDesc;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        AttributeDesc field = new AttributeDesc();
        field.setFieldName("name");
        field.setXmlName(Constants.XSD_NCNAME);
        typeDesc.addFieldDesc(field);
        field = new AttributeDesc();
        field.setFieldName("public");
        field.setXmlName(Constants.XSD_ANYURI);
        typeDesc.addFieldDesc(field);
        ElementDesc element = null;
        element = new ElementDesc();
        element.setFieldName("system");
        element.setXmlName(Constants.XSD_ANYURI);
        element.setNillable(true);
        typeDesc.addFieldDesc(field);
    }
}

