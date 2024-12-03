/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.filter;

import java.util.function.Predicate;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;

public class PeriodRule<T extends Component>
implements Predicate<T> {
    private Period period;

    public PeriodRule(Period period) {
        this.period = period;
    }

    @Override
    public final boolean test(Component component) {
        PeriodList recurrenceSet = component.calculateRecurrenceSet(this.period);
        return !recurrenceSet.isEmpty();
    }
}

