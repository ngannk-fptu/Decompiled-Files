/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.profiling.ConfluenceMonitoringControl
 *  com.atlassian.confluence.util.profiling.TimerSnapshot
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 */
package com.atlassian.confluence.plugins.monitoring.rest;

import com.atlassian.confluence.plugins.monitoring.rest.AllTimerSnapshotsModel;
import com.atlassian.confluence.plugins.monitoring.rest.TimerSnapshotModel;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoringControl;
import com.atlassian.confluence.util.profiling.TimerSnapshot;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path(value="/timers")
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class TimerStatsResource {
    private final ConfluenceMonitoringControl control;

    public TimerStatsResource(ConfluenceMonitoringControl control) {
        this.control = control;
    }

    @DELETE
    @Produces(value={"application/json", "application/xml"})
    public void clearTimerStats() {
        this.control.clear();
    }

    @GET
    @Produces(value={"application/json", "application/xml"})
    public AllTimerSnapshotsModel getTimerStats() {
        List<TimerSnapshotModel> tsms = this.buildList();
        return new AllTimerSnapshotsModel(tsms);
    }

    private List<TimerSnapshotModel> buildList() {
        ArrayList<TimerSnapshotModel> result = new ArrayList<TimerSnapshotModel>();
        for (TimerSnapshot snapshot : this.control.snapshotTimers()) {
            if (snapshot.getInvocationCount() <= 0L) continue;
            TimerSnapshotModel model = new TimerSnapshotModel(snapshot);
            result.add(model);
        }
        return result;
    }
}

