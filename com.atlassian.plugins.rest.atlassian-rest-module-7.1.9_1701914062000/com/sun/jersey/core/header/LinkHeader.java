/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.core.impl.provider.header.WriterUtil;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class LinkHeader {
    private URI uri;
    private Set<String> rels;
    private MediaType type;
    private MultivaluedMap<String, String> parameters;

    public LinkHeader(String header) throws ParseException, IllegalArgumentException {
        this(HttpHeaderReader.newInstance(header));
    }

    public LinkHeader(HttpHeaderReader reader) throws ParseException, IllegalArgumentException {
        this.uri = URI.create(reader.nextSeparatedString('<', '>'));
        if (reader.hasNext()) {
            this.parseParameters(reader);
        }
    }

    protected LinkHeader(LinkHeaderBuilder builder) {
        this.uri = builder.uri;
        if (builder.rels != null) {
            this.rels = builder.rels.size() == 1 ? builder.rels : Collections.unmodifiableSet(new HashSet<String>(builder.rels));
        }
        this.type = builder.type;
        if (builder.parameters != null) {
            this.parameters = new MultivaluedMapImpl(builder.parameters);
        }
    }

    public static LinkHeader valueOf(String header) throws IllegalArgumentException {
        try {
            return new LinkHeader(HttpHeaderReader.newInstance(header));
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(this.uri.toASCIIString()).append('>');
        if (this.rels != null) {
            sb.append(';').append("rel=");
            if (this.rels.size() == 1) {
                sb.append(this.rels.iterator().next());
            } else {
                sb.append('\"');
                boolean first = true;
                for (String rel : this.rels) {
                    if (!first) {
                        sb.append(' ');
                    }
                    sb.append(rel);
                    first = false;
                }
                sb.append('\"');
            }
        }
        if (this.type != null) {
            sb.append(';').append("type=").append(this.type.getType()).append('/').append(this.type.getSubtype());
        }
        if (this.parameters != null) {
            for (Map.Entry e : this.parameters.entrySet()) {
                String key = (String)e.getKey();
                List values = (List)e.getValue();
                if (key.equals("anchor") || key.equals("title")) {
                    sb.append(";").append(key).append("=");
                    WriterUtil.appendQuoted(sb, (String)values.get(0));
                    continue;
                }
                if (key.equals("hreflang")) {
                    for (String value : (List)e.getValue()) {
                        sb.append(";").append((String)e.getKey()).append("=").append(value);
                    }
                    continue;
                }
                for (String value : (List)e.getValue()) {
                    sb.append(";").append((String)e.getKey()).append("=");
                    WriterUtil.appendQuoted(sb, value);
                }
            }
        }
        return sb.toString();
    }

    public MultivaluedMap<String, String> getParams() {
        this.checkNull();
        return this.parameters;
    }

    public URI getUri() {
        return this.uri;
    }

    public Set<String> getRel() {
        if (this.rels == null) {
            this.rels = Collections.emptySet();
        }
        return this.rels;
    }

    public MediaType getType() {
        return this.type;
    }

    public String getOp() {
        if (this.parameters != null) {
            return this.parameters.getFirst("op");
        }
        return null;
    }

    private void parseParameters(HttpHeaderReader reader) throws ParseException {
        while (reader.hasNext()) {
            reader.nextSeparator(';');
            while (reader.hasNextSeparator(';', true)) {
                reader.next();
            }
            if (!reader.hasNext()) break;
            String name = reader.nextToken().toLowerCase();
            reader.nextSeparator('=');
            if (name.equals("rel")) {
                String value = reader.nextTokenOrQuotedString();
                if (reader.getEvent() == HttpHeaderReader.Event.Token) {
                    this.rels = Collections.singleton(value);
                    continue;
                }
                String[] values = value.split(" ");
                this.rels = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(values)));
                continue;
            }
            if (name.equals("hreflang")) {
                this.add(name, reader.nextTokenOrQuotedString());
                continue;
            }
            if (name.equals("media")) {
                if (this.containsKey("media")) continue;
                this.add(name, reader.nextTokenOrQuotedString());
                continue;
            }
            if (name.equals("title")) {
                if (this.containsKey("title")) continue;
                this.add(name, reader.nextQuotedString());
                continue;
            }
            if (name.equals("title*")) {
                this.add(name, reader.nextQuotedString());
                continue;
            }
            if (name.equals("type")) {
                String typeName = reader.nextToken();
                reader.nextSeparator('/');
                String subTypeName = reader.nextToken();
                this.type = new MediaType(typeName, subTypeName);
                continue;
            }
            this.add(name, reader.nextTokenOrQuotedString());
        }
    }

    private void checkNull() {
        if (this.parameters == null) {
            this.parameters = new MultivaluedMapImpl();
        }
    }

    private boolean containsKey(String key) {
        this.checkNull();
        return this.parameters.containsKey(key);
    }

    private void add(String key, String value) {
        this.checkNull();
        this.parameters.add(key, value);
    }

    public static LinkHeaderBuilder uri(URI uri) {
        return new LinkHeaderBuilder(uri);
    }

    public static class LinkHeaderBuilder<T extends LinkHeaderBuilder, V extends LinkHeader> {
        protected URI uri;
        protected Set<String> rels;
        protected MediaType type;
        protected MultivaluedMap<String, String> parameters;

        LinkHeaderBuilder(URI uri) {
            this.uri = uri;
        }

        public T rel(String rel) {
            if (rel == null) {
                throw new IllegalArgumentException("rel parameter cannot be null");
            }
            if ((rel = rel.trim()).length() == 0) {
                throw new IllegalArgumentException("rel parameter cannot an empty string or just white space");
            }
            if (this.rels == null) {
                this.rels = Collections.singleton(rel);
            } else if (this.rels.size() == 1 && !this.rels.contains(rel)) {
                this.rels = new HashSet<String>(this.rels);
                this.rels.add(rel);
            } else {
                this.rels.add(rel);
            }
            return (T)this;
        }

        public T type(MediaType type) {
            this.type = type;
            return (T)this;
        }

        public T op(String op) {
            this.parameter("op", op);
            return (T)this;
        }

        public T parameter(String key, String value) {
            if (key.equals("rel")) {
                return this.rel(value);
            }
            if (key.equals("type")) {
                return this.type(MediaType.valueOf(value));
            }
            if (this.parameters == null) {
                this.parameters = new MultivaluedMapImpl();
            }
            this.parameters.add(key, value);
            return (T)this;
        }

        public V build() {
            LinkHeader lh = new LinkHeader(this);
            return (V)lh;
        }
    }
}

