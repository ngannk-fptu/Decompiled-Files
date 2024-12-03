/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.user;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.user.PersonalInformation;

public class PersonalInformationEvent
extends ContentEvent {
    private static final long serialVersionUID = 3364962137845739767L;
    private final PersonalInformation personalInformation;

    @Deprecated
    public PersonalInformationEvent(Object src, PersonalInformation personalInformation) {
        this(src, personalInformation, false);
    }

    @Deprecated
    public PersonalInformationEvent(Object source, PersonalInformation personalInformation, boolean suppressNotifications) {
        super(source, suppressNotifications);
        this.personalInformation = personalInformation;
    }

    public PersonalInformationEvent(Object source, PersonalInformation personalInformation, OperationContext<?> context) {
        super(source, context);
        this.personalInformation = personalInformation;
    }

    public PersonalInformation getPersonalInformation() {
        return this.personalInformation;
    }

    @Override
    public ContentEntityObject getContent() {
        return this.getPersonalInformation();
    }
}

