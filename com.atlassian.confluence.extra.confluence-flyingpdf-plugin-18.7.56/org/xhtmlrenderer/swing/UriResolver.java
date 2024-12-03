/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.xhtmlrenderer.util.XRLog;

public class UriResolver {
    private String _baseUri;

    public String resolve(String uri) {
        if (uri == null) {
            return null;
        }
        String ret = null;
        if (this._baseUri == null) {
            try {
                URL result = new URL(uri);
                this.setBaseUri(result.toExternalForm());
            }
            catch (MalformedURLException e) {
                try {
                    this.setBaseUri(new File(".").toURI().toURL().toExternalForm());
                }
                catch (Exception e1) {
                    XRLog.exception("The default NaiveUserAgent doesn't know how to resolve the base URL for " + uri);
                    return null;
                }
            }
        }
        try {
            return new URL(uri).toString();
        }
        catch (MalformedURLException e) {
            XRLog.load(Level.FINE, "Could not read " + uri + " as a URL; may be relative. Testing using parent URL " + this._baseUri);
            try {
                URL result = new URL(new URL(this._baseUri), uri);
                ret = result.toString();
                XRLog.load(Level.FINE, "Was able to read from " + uri + " using parent URL " + this._baseUri);
            }
            catch (MalformedURLException e1) {
                XRLog.exception("The default NaiveUserAgent cannot resolve the URL " + uri + " with base URL " + this._baseUri);
            }
            return ret;
        }
    }

    public void setBaseUri(String baseUri) {
        this._baseUri = baseUri;
    }

    public String getBaseUri() {
        return this._baseUri;
    }
}

