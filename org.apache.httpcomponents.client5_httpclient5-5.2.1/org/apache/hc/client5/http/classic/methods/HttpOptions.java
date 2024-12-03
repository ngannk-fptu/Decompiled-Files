/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HeaderElement
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.MessageHeaders
 *  org.apache.hc.core5.http.message.MessageSupport
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.classic.methods;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.util.Args;

public class HttpOptions
extends HttpUriRequestBase {
    private static final long serialVersionUID = 1L;
    public static final String METHOD_NAME = "OPTIONS";

    public HttpOptions(URI uri) {
        super(METHOD_NAME, uri);
    }

    public HttpOptions(String uri) {
        this(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }

    public Set<String> getAllowedMethods(HttpResponse response) {
        Args.notNull((Object)response, (String)"HTTP response");
        Iterator it = MessageSupport.iterate((MessageHeaders)response, (String)"Allow");
        HashSet<String> methods = new HashSet<String>();
        while (it.hasNext()) {
            HeaderElement element = (HeaderElement)it.next();
            methods.add(element.getName());
        }
        return methods;
    }
}

