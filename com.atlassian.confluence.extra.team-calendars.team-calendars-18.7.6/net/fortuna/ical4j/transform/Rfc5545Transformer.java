/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.transform.rfc5545.RuleManager;

public class Rfc5545Transformer
implements Transformer<Calendar> {
    @Override
    public Calendar transform(Calendar object) {
        Rfc5545Transformer.conformPropertiesToRfc5545(object.getProperties());
        for (Component component : object.getComponents()) {
            CountableProperties.removeExceededPropertiesForComponent(component);
            Rfc5545Transformer.conformComponentToRfc5545(component);
            Rfc5545Transformer.conformPropertiesToRfc5545(component.getProperties());
            for (Method m : component.getClass().getDeclaredMethods()) {
                if (!ComponentList.class.isAssignableFrom(m.getReturnType()) || !m.getName().startsWith("get")) continue;
                try {
                    List components = (List)m.invoke((Object)component, new Object[0]);
                    for (Component c : components) {
                        Rfc5545Transformer.conformComponentToRfc5545(c);
                        Rfc5545Transformer.conformPropertiesToRfc5545(c.getProperties());
                    }
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return object;
    }

    private static void conformPropertiesToRfc5545(List<Property> properties) {
        for (Property property : properties) {
            RuleManager.applyTo(property);
        }
    }

    private static void conformComponentToRfc5545(Component component) {
        RuleManager.applyTo(component);
    }

    private static enum CountableProperties {
        STATUS("STATUS", 1);

        private int maxApparitionNumber;
        private String name;

        private CountableProperties(String name, int maxApparitionNumber) {
            this.maxApparitionNumber = maxApparitionNumber;
            this.name = name;
        }

        protected void limitApparitionsNumberIn(Component component) {
            PropertyList propertyList = component.getProperties(this.name);
            if (propertyList.size() <= this.maxApparitionNumber) {
                return;
            }
            int toRemove = propertyList.size() - this.maxApparitionNumber;
            for (int i = 0; i < toRemove; ++i) {
                component.getProperties().remove((Property)propertyList.get(i));
            }
        }

        private static void removeExceededPropertiesForComponent(Component component) {
            for (CountableProperties cp : CountableProperties.values()) {
                cp.limitApparitionsNumberIn(component);
            }
        }
    }
}

