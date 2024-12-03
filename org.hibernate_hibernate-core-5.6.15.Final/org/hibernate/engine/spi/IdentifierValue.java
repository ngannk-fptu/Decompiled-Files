/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import org.hibernate.engine.spi.UnsavedValueStrategy;
import org.hibernate.internal.CoreLogging;
import org.jboss.logging.Logger;

public class IdentifierValue
implements UnsavedValueStrategy {
    private static final Logger LOG = CoreLogging.logger(IdentifierValue.class);
    private final Serializable value;
    public static final IdentifierValue ANY = new IdentifierValue(){

        @Override
        public final Boolean isUnsaved(Object id) {
            LOG.trace((Object)"ID unsaved-value strategy ANY");
            return Boolean.TRUE;
        }

        @Override
        public Serializable getDefaultValue(Object currentValue) {
            return (Serializable)currentValue;
        }

        @Override
        public String toString() {
            return "SAVE_ANY";
        }
    };
    public static final IdentifierValue NONE = new IdentifierValue(){

        @Override
        public final Boolean isUnsaved(Object id) {
            LOG.trace((Object)"ID unsaved-value strategy NONE");
            return Boolean.FALSE;
        }

        @Override
        public Serializable getDefaultValue(Object currentValue) {
            return (Serializable)currentValue;
        }

        @Override
        public String toString() {
            return "SAVE_NONE";
        }
    };
    public static final IdentifierValue NULL = new IdentifierValue(){

        @Override
        public final Boolean isUnsaved(Object id) {
            LOG.trace((Object)"ID unsaved-value strategy NULL");
            return id == null;
        }

        @Override
        public Serializable getDefaultValue(Object currentValue) {
            return null;
        }

        @Override
        public String toString() {
            return "SAVE_NULL";
        }
    };
    public static final IdentifierValue UNDEFINED = new IdentifierValue(){

        @Override
        public final Boolean isUnsaved(Object id) {
            LOG.trace((Object)"ID unsaved-value strategy UNDEFINED");
            return null;
        }

        @Override
        public Serializable getDefaultValue(Object currentValue) {
            return null;
        }

        @Override
        public String toString() {
            return "UNDEFINED";
        }
    };

    protected IdentifierValue() {
        this.value = null;
    }

    public IdentifierValue(Serializable value) {
        this.value = value;
    }

    @Override
    public Boolean isUnsaved(Object id) {
        LOG.tracev("ID unsaved-value: {0}", (Object)this.value);
        return id == null || id.equals(this.value);
    }

    @Override
    public Serializable getDefaultValue(Object currentValue) {
        return this.value;
    }

    public String toString() {
        return "identifier unsaved-value: " + this.value;
    }
}

