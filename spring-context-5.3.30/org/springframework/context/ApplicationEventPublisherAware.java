/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.context.ApplicationEventPublisher;

public interface ApplicationEventPublisherAware
extends Aware {
    public void setApplicationEventPublisher(ApplicationEventPublisher var1);
}

