/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.ConnectionKeepAliveStrategy
 *  org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy
 */
package software.amazon.awssdk.http.apache.internal.conn;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class SdkConnectionKeepAliveStrategy
implements ConnectionKeepAliveStrategy {
    private final long maxIdleTime;

    public SdkConnectionKeepAliveStrategy(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        long duration = DefaultConnectionKeepAliveStrategy.INSTANCE.getKeepAliveDuration(response, context);
        if (0L < duration && duration < this.maxIdleTime) {
            return duration;
        }
        return this.maxIdleTime;
    }
}

