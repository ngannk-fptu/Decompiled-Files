/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.util.Attributes
 *  org.eclipse.jetty.util.StringUtil
 */
package org.eclipse.jetty.client.api;

import java.net.URI;
import java.util.Map;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.StringUtil;

public interface Authentication {
    public static final String ANY_REALM = "<<ANY_REALM>>";

    public boolean matches(String var1, URI var2, String var3);

    public Result authenticate(Request var1, ContentResponse var2, HeaderInfo var3, Attributes var4);

    public static interface Result {
        public URI getURI();

        public void apply(Request var1);
    }

    public static class HeaderInfo {
        private final HttpHeader header;
        private final String type;
        private final Map<String, String> params;

        public HeaderInfo(HttpHeader header, String type, Map<String, String> params) throws IllegalArgumentException {
            this.header = header;
            this.type = type;
            this.params = params;
        }

        public String getType() {
            return this.type;
        }

        public String getRealm() {
            return this.params.get("realm");
        }

        public String getBase64() {
            return this.params.get("base64");
        }

        public Map<String, String> getParameters() {
            return this.params;
        }

        public String getParameter(String paramName) {
            return this.params.get(StringUtil.asciiToLowerCase((String)paramName));
        }

        public HttpHeader getHeader() {
            return this.header;
        }
    }
}

