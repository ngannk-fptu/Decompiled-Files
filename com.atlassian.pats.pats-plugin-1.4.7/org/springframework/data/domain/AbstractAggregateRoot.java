/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.util.Assert;

public class AbstractAggregateRoot<A extends AbstractAggregateRoot<A>> {
    @Transient
    private final transient List<Object> domainEvents = new ArrayList<Object>();

    protected <T> T registerEvent(T event) {
        Assert.notNull(event, (String)"Domain event must not be null!");
        this.domainEvents.add(event);
        return event;
    }

    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }

    @DomainEvents
    protected Collection<Object> domainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    protected final A andEventsFrom(A aggregate) {
        Assert.notNull(aggregate, (String)"Aggregate must not be null!");
        this.domainEvents.addAll(((AbstractAggregateRoot)aggregate).domainEvents());
        return (A)this;
    }

    protected final A andEvent(Object event) {
        this.registerEvent(event);
        return (A)this;
    }
}

