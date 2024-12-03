/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.jmx;

import javax.management.openmbean.CompositeData;

public interface EventListenerMBean {
    public String getClassName();

    public String getToString();

    public String getInitStackTrace();

    public int getEventTypes();

    public String getAbsPath();

    public boolean isDeep();

    public String[] getUuid();

    public String[] getNodeTypeName();

    public boolean isNoLocal();

    public long getEventDeliveries();

    public long getEventDeliveriesPerHour();

    public long getMicrosecondsPerEventDelivery();

    public long getEventsDelivered();

    public long getEventsDeliveredPerHour();

    public long getMicrosecondsPerEventDelivered();

    public double getRatioOfTimeSpentProcessingEvents();

    public double getEventConsumerTimeRatio();

    public boolean isUserInfoAccessedWithoutExternalsCheck();

    public boolean isUserInfoAccessedFromExternalEvent();

    public boolean isDateAccessedWithoutExternalsCheck();

    public boolean isDateAccessedFromExternalEvent();

    public long getQueueBacklogMillis();

    public CompositeData getQueueLength();

    public CompositeData getEventCount();

    public CompositeData getEventConsumerTime();

    public CompositeData getEventProducerTime();
}

