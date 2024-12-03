/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLInputSource;

public final class HTTPInputSource
extends XMLInputSource {
    protected boolean fFollowRedirects = true;
    protected Map fHTTPRequestProperties = new HashMap();

    public HTTPInputSource(String string, String string2, String string3) {
        super(string, string2, string3);
    }

    public HTTPInputSource(XMLResourceIdentifier xMLResourceIdentifier) {
        super(xMLResourceIdentifier);
    }

    public HTTPInputSource(String string, String string2, String string3, InputStream inputStream, String string4) {
        super(string, string2, string3, inputStream, string4);
    }

    public HTTPInputSource(String string, String string2, String string3, Reader reader, String string4) {
        super(string, string2, string3, reader, string4);
    }

    public boolean getFollowHTTPRedirects() {
        return this.fFollowRedirects;
    }

    public void setFollowHTTPRedirects(boolean bl) {
        this.fFollowRedirects = bl;
    }

    public String getHTTPRequestProperty(String string) {
        return (String)this.fHTTPRequestProperties.get(string);
    }

    public Iterator getHTTPRequestProperties() {
        return this.fHTTPRequestProperties.entrySet().iterator();
    }

    public void setHTTPRequestProperty(String string, String string2) {
        if (string2 != null) {
            this.fHTTPRequestProperties.put(string, string2);
        } else {
            this.fHTTPRequestProperties.remove(string);
        }
    }
}

