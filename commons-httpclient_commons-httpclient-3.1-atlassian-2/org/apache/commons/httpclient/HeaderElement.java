/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.ParameterParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HeaderElement
extends NameValuePair {
    private static final Log LOG = LogFactory.getLog(HeaderElement.class);
    private NameValuePair[] parameters = null;

    public HeaderElement() {
        this(null, null, null);
    }

    public HeaderElement(String name, String value) {
        this(name, value, null);
    }

    public HeaderElement(String name, String value, NameValuePair[] parameters) {
        super(name, value);
        this.parameters = parameters;
    }

    public HeaderElement(char[] chars, int offset, int length) {
        this();
        if (chars == null) {
            return;
        }
        ParameterParser parser = new ParameterParser();
        List params = parser.parse(chars, offset, length, ';');
        if (params.size() > 0) {
            NameValuePair element = (NameValuePair)params.remove(0);
            this.setName(element.getName());
            this.setValue(element.getValue());
            if (params.size() > 0) {
                this.parameters = params.toArray(new NameValuePair[params.size()]);
            }
        }
    }

    public HeaderElement(char[] chars) {
        this(chars, 0, chars.length);
    }

    public NameValuePair[] getParameters() {
        return this.parameters;
    }

    public static final HeaderElement[] parseElements(char[] headerValue) {
        LOG.trace((Object)"enter HeaderElement.parseElements(char[])");
        if (headerValue == null) {
            return new HeaderElement[0];
        }
        ArrayList<NameValuePair> elements = new ArrayList<NameValuePair>();
        int from = 0;
        int len = headerValue.length;
        boolean qouted = false;
        for (int i = 0; i < len; ++i) {
            char ch = headerValue[i];
            if (ch == '\"') {
                qouted = !qouted;
            }
            NameValuePair element = null;
            if (!qouted && ch == ',') {
                element = new HeaderElement(headerValue, from, i);
                from = i + 1;
            } else if (i == len - 1) {
                element = new HeaderElement(headerValue, from, len);
            }
            if (element == null || element.getName() == null) continue;
            elements.add(element);
        }
        return elements.toArray(new HeaderElement[elements.size()]);
    }

    public static final HeaderElement[] parseElements(String headerValue) {
        LOG.trace((Object)"enter HeaderElement.parseElements(String)");
        if (headerValue == null) {
            return new HeaderElement[0];
        }
        return HeaderElement.parseElements(headerValue.toCharArray());
    }

    public static final HeaderElement[] parse(String headerValue) throws HttpException {
        LOG.trace((Object)"enter HeaderElement.parse(String)");
        if (headerValue == null) {
            return new HeaderElement[0];
        }
        return HeaderElement.parseElements(headerValue.toCharArray());
    }

    public NameValuePair getParameterByName(String name) {
        LOG.trace((Object)"enter HeaderElement.getParameterByName(String)");
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        NameValuePair found = null;
        NameValuePair[] parameters = this.getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                NameValuePair current = parameters[i];
                if (!current.getName().equalsIgnoreCase(name)) continue;
                found = current;
                break;
            }
        }
        return found;
    }
}

