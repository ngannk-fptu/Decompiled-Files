/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.util.profiling.UtilTimerStack
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.ical4j.VEventMapper;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.ArrayList;
import java.util.List;
import net.fortuna.ical4j.model.component.VEvent;

public class EventEntitiesToEventsTransformer {
    private final TransactionTemplate transactionTemplate;
    private final VEventMapper vEventMapper;

    public EventEntitiesToEventsTransformer(TransactionTemplate transactionTemplate, VEventMapper vEventMapper) {
        this.transactionTemplate = transactionTemplate;
        this.vEventMapper = vEventMapper;
    }

    public List<VEvent> transform(List<EventEntity> eventEntities) throws Exception {
        UtilTimerStack.push((String)"EventEntitiesToEventsTransformer.transform");
        ArrayList<VEvent> vEvents = new ArrayList<VEvent>();
        vEvents.addAll(this.vEventMapper.toVEvents(eventEntities.toArray(new EventEntity[0])));
        UtilTimerStack.pop((String)"EventEntitiesToEventsTransformer.transform");
        return vEvents;
    }
}

