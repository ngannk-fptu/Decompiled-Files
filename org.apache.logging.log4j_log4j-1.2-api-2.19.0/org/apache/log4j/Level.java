/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 */
package org.apache.log4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Locale;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.OptionConverter;

public class Level
extends Priority
implements Serializable {
    public static final int TRACE_INT = 5000;
    public static final Level OFF = new Level(Integer.MAX_VALUE, "OFF", 0, org.apache.logging.log4j.Level.OFF);
    public static final Level FATAL = new Level(50000, "FATAL", 0, org.apache.logging.log4j.Level.FATAL);
    public static final Level ERROR = new Level(40000, "ERROR", 3, org.apache.logging.log4j.Level.ERROR);
    public static final Level WARN = new Level(30000, "WARN", 4, org.apache.logging.log4j.Level.WARN);
    public static final Level INFO = new Level(20000, "INFO", 6, org.apache.logging.log4j.Level.INFO);
    public static final Level DEBUG = new Level(10000, "DEBUG", 7, org.apache.logging.log4j.Level.DEBUG);
    public static final Level TRACE = new Level(5000, "TRACE", 7, org.apache.logging.log4j.Level.TRACE);
    public static final Level ALL = new Level(Integer.MIN_VALUE, "ALL", 7, org.apache.logging.log4j.Level.ALL);
    private static final long serialVersionUID = 3491141966387921974L;

    protected Level(int level, String levelStr, int syslogEquivalent) {
        this(level, levelStr, syslogEquivalent, null);
    }

    protected Level(int level, String levelStr, int syslogEquivalent, org.apache.logging.log4j.Level version2Equivalent) {
        super(level, levelStr, syslogEquivalent);
        this.version2Level = version2Equivalent != null ? version2Equivalent : OptionConverter.createLevel(this);
    }

    public static Level toLevel(String sArg) {
        return Level.toLevel(sArg, DEBUG);
    }

    public static Level toLevel(int val) {
        return Level.toLevel(val, DEBUG);
    }

    public static Level toLevel(int val, Level defaultLevel) {
        switch (val) {
            case -2147483648: {
                return ALL;
            }
            case 10000: {
                return DEBUG;
            }
            case 20000: {
                return INFO;
            }
            case 30000: {
                return WARN;
            }
            case 40000: {
                return ERROR;
            }
            case 50000: {
                return FATAL;
            }
            case 0x7FFFFFFF: {
                return OFF;
            }
            case 5000: {
                return TRACE;
            }
        }
        return defaultLevel;
    }

    public static Level toLevel(String sArg, Level defaultLevel) {
        String s;
        if (sArg == null) {
            return defaultLevel;
        }
        switch (s = sArg.toUpperCase(Locale.ROOT)) {
            case "ALL": {
                return ALL;
            }
            case "DEBUG": {
                return DEBUG;
            }
            case "INFO": {
                return INFO;
            }
            case "WARN": {
                return WARN;
            }
            case "ERROR": {
                return ERROR;
            }
            case "FATAL": {
                return FATAL;
            }
            case "OFF": {
                return OFF;
            }
            case "TRACE": {
                return TRACE;
            }
        }
        return defaultLevel;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.level = s.readInt();
        this.syslogEquivalent = s.readInt();
        this.levelStr = s.readUTF();
        if (this.levelStr == null) {
            this.levelStr = "";
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.level);
        s.writeInt(this.syslogEquivalent);
        s.writeUTF(this.levelStr);
    }

    protected Object readResolve() throws ObjectStreamException {
        if (this.getClass() == Level.class) {
            return Level.toLevel(this.level);
        }
        return this;
    }
}

