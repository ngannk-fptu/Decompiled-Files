/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.SchemePortResolver
 *  org.apache.http.impl.conn.DefaultRoutePlanner
 *  org.apache.http.impl.conn.DefaultSchemePortResolver
 */
package software.amazon.awssdk.http.apache.internal;

import java.util.Set;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public class SdkProxyRoutePlanner
extends DefaultRoutePlanner {
    private HttpHost proxy;
    private Set<String> hostPatterns;

    public SdkProxyRoutePlanner(String proxyHost, int proxyPort, String proxyProtocol, Set<String> nonProxyHosts) {
        super((SchemePortResolver)DefaultSchemePortResolver.INSTANCE);
        this.proxy = new HttpHost(proxyHost, proxyPort, proxyProtocol);
        this.hostPatterns = nonProxyHosts;
    }

    private boolean doesTargetMatchNonProxyHosts(HttpHost target) {
        if (this.hostPatterns == null) {
            return false;
        }
        String targetHost = StringUtils.lowerCase(target.getHostName());
        for (String pattern : this.hostPatterns) {
            if (!targetHost.matches(pattern)) continue;
            return true;
        }
        return false;
    }

    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        return this.doesTargetMatchNonProxyHosts(target) ? null : this.proxy;
    }
}

