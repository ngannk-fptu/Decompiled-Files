/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader;

import java.util.Locale;
import org.jboss.logging.Logger;

public enum BatchFetchStyle {
    LEGACY,
    PADDED,
    DYNAMIC;

    private static final Logger log;

    public static BatchFetchStyle byName(String name) {
        return BatchFetchStyle.valueOf(name.toUpperCase(Locale.ROOT));
    }

    public static BatchFetchStyle interpret(Object setting) {
        log.tracef("Interpreting BatchFetchStyle from setting : %s", setting);
        if (setting == null) {
            return LEGACY;
        }
        if (BatchFetchStyle.class.isInstance(setting)) {
            return (BatchFetchStyle)((Object)setting);
        }
        try {
            BatchFetchStyle byName = BatchFetchStyle.byName(setting.toString());
            if (byName != null) {
                return byName;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        log.debugf("Unable to interpret given setting [%s] as BatchFetchStyle", setting);
        return LEGACY;
    }

    static {
        log = Logger.getLogger(BatchFetchStyle.class);
    }
}

