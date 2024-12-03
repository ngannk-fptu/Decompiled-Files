/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.crowd.embedded.admin.list;

import com.atlassian.crowd.embedded.admin.list.DirectoriesController;
import com.atlassian.crowd.embedded.admin.list.DirectoryListItemOperation;
import com.atlassian.crowd.embedded.admin.list.ListItemPosition;
import com.atlassian.crowd.embedded.admin.util.SimpleMessage;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public final class DirectoryListItem {
    private final long id;
    private final String name;
    private final Message type;
    private final boolean active;
    private final Map<Message, DirectoryListItemOperation> operations = new LinkedHashMap<Message, DirectoryListItemOperation>();
    private ListItemPosition position;
    private final DirectorySynchronisationInformation syncInfo;
    private boolean showLoggedIntoWarning;
    private final Locale locale;
    private final TimeZone timeZone;

    public DirectoryListItem(Directory directory, Set<DirectoriesController.Operation> allowedOperations, boolean showLoggedIntoWarning, Message type, ListItemPosition position, DirectorySynchronisationInformation syncInfo, Locale locale, TimeZone timeZone) {
        this.id = directory.getId();
        this.name = directory.getName();
        this.type = type;
        this.active = directory.isActive();
        this.showLoggedIntoWarning = showLoggedIntoWarning;
        this.position = position;
        this.syncInfo = syncInfo;
        for (DirectoriesController.Operation operation : allowedOperations) {
            this.operations.put(operation.getMessage(), new DirectoryListItemOperation(operation.getUrl(directory), operation.getMethodName(), operation.getHttpMethod()));
        }
        this.locale = locale;
        this.timeZone = timeZone;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Message getType() {
        return this.type;
    }

    public boolean isActive() {
        return this.active;
    }

    public Map<Message, DirectoryListItemOperation> getOperations() {
        return this.operations;
    }

    public boolean canMoveUp() {
        return this.position.canMoveUp();
    }

    public boolean canMoveDown() {
        return this.position.canMoveDown();
    }

    public boolean isSynchronisable() {
        return this.syncInfo != null;
    }

    public String getLastSyncTime() {
        if (this.syncInfo.getLastRound() == null) {
            return null;
        }
        if (this.syncInfo.getLastRound().getStartTime() == 0L) {
            return null;
        }
        DateFormat dateFormat = DateFormat.getDateTimeInstance(3, 3, this.locale);
        dateFormat.setTimeZone(this.timeZone);
        return dateFormat.format(this.syncInfo.getLastRound().getStartTime() + this.syncInfo.getLastRound().getDurationMs());
    }

    public long getLastSyncDuration() {
        return this.syncInfo.getLastRound() == null ? 0L : this.syncInfo.getLastRound().getDurationMs() / 1000L;
    }

    public boolean isSynchronising() {
        return this.syncInfo.isSynchronising();
    }

    public long getSecondsSinceSyncStarted() {
        if (this.syncInfo == null || !this.syncInfo.isSynchronising()) {
            return 0L;
        }
        return (System.currentTimeMillis() - this.syncInfo.getActiveRound().getStartTime()) / 1000L;
    }

    public boolean isShowLoggedIntoWarning() {
        return this.showLoggedIntoWarning;
    }

    public void setShowLoggedIntoWarning(boolean showLoggedIntoWarning) {
        this.showLoggedIntoWarning = showLoggedIntoWarning;
    }

    public Message getSyncStatusMessage() {
        DirectorySynchronisationRoundInformation syncRound;
        DirectorySynchronisationRoundInformation directorySynchronisationRoundInformation = syncRound = this.isSynchronising() ? this.syncInfo.getActiveRound() : this.syncInfo.getLastRound();
        if (syncRound == null) {
            return null;
        }
        String statusKey = syncRound.getStatusKey();
        if (statusKey == null) {
            return null;
        }
        Serializable[] params = syncRound.getStatusParameters().toArray(new Serializable[0]);
        return SimpleMessage.instance("embedded.crowd." + statusKey, params);
    }
}

