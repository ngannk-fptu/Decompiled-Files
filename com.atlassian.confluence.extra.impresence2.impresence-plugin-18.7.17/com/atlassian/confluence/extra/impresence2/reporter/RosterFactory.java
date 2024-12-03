/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jivesoftware.smack.XMPPConnection
 *  org.jivesoftware.smack.roster.Roster
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.springframework.stereotype.Component;

@Component
class RosterFactory {
    RosterFactory() {
    }

    public Roster createRoster(XMPPConnection connection) {
        return Roster.getInstanceFor((XMPPConnection)connection);
    }
}

