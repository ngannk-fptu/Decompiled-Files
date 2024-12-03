/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule.field;

import java.util.BitSet;

class DayOfWeekConstantConverter {
    private static final int[] CRON_TO_ISO = new int[]{-1, 7, 1, 2, 3, 4, 5, 6};
    private static final int[] ISO_TO_CRON = new int[]{-1, 2, 3, 4, 5, 6, 7, 1};
    private static final String[] ISO_TO_NAME = new String[]{null, "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

    DayOfWeekConstantConverter() {
    }

    static int cronToIso(int cronDayOfWeek) {
        return CRON_TO_ISO[cronDayOfWeek];
    }

    static BitSet cronToIso(BitSet cronDaysOfWeek) {
        return DayOfWeekConstantConverter.mapBitSet(cronDaysOfWeek, CRON_TO_ISO);
    }

    static int isoToCron(int isoDayOfWeek) {
        return ISO_TO_CRON[isoDayOfWeek];
    }

    static BitSet isoToCron(BitSet isoDaysOfWeek) {
        return DayOfWeekConstantConverter.mapBitSet(isoDaysOfWeek, ISO_TO_CRON);
    }

    static String isoToName(int isoDayOfWeek) {
        return ISO_TO_NAME[isoDayOfWeek];
    }

    private static BitSet mapBitSet(BitSet inBits, int[] mapper) {
        BitSet outBits = new BitSet(8);
        int inBit = inBits.nextSetBit(1);
        while (inBit != -1) {
            outBits.set(mapper[inBit]);
            inBit = inBits.nextSetBit(inBit + 1);
        }
        return outBits;
    }
}

