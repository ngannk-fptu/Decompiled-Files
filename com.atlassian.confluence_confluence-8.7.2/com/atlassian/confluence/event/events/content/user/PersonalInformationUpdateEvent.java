/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.user;

import com.atlassian.confluence.event.events.content.user.PersonalInformationEvent;
import com.atlassian.confluence.user.PersonalInformation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PersonalInformationUpdateEvent
extends PersonalInformationEvent {
    private static final long serialVersionUID = 8897236059795136422L;
    private final PersonalInformation originalPersonalInformation;

    @Deprecated
    public PersonalInformationUpdateEvent(Object source, @NonNull PersonalInformation personalInformation, @Nullable PersonalInformation originalPersonalInformation) {
        this(source, personalInformation, originalPersonalInformation, false);
    }

    public PersonalInformationUpdateEvent(Object source, @NonNull PersonalInformation personalInformation, @Nullable PersonalInformation originalPersonalInformation, boolean suppressNotifications) {
        super(source, personalInformation, suppressNotifications);
        this.originalPersonalInformation = originalPersonalInformation;
    }

    public PersonalInformation getOriginalPersonalInformation() {
        return this.originalPersonalInformation;
    }
}

