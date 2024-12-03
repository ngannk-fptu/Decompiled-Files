/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.user;

import com.atlassian.confluence.event.events.content.user.PersonalInformationEvent;
import com.atlassian.confluence.user.PersonalInformation;

public class PersonalInformationRemoveEvent
extends PersonalInformationEvent {
    private static final long serialVersionUID = 4664846854378805359L;

    public PersonalInformationRemoveEvent(Object src, PersonalInformation personalInformation) {
        super(src, personalInformation);
    }
}

