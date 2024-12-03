/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.Participant;
import com.atlassian.confluence.notifications.PayloadTransformer;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.RenderContextProvider;
import com.atlassian.confluence.notifications.impl.NotificationDescriptorLocator;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTemplateDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTransformerDescriptor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class DefaultNotificationDescriptorLocator
implements NotificationDescriptorLocator {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private final PluginAccessor pluginAccessor;
    private final PluginModuleTracker<PayloadTransformer, NotificationTransformerDescriptor> payloadTransformerDescriptorTracker;
    private final PluginModuleTracker<RenderContextProvider, NotificationTemplateDescriptor> templateDescriptorTracker;

    public DefaultNotificationDescriptorLocator(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.pluginAccessor = pluginAccessor;
        this.payloadTransformerDescriptorTracker = DefaultPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, NotificationTransformerDescriptor.class);
        this.templateDescriptorTracker = DefaultPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, NotificationTemplateDescriptor.class);
    }

    @Override
    public <PAYLOAD extends NotificationPayload> Maybe<NotificationDescriptor<PAYLOAD>> findNotificationDescriptor(PAYLOAD payload, ModuleCompleteKey suppliedNotificationDescriptorKey) {
        List allNotificationDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(NotificationDescriptor.class);
        Iterator matchingPayloadModuleDescriptors = Iterables.filter((Iterable)allNotificationDescriptors, (Predicate)new MatchingKeyAndPayloadClassFilter(suppliedNotificationDescriptorKey, payload.getClass())).iterator();
        if (matchingPayloadModuleDescriptors.hasNext()) {
            return Option.some((Object)((Object)((NotificationDescriptor)((Object)matchingPayloadModuleDescriptors.next()))));
        }
        return MaybeNot.becauseOf((String)"Could not locate a [%s] for payload type [%s].", (Object[])new Object[]{NotificationDescriptor.class.getName(), payload.getClass().getName()});
    }

    @Override
    public Iterable<NotificationTransformerDescriptor> findPayloadTransformerDescriptors(Object source) {
        Iterable transformerDescriptorIterable = Iterables.filter((Iterable)this.payloadTransformerDescriptorTracker.getModuleDescriptors(), descriptor -> descriptor.transforms(source.getClass()));
        return new Ordering<NotificationTransformerDescriptor>(){

            public int compare(NotificationTransformerDescriptor d1, NotificationTransformerDescriptor d2) {
                return d1.getWeight() - d2.getWeight();
            }
        }.reverse().sortedCopy(transformerDescriptorIterable);
    }

    @Override
    public <P extends Participant> Iterable<? extends AbstractParticipantDescriptor<P>> findParticipantDescriptors(Class<P> participantModuleClass) {
        List allDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(AbstractParticipantDescriptor.class);
        return Iterables.transform((Iterable)Iterables.filter((Iterable)allDescriptors, input -> {
            Class actualModuleClass = input.getModuleClass();
            if (actualModuleClass == null) {
                log.warnOrDebug("Module class for descriptor [%s] is null", input.getCompleteKey());
                return false;
            }
            return participantModuleClass.isAssignableFrom(actualModuleClass);
        }), new PassThroughFunction());
    }

    @Override
    public Maybe<NotificationTemplateDescriptor> findTemplateDescriptor(Notification notification, String mediumKey) {
        Iterator templateDescriptors = Iterables.filter((Iterable)this.templateDescriptorTracker.getModuleDescriptors(), notificationTemplateDescriptor -> notificationTemplateDescriptor.getMedium().equals(mediumKey) && notification.getKey().equals((Object)notificationTemplateDescriptor.getNotificationKey()) && ((RenderContextProvider)notificationTemplateDescriptor.getModule()).getPayloadType().isAssignableFrom(notification.getPayload().getClass())).iterator();
        if (!templateDescriptors.hasNext()) {
            return MaybeNot.becauseOf((String)"Could not locate a [%s] for medium [%s].", (Object[])new Object[]{NotificationTemplateDescriptor.class.getName(), mediumKey});
        }
        return Option.some((Object)((Object)((NotificationTemplateDescriptor)((Object)templateDescriptors.next()))));
    }

    private static class PassThroughFunction<P extends Participant>
    implements Function<AbstractParticipantDescriptor, AbstractParticipantDescriptor<P>> {
        private PassThroughFunction() {
        }

        public AbstractParticipantDescriptor<P> apply(@Nullable AbstractParticipantDescriptor input) {
            return input;
        }
    }

    private static class MatchingKeyAndPayloadClassFilter
    implements Predicate<NotificationDescriptor> {
        private final ModuleCompleteKey suppliedNotificationDescriptorKey;
        private final Class payloadClass;

        public MatchingKeyAndPayloadClassFilter(ModuleCompleteKey suppliedNotificationDescriptorKey, Class payloadClass) {
            this.suppliedNotificationDescriptorKey = suppliedNotificationDescriptorKey;
            this.payloadClass = payloadClass;
        }

        public boolean apply(@Nullable NotificationDescriptor descriptor) {
            return this.suppliedNotificationDescriptorKey.getCompleteKey().equals(descriptor.getCompleteKey()) && descriptor.getPayloadType().isAssignableFrom(this.payloadClass);
        }
    }
}

