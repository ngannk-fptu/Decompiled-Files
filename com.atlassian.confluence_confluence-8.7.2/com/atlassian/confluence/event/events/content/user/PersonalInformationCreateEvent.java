/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.user;

import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.user.PersonalInformationEvent;
import com.atlassian.confluence.user.PersonalInformation;

public class PersonalInformationCreateEvent
extends PersonalInformationEvent {
    private static final long serialVersionUID = -3063827986092301251L;

    @Deprecated
    public PersonalInformationCreateEvent(Object src, PersonalInformation personalInformation) {
        super(src, personalInformation);
    }

    public PersonalInformationCreateEvent(Object source, PersonalInformation personalInformation, OperationContext<?> context) {
        super(source, personalInformation, context);
    }
}

