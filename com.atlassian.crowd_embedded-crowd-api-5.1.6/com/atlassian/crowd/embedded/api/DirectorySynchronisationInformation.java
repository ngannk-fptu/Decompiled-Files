/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import java.io.Serializable;
import javax.annotation.Nullable;

public class DirectorySynchronisationInformation
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final DirectorySynchronisationRoundInformation lastRound;
    private final DirectorySynchronisationRoundInformation activeRound;

    public DirectorySynchronisationInformation(@Nullable DirectorySynchronisationRoundInformation lastRound, @Nullable DirectorySynchronisationRoundInformation activeRound) {
        this.lastRound = lastRound;
        this.activeRound = activeRound;
    }

    @Nullable
    public DirectorySynchronisationRoundInformation getLastRound() {
        return this.lastRound;
    }

    @Nullable
    public DirectorySynchronisationRoundInformation getActiveRound() {
        return this.activeRound;
    }

    public boolean isSynchronising() {
        return this.activeRound != null;
    }
}

