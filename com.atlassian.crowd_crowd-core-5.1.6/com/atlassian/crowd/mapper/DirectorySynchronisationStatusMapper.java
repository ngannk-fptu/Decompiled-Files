/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 */
package com.atlassian.crowd.mapper;

import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import java.util.Optional;

public final class DirectorySynchronisationStatusMapper {
    public static DirectorySynchronisationRoundInformation mapDirectoryStatusToRoundInformation(DirectorySynchronisationStatus st) {
        return DirectorySynchronisationRoundInformation.builder().setStartTime(st.getStartTimestamp()).setDurationMs(DirectorySynchronisationStatusMapper.mapEndAndStartToDuration(st).longValue()).setStatusKey(st.getStatus().getI18Key()).setStatusParameters(st.getStatus().unmarshallParams(st.getStatusParameters())).setNodeId(st.getNodeId()).setNodeName(st.getNodeName()).setIncrementalSyncError(st.getIncrementalSyncError()).setFullSyncError(st.getFullSyncError()).build();
    }

    private static Long mapEndAndStartToDuration(DirectorySynchronisationStatus st) {
        return Optional.ofNullable(st.getEndTimestamp()).map(end -> end - st.getStartTimestamp()).orElse(System.currentTimeMillis() - st.getStartTimestamp());
    }

    private DirectorySynchronisationStatusMapper() {
    }
}

