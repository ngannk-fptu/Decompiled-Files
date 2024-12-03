/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.async.listener.PublisherListener
 *  software.amazon.awssdk.http.SdkHttpExecutionAttribute
 */
package software.amazon.awssdk.services.s3.crt;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.listener.PublisherListener;
import software.amazon.awssdk.http.SdkHttpExecutionAttribute;
import software.amazon.awssdk.services.s3.internal.crt.S3MetaRequestPauseObservable;

@SdkProtectedApi
public final class S3CrtSdkHttpExecutionAttribute<T>
extends SdkHttpExecutionAttribute<T> {
    public static final S3CrtSdkHttpExecutionAttribute<S3MetaRequestPauseObservable> METAREQUEST_PAUSE_OBSERVABLE = new S3CrtSdkHttpExecutionAttribute<S3MetaRequestPauseObservable>(S3MetaRequestPauseObservable.class);
    public static final S3CrtSdkHttpExecutionAttribute<PublisherListener> CRT_PROGRESS_LISTENER = new S3CrtSdkHttpExecutionAttribute<PublisherListener>(PublisherListener.class);

    private S3CrtSdkHttpExecutionAttribute(Class<T> valueClass) {
        super(valueClass);
    }
}

