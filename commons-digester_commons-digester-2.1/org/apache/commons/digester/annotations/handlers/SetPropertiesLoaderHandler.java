/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.handlers;

import java.lang.reflect.Field;
import org.apache.commons.digester.annotations.DigesterLoaderHandler;
import org.apache.commons.digester.annotations.FromAnnotationsRuleSet;
import org.apache.commons.digester.annotations.providers.SetPropertiesRuleProvider;
import org.apache.commons.digester.annotations.rules.SetProperty;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SetPropertiesLoaderHandler
implements DigesterLoaderHandler<SetProperty, Field> {
    @Override
    public void handle(SetProperty annotation, Field element, FromAnnotationsRuleSet ruleSet) {
        SetPropertiesRuleProvider ruleProvider = ruleSet.getProvider(annotation.pattern(), SetPropertiesRuleProvider.class);
        if (ruleProvider == null) {
            ruleProvider = new SetPropertiesRuleProvider();
            ruleSet.addRuleProvider(annotation.pattern(), ruleProvider);
        }
        ruleProvider.addAlias(annotation, element);
    }
}

