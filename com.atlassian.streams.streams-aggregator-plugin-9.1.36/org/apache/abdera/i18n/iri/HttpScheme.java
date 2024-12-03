/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import org.apache.abdera.i18n.iri.AbstractScheme;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.UrlEncoding;

class HttpScheme
extends AbstractScheme {
    static final String NAME = "http";
    static final int DEFAULT_PORT = 80;

    public HttpScheme() {
        super(NAME, 80);
    }

    protected HttpScheme(String name, int port) {
        super(name, port);
    }

    public IRI normalize(IRI iri) {
        StringBuilder buf = new StringBuilder();
        int port = iri.getPort() == this.getDefaultPort() ? -1 : iri.getPort();
        String host = iri.getHost();
        if (host != null) {
            host = host.toLowerCase();
        }
        String ui = iri.getUserInfo();
        iri.buildAuthority(buf, ui, host, port);
        String authority = buf.toString();
        return new IRI(iri._scheme, iri.getScheme(), authority, ui, host, port, IRI.normalize(iri.getPath()), UrlEncoding.encode((CharSequence)UrlEncoding.decode(iri.getQuery()), CharUtils.Profile.IQUERY.filter()), UrlEncoding.encode((CharSequence)UrlEncoding.decode(iri.getFragment()), CharUtils.Profile.IFRAGMENT.filter()));
    }

    public String normalizePath(String path) {
        return null;
    }
}

