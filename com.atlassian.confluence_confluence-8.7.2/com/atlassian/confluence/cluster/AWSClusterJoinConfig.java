/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  io.atlassian.fugue.Either
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.util.i18n.Message;
import com.google.common.base.Strings;
import io.atlassian.fugue.Either;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AWSClusterJoinConfig
implements ClusterJoinConfig {
    private final String accessKey;
    private final String secretKey;
    private final String iamRole;
    private final String region;
    private final String hostHeader;
    private final String securityGroupName;
    private final String tagKey;
    private final String tagValue;

    private AWSClusterJoinConfig(@Nullable String accessKey, @Nullable String secretKey, @Nullable String iamRole, @Nullable String region, @Nullable String hostHeader, @Nullable String securityGroupName, @Nullable String tagKey, @Nullable String tagValue) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.iamRole = iamRole;
        this.region = region;
        this.hostHeader = hostHeader;
        this.securityGroupName = securityGroupName;
        this.tagKey = tagKey;
        this.tagValue = tagValue;
    }

    public static Either<Message, AWSClusterJoinConfig> createForKeys(@Nullable String accessKey, @Nullable String secretKey, @Nullable String iamRole, @Nullable String region, @Nullable String hostHeader, @Nullable String securityGroupName, @Nullable String tagKey, @Nullable String tagValue) {
        if (StringUtils.isBlank((CharSequence)iamRole) && StringUtils.isBlank((CharSequence)accessKey)) {
            return Either.left((Object)Message.getInstance("error.cluster.aws.iam.role.and.access.key.not.defined"));
        }
        if (!StringUtils.isBlank((CharSequence)iamRole)) {
            if (!StringUtils.isBlank((CharSequence)accessKey)) {
                return Either.left((Object)Message.getInstance("error.cluster.aws.iam.role.and.access.key.both.defined"));
            }
            if (!StringUtils.isBlank((CharSequence)secretKey)) {
                return Either.left((Object)Message.getInstance("error.cluster.aws.iam.role.and.secret.key.both.defined"));
            }
        } else {
            if (StringUtils.isBlank((CharSequence)accessKey)) {
                return Either.left((Object)Message.getInstance("error.cluster.aws.access.key.not.defined"));
            }
            if (StringUtils.isBlank((CharSequence)secretKey)) {
                return Either.left((Object)Message.getInstance("error.cluster.aws.secret.key.not.defined"));
            }
        }
        return Either.right((Object)new AWSClusterJoinConfig(accessKey, secretKey, iamRole, region, hostHeader, securityGroupName, tagKey, tagValue));
    }

    public Optional<String> getAccessKey() {
        return AWSClusterJoinConfig.optionallyEmpty(this.accessKey);
    }

    public Optional<String> getSecretKey() {
        return AWSClusterJoinConfig.optionallyEmpty(this.secretKey);
    }

    public Optional<String> getIamRole() {
        return AWSClusterJoinConfig.optionallyEmpty(this.iamRole);
    }

    public Optional<String> getRegion() {
        return AWSClusterJoinConfig.optionallyEmpty(this.region);
    }

    public Optional<String> getHostHeader() {
        return AWSClusterJoinConfig.optionallyEmpty(this.hostHeader);
    }

    public Optional<String> getSecurityGroupName() {
        return AWSClusterJoinConfig.optionallyEmpty(this.securityGroupName);
    }

    public Optional<String> getTagKey() {
        return AWSClusterJoinConfig.optionallyEmpty(this.tagKey);
    }

    public Optional<String> getTagValue() {
        return AWSClusterJoinConfig.optionallyEmpty(this.tagValue);
    }

    @Override
    public ClusterJoinConfig.ClusterJoinType getType() {
        return ClusterJoinConfig.ClusterJoinType.AWS;
    }

    @Override
    public void decode(ClusterJoinConfig.Decoder decoder) {
        decoder.accept(this);
    }

    public String toString() {
        return "AWS config (accessKey|iamRole|region|host header|security group|tag key|tag value: " + this.accessKey + "|" + this.iamRole + "|" + this.region + "|" + this.hostHeader + "|" + this.securityGroupName + "|" + this.tagKey + "|" + this.tagValue;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof AWSClusterJoinConfig)) {
            return false;
        }
        AWSClusterJoinConfig that = (AWSClusterJoinConfig)o;
        return Objects.equals(this.getAccessKey(), that.getAccessKey()) && Objects.equals(this.getSecretKey(), that.getSecretKey()) && Objects.equals(this.getIamRole(), that.getIamRole()) && Objects.equals(this.getRegion(), that.getRegion()) && Objects.equals(this.getHostHeader(), that.getHostHeader()) && Objects.equals(this.getSecurityGroupName(), that.getSecurityGroupName()) && Objects.equals(this.getTagKey(), that.getTagKey()) && Objects.equals(this.getTagValue(), that.getTagValue());
    }

    public int hashCode() {
        return Objects.hash(this.accessKey, this.secretKey, this.iamRole, this.region, this.hostHeader, this.securityGroupName, this.tagKey, this.tagValue);
    }

    private static Optional<String> optionallyEmpty(String value) {
        return Optional.ofNullable(Strings.emptyToNull((String)value));
    }
}

