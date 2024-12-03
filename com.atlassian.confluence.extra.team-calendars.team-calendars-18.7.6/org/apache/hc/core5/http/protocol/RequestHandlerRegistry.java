/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestMapper;
import org.apache.hc.core5.http.MisdirectedRequestException;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.LookupRegistry;
import org.apache.hc.core5.http.protocol.UriPatternMatcher;
import org.apache.hc.core5.http.protocol.UriPatternType;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class RequestHandlerRegistry<T>
implements HttpRequestMapper<T> {
    private static final String LOCALHOST = "localhost";
    private static final String IP_127_0_0_1 = "127.0.0.1";
    private final String canonicalHostName;
    private final Supplier<LookupRegistry<T>> registrySupplier;
    private final LookupRegistry<T> primary;
    private final ConcurrentMap<String, LookupRegistry<T>> virtualMap;

    public RequestHandlerRegistry(String canonicalHostName, Supplier<LookupRegistry<T>> registrySupplier) {
        this.canonicalHostName = TextUtils.toLowerCase(Args.notNull(canonicalHostName, "Canonical hostname"));
        this.registrySupplier = registrySupplier != null ? registrySupplier : UriPatternMatcher::new;
        this.primary = this.registrySupplier.get();
        this.virtualMap = new ConcurrentHashMap<String, LookupRegistry<T>>();
    }

    public RequestHandlerRegistry(String canonicalHostName, UriPatternType patternType) {
        this(canonicalHostName, () -> UriPatternType.newMatcher(patternType));
    }

    public RequestHandlerRegistry(UriPatternType patternType) {
        this(LOCALHOST, patternType);
    }

    public RequestHandlerRegistry() {
        this(LOCALHOST, UriPatternType.URI_PATTERN);
    }

    private LookupRegistry<T> getPatternMatcher(String hostname) {
        if (hostname == null || hostname.equals(this.canonicalHostName) || hostname.equals(LOCALHOST) || hostname.equals(IP_127_0_0_1)) {
            return this.primary;
        }
        return (LookupRegistry)this.virtualMap.get(hostname);
    }

    @Override
    public T resolve(HttpRequest request, HttpContext context) throws MisdirectedRequestException {
        URIAuthority authority = request.getAuthority();
        String key = authority != null ? TextUtils.toLowerCase(authority.getHostName()) : null;
        LookupRegistry<T> patternMatcher = this.getPatternMatcher(key);
        if (patternMatcher == null) {
            throw new MisdirectedRequestException("Not authoritative");
        }
        String path = request.getPath();
        int i = path.indexOf(63);
        if (i != -1) {
            path = path.substring(0, i);
        }
        return patternMatcher.lookup(path);
    }

    public void register(String hostname, String uriPattern, T object) {
        Args.notBlank(uriPattern, "URI pattern");
        if (object == null) {
            return;
        }
        String key = TextUtils.toLowerCase(hostname);
        if (hostname == null || hostname.equals(this.canonicalHostName) || hostname.equals(LOCALHOST)) {
            this.primary.register(uriPattern, object);
        } else {
            LookupRegistry<T> newPatternMatcher;
            LookupRegistry<T> patternMatcher = (LookupRegistry<T>)this.virtualMap.get(key);
            if (patternMatcher == null && (patternMatcher = this.virtualMap.putIfAbsent(key, newPatternMatcher = this.registrySupplier.get())) == null) {
                patternMatcher = newPatternMatcher;
            }
            patternMatcher.register(uriPattern, object);
        }
    }
}

