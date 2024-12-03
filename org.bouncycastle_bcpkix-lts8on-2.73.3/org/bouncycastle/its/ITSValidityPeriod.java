/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Duration
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod
 */
package org.bouncycastle.its;

import java.util.Date;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Duration;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class ITSValidityPeriod {
    private final long startDate;
    private final UINT16 duration;
    private final Unit timeUnit;

    public static Builder from(Date startDate) {
        return new Builder(startDate);
    }

    public ITSValidityPeriod(ValidityPeriod validityPeriod) {
        this.startDate = validityPeriod.getStart().getValue().longValue();
        Duration duration = validityPeriod.getDuration();
        this.duration = duration.getDuration();
        this.timeUnit = Unit.values()[duration.getChoice()];
    }

    ITSValidityPeriod(long startDate, UINT16 duration, Unit timeUnit) {
        this.startDate = startDate;
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public Date getStartDate() {
        return new Date(this.startDate);
    }

    public ValidityPeriod toASN1Structure() {
        return ValidityPeriod.builder().setStart(new Time32(this.startDate / 1000L)).setDuration(new Duration(this.timeUnit.unitTag, this.duration)).createValidityPeriod();
    }

    public static class Builder {
        private final long startDate;

        Builder(Date startDate) {
            this.startDate = startDate.getTime();
        }

        public ITSValidityPeriod plusYears(int years) {
            return new ITSValidityPeriod(this.startDate, UINT16.valueOf((int)years), Unit.years);
        }

        public ITSValidityPeriod plusSixtyHours(int periods) {
            return new ITSValidityPeriod(this.startDate, UINT16.valueOf((int)periods), Unit.sixtyHours);
        }
    }

    public static enum Unit {
        microseconds(0),
        milliseconds(1),
        seconds(2),
        minutes(3),
        hours(4),
        sixtyHours(5),
        years(6);

        private final int unitTag;

        private Unit(int unitTag) {
            this.unitTag = unitTag;
        }
    }
}

