/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.utils.CollectionUtils
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.MutableRequestToRequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.CollectionUtils;

@SdkInternalApi
public class MergeCustomQueryParamsStage
implements MutableRequestToRequestPipeline {
    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest.Builder request, RequestExecutionContext context) throws Exception {
        return request.rawQueryParameters(this.mergeParams(request, context));
    }

    private Map<String, List<String>> mergeParams(SdkHttpFullRequest.Builder request, RequestExecutionContext context) {
        LinkedHashMap<String, List<String>> merged = new LinkedHashMap<String, List<String>>(request.numRawQueryParameters());
        request.forEachRawQueryParameter(merged::put);
        context.requestConfig().rawQueryParameters().forEach((key, val) -> merged.put((String)key, CollectionUtils.mergeLists((List)((List)merged.get(key)), (List)val)));
        return merged;
    }
}

