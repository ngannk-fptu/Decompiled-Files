/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 */
package software.amazon.awssdk.protocols.query.unmarshall;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkProtectedApi
public interface XmlErrorUnmarshaller {
    public <TypeT extends SdkPojo> TypeT unmarshall(SdkPojo var1, XmlElement var2, SdkHttpFullResponse var3);
}

