/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods;

import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OptionsMethod
extends HttpMethodBase {
    private static final Log LOG = LogFactory.getLog(OptionsMethod.class);
    private Vector methodsAllowed = new Vector();

    public OptionsMethod() {
    }

    public OptionsMethod(String uri) {
        super(uri);
    }

    @Override
    public String getName() {
        return "OPTIONS";
    }

    public boolean isAllowed(String method) {
        this.checkUsed();
        return this.methodsAllowed.contains(method);
    }

    public Enumeration getAllowedMethods() {
        this.checkUsed();
        return this.methodsAllowed.elements();
    }

    @Override
    protected void processResponseHeaders(HttpState state, HttpConnection conn) {
        LOG.trace((Object)"enter OptionsMethod.processResponseHeaders(HttpState, HttpConnection)");
        Header allowHeader = this.getResponseHeader("allow");
        if (allowHeader != null) {
            String allowHeaderValue = allowHeader.getValue();
            StringTokenizer tokenizer = new StringTokenizer(allowHeaderValue, ",");
            while (tokenizer.hasMoreElements()) {
                String methodAllowed = tokenizer.nextToken().trim().toUpperCase(Locale.ENGLISH);
                this.methodsAllowed.addElement(methodAllowed);
            }
        }
    }

    public boolean needContentLength() {
        return false;
    }
}

