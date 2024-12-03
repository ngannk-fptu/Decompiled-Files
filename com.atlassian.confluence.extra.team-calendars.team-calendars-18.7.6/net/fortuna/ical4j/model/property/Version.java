/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Version
extends Property {
    private static final long serialVersionUID = 8872508067309087704L;
    public static final Version VERSION_2_0 = new ImmutableVersion("2.0");
    private String minVersion;
    private String maxVersion;

    public Version() {
        super("VERSION", new Factory());
    }

    public Version(ParameterList aList, String aValue) {
        super("VERSION", aList, new Factory());
        if (aValue.indexOf(59) >= 0) {
            this.minVersion = aValue.substring(0, aValue.indexOf(59) - 1);
            this.maxVersion = aValue.substring(aValue.indexOf(59));
        } else {
            this.maxVersion = aValue;
        }
    }

    public Version(String minVersion, String maxVersion) {
        super("VERSION", new Factory());
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    public Version(ParameterList aList, String aVersion1, String aVersion2) {
        super("VERSION", aList, new Factory());
        this.minVersion = aVersion1;
        this.maxVersion = aVersion2;
    }

    public final String getMaxVersion() {
        return this.maxVersion;
    }

    public final String getMinVersion() {
        return this.minVersion;
    }

    @Override
    public void setValue(String aValue) {
        if (aValue.indexOf(59) >= 0) {
            this.minVersion = aValue.substring(0, aValue.indexOf(59) - 1);
            this.maxVersion = aValue.substring(aValue.indexOf(59));
        } else {
            this.maxVersion = aValue;
        }
    }

    @Override
    public final String getValue() {
        StringBuilder b = new StringBuilder();
        if (this.getMinVersion() != null) {
            b.append(this.getMinVersion());
            if (this.getMaxVersion() != null) {
                b.append(';');
            }
        }
        if (this.getMaxVersion() != null) {
            b.append(this.getMaxVersion());
        }
        return b.toString();
    }

    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<Version> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("VERSION");
        }

        @Override
        public Version createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            Version version = VERSION_2_0.getValue().equals(value) ? VERSION_2_0 : new Version(parameters, value);
            return version;
        }

        @Override
        public Version createProperty() {
            return new Version();
        }
    }

    private static final class ImmutableVersion
    extends Version {
        private static final long serialVersionUID = -5040679357859594835L;

        private ImmutableVersion(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }

        @Override
        public void setMaxVersion(String maxVersion) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }

        @Override
        public void setMinVersion(String minVersion) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

