/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.regions.internal.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class ConnectionUtils {
    public static ConnectionUtils create() {
        return new ConnectionUtils();
    }

    public HttpURLConnection connectToEndpoint(URI endpoint, Map<String, String> headers) throws IOException {
        return this.connectToEndpoint(endpoint, headers, "GET");
    }

    public HttpURLConnection connectToEndpoint(URI endpoint, Map<String, String> headers, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)endpoint.toURL().openConnection(Proxy.NO_PROXY);
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        headers.forEach(connection::addRequestProperty);
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        return connection;
    }
}

