/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.agent.AbstractUserAgent;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.util.UidGenerator;

public class VFreeBusyUserAgent
extends AbstractUserAgent<VFreeBusy> {
    public VFreeBusyUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        super(prodId, organizer, uidGenerator);
    }

    public Calendar publish(VFreeBusy ... component) {
        Calendar published = this.wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    public Calendar request(VFreeBusy ... component) {
        Calendar request = this.wrap(Method.REQUEST, component);
        request.validate();
        return request;
    }

    @Override
    public Calendar delegate(Calendar request) {
        throw new UnsupportedOperationException("REQUEST delegation not supported by VFREEBUSY");
    }

    @Override
    public Calendar reply(Calendar request) {
        Calendar reply = this.transform(Method.REPLY, request);
        reply.validate();
        return reply;
    }

    @Override
    public Calendar add(VFreeBusy component) {
        throw new UnsupportedOperationException("Method [ADD] not supported by VFREEBUSY");
    }

    public Calendar cancel(VFreeBusy ... component) {
        throw new UnsupportedOperationException("Method [CANCEL] not supported by VFREEBUSY");
    }

    @Override
    public Calendar refresh(VFreeBusy component) {
        throw new UnsupportedOperationException("Method [REFRESH] not supported by VFREEBUSY");
    }

    @Override
    public Calendar counter(Calendar request) {
        throw new UnsupportedOperationException("Method [COUNTER] not supported by VFREEBUSY");
    }

    @Override
    public Calendar declineCounter(Calendar counter) {
        throw new UnsupportedOperationException("Method [DECLINECOUNTER] not supported by VFREEBUSY");
    }
}

