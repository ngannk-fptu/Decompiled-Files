/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.property.Kind;
import net.fortuna.ical4j.vcard.property.Version;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class VCard
implements Serializable {
    private static final long serialVersionUID = -4784034340843199392L;
    private final List<Property> properties;
    public static final String v4AsXpropPrefix = "X-ICAL4J-TOV3-";
    private static Set<Property.Id> notV3Ok = new TreeSet<Property.Id>();

    public VCard() {
        this(new ArrayList<Property>());
    }

    public VCard(List<Property> properties) {
        this.properties = new CopyOnWriteArrayList<Property>(properties);
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public List<Property> getProperties(Property.Id id) {
        ArrayList<Property> matches = new ArrayList<Property>();
        for (Property p : this.properties) {
            if (!p.getId().equals((Object)id)) continue;
            matches.add(p);
        }
        return Collections.unmodifiableList(matches);
    }

    public Property getProperty(Property.Id id) {
        for (Property p : this.properties) {
            if (!p.getId().equals((Object)id)) continue;
            return p;
        }
        return null;
    }

    public List<Property> getExtendedProperties(String name) {
        ArrayList<Property> matches = new ArrayList<Property>();
        for (Property p : this.properties) {
            if (!p.getId().equals((Object)Property.Id.EXTENDED) || !p.extendedName.equals(name)) continue;
            matches.add(p);
        }
        return Collections.unmodifiableList(matches);
    }

    public Property getExtendedProperty(String name) {
        for (Property p : this.properties) {
            if (!p.getId().equals((Object)Property.Id.EXTENDED) || !p.extendedName.equals(name)) continue;
            return p;
        }
        return null;
    }

    public void validate() throws ValidationException {
        this.assertOne(Property.Id.VERSION);
        this.assertOne(Property.Id.FN);
        boolean isKindGroup = false;
        List<Property> properties = this.getProperties(Property.Id.KIND);
        if (properties.size() > 1) {
            throw new ValidationException("Property [" + (Object)((Object)Property.Id.KIND) + "] must be specified zero or once");
        }
        if (properties.size() == 1) {
            isKindGroup = properties.iterator().next().getValue().equals(Kind.GROUP.getValue());
        }
        for (Property property : this.getProperties()) {
            if (!isKindGroup && property.getId().equals((Object)Property.Id.MEMBER)) {
                throw new ValidationException("Property [" + (Object)((Object)Property.Id.MEMBER) + "] can only be specified if the KIND property value is \"group\".");
            }
            property.validate();
        }
    }

    private void assertOne(Property.Id propertyId) throws ValidationException {
        List<Property> properties = this.getProperties(propertyId);
        if (properties.size() != 1) {
            throw new ValidationException("Property [" + (Object)((Object)propertyId) + "] must be specified once");
        }
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("BEGIN:VCARD");
        b.append("\r\n");
        boolean version4 = true;
        Version v = (Version)this.getProperty(Property.Id.VERSION);
        if (v != null) {
            if (!v.equals(Version.VERSION_4_0)) {
                version4 = false;
            }
            b.append(v);
        }
        for (Property prop : this.properties) {
            if (prop.getId() == Property.Id.VERSION) continue;
            if (version4) {
                b.append(prop);
                continue;
            }
            this.appendDowngraded(b, prop);
        }
        b.append("END:VCARD");
        b.append("\r\n");
        return b.toString();
    }

    private void appendDowngraded(StringBuilder b, Property prop) {
        if (this.v3Ok(prop)) {
            b.append(prop);
            return;
        }
        if (Property.Id.EXTENDED == prop.getId()) {
            b.append(prop);
            return;
        }
        if (prop.getGroup() != null) {
            b.append(prop.getGroup());
            b.append('.');
        }
        b.append(v4AsXpropPrefix + prop.getId().getPropertyName());
        for (Parameter param : prop.getParameters()) {
            b.append(';');
            b.append(param);
        }
        b.append(':');
        if (prop instanceof Escapable) {
            b.append(Strings.escape(Strings.valueOf(prop.getValue())));
        } else {
            b.append(Strings.valueOf(prop.getValue()));
        }
        b.append("\r\n");
    }

    private boolean v3Ok(Property prop) {
        Property.Id id = prop.getId();
        if (notV3Ok.contains((Object)id)) {
            return false;
        }
        Parameter par = prop.getParameter(Parameter.Id.ALTID);
        if (par != null) {
            return false;
        }
        par = prop.getParameter(Parameter.Id.PID);
        return par == null;
    }

    static {
        notV3Ok.add(Property.Id.KIND);
        notV3Ok.add(Property.Id.GENDER);
        notV3Ok.add(Property.Id.LANG);
        notV3Ok.add(Property.Id.ANNIVERSARY);
        notV3Ok.add(Property.Id.XML);
        notV3Ok.add(Property.Id.CLIENTPIDMAP);
    }
}

