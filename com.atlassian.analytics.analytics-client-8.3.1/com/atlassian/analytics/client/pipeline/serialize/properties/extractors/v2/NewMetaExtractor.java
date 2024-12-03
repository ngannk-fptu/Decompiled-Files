/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.v2.AnalyticsEvent
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.v2;

import com.atlassian.analytics.api.events.v2.AnalyticsEvent;
import java.util.Optional;
import java.util.regex.Pattern;

public class NewMetaExtractor {
    private Pattern FIND_TRAILING_EVENT = Pattern.compile("Event$");
    private Pattern FIND_CAPITAL_LETTERS = Pattern.compile("(?<!^)[A-Z]");

    public String getEventName(Object event) {
        return Optional.of(event).filter(eventInstance -> eventInstance instanceof AnalyticsEvent).map(eventInstance -> (AnalyticsEvent)eventInstance).flatMap(AnalyticsEvent::getEventName).orElseGet(() -> this.calculateEventName(event));
    }

    private String calculateEventName(Object analyticsEvent) {
        Class<?> clazz = analyticsEvent.getClass();
        return this.getCleanName(clazz);
    }

    private String getCleanName(Class clazz) {
        String className = clazz.getSimpleName();
        String classNameWithoutTrailingEvent = this.FIND_TRAILING_EVENT.matcher(className).replaceAll("");
        String classNameInSnakeCase = this.FIND_CAPITAL_LETTERS.matcher(classNameWithoutTrailingEvent).replaceAll("-$0").toLowerCase();
        return classNameInSnakeCase;
    }
}

