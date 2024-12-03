/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.net;

import java.nio.charset.Charset;
import java.util.List;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

public class WWWFormCodec {
    private static final char QP_SEP_A = '&';

    public static List<NameValuePair> parse(CharSequence s, Charset charset) {
        return URIBuilder.parseQuery(s, charset, true);
    }

    public static void format(StringBuilder buf, Iterable<? extends NameValuePair> params, Charset charset) {
        URIBuilder.formatQuery(buf, params, charset, true);
    }

    public static String format(Iterable<? extends NameValuePair> params, Charset charset) {
        StringBuilder buf = new StringBuilder();
        URIBuilder.formatQuery(buf, params, charset, true);
        return buf.toString();
    }
}

