/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.event;

import javax.mail.Store;
import javax.mail.event.MailEvent;
import javax.mail.event.StoreListener;

public class StoreEvent
extends MailEvent {
    public static final int ALERT = 1;
    public static final int NOTICE = 2;
    protected int type;
    protected String message;
    private static final long serialVersionUID = 1938704919992515330L;

    public StoreEvent(Store store, int type, String message) {
        super(store);
        this.type = type;
        this.message = message;
    }

    public int getMessageType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void dispatch(Object listener) {
        ((StoreListener)listener).notification(this);
    }
}

