/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.UnsavedValueStrategy;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.internal.CoreLogging;
import org.jboss.logging.Logger;

public class VersionValue
implements UnsavedValueStrategy {
    private static final Logger LOG = CoreLogging.logger(VersionValue.class);
    private final Object value;
    public static final VersionValue NULL = new VersionValue(){

        @Override
        public final Boolean isUnsaved(Object version) {
            LOG.trace((Object)"Version unsaved-value strategy NULL");
            return version == null;
        }

        @Override
        public Object getDefaultValue(Object currentValue) {
            return null;
        }

        @Override
        public String toString() {
            return "VERSION_SAVE_NULL";
        }
    };
    public static final VersionValue UNDEFINED = new VersionValue(){

        @Override
        public final Boolean isUnsaved(Object version) {
            LOG.trace((Object)"Version unsaved-value strategy UNDEFINED");
            return version == null ? Boolean.TRUE : null;
        }

        @Override
        public Object getDefaultValue(Object currentValue) {
            return currentValue;
        }

        @Override
        public String toString() {
            return "VERSION_UNDEFINED";
        }
    };
    public static final VersionValue NEGATIVE = new VersionValue(){

        @Override
        public final Boolean isUnsaved(Object version) throws MappingException {
            LOG.trace((Object)"Version unsaved-value strategy NEGATIVE");
            if (version == null) {
                return Boolean.TRUE;
            }
            if (version instanceof Number) {
                return ((Number)version).longValue() < 0L;
            }
            throw new MappingException("unsaved-value NEGATIVE may only be used with short, int and long types");
        }

        @Override
        public Object getDefaultValue(Object currentValue) {
            return IdentifierGeneratorHelper.getIntegralDataTypeHolder(currentValue.getClass()).initialize(-1L).makeValue();
        }

        @Override
        public String toString() {
            return "VERSION_NEGATIVE";
        }
    };

    protected VersionValue() {
        this.value = null;
    }

    public VersionValue(Object value) {
        this.value = value;
    }

    @Override
    public Boolean isUnsaved(Object version) throws MappingException {
        LOG.tracev("Version unsaved-value: {0}", this.value);
        return version == null || version.equals(this.value);
    }

    @Override
    public Object getDefaultValue(Object currentValue) {
        return this.value;
    }

    public String toString() {
        return "version unsaved-value: " + this.value;
    }
}

