/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.context.ApplicationEventPublisher;

public interface ApplicationEventPublisherAware
extends Aware {
    public void setApplicationEventPublisher(ApplicationEventPublisher var1);
}

