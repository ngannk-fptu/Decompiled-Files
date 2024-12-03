/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Marker
 */
package aQute.libg.slf4j;

import org.slf4j.Marker;

public class GradleLogging {
    public static final Marker LIFECYCLE;
    public static final Marker QUIET;

    private GradleLogging() {
    }

    static {
        Marker lifecycle = null;
        Marker quiet = null;
        try {
            Class<?> logging = Class.forName("org.gradle.api.logging.Logging");
            lifecycle = (Marker)logging.getField("LIFECYCLE").get(null);
            quiet = (Marker)logging.getField("QUIET").get(null);
        }
        catch (Exception exception) {
            // empty catch block
        }
        LIFECYCLE = lifecycle;
        QUIET = quiet;
    }
}

