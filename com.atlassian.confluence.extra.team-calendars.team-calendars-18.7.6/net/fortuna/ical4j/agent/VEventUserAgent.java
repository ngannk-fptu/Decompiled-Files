/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.agent.AbstractUserAgent;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.transform.RequestTransformer;
import net.fortuna.ical4j.util.UidGenerator;

public class VEventUserAgent
extends AbstractUserAgent<VEvent> {
    private final RequestTransformer delegateTransformer;

    public VEventUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        super(prodId, organizer, uidGenerator);
        this.delegateTransformer = new RequestTransformer(uidGenerator);
    }

    public Calendar publish(VEvent ... component) {
        Calendar published = this.wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    public Calendar request(VEvent ... component) {
        Calendar request = this.wrap(Method.REQUEST, component);
        request.validate();
        return request;
    }

    @Override
    public Calendar delegate(Calendar request) {
        Calendar delegated = this.delegateTransformer.transform(request);
        delegated.validate();
        return delegated;
    }

    @Override
    public Calendar reply(Calendar request) {
        Calendar reply = this.transform(Method.REPLY, request);
        reply.validate();
        return reply;
    }

    @Override
    public Calendar add(VEvent component) {
        Calendar add = this.wrap(Method.ADD, new VEvent[]{component});
        add.validate();
        return add;
    }

    public Calendar cancel(VEvent ... component) {
        Calendar cancel = this.wrap(Method.CANCEL, component);
        cancel.validate();
        return cancel;
    }

    @Override
    public Calendar refresh(VEvent component) {
        Calendar refresh = this.wrap(Method.REFRESH, new VEvent[]{component});
        refresh.validate();
        return refresh;
    }

    @Override
    public Calendar counter(Calendar request) {
        Calendar counter = this.transform(Method.COUNTER, request);
        counter.validate();
        return counter;
    }

    @Override
    public Calendar declineCounter(Calendar counter) {
        Calendar declineCounter = this.transform(Method.DECLINE_COUNTER, counter);
        declineCounter.validate();
        return declineCounter;
    }
}

