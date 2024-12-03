/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.auth.credentials.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public enum CredentialSourceType {
    EC2_INSTANCE_METADATA,
    ECS_CONTAINER,
    ENVIRONMENT;


    public static CredentialSourceType parse(String value) {
        if (value.equalsIgnoreCase("Ec2InstanceMetadata")) {
            return EC2_INSTANCE_METADATA;
        }
        if (value.equalsIgnoreCase("EcsContainer")) {
            return ECS_CONTAINER;
        }
        if (value.equalsIgnoreCase("Environment")) {
            return ENVIRONMENT;
        }
        throw new IllegalArgumentException(String.format("%s is not a valid credential_source", value));
    }
}

