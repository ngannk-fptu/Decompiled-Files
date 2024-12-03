/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log.jdk14logging;

import com.mchange.v2.log.MLevel;
import java.util.logging.Level;

public final class Jdk14LoggingUtils {
    public static MLevel mlevelFromLevel(Level level) {
        if (level == Level.ALL) {
            return MLevel.ALL;
        }
        if (level == Level.CONFIG) {
            return MLevel.CONFIG;
        }
        if (level == Level.FINE) {
            return MLevel.FINE;
        }
        if (level == Level.FINER) {
            return MLevel.FINER;
        }
        if (level == Level.FINEST) {
            return MLevel.FINEST;
        }
        if (level == Level.INFO) {
            return MLevel.INFO;
        }
        if (level == Level.OFF) {
            return MLevel.OFF;
        }
        if (level == Level.SEVERE) {
            return MLevel.SEVERE;
        }
        if (level == Level.WARNING) {
            return MLevel.WARNING;
        }
        throw new IllegalArgumentException("Unexpected Jdk14 logging level: " + level);
    }

    public static Level levelFromMLevel(MLevel mLevel) {
        if (mLevel == MLevel.ALL) {
            return Level.ALL;
        }
        if (mLevel == MLevel.CONFIG) {
            return Level.CONFIG;
        }
        if (mLevel == MLevel.FINE) {
            return Level.FINE;
        }
        if (mLevel == MLevel.FINER) {
            return Level.FINER;
        }
        if (mLevel == MLevel.FINEST) {
            return Level.FINEST;
        }
        if (mLevel == MLevel.INFO) {
            return Level.INFO;
        }
        if (mLevel == MLevel.OFF) {
            return Level.OFF;
        }
        if (mLevel == MLevel.SEVERE) {
            return Level.SEVERE;
        }
        if (mLevel == MLevel.WARNING) {
            return Level.WARNING;
        }
        throw new IllegalArgumentException("Unexpected MLevel: " + mLevel);
    }

    private Jdk14LoggingUtils() {
    }
}

