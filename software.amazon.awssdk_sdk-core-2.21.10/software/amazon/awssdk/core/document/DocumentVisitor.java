/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.document;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.Document;

@SdkPublicApi
public interface DocumentVisitor<R> {
    public R visitNull();

    public R visitBoolean(Boolean var1);

    public R visitString(String var1);

    public R visitNumber(SdkNumber var1);

    public R visitMap(Map<String, Document> var1);

    public R visitList(List<Document> var1);
}

