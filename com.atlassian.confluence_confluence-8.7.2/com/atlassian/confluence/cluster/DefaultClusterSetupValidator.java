/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterSetupValidator;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.atlassian.confluence.util.ClusterUtils;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.confluence.validation.MessageHolder;
import io.atlassian.fugue.Either;
import java.io.File;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class DefaultClusterSetupValidator
implements ClusterSetupValidator {
    private static final Object[] NO_ARGS = new Object[0];
    private final MessageHolder result;

    public DefaultClusterSetupValidator(MessageHolder holder) {
        this.result = holder;
    }

    @Override
    public MessageHolder getResult() {
        return this.result;
    }

    @Override
    public ClusterSetupValidator validateClusterJoinMethod(String joinMethod) {
        if (Arrays.stream(ClusterJoinConfig.ClusterJoinType.values()).anyMatch(clusterJoinType -> clusterJoinType.getText().equals(joinMethod))) {
            return this;
        }
        this.result.addActionError("invalid.cluster.join.method", joinMethod);
        return this;
    }

    @Override
    public ClusterSetupValidator validateMulticastClusterJoinConfig(Boolean generateAddress, String clusterAddress) {
        if (Boolean.TRUE.equals(generateAddress)) {
            return this;
        }
        try {
            Either<Message, MulticastClusterJoinConfig> joinConfig = MulticastClusterJoinConfig.createForAddress(ClusterUtils.addressFromIpString(clusterAddress));
            if (joinConfig.isLeft()) {
                Message message = (Message)joinConfig.left().get();
                this.result.addActionError(message.getKey(), message.getArguments());
            }
        }
        catch (IllegalArgumentException e) {
            this.result.addActionError("error.cluster.address.not.valid", clusterAddress);
        }
        return this;
    }

    @Override
    public ClusterSetupValidator validateNetworkInterface(String networkInterface) {
        return this;
    }

    @Override
    public ClusterSetupValidator validateClusterName(String clusterName) {
        if (StringUtils.isEmpty((CharSequence)clusterName)) {
            this.result.addActionError("no.cluster.name", NO_ARGS);
        }
        return this;
    }

    @Override
    public ClusterSetupValidator validateClusterHome(String clusterHome) {
        if (StringUtils.isEmpty((CharSequence)clusterHome)) {
            this.result.addActionError("error.cluster.home.not.defined", NO_ARGS);
            return this;
        }
        File clusterHomeDir = new File(clusterHome);
        if (!clusterHomeDir.exists()) {
            this.result.addActionError("error.cluster.home.not.found", clusterHome);
            return this;
        }
        if (!clusterHomeDir.isDirectory()) {
            this.result.addActionError("error.cluster.home.not.directory", clusterHome);
            return this;
        }
        if (!clusterHomeDir.canRead()) {
            this.result.addActionError("error.cluster.home.not.readable", clusterHome);
        }
        return this;
    }

    @Override
    public ClusterSetupValidator validateTCPIPClusterJoinConfig(String clusterPeersString) {
        Either<Message, TCPIPClusterJoinConfig> joinConfig = TCPIPClusterJoinConfig.createForPeers(clusterPeersString);
        if (joinConfig.isLeft()) {
            Message message = (Message)joinConfig.left().get();
            this.result.addActionError(message.getKey(), message.getArguments());
        }
        return this;
    }

    @Override
    public ClusterSetupValidator validateAWSJoinConfig(String accessKey, String secretKey, String iamRole, String region, String hostHeader, String securityGroupName, String tagKey, String tagValue) {
        Either<Message, AWSClusterJoinConfig> joinConfig = AWSClusterJoinConfig.createForKeys(accessKey, secretKey, iamRole, region, hostHeader, securityGroupName, tagKey, tagValue);
        if (joinConfig.isLeft()) {
            Message message = (Message)joinConfig.left().get();
            this.result.addActionError(message.getKey(), message.getArguments());
        }
        return this;
    }

    @Override
    public void validateKubernetesJoinConfig() {
    }
}

