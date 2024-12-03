/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.internal;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class Logger {
    private final System.Logger logger;

    private Logger(String name) {
        this.logger = System.getLogger(name);
    }

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public boolean isLoggable(Level level) {
        return this.logger.isLoggable(level.level);
    }

    public void warn(String msg) {
        this.logger.log(Level.WARNING.level, msg);
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static enum Level {
        WARNING(System.Logger.Level.WARNING);

        private final System.Logger.Level level;

        private Level(System.Logger.Level level) {
            this.level = level;
        }
    }
}

