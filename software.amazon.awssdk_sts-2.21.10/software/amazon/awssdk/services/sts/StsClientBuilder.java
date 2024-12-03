/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.client.builder.AwsSyncClientBuilder
 */
package software.amazon.awssdk.services.sts;

import software.amazon.awssdk.awscore.client.builder.AwsSyncClientBuilder;
import software.amazon.awssdk.services.sts.StsBaseClientBuilder;
import software.amazon.awssdk.services.sts.StsClient;

public interface StsClientBuilder
extends AwsSyncClientBuilder<StsClientBuilder, StsClient>,
StsBaseClientBuilder<StsClientBuilder, StsClient> {
}

