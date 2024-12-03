/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.ResettableTableHiLoGenerator
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  com.atlassian.spring.container.ContainerManager
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.persister.entity.EntityPersister
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.event.EventUtils;
import com.atlassian.confluence.event.events.admin.ResetHibernateIdRangeEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.hibernate.ResettableTableHiLoGenerator;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import com.atlassian.spring.container.ContainerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetHiLoAfterImportListener
implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(ResetHiLoAfterImportListener.class);

    public void handleEvent(Event event) {
        Event underlying = EventUtils.extractWrappedEventOrOriginal(event);
        if (!(underlying instanceof ResetHibernateIdRangeEvent)) {
            return;
        }
        this.forceReadOfHiLo();
        log.info("Reset Hi/Lo ids in response to import event");
    }

    private void forceReadOfHiLo() {
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor)ContainerManager.getComponent((String)"sessionFactory");
        for (String entityName : sessionFactory.getMetamodel().getAllEntityNames()) {
            EntityPersister persister = sessionFactory.getMetamodel().entityPersister(entityName);
            IdentifierGenerator idGen = persister.getIdentifierGenerator();
            if (!(idGen instanceof ResettableTableHiLoGenerator)) continue;
            ((ResettableTableHiLoGenerator)idGen).reset();
        }
    }

    public Class[] getHandledEventClasses() {
        return new Class[]{ResetHibernateIdRangeEvent.class, ClusterEventWrapper.class};
    }
}

