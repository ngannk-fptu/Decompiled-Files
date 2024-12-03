/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545ComponentRule;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545PropertyRule;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545Rule;

public class RuleManager {
    private static final Set<Rfc5545PropertyRule<? extends Property>> PROPERTY_RULES = new LinkedHashSet<Rfc5545PropertyRule<? extends Property>>();
    private static final Set<Rfc5545ComponentRule<? extends Component>> COMPONENT_RULES = new LinkedHashSet<Rfc5545ComponentRule<? extends Component>>();

    public static void applyTo(Property element) {
        for (Rfc5545PropertyRule<Property> rule : RuleManager.getSupportedRulesFor(element)) {
            rule.applyTo(element);
        }
    }

    public static void applyTo(Component element) {
        for (Rfc5545ComponentRule<Component> rule : RuleManager.getSupportedRulesFor(element)) {
            rule.applyTo(element);
        }
    }

    private static Set<Rfc5545PropertyRule<Property>> getSupportedRulesFor(Property element) {
        if (element == null) {
            throw new NullPointerException();
        }
        LinkedHashSet<Rfc5545PropertyRule<Property>> rules = new LinkedHashSet<Rfc5545PropertyRule<Property>>(1);
        for (Rfc5545Rule rfc5545Rule : PROPERTY_RULES) {
            if (!rfc5545Rule.getSupportedType().isInstance(element)) continue;
            rules.add((Rfc5545PropertyRule)rfc5545Rule);
        }
        return rules;
    }

    private static Set<Rfc5545ComponentRule<Component>> getSupportedRulesFor(Component element) {
        if (element == null) {
            throw new NullPointerException();
        }
        LinkedHashSet<Rfc5545ComponentRule<Component>> rules = new LinkedHashSet<Rfc5545ComponentRule<Component>>(1);
        for (Rfc5545Rule rfc5545Rule : COMPONENT_RULES) {
            if (!rfc5545Rule.getSupportedType().isInstance(element)) continue;
            rules.add((Rfc5545ComponentRule)rfc5545Rule);
        }
        return rules;
    }

    static {
        for (Rfc5545PropertyRule rfc5545PropertyRule : ServiceLoader.load(Rfc5545PropertyRule.class)) {
            if (rfc5545PropertyRule.getSupportedType() == null) {
                throw new NullPointerException();
            }
            PROPERTY_RULES.add(rfc5545PropertyRule);
        }
        for (Rfc5545ComponentRule rfc5545ComponentRule : ServiceLoader.load(Rfc5545ComponentRule.class)) {
            if (rfc5545ComponentRule.getSupportedType() == null) {
                throw new NullPointerException();
            }
            COMPONENT_RULES.add(rfc5545ComponentRule);
        }
    }
}

