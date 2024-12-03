/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.tomcat.util.http;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.TokenList;

public class ResponseUtil {
    private static final String VARY_HEADER = "vary";
    private static final String VARY_ALL = "*";

    private ResponseUtil() {
    }

    public static void addVaryFieldName(MimeHeaders headers, String name) {
        ResponseUtil.addVaryFieldName(new HeaderAdapter(headers), name);
    }

    public static void addVaryFieldName(HttpServletResponse response, String name) {
        ResponseUtil.addVaryFieldName(new ResponseAdapter(response), name);
    }

    private static void addVaryFieldName(Adapter adapter, String name) {
        Collection<String> varyHeaders = adapter.getHeaders(VARY_HEADER);
        if (varyHeaders.size() == 1 && varyHeaders.iterator().next().trim().equals(VARY_ALL)) {
            return;
        }
        if (varyHeaders.size() == 0) {
            adapter.addHeader(VARY_HEADER, name);
            return;
        }
        if (VARY_ALL.equals(name.trim())) {
            adapter.setHeader(VARY_HEADER, VARY_ALL);
            return;
        }
        LinkedHashSet<String> fieldNames = new LinkedHashSet<String>();
        for (String varyHeader : varyHeaders) {
            StringReader input = new StringReader(varyHeader);
            try {
                TokenList.parseTokenList(input, fieldNames);
            }
            catch (IOException iOException) {}
        }
        if (fieldNames.contains(VARY_ALL)) {
            adapter.setHeader(VARY_HEADER, VARY_ALL);
            return;
        }
        fieldNames.add(name);
        StringBuilder varyHeader = new StringBuilder();
        Iterator iter = fieldNames.iterator();
        varyHeader.append((String)iter.next());
        while (iter.hasNext()) {
            varyHeader.append(',');
            varyHeader.append((String)iter.next());
        }
        adapter.setHeader(VARY_HEADER, varyHeader.toString());
    }

    private static final class HeaderAdapter
    implements Adapter {
        private final MimeHeaders headers;

        HeaderAdapter(MimeHeaders headers) {
            this.headers = headers;
        }

        @Override
        public Collection<String> getHeaders(String name) {
            Enumeration<String> values = this.headers.values(name);
            ArrayList<String> result = new ArrayList<String>();
            while (values.hasMoreElements()) {
                result.add(values.nextElement());
            }
            return result;
        }

        @Override
        public void setHeader(String name, String value) {
            this.headers.setValue(name).setString(value);
        }

        @Override
        public void addHeader(String name, String value) {
            this.headers.addValue(name).setString(value);
        }
    }

    private static interface Adapter {
        public Collection<String> getHeaders(String var1);

        public void setHeader(String var1, String var2);

        public void addHeader(String var1, String var2);
    }

    private static final class ResponseAdapter
    implements Adapter {
        private final HttpServletResponse response;

        ResponseAdapter(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return this.response.getHeaders(name);
        }

        @Override
        public void setHeader(String name, String value) {
            this.response.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            this.response.addHeader(name, value);
        }
    }
}

