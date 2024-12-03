/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;

@SdkInternalApi
public abstract class EnhancedJsonErrorUnmarshaller
extends JsonErrorUnmarshaller {
    public EnhancedJsonErrorUnmarshaller(Class<? extends AmazonServiceException> exceptionClass, String handledErrorCode) {
        super(exceptionClass, handledErrorCode);
    }

    public abstract AmazonServiceException unmarshallFromContext(JsonUnmarshallerContext var1) throws Exception;
}

