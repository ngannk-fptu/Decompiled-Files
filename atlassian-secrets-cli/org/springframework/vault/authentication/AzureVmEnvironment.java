/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.util.Assert;

public class AzureVmEnvironment {
    private final String subscriptionId;
    private final String resourceGroupName;
    private final String vmName;
    private final String vmScaleSetName;

    public AzureVmEnvironment(String subscriptionId, String resourceGroupName, String vmName) {
        this(subscriptionId, resourceGroupName, vmName, "");
    }

    public AzureVmEnvironment(String subscriptionId, String resourceGroupName, String vmName, String vmScaleSetName) {
        Assert.notNull((Object)subscriptionId, "SubscriptionId must not be null");
        Assert.notNull((Object)resourceGroupName, "Resource group name must not be null");
        Assert.notNull((Object)vmName, "VM name must not be null");
        Assert.notNull((Object)vmScaleSetName, "VM Scale Set name must not be null");
        this.subscriptionId = subscriptionId;
        this.resourceGroupName = resourceGroupName;
        this.vmName = vmName;
        this.vmScaleSetName = vmScaleSetName;
    }

    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    public String getResourceGroupName() {
        return this.resourceGroupName;
    }

    public String getVmName() {
        return this.vmName;
    }

    public String getVmScaleSetName() {
        return this.vmScaleSetName;
    }
}

