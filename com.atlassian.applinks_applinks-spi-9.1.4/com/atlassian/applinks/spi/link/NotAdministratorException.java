/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.spi.link.ReciprocalActionException;

public class NotAdministratorException
extends ReciprocalActionException {
    public NotAdministratorException() {
        super("The supplied credentials do not belong to an user with administrative privileges in the remote application.");
    }
}

