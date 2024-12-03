/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.cluster.impl.TcpIpJoiner
 *  com.hazelcast.config.AwsConfig
 *  com.hazelcast.instance.Node
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.util.ExceptionUtil
 */
package com.hazelcast.cluster.impl;

import com.hazelcast.aws.AWSClient;
import com.hazelcast.aws.AwsConfig;
import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.instance.Node;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;

@Deprecated
public class TcpIpJoinerOverAWS
extends TcpIpJoiner {
    private final AWSClient aws;
    private final ILogger logger;

    public TcpIpJoinerOverAWS(Node node) {
        super(node);
        this.logger = node.getLogger(((Object)((Object)this)).getClass());
        AwsConfig awsConfig = TcpIpJoinerOverAWS.fromDeprecatedAwsConfig(node.getConfig().getNetworkConfig().getJoin().getAwsConfig());
        this.aws = new AWSClient(awsConfig);
    }

    static AwsConfig fromDeprecatedAwsConfig(com.hazelcast.config.AwsConfig awsConfig) {
        return AwsConfig.builder().setAccessKey(awsConfig.getAccessKey()).setSecretKey(awsConfig.getSecretKey()).setRegion(awsConfig.getRegion()).setSecurityGroupName(awsConfig.getSecurityGroupName()).setTagKey(awsConfig.getTagKey()).setTagValue(awsConfig.getTagValue()).setHostHeader(awsConfig.getHostHeader()).setIamRole(awsConfig.getIamRole()).setConnectionTimeoutSeconds(awsConfig.getConnectionTimeoutSeconds()).build();
    }

    protected Collection<String> getMembers() {
        try {
            Collection<String> list = this.aws.getPrivateIpAddresses();
            if (list.isEmpty()) {
                this.logger.warning("No EC2 instances found!");
            } else if (this.logger.isFinestEnabled()) {
                StringBuilder sb = new StringBuilder("Found the following EC2 instances:\n");
                for (String ip : list) {
                    sb.append("    ").append(ip).append("\n");
                }
                this.logger.finest(sb.toString());
            }
            return list;
        }
        catch (Exception e) {
            this.logger.warning((Throwable)e);
            throw ExceptionUtil.rethrow((Throwable)e);
        }
    }

    public String getType() {
        return "aws";
    }
}

