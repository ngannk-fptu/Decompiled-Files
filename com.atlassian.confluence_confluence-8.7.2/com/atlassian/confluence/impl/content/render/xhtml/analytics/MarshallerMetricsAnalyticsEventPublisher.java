/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Effect
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.RenderingEventPublisher;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkMarshallerMetricsKey;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulatorStack;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAnalyticsEventFactory;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsConsumer;
import com.atlassian.confluence.macro.count.MacroMetricsKey;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Effect;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarshallerMetricsAnalyticsEventPublisher
implements RenderingEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(MarshallerMetricsAnalyticsEventPublisher.class);
    private final EventPublisher eventPublisher;
    private final Collection<EventSender> eventSenders = new CopyOnWriteArrayList<EventSender>();

    public MarshallerMetricsAnalyticsEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
    }

    @PostConstruct
    public void registerCallbacks() {
        this.register("confluence.render.macro", MacroMetricsKey.class);
        this.register("confluence.render.link", ViewLinkMarshallerMetricsKey.class);
    }

    private void register(String eventName, Class<? extends MarshallerMetricsAccumulationKey> accumulationKeyType) {
        this.eventSenders.add(new EventSender(accumulationKeyType, eventName));
    }

    @Override
    public void publish(Object src, ConversionContext context) {
        try {
            ContentEntityObject entity = context.getEntity();
            if (!(context instanceof PageTemplateContext)) {
                for (EventSender handler : this.eventSenders) {
                    handler.publishEvents(context);
                }
            }
        }
        catch (Exception e) {
            log.error("Failed to publish marshaller metrics render event: {}", (Object)e.getMessage());
            log.debug("Unable finish publishing marshaller metrics render events.", (Throwable)e);
        }
    }

    private class EventSender {
        private final Class<? extends MarshallerMetricsAccumulationKey> accumulationKeyType;
        private final String eventName;

        EventSender(Class<? extends MarshallerMetricsAccumulationKey> accumulationKeyType, String eventName) {
            this.accumulationKeyType = (Class)Preconditions.checkNotNull(accumulationKeyType);
            this.eventName = (String)Preconditions.checkNotNull((Object)eventName);
        }

        void publishEvents(ConversionContext context) {
            Set<MarshallerMetricsConsumer> consumers = context.getMarshallerMetricsConsumers();
            MarshallerMetricsAccumulatorStack.forEachMetricsSnapshot(context, (Predicate<? super MarshallerMetricsAccumulationKey>)Predicates.instanceOf(this.accumulationKeyType), (Effect<MarshallerMetrics>)((Effect)snapshot -> {
                consumers.forEach(consumer -> consumer.accept((MarshallerMetrics)snapshot));
                MarshallerMetricsAnalyticsEventPublisher.this.eventPublisher.publish((Object)MarshallerMetricsAnalyticsEventFactory.newMarshallerMetricsAnalyticsEvent(context, snapshot, this.eventName, snapshot.getAccumulationKey().getAccumulationKeyAsString()));
            }));
        }
    }
}

