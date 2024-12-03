/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.SetProperty;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SetPropertiesRuleProvider
implements AnnotationRuleProvider<SetProperty, Field, SetPropertiesRule> {
    private final Map<String, String> aliases = new HashMap<String, String>();

    @Override
    public void init(SetProperty annotation, Field element) {
        this.addAlias(annotation, element);
    }

    public void addAlias(SetProperty annotation, Field element) {
        String attributeName = annotation.attributeName();
        String propertyName = element.getName();
        if (attributeName.length() > 0) {
            this.aliases.put(attributeName, propertyName);
        } else {
            this.aliases.put(propertyName, propertyName);
        }
    }

    @Override
    public SetPropertiesRule get() {
        String[] attributeNames = new String[this.aliases.size()];
        String[] propertyNames = new String[this.aliases.size()];
        int i = 0;
        for (Map.Entry<String, String> alias : this.aliases.entrySet()) {
            attributeNames[i] = alias.getKey();
            propertyNames[i++] = alias.getValue();
        }
        return new SetPropertiesRule(attributeNames, propertyNames);
    }
}

