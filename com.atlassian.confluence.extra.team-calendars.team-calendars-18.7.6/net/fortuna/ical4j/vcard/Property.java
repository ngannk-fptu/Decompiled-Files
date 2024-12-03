/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.parameter.Value;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Property
implements Serializable {
    private static final long serialVersionUID = 7813173744145071469L;
    protected static final String ILLEGAL_PARAMETER_MESSAGE = "Illegal parameter [{0}]";
    private static final String ILLEGAL_PARAMETER_COUNT_MESSAGE = "Parameter [{0}] exceeds allowable count";
    private final Group group;
    private final Id id;
    String extendedName = "";
    private final List<Parameter> parameters;

    public Property(String extendedName) {
        this(null, extendedName);
    }

    public Property(Group group, String extendedName) {
        this(group, Id.EXTENDED);
        this.extendedName = extendedName;
    }

    public Property(String extendedName, List<Parameter> parameters) {
        this(null, extendedName, parameters);
    }

    public Property(Group group, String extendedName, List<Parameter> parameters) {
        this(group, Id.EXTENDED, parameters);
        this.extendedName = extendedName;
    }

    public Property(Id id) {
        this(null, id);
    }

    public Property(Group group, Id id) {
        this(group, id, new ArrayList<Parameter>());
    }

    protected Property(Id id, List<Parameter> parameters) {
        this(null, id, parameters);
    }

    protected Property(Group group, Id id, List<Parameter> parameters) {
        this.group = group;
        this.id = id;
        this.parameters = new CopyOnWriteArrayList<Parameter>(parameters);
    }

    public final Group getGroup() {
        return this.group;
    }

    public final Id getId() {
        return this.id;
    }

    public final List<Parameter> getParameters() {
        return this.parameters;
    }

    public final List<Parameter> getParameters(Parameter.Id id) {
        ArrayList<Parameter> matches = new ArrayList<Parameter>();
        for (Parameter p : this.parameters) {
            if (!p.getId().equals((Object)id)) continue;
            matches.add(p);
        }
        return Collections.unmodifiableList(matches);
    }

    public final Parameter getParameter(Parameter.Id id) {
        for (Parameter p : this.parameters) {
            if (!p.getId().equals((Object)id)) continue;
            return p;
        }
        return null;
    }

    public final List<Parameter> getExtendedParameters(String name) {
        ArrayList<Parameter> matches = new ArrayList<Parameter>();
        for (Parameter p : this.parameters) {
            if (!p.getId().equals((Object)Parameter.Id.EXTENDED) || !p.extendedName.equals(name)) continue;
            matches.add(p);
        }
        return Collections.unmodifiableList(matches);
    }

    public final Parameter getExtendedParameter(String name) {
        for (Parameter p : this.parameters) {
            if (!p.getId().equals((Object)Parameter.Id.EXTENDED) || !p.extendedName.equals(name)) continue;
            return p;
        }
        return null;
    }

    public String getExtendedName() {
        return this.extendedName;
    }

    public abstract String getValue();

    public abstract void validate() throws ValidationException;

    protected final void assertParametersEmpty() throws ValidationException {
        if (!this.getParameters().isEmpty()) {
            throw new ValidationException("No parameters allowed for property: " + (Object)((Object)this.id));
        }
    }

    protected final void assertTextParameter(Parameter param) throws ValidationException {
        if (!(Value.TEXT.equals(param) || Parameter.Id.LANGUAGE.equals((Object)param.getId()) || Parameter.Id.EXTENDED.equals((Object)param.getId()))) {
            throw new ValidationException(MessageFormat.format(ILLEGAL_PARAMETER_MESSAGE, new Object[]{param.getId()}));
        }
    }

    protected final void assertTypeParameter(Parameter param) throws ValidationException {
        if (!Parameter.Id.TYPE.equals((Object)param.getId())) {
            throw new ValidationException(MessageFormat.format(ILLEGAL_PARAMETER_MESSAGE, new Object[]{param.getId()}));
        }
    }

    protected final void assertPidParameter(Parameter param) throws ValidationException {
        if (!Parameter.Id.PID.equals((Object)param.getId())) {
            throw new ValidationException(MessageFormat.format(ILLEGAL_PARAMETER_MESSAGE, new Object[]{param.getId()}));
        }
    }

    protected final void assertPrefParameter(Parameter param) throws ValidationException {
        if (!Parameter.Id.PREF.equals((Object)param.getId())) {
            throw new ValidationException(MessageFormat.format(ILLEGAL_PARAMETER_MESSAGE, new Object[]{param.getId()}));
        }
    }

    protected final void assertOneOrLess(Parameter.Id paramId) throws ValidationException {
        if (this.getParameters(paramId).size() > 1) {
            throw new ValidationException(MessageFormat.format(ILLEGAL_PARAMETER_COUNT_MESSAGE, new Object[]{paramId}));
        }
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        if (this.group != null) {
            b.append(this.group);
            b.append('.');
        }
        if (Id.EXTENDED.equals((Object)this.id)) {
            b.append(this.extendedName);
        } else {
            b.append(this.id.getPropertyName());
        }
        for (Parameter param : this.parameters) {
            b.append(';');
            b.append(param);
        }
        b.append(':');
        if (this instanceof Escapable) {
            b.append(Strings.escape(Strings.valueOf(this.getValue())));
        } else {
            b.append(Strings.valueOf(this.getValue()));
        }
        b.append("\r\n");
        return b.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Id {
        SOURCE,
        NAME,
        KIND,
        XML,
        FN,
        N,
        NICKNAME,
        PHOTO,
        BDAY,
        DDAY,
        BIRTH,
        DEATH,
        GENDER,
        ANNIVERSARY,
        ADR,
        LABEL,
        TEL,
        EMAIL,
        IMPP,
        LANG,
        TZ,
        GEO,
        TITLE,
        ROLE,
        LOGO,
        AGENT,
        ORG,
        MEMBER,
        RELATED,
        CATEGORIES,
        NOTE,
        PRODID,
        REV,
        SORT_STRING("SORT-STRING"),
        SOUND,
        UID,
        URL,
        VERSION,
        CLIENTPIDMAP,
        CLASS,
        KEY,
        FBURL,
        CALADRURI,
        CALURI,
        EXTENDED,
        AUTOSCHEDULE,
        BOOKINGINFO,
        BOOKINGRESTRICTED,
        BOOKINGWINDOWEND,
        BOOKINGWINDOWSTART,
        MAXINSTANCES,
        MULTIBOOK,
        SCHEDADMININFO,
        ACCESSIBLE,
        ACCESSIBILITYINFO,
        RESTRICTEDACCESSINFO,
        CAPACITY,
        COSTINFO,
        INVENTORY,
        LOCATIONTYPE,
        NOCOST,
        RESOURCEMANAGERINFO,
        RESOURCEOWNERINFO,
        RESTRICTED,
        MAILER;

        private String propertyName;

        private Id() {
            this(null);
        }

        private Id(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            if (StringUtils.isNotEmpty(this.propertyName)) {
                return this.propertyName;
            }
            return this.toString();
        }
    }
}

