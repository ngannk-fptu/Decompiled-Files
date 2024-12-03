/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.agent.AbstractUserAgent;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.util.UidGenerator;

public class VJournalUserAgent
extends AbstractUserAgent<VJournal> {
    public VJournalUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        super(prodId, organizer, uidGenerator);
    }

    public Calendar publish(VJournal ... component) {
        Calendar published = this.wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    public Calendar request(VJournal ... component) {
        throw new UnsupportedOperationException("Method [REQUEST] not supported by VJOURNAL");
    }

    @Override
    public Calendar delegate(Calendar request) {
        throw new UnsupportedOperationException("REQUEST delegation not supported by VJOURNAL");
    }

    @Override
    public Calendar reply(Calendar request) {
        throw new UnsupportedOperationException("Method [REPLY] not supported by VJOURNAL");
    }

    @Override
    public Calendar add(VJournal component) {
        Calendar add = this.wrap(Method.ADD, new VJournal[]{component});
        add.validate();
        return add;
    }

    public Calendar cancel(VJournal ... component) {
        Calendar cancel = this.wrap(Method.CANCEL, component);
        cancel.validate();
        return cancel;
    }

    @Override
    public Calendar refresh(VJournal component) {
        throw new UnsupportedOperationException("Method [REFRESH] not supported by VJOURNAL");
    }

    @Override
    public Calendar counter(Calendar request) {
        throw new UnsupportedOperationException("Method [COUNTER] not supported by VJOURNAL");
    }

    @Override
    public Calendar declineCounter(Calendar counter) {
        throw new UnsupportedOperationException("Method [DECLINECOUNTER] not supported by VJOURNAL");
    }
}

