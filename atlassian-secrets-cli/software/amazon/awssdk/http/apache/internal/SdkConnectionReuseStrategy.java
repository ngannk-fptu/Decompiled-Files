/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.impl.client.DefaultClientConnectionReuseStrategy
 */
package software.amazon.awssdk.http.apache.internal;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class SdkConnectionReuseStrategy
extends DefaultClientConnectionReuseStrategy {
    public boolean keepAlive(HttpResponse response, HttpContext context) {
        if (!super.keepAlive(response, context)) {
            return false;
        }
        if (response == null || response.getStatusLine() == null) {
            return false;
        }
        return !this.is500(response);
    }

    private boolean is500(HttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode() / 100 == 5;
    }
}

