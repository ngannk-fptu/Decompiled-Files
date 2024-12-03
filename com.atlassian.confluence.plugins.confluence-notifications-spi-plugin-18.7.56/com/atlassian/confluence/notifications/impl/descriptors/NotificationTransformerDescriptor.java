/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.dom4j.Element
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.confluence.notifications.PayloadTransformer;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.util.concurrent.ResettableLazyReference;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.dom4j.Element;

public class NotificationTransformerDescriptor
extends AbstractParticipantDescriptor<PayloadTransformer> {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private List<Class> scopeClasses;
    private final ResettableLazyReference<Predicate<Class>> transformerPredicate = new ResettableLazyReference<Predicate<Class>>(){

        protected Predicate<Class> create() throws Exception {
            return new Predicate<Class>(){

                public boolean apply(@Nullable Class notificationCandidate) {
                    if (!((PayloadTransformer)NotificationTransformerDescriptor.this.getModule()).getSourceType().isAssignableFrom(notificationCandidate)) {
                        log.onlyTrace("[%s] doesn't want to consume [%s].", ((PayloadTransformer)NotificationTransformerDescriptor.this.getModule()).getClass().getName(), notificationCandidate.getName());
                        return false;
                    }
                    if (!NotificationTransformerDescriptor.this.scopeClasses.isEmpty()) {
                        for (Class scopeClass : NotificationTransformerDescriptor.this.scopeClasses) {
                            if (!scopeClass.isAssignableFrom(notificationCandidate)) continue;
                            return true;
                        }
                        log.onlyTrace("[%s] doesn't want to consume [%s] because it's scoped to the following classes [%s].", ((PayloadTransformer)NotificationTransformerDescriptor.this.getModule()).getClass().getName(), notificationCandidate.getName(), ToStringBuilder.reflectionToString((Object)NotificationTransformerDescriptor.this.scopeClasses.toArray(), (ToStringStyle)ToStringStyle.SIMPLE_STYLE));
                        return false;
                    }
                    return true;
                }
            };
        }
    };
    private ModuleCompleteKey forNotificationDescriptor;
    private int weight;

    public NotificationTransformerDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public NotificationTransformerDescriptor(ModuleFactory moduleFactory, ModuleCompleteKey forNotificationDescriptor) {
        super(moduleFactory);
        this.forNotificationDescriptor = forNotificationDescriptor;
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        if (this.forNotificationDescriptor == null) {
            pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@for-notification").withError("Missing attribute 'for-notification', which should contain the module complete key of the notification descriptor that this descriptor is tied to")});
        }
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element descriptor) throws PluginParseException {
        super.init(plugin, descriptor);
        ImmutableList.Builder scopeClassesBuilder = ImmutableList.builder();
        for (Element scopeDescriptor : descriptor.elements("scope")) {
            scopeClassesBuilder.add((Object)this.loadScopeClass(scopeDescriptor));
        }
        this.scopeClasses = scopeClassesBuilder.build();
        if (this.forNotificationDescriptor == null) {
            try {
                this.forNotificationDescriptor = new ModuleCompleteKey(descriptor.attributeValue("for-notification"));
            }
            catch (IllegalArgumentException ignored) {
                this.forNotificationDescriptor = new ModuleCompleteKey(this.getPluginKey(), descriptor.attributeValue("for-notification"));
            }
        }
        this.weight = descriptor.attributeValue("weight") == null ? 0 : Integer.parseInt(descriptor.attributeValue("weight"));
    }

    private Class loadScopeClass(Element descriptor) {
        String scopeClassName = descriptor.attributeValue("class");
        Preconditions.checkNotNull((Object)scopeClassName, (String)"Descriptor [%s] in plugin [%] doesn't contain a class attribute.", (Object)descriptor, (Object)this.plugin);
        return this.loadClassUnchecked(scopeClassName);
    }

    public ModuleCompleteKey keyForNotificationDescriptor() {
        return this.forNotificationDescriptor;
    }

    public boolean transforms(Class notificationCandidate) {
        return ((Predicate)this.transformerPredicate.get()).apply((Object)notificationCandidate);
    }

    public int getWeight() {
        return this.weight;
    }
}

