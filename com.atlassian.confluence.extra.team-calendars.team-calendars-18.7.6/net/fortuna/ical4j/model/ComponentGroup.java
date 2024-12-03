/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.HasPropertyRule;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ComponentSequenceComparator;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

public class ComponentGroup<T extends Component> {
    private final ComponentList<T> components;
    private final Filter<T> componentFilter;

    public ComponentGroup(ComponentList<T> components, Uid uid) {
        this(components, uid, null);
    }

    public ComponentGroup(ComponentList<T> components, Uid uid, RecurrenceId recurrenceId) {
        this.components = components;
        Predicate componentPredicate = recurrenceId != null ? new HasPropertyRule(uid).and(new HasPropertyRule(recurrenceId)) : new HasPropertyRule(uid);
        this.componentFilter = new Filter(componentPredicate);
    }

    public ComponentList<T> getRevisions() {
        return (ComponentList)this.componentFilter.filter(this.components);
    }

    public T getLatestRevision() {
        ComponentList<T> revisions = this.getRevisions();
        revisions.sort(new ComponentSequenceComparator());
        Collections.reverse(revisions);
        return (T)((Component)revisions.iterator().next());
    }

    public PeriodList calculateRecurrenceSet(Period period) {
        PeriodList periods = new PeriodList();
        ArrayList<Component> replacements = new ArrayList<Component>();
        for (Component component2 : this.getRevisions()) {
            if (!component2.getProperties("RECURRENCE-ID").isEmpty()) {
                replacements.add(component2);
                continue;
            }
            periods = periods.add(component2.calculateRecurrenceSet(period));
        }
        PeriodList finalPeriods = periods;
        replacements.forEach(component -> {
            RecurrenceId recurrenceId = (RecurrenceId)component.getProperty("RECURRENCE-ID");
            List match = finalPeriods.stream().filter(p -> p.getStart().equals(recurrenceId.getDate())).collect(Collectors.toList());
            finalPeriods.removeAll(match);
            finalPeriods.addAll(component.calculateRecurrenceSet(period));
        });
        return periods;
    }
}

