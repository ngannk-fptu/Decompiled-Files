/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.adapter.TypeAdapter
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.adapter.TypeAdapter;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class TaggingAdapter
implements TypeAdapter<Tagging, String> {
    private static final TaggingAdapter INSTANCE = new TaggingAdapter();

    private TaggingAdapter() {
    }

    public String adapt(Tagging tagging) {
        StringBuilder tagBuilder = new StringBuilder();
        if (tagging != null && !tagging.tagSet().isEmpty()) {
            Tagging taggingClone = (Tagging)tagging.toBuilder().build();
            Tag firstTag = taggingClone.tagSet().get(0);
            tagBuilder.append(SdkHttpUtils.urlEncode((String)firstTag.key()));
            tagBuilder.append("=");
            tagBuilder.append(SdkHttpUtils.urlEncode((String)firstTag.value()));
            for (int i = 1; i < taggingClone.tagSet().size(); ++i) {
                Tag t = taggingClone.tagSet().get(i);
                tagBuilder.append("&");
                tagBuilder.append(SdkHttpUtils.urlEncode((String)t.key()));
                tagBuilder.append("=");
                tagBuilder.append(SdkHttpUtils.urlEncode((String)t.value()));
            }
        }
        return tagBuilder.toString();
    }

    public static TaggingAdapter instance() {
        return INSTANCE;
    }
}

