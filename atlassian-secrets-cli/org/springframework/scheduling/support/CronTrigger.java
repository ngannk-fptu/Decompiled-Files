/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.support;

import java.util.Date;
import java.util.TimeZone;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronSequenceGenerator;

public class CronTrigger
implements Trigger {
    private final CronSequenceGenerator sequenceGenerator;

    public CronTrigger(String expression) {
        this.sequenceGenerator = new CronSequenceGenerator(expression);
    }

    public CronTrigger(String expression, TimeZone timeZone) {
        this.sequenceGenerator = new CronSequenceGenerator(expression, timeZone);
    }

    public String getExpression() {
        return this.sequenceGenerator.getExpression();
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
            date = new Date();
        }
        return this.sequenceGenerator.next(date);
    }

    public boolean equals(Object other) {
        return this == other || other instanceof CronTrigger && this.sequenceGenerator.equals(((CronTrigger)other).sequenceGenerator);
    }

    public int hashCode() {
        return this.sequenceGenerator.hashCode();
    }

    public String toString() {
        return this.sequenceGenerator.toString();
    }
}

