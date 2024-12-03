/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.validation.MessageHolder;

public interface ClusterSetupValidator {
    public MessageHolder getResult();

    public ClusterSetupValidator validateClusterName(String var1);

    public ClusterSetupValidator validateClusterHome(String var1);

    public ClusterSetupValidator validateTCPIPClusterJoinConfig(String var1);

    public ClusterSetupValidator validateClusterJoinMethod(String var1);

    public ClusterSetupValidator validateMulticastClusterJoinConfig(Boolean var1, String var2);

    public ClusterSetupValidator validateNetworkInterface(String var1);

    public ClusterSetupValidator validateAWSJoinConfig(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8);

    public void validateKubernetesJoinConfig();
}

