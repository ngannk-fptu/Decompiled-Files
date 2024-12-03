/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.AwsResponseMetadata;

@SdkProtectedApi
public final class DefaultAwsResponseMetadata
extends AwsResponseMetadata {
    private DefaultAwsResponseMetadata(Map<String, String> metadata) {
        super(metadata);
    }

    public static DefaultAwsResponseMetadata create(Map<String, String> metadata) {
        return new DefaultAwsResponseMetadata(metadata);
    }
}

