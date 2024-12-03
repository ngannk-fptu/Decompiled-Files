/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.BooleanEqualsFn;
import software.amazon.awssdk.services.sts.endpoints.internal.GetAttr;
import software.amazon.awssdk.services.sts.endpoints.internal.IsSet;
import software.amazon.awssdk.services.sts.endpoints.internal.IsValidHostLabel;
import software.amazon.awssdk.services.sts.endpoints.internal.IsVirtualHostableS3Bucket;
import software.amazon.awssdk.services.sts.endpoints.internal.Not;
import software.amazon.awssdk.services.sts.endpoints.internal.ParseArn;
import software.amazon.awssdk.services.sts.endpoints.internal.ParseUrl;
import software.amazon.awssdk.services.sts.endpoints.internal.PartitionFn;
import software.amazon.awssdk.services.sts.endpoints.internal.StringEqualsFn;
import software.amazon.awssdk.services.sts.endpoints.internal.Substring;
import software.amazon.awssdk.services.sts.endpoints.internal.UriEncodeFn;

@SdkInternalApi
public interface FnVisitor<R> {
    public R visitPartition(PartitionFn var1);

    public R visitParseArn(ParseArn var1);

    public R visitIsValidHostLabel(IsValidHostLabel var1);

    public R visitBoolEquals(BooleanEqualsFn var1);

    public R visitStringEquals(StringEqualsFn var1);

    public R visitIsSet(IsSet var1);

    public R visitNot(Not var1);

    public R visitGetAttr(GetAttr var1);

    public R visitParseUrl(ParseUrl var1);

    public R visitSubstring(Substring var1);

    public R visitUriEncode(UriEncodeFn var1);

    public R visitIsVirtualHostLabelsS3Bucket(IsVirtualHostableS3Bucket var1);
}

