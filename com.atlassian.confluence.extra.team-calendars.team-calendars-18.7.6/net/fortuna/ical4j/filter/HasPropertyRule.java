/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.filter;

import java.util.function.Predicate;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;

public class HasPropertyRule<T extends Component>
implements Predicate<T> {
    private Property property;
    private boolean matchEquals;

    public HasPropertyRule(Property property) {
        this(property, false);
    }

    public HasPropertyRule(Property property, boolean matchEquals) {
        this.property = property;
        this.matchEquals = matchEquals;
    }

    @Override
    public final boolean test(Component component) {
        boolean match = false;
        PropertyList properties = component.getProperties(this.property.getName());
        for (Property p : properties) {
            if (this.matchEquals && this.property.equals(p)) {
                match = true;
                continue;
            }
            if (!this.property.getValue().equals(p.getValue())) continue;
            match = true;
        }
        return match;
    }
}

