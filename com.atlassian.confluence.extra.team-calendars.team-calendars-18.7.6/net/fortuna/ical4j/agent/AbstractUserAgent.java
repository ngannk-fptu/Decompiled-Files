/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.agent;

import java.util.HashMap;
import java.util.Map;
import net.fortuna.ical4j.agent.UserAgent;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.transform.AddTransformer;
import net.fortuna.ical4j.transform.CancelTransformer;
import net.fortuna.ical4j.transform.CounterTransformer;
import net.fortuna.ical4j.transform.DeclineCounterTransformer;
import net.fortuna.ical4j.transform.PublishTransformer;
import net.fortuna.ical4j.transform.RefreshTransformer;
import net.fortuna.ical4j.transform.ReplyTransformer;
import net.fortuna.ical4j.transform.RequestTransformer;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.UidGenerator;

public abstract class AbstractUserAgent<T extends CalendarComponent>
implements UserAgent<T> {
    private final ProdId prodId;
    private final Map<Method, Transformer<Calendar>> transformers;

    public AbstractUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        this.prodId = prodId;
        this.transformers = new HashMap<Method, Transformer<Calendar>>();
        this.transformers.put(Method.PUBLISH, new PublishTransformer(organizer, uidGenerator, true));
        this.transformers.put(Method.REQUEST, new RequestTransformer(organizer, uidGenerator));
        this.transformers.put(Method.ADD, new AddTransformer(organizer, uidGenerator));
        this.transformers.put(Method.CANCEL, new CancelTransformer(organizer, uidGenerator));
        this.transformers.put(Method.REPLY, new ReplyTransformer(uidGenerator));
        this.transformers.put(Method.REFRESH, new RefreshTransformer(uidGenerator));
        this.transformers.put(Method.COUNTER, new CounterTransformer(uidGenerator));
        this.transformers.put(Method.DECLINE_COUNTER, new DeclineCounterTransformer(organizer, uidGenerator));
    }

    @SafeVarargs
    protected final Calendar wrap(Method method, T ... component) {
        Calendar calendar = Calendars.wrap(component);
        calendar.getProperties().add(this.prodId);
        calendar.getProperties().add(Version.VERSION_2_0);
        return this.transform(method, calendar);
    }

    protected Calendar transform(Method method, Calendar calendar) {
        Transformer<Calendar> transformer = this.transformers.get(method);
        transformer.transform(calendar);
        return calendar;
    }

    public ProdId getProdId() {
        return this.prodId;
    }
}

