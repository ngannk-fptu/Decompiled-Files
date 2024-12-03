/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

public final class TidyMessage {
    private int line;
    private int column;
    private Level level;
    private String message;
    private int errorCode;

    public TidyMessage(int errorCode, int line, int column, Level level, String message) {
        this.errorCode = errorCode;
        this.line = line;
        this.column = column;
        this.level = level;
        this.message = message;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public int getColumn() {
        return this.column;
    }

    public Level getLevel() {
        return this.level;
    }

    public int getLine() {
        return this.line;
    }

    public String getMessage() {
        return this.message;
    }

    public static final class Level
    implements Comparable {
        public static final Level SUMMARY = new Level(0);
        public static final Level INFO = new Level(1);
        public static final Level WARNING = new Level(2);
        public static final Level ERROR = new Level(3);
        private short code;

        private Level(int code) {
            this.code = (short)code;
        }

        public short getCode() {
            return this.code;
        }

        public static Level fromCode(int code) {
            switch (code) {
                case 0: {
                    return SUMMARY;
                }
                case 1: {
                    return INFO;
                }
                case 2: {
                    return WARNING;
                }
                case 3: {
                    return ERROR;
                }
            }
            return null;
        }

        public int compareTo(Object object) {
            return this.code - ((Level)object).code;
        }

        public boolean equals(Object object) {
            if (!(object instanceof Level)) {
                return false;
            }
            return this.code == ((Level)object).code;
        }

        public String toString() {
            switch (this.code) {
                case 0: {
                    return "SUMMARY";
                }
                case 1: {
                    return "INFO";
                }
                case 2: {
                    return "WARNING";
                }
                case 3: {
                    return "ERROR";
                }
            }
            return "?";
        }

        public int hashCode() {
            return super.hashCode();
        }
    }
}

