/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  org.jboss.logging.Logger
 */
package org.hibernate.jpa.internal.util;

import java.util.Locale;
import javax.persistence.FlushModeType;
import org.hibernate.AssertionFailure;
import org.hibernate.FlushMode;
import org.hibernate.MappingException;
import org.jboss.logging.Logger;

public class FlushModeTypeHelper {
    private static final Logger log = Logger.getLogger(FlushModeTypeHelper.class);

    private FlushModeTypeHelper() {
    }

    public static FlushModeType getFlushModeType(FlushMode flushMode) {
        if (flushMode == FlushMode.ALWAYS) {
            log.debug((Object)"Interpreting Hibernate FlushMode#ALWAYS to JPA FlushModeType#AUTO; may cause problems if relying on FlushMode#ALWAYS-specific behavior");
            return FlushModeType.AUTO;
        }
        if (flushMode == FlushMode.MANUAL) {
            log.debug((Object)"Interpreting Hibernate FlushMode#MANUAL to JPA FlushModeType#COMMIT; may cause problems if relying on FlushMode#MANUAL-specific behavior");
            return FlushModeType.COMMIT;
        }
        if (flushMode == FlushMode.COMMIT) {
            return FlushModeType.COMMIT;
        }
        if (flushMode == FlushMode.AUTO) {
            return FlushModeType.AUTO;
        }
        throw new AssertionFailure("unhandled FlushMode " + (Object)((Object)flushMode));
    }

    public static FlushMode getFlushMode(FlushModeType flushModeType) {
        if (flushModeType == FlushModeType.AUTO) {
            return FlushMode.AUTO;
        }
        if (flushModeType == FlushModeType.COMMIT) {
            return FlushMode.COMMIT;
        }
        throw new AssertionFailure("unhandled FlushModeType " + flushModeType);
    }

    public static FlushMode interpretFlushMode(Object value) {
        if (value == null) {
            return FlushMode.AUTO;
        }
        if (FlushMode.class.isInstance(value)) {
            return (FlushMode)((Object)value);
        }
        if (FlushModeType.class.isInstance(value)) {
            return FlushModeTypeHelper.getFlushMode((FlushModeType)value);
        }
        if (String.class.isInstance(value)) {
            return FlushModeTypeHelper.interpretExternalSetting((String)value);
        }
        throw new IllegalArgumentException("Unknown FlushMode source : " + value);
    }

    public static FlushMode interpretExternalSetting(String externalName) {
        if (externalName == null) {
            return null;
        }
        try {
            log.debug((Object)("Attempting to interpret external setting [" + externalName + "] as FlushMode name"));
            return FlushMode.valueOf(externalName.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException e) {
            log.debug((Object)("Attempting to interpret external setting [" + externalName + "] as FlushModeType name"));
            try {
                return FlushModeTypeHelper.getFlushMode(FlushModeType.valueOf((String)externalName.toLowerCase(Locale.ROOT)));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw new MappingException("unknown FlushMode : " + externalName);
            }
        }
    }
}

