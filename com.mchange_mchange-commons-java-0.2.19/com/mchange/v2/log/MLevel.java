/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MLevel {
    public static final MLevel ALL;
    public static final MLevel CONFIG;
    public static final MLevel FINE;
    public static final MLevel FINER;
    public static final MLevel FINEST;
    public static final MLevel INFO;
    public static final MLevel OFF;
    public static final MLevel SEVERE;
    public static final MLevel WARNING;
    public static final MLevel DEBUG;
    public static final MLevel TRACE;
    private static final Map integersToMLevels;
    private static final Map namesToMLevels;
    private static final int ALL_INTVAL = Integer.MIN_VALUE;
    private static final int CONFIG_INTVAL = 700;
    private static final int FINE_INTVAL = 500;
    private static final int FINER_INTVAL = 400;
    private static final int FINEST_INTVAL = 300;
    private static final int INFO_INTVAL = 800;
    private static final int OFF_INTVAL = Integer.MAX_VALUE;
    private static final int SEVERE_INTVAL = 1000;
    private static final int WARNING_INTVAL = 900;
    Object level;
    int intval;
    String lvlstring;

    public static MLevel fromIntValue(int n) {
        return (MLevel)integersToMLevels.get(new Integer(n));
    }

    public static MLevel fromSeverity(String string) {
        return (MLevel)namesToMLevels.get(string);
    }

    public int intValue() {
        return this.intval;
    }

    public Object asJdk14Level() {
        return this.level;
    }

    public String getSeverity() {
        return this.lvlstring;
    }

    public String toString() {
        return this.getClass().getName() + this.getLineHeader();
    }

    public String getLineHeader() {
        return "[" + this.lvlstring + ']';
    }

    public boolean isLoggable(MLevel mLevel) {
        return this.intval >= mLevel.intval;
    }

    private MLevel(Object object, int n, String string) {
        this.level = object;
        this.intval = n;
        this.lvlstring = string;
    }

    static {
        MLevel mLevel;
        MLevel mLevel2;
        MLevel mLevel3;
        MLevel mLevel4;
        MLevel mLevel5;
        MLevel mLevel6;
        MLevel mLevel7;
        MLevel mLevel8;
        MLevel mLevel9;
        boolean bl;
        Class<?> clazz;
        try {
            clazz = Class.forName("java.util.logging.Level");
            bl = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            clazz = null;
            bl = false;
        }
        try {
            mLevel9 = new MLevel(bl ? clazz.getField("ALL").get(null) : null, Integer.MIN_VALUE, "ALL");
            mLevel8 = new MLevel(bl ? clazz.getField("CONFIG").get(null) : null, 700, "CONFIG");
            mLevel7 = new MLevel(bl ? clazz.getField("FINE").get(null) : null, 500, "FINE");
            mLevel6 = new MLevel(bl ? clazz.getField("FINER").get(null) : null, 400, "FINER");
            mLevel5 = new MLevel(bl ? clazz.getField("FINEST").get(null) : null, 300, "FINEST");
            mLevel4 = new MLevel(bl ? clazz.getField("INFO").get(null) : null, 800, "INFO");
            mLevel3 = new MLevel(bl ? clazz.getField("OFF").get(null) : null, Integer.MAX_VALUE, "OFF");
            mLevel2 = new MLevel(bl ? clazz.getField("SEVERE").get(null) : null, 1000, "SEVERE");
            mLevel = new MLevel(bl ? clazz.getField("WARNING").get(null) : null, 900, "WARNING");
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new InternalError("Huh? java.util.logging.Level is here, but not its expected public fields?");
        }
        ALL = mLevel9;
        CONFIG = mLevel8;
        FINE = mLevel7;
        FINER = mLevel6;
        FINEST = mLevel5;
        INFO = mLevel4;
        OFF = mLevel3;
        SEVERE = mLevel2;
        WARNING = mLevel;
        DEBUG = mLevel6;
        TRACE = mLevel5;
        HashMap<Object, MLevel> hashMap = new HashMap<Object, MLevel>();
        hashMap.put(new Integer(mLevel9.intValue()), mLevel9);
        hashMap.put(new Integer(mLevel8.intValue()), mLevel8);
        hashMap.put(new Integer(mLevel7.intValue()), mLevel7);
        hashMap.put(new Integer(mLevel6.intValue()), mLevel6);
        hashMap.put(new Integer(mLevel5.intValue()), mLevel5);
        hashMap.put(new Integer(mLevel4.intValue()), mLevel4);
        hashMap.put(new Integer(mLevel3.intValue()), mLevel3);
        hashMap.put(new Integer(mLevel2.intValue()), mLevel2);
        hashMap.put(new Integer(mLevel.intValue()), mLevel);
        integersToMLevels = Collections.unmodifiableMap(hashMap);
        hashMap = new HashMap();
        hashMap.put(mLevel9.getSeverity(), mLevel9);
        hashMap.put(mLevel8.getSeverity(), mLevel8);
        hashMap.put(mLevel7.getSeverity(), mLevel7);
        hashMap.put(mLevel6.getSeverity(), mLevel6);
        hashMap.put(mLevel5.getSeverity(), mLevel5);
        hashMap.put(mLevel4.getSeverity(), mLevel4);
        hashMap.put(mLevel3.getSeverity(), mLevel3);
        hashMap.put(mLevel2.getSeverity(), mLevel2);
        hashMap.put(mLevel.getSeverity(), mLevel);
        namesToMLevels = Collections.unmodifiableMap(hashMap);
    }
}

