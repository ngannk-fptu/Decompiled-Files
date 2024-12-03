/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import java.io.Serializable;
import net.fortuna.ical4j.vcard.parameter.Type;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class Parameter
implements Serializable {
    private static final long serialVersionUID = 6858428041113700722L;
    private final Id id;
    String extendedName = "";

    public Parameter(String extendedName) {
        this(Id.EXTENDED);
        this.extendedName = extendedName;
    }

    public Parameter(Id id) {
        this.id = id;
    }

    public final Id getId() {
        return this.id;
    }

    public abstract String getValue();

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        if (Id.EXTENDED.equals((Object)this.id)) {
            b.append(this.extendedName);
        } else if (this instanceof Type) {
            b.append(this.id.getPname().toLowerCase());
        } else {
            b.append(this.id.getPname());
        }
        if (this.getValue() != null) {
            b.append('=');
            b.append(this.getValue());
        }
        return b.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Id {
        LANGUAGE,
        ENCODING,
        VALUE,
        PREF,
        ALTID,
        PID,
        TYPE,
        CALSCALE,
        SORT_AS("SORT-AS"),
        GEO,
        TZ,
        VERSION,
        FMTTYPE,
        EXTENDED;

        private String pname;

        private Id() {
            this(null);
        }

        private Id(String pname) {
            this.pname = pname;
        }

        public String getPname() {
            if (StringUtils.isNotEmpty(this.pname)) {
                return this.pname;
            }
            return this.toString();
        }
    }
}

