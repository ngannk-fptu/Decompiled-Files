/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpRequest
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.config.Lookup
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.Lookup;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
@Contract(threading=ThreadingBehavior.SAFE)
public final class CookieSpecRegistry
implements Lookup<CookieSpecProvider> {
    private final ConcurrentHashMap<String, CookieSpecFactory> registeredSpecs = new ConcurrentHashMap();

    public void register(String name, CookieSpecFactory factory) {
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)factory, (String)"Cookie spec factory");
        this.registeredSpecs.put(name.toLowerCase(Locale.ENGLISH), factory);
    }

    public void unregister(String id) {
        Args.notNull((Object)id, (String)"Id");
        this.registeredSpecs.remove(id.toLowerCase(Locale.ENGLISH));
    }

    public CookieSpec getCookieSpec(String name, HttpParams params) throws IllegalStateException {
        Args.notNull((Object)name, (String)"Name");
        CookieSpecFactory factory = this.registeredSpecs.get(name.toLowerCase(Locale.ENGLISH));
        if (factory != null) {
            return factory.newInstance(params);
        }
        throw new IllegalStateException("Unsupported cookie spec: " + name);
    }

    public CookieSpec getCookieSpec(String name) throws IllegalStateException {
        return this.getCookieSpec(name, null);
    }

    public List<String> getSpecNames() {
        return new ArrayList<String>(this.registeredSpecs.keySet());
    }

    public void setItems(Map<String, CookieSpecFactory> map) {
        if (map == null) {
            return;
        }
        this.registeredSpecs.clear();
        this.registeredSpecs.putAll(map);
    }

    public CookieSpecProvider lookup(final String name) {
        return new CookieSpecProvider(){

            @Override
            public CookieSpec create(HttpContext context) {
                HttpRequest request = (HttpRequest)context.getAttribute("http.request");
                return CookieSpecRegistry.this.getCookieSpec(name, request.getParams());
            }
        };
    }
}

