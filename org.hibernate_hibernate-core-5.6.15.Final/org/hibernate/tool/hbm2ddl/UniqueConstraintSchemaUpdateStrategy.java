/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.hbm2ddl;

import java.util.Locale;
import org.jboss.logging.Logger;

public enum UniqueConstraintSchemaUpdateStrategy {
    DROP_RECREATE_QUIETLY,
    RECREATE_QUIETLY,
    SKIP;

    private static final Logger log;

    public static UniqueConstraintSchemaUpdateStrategy byName(String name) {
        return UniqueConstraintSchemaUpdateStrategy.valueOf(name.toUpperCase(Locale.ROOT));
    }

    public static UniqueConstraintSchemaUpdateStrategy interpret(Object setting) {
        log.tracef("Interpreting UniqueConstraintSchemaUpdateStrategy from setting : %s", setting);
        if (setting == null) {
            return DROP_RECREATE_QUIETLY;
        }
        if (UniqueConstraintSchemaUpdateStrategy.class.isInstance(setting)) {
            return (UniqueConstraintSchemaUpdateStrategy)((Object)setting);
        }
        try {
            UniqueConstraintSchemaUpdateStrategy byName = UniqueConstraintSchemaUpdateStrategy.byName(setting.toString());
            if (byName != null) {
                return byName;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        log.debugf("Unable to interpret given setting [%s] as UniqueConstraintSchemaUpdateStrategy", setting);
        return DROP_RECREATE_QUIETLY;
    }

    static {
        log = Logger.getLogger(UniqueConstraintSchemaUpdateStrategy.class);
    }
}

