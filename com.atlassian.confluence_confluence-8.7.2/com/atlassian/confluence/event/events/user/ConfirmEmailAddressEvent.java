/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.user.UserSignupEvent;
import com.atlassian.user.User;

public class ConfirmEmailAddressEvent
extends UserSignupEvent {
    private static final long serialVersionUID = 4487967229749618017L;

    public ConfirmEmailAddressEvent(Object src, User user) {
        super(src, user);
    }
}

