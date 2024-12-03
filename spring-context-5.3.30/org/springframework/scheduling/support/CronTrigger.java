/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.scheduling.support;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.Assert;

public class CronTrigger
implements Trigger {
    private final CronExpression expression;
    private final ZoneId zoneId;

    public CronTrigger(String expression) {
        this(expression, ZoneId.systemDefault());
    }

    public CronTrigger(String expression, TimeZone timeZone) {
        this(expression, timeZone.toZoneId());
    }

    public CronTrigger(String expression, ZoneId zoneId) {
        Assert.hasLength((String)expression, (String)"Expression must not be empty");
        Assert.notNull((Object)zoneId, (String)"ZoneId must not be null");
        this.expression = CronExpression.parse(expression);
        this.zoneId = zoneId;
    }

    public String getExpression() {
        return this.expression.toString();
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        if (date != null) {
            Date scheduled = triggerContext.lastScheduledExecutionTime();
            if (scheduled != null && date.before(scheduled)) {
                date = scheduled;
            }
        } else {
            date = new Date(triggerContext.getClock().millis());
        }
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), this.zoneId);
        ZonedDateTime next = this.expression.next(dateTime);
        return next != null ? Date.from(next.toInstant()) : null;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof CronTrigger && this.expression.equals(((CronTrigger)other).expression);
    }

    public int hashCode() {
        return this.expression.hashCode();
    }

    public String toString() {
        return this.expression.toString();
    }
}

