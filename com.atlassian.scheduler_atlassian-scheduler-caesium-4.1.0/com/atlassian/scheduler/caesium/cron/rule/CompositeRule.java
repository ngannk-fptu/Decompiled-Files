/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.caesium.cron.rule;

import com.atlassian.scheduler.caesium.cron.rule.CronRule;
import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import java.util.Objects;

class CompositeRule
implements CronRule {
    private static final long serialVersionUID = -8319039114000087612L;
    private final CronRule major;
    private final CronRule minor;

    static CompositeRule compose(CronRule major, CronRule minor) {
        return new CompositeRule(major, minor);
    }

    private CompositeRule(CronRule major, CronRule minor) {
        this.major = Objects.requireNonNull(major, "major");
        this.minor = Objects.requireNonNull(minor, "minor");
    }

    @Override
    public boolean matches(DateTimeTemplate dateTime) {
        return this.major.matches(dateTime) && this.minor.matches(dateTime);
    }

    @Override
    public boolean next(DateTimeTemplate dateTime) {
        if (this.major.matches(dateTime) && this.minor.next(dateTime)) {
            return true;
        }
        while (this.major.next(dateTime)) {
            if (!this.minor.first(dateTime)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean first(DateTimeTemplate dateTime) {
        return this.major.first(dateTime) && this.minor.first(dateTime);
    }

    public String toString() {
        return this.major + "\n\t" + this.minor;
    }
}

