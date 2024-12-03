/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Prompt
extends LinkedHashSet<Type> {
    private static final long serialVersionUID = -3672900533669609699L;

    public Prompt() {
    }

    public Prompt(Type ... type) {
        this.addAll(Arrays.asList(type));
    }

    public Prompt(String ... values) {
        for (String v : values) {
            try {
                this.add(Type.parse(v));
            }
            catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }

    public boolean isValid() {
        return this.size() <= 1 || !this.contains((Object)Type.NONE);
    }

    public List<String> toStringList() {
        ArrayList<String> list = new ArrayList<String>(this.size());
        for (Type t : this) {
            list.add(t.toString());
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator it = super.iterator();
        while (it.hasNext()) {
            sb.append(((Type)((Object)it.next())).toString());
            if (!it.hasNext()) continue;
            sb.append(" ");
        }
        return sb.toString();
    }

    public static Prompt parse(Collection<String> collection) throws ParseException {
        if (collection == null) {
            return null;
        }
        Prompt prompt = new Prompt();
        for (String s : collection) {
            prompt.add(Type.parse(s));
        }
        if (!prompt.isValid()) {
            throw new ParseException("Invalid prompt: " + collection);
        }
        return prompt;
    }

    public static Prompt parse(String s) throws ParseException {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        Prompt prompt = new Prompt();
        StringTokenizer st = new StringTokenizer(s, " ");
        while (st.hasMoreTokens()) {
            prompt.add(Type.parse(st.nextToken()));
        }
        if (!prompt.isValid()) {
            throw new ParseException("Invalid prompt: " + s);
        }
        return prompt;
    }

    public static enum Type {
        NONE,
        LOGIN,
        CONSENT,
        SELECT_ACCOUNT,
        CREATE;


        public String toString() {
            return super.toString().toLowerCase();
        }

        public static Type parse(String s) throws ParseException {
            if (StringUtils.isBlank(s)) {
                throw new ParseException("Null or empty prompt type string");
            }
            if ("none".equals(s)) {
                return NONE;
            }
            if ("login".equals(s)) {
                return LOGIN;
            }
            if ("consent".equals(s)) {
                return CONSENT;
            }
            if ("select_account".equals(s)) {
                return SELECT_ACCOUNT;
            }
            if ("create".equals(s)) {
                return CREATE;
            }
            throw new ParseException("Unknown prompt type: " + s);
        }
    }
}

