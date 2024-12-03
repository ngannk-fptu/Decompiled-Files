/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.core.JwtUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestCanonicalizer {
    private static final char ENCODED_PARAM_VALUE_SEPARATOR = ',';
    private static final char CANONICAL_REQUEST_PART_SEPARATOR = '&';

    public static String canonicalize(CanonicalHttpRequest request) throws UnsupportedEncodingException {
        return String.format("%s%s%s%s%s", HttpRequestCanonicalizer.canonicalizeMethod(request), Character.valueOf('&'), HttpRequestCanonicalizer.canonicalizeUri(request), Character.valueOf('&'), HttpRequestCanonicalizer.canonicalizeQueryParameters(request));
    }

    public static String computeCanonicalRequestHash(CanonicalHttpRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return JwtUtil.computeSha256Hash(HttpRequestCanonicalizer.canonicalize(request));
    }

    private static String canonicalizeUri(CanonicalHttpRequest request) throws UnsupportedEncodingException {
        String path = (String)StringUtils.defaultIfBlank((CharSequence)StringUtils.removeEnd((String)request.getRelativePath(), (String)"/"), (CharSequence)"/");
        String separatorAsString = String.valueOf('&');
        return (path = path.replaceAll(separatorAsString, JwtUtil.percentEncode(separatorAsString))).startsWith("/") ? path : "/" + path;
    }

    private static String canonicalizeMethod(CanonicalHttpRequest request) {
        return StringUtils.upperCase((String)request.getMethod());
    }

    private static String canonicalizeQueryParameters(CanonicalHttpRequest request) throws UnsupportedEncodingException {
        String result = "";
        if (null != request.getParameterMap()) {
            ArrayList<ComparableParameter> parameterList = new ArrayList<ComparableParameter>(request.getParameterMap().size());
            for (Map.Entry<String, String[]> parameter : request.getParameterMap().entrySet()) {
                if ("jwt".equals(parameter.getKey())) continue;
                parameterList.add(new ComparableParameter(parameter));
            }
            Collections.sort(parameterList);
            result = HttpRequestCanonicalizer.percentEncode(HttpRequestCanonicalizer.getParameters(parameterList));
        }
        return result;
    }

    private static List<Map.Entry<String, String[]>> getParameters(Collection<ComparableParameter> parameters) {
        if (parameters == null) {
            return null;
        }
        ArrayList<Map.Entry<String, String[]>> list = new ArrayList<Map.Entry<String, String[]>>(parameters.size());
        for (ComparableParameter parameter : parameters) {
            list.add(parameter.parameter);
        }
        return list;
    }

    private static String percentEncode(Iterable<? extends Map.Entry<String, String[]>> parameters) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {
            HttpRequestCanonicalizer.percentEncode(parameters, b);
            return new String(b.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void percentEncode(Iterable<? extends Map.Entry<String, String[]>> parameters, OutputStream into) throws IOException {
        if (parameters != null) {
            boolean first = true;
            for (Map.Entry<String, String[]> entry : parameters) {
                if (first) {
                    first = false;
                } else {
                    into.write(38);
                }
                into.write(JwtUtil.percentEncode(HttpRequestCanonicalizer.safeToString(entry.getKey())).getBytes());
                into.write(61);
                ArrayList<String> percentEncodedValues = new ArrayList<String>(entry.getValue().length);
                for (String value : entry.getValue()) {
                    percentEncodedValues.add(JwtUtil.percentEncode(value));
                }
                into.write(StringUtils.join(percentEncodedValues, (char)',').getBytes());
            }
        }
    }

    private static String safeToString(Object from) {
        return null == from ? null : from.toString();
    }

    private static class ComparableParameter
    implements Comparable<ComparableParameter> {
        final Map.Entry<String, String[]> parameter;
        private final String key;

        ComparableParameter(Map.Entry<String, String[]> parameter) throws UnsupportedEncodingException {
            this.parameter = parameter;
            String name = HttpRequestCanonicalizer.safeToString(parameter.getKey());
            List<Object> sortedValues = Arrays.asList((Object[])parameter.getValue());
            Collections.sort(sortedValues);
            String value = StringUtils.join(sortedValues, (char)',');
            this.key = JwtUtil.percentEncode(name) + ' ' + JwtUtil.percentEncode(value);
        }

        @Override
        public int compareTo(ComparableParameter that) {
            return this.key.compareTo(that.key);
        }

        public String toString() {
            return this.key;
        }
    }
}

