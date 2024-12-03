/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import com.atlassian.migration.agent.service.catalogue.model.TransferStatusResponse;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.List;
import lombok.Generated;

public class AppContainer
extends AbstractContainer
implements Serializable {
    private static final long serialVersionUID = -8036463990988222217L;
    private final String sourceKey;
    private final String destinationKey;

    public AppContainer(String sourceKey, String destinationKey) {
        super(AbstractContainer.Type.App);
        this.sourceKey = sourceKey;
        this.destinationKey = destinationKey;
    }

    @VisibleForTesting
    public AppContainer(String sourceKey, String destinationKey, String containerId, AbstractContainer.ContainerStatus status, List<TransferStatusResponse> transfers) {
        super(AbstractContainer.Type.App, containerId, status, transfers, null);
        this.sourceKey = sourceKey;
        this.destinationKey = destinationKey;
    }

    @VisibleForTesting
    public AppContainer(String sourceKey, String destinationKey, String containerId, AbstractContainer.ContainerStatus status, List<TransferStatusResponse> transfers, String statusMessage) {
        super(AbstractContainer.Type.App, containerId, status, transfers, statusMessage);
        this.sourceKey = sourceKey;
        this.destinationKey = destinationKey;
    }

    @Generated
    public String getSourceKey() {
        return this.sourceKey;
    }

    @Generated
    public String getDestinationKey() {
        return this.destinationKey;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AppContainer)) {
            return false;
        }
        AppContainer other = (AppContainer)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$sourceKey = this.getSourceKey();
        String other$sourceKey = other.getSourceKey();
        if (this$sourceKey == null ? other$sourceKey != null : !this$sourceKey.equals(other$sourceKey)) {
            return false;
        }
        String this$destinationKey = this.getDestinationKey();
        String other$destinationKey = other.getDestinationKey();
        return !(this$destinationKey == null ? other$destinationKey != null : !this$destinationKey.equals(other$destinationKey));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AppContainer;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $sourceKey = this.getSourceKey();
        result = result * 59 + ($sourceKey == null ? 43 : $sourceKey.hashCode());
        String $destinationKey = this.getDestinationKey();
        result = result * 59 + ($destinationKey == null ? 43 : $destinationKey.hashCode());
        return result;
    }
}

