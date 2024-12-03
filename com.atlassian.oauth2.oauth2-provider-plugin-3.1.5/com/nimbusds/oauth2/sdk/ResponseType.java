/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class ResponseType
extends HashSet<Value> {
    public static final ResponseType CODE = new ResponseType(true, Value.CODE);
    public static final ResponseType TOKEN = new ResponseType(true, Value.TOKEN);
    public static final ResponseType IDTOKEN_TOKEN = new ResponseType(true, OIDCResponseTypeValue.ID_TOKEN, Value.TOKEN);
    public static final ResponseType IDTOKEN = new ResponseType(true, OIDCResponseTypeValue.ID_TOKEN);
    public static final ResponseType CODE_IDTOKEN = new ResponseType(true, Value.CODE, OIDCResponseTypeValue.ID_TOKEN);
    public static final ResponseType CODE_TOKEN = new ResponseType(true, Value.CODE, Value.TOKEN);
    public static final ResponseType CODE_IDTOKEN_TOKEN = new ResponseType(true, Value.CODE, OIDCResponseTypeValue.ID_TOKEN, Value.TOKEN);
    private static final long serialVersionUID = 1351973244616920112L;
    private final boolean unmodifiable;

    public static ResponseType getDefault() {
        return CODE;
    }

    public ResponseType() {
        this.unmodifiable = false;
    }

    public ResponseType(String ... values) {
        for (String v : values) {
            this.add(new Value(v));
        }
        this.unmodifiable = false;
    }

    public ResponseType(Value ... values) {
        this(false, values);
    }

    private ResponseType(boolean unmodifiable, Value ... values) {
        super(Arrays.asList(values));
        this.unmodifiable = unmodifiable;
    }

    public static ResponseType parse(String s) throws ParseException {
        if (StringUtils.isBlank(s)) {
            throw new ParseException("Null or empty response type string");
        }
        ResponseType rt = new ResponseType();
        StringTokenizer st = new StringTokenizer(s, " ");
        while (st.hasMoreTokens()) {
            rt.add(new Value(st.nextToken()));
        }
        return rt;
    }

    public boolean impliesCodeFlow() {
        return this.equals(new ResponseType(Value.CODE));
    }

    public boolean impliesImplicitFlow() {
        return this.equals(new ResponseType(Value.TOKEN)) || this.equals(new ResponseType(OIDCResponseTypeValue.ID_TOKEN, Value.TOKEN)) || this.equals(new ResponseType(OIDCResponseTypeValue.ID_TOKEN));
    }

    public boolean impliesHybridFlow() {
        return this.equals(new ResponseType(Value.CODE, OIDCResponseTypeValue.ID_TOKEN)) || this.equals(new ResponseType(Value.CODE, Value.TOKEN)) || this.equals(new ResponseType(Value.CODE, OIDCResponseTypeValue.ID_TOKEN, Value.TOKEN));
    }

    public boolean contains(String value) {
        return this.contains(new Value(value));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Value v : this) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(v.getValue());
        }
        return sb.toString();
    }

    @Override
    public boolean add(Value value) {
        if (this.unmodifiable) {
            throw new UnsupportedOperationException();
        }
        return super.add(value);
    }

    @Override
    public boolean remove(Object o) {
        if (this.unmodifiable) {
            throw new UnsupportedOperationException();
        }
        return super.remove(o);
    }

    @Override
    public void clear() {
        if (this.unmodifiable) {
            throw new UnsupportedOperationException();
        }
        super.clear();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (this.unmodifiable) {
            throw new UnsupportedOperationException();
        }
        return super.removeAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Value> c) {
        if (this.unmodifiable) {
            throw new UnsupportedOperationException();
        }
        return super.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (this.unmodifiable) {
            throw new UnsupportedOperationException();
        }
        return super.retainAll(c);
    }

    @Immutable
    public static final class Value
    extends Identifier {
        public static final Value CODE = new Value("code");
        public static final Value TOKEN = new Value("token");
        private static final long serialVersionUID = 5339971450891463852L;

        public Value(String value) {
            super(value);
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof Value && this.toString().equals(object.toString());
        }
    }
}

