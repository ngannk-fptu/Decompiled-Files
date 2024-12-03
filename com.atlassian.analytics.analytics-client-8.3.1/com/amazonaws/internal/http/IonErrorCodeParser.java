/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.internal.http;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.internal.http.ErrorCodeParser;
import com.amazonaws.protocol.json.JsonContent;
import com.amazonaws.util.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonType;

@SdkInternalApi
public class IonErrorCodeParser
implements ErrorCodeParser {
    private static final Log log = LogFactory.getLog(IonErrorCodeParser.class);
    private static final String TYPE_PREFIX = "aws-type:";
    private static final String X_AMZN_REQUEST_ID_HEADER = "x-amzn-RequestId";
    private final IonSystem ionSystem;

    public IonErrorCodeParser(IonSystem ionSystem) {
        this.ionSystem = ionSystem;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String parseErrorCode(HttpResponse response, JsonContent jsonContents) {
        IonReader reader = this.ionSystem.newReader(jsonContents.getRawContent());
        try {
            String[] annotations;
            IonType type = reader.next();
            if (type != IonType.STRUCT) {
                throw new SdkClientException(String.format("Can only get error codes from structs (saw %s), request id %s", new Object[]{type, IonErrorCodeParser.getRequestId(response)}));
            }
            boolean errorCodeSeen = false;
            String errorCode = null;
            for (String annotation : annotations = reader.getTypeAnnotations()) {
                if (!annotation.startsWith(TYPE_PREFIX)) continue;
                if (errorCodeSeen) {
                    throw new SdkClientException(String.format("Multiple error code annotations found for request id %s", IonErrorCodeParser.getRequestId(response)));
                }
                errorCodeSeen = true;
                errorCode = annotation.substring(TYPE_PREFIX.length());
            }
            String[] stringArray = errorCode;
            return stringArray;
        }
        finally {
            IOUtils.closeQuietly(reader, log);
        }
    }

    private static String getRequestId(HttpResponse response) {
        return response.getHeaders().get(X_AMZN_REQUEST_ID_HEADER);
    }
}

