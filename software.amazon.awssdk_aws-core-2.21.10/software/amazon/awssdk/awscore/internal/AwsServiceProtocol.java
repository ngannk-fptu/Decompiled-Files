/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.awscore.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public enum AwsServiceProtocol {
    EC2("ec2"),
    AWS_JSON("json"),
    REST_JSON("rest-json"),
    CBOR("cbor"),
    QUERY("query"),
    REST_XML("rest-xml");

    private String protocol;

    private AwsServiceProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static AwsServiceProtocol fromValue(String strProtocol) {
        if (strProtocol == null) {
            return null;
        }
        for (AwsServiceProtocol protocol : AwsServiceProtocol.values()) {
            if (!protocol.protocol.equals(strProtocol)) continue;
            return protocol;
        }
        throw new IllegalArgumentException("Unknown enum value for Protocol : " + strProtocol);
    }

    public String toString() {
        return this.protocol;
    }
}

