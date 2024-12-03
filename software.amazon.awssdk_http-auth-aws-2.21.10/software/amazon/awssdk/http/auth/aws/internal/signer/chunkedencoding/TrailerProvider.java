/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Resettable;
import software.amazon.awssdk.utils.Pair;

@FunctionalInterface
@SdkInternalApi
public interface TrailerProvider
extends Resettable {
    public Pair<String, List<String>> get();
}

