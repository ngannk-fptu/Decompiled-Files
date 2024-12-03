/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class Http
extends ProjectComponent
implements Condition {
    private static final int ERROR_BEGINS = 400;
    private static final String DEFAULT_REQUEST_METHOD = "GET";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private String spec = null;
    private String requestMethod = "GET";
    private boolean followRedirects = true;
    private int errorsBeginAt = 400;
    private int readTimeout = 0;

    public void setUrl(String url) {
        this.spec = url;
    }

    public void setErrorsBeginAt(int errorsBeginAt) {
        this.errorsBeginAt = errorsBeginAt;
    }

    public void setRequestMethod(String method) {
        this.requestMethod = method == null ? DEFAULT_REQUEST_METHOD : method.toUpperCase(Locale.ENGLISH);
    }

    public void setFollowRedirects(boolean f) {
        this.followRedirects = f;
    }

    public void setReadTimeout(int t) {
        if (t >= 0) {
            this.readTimeout = t;
        }
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.spec == null) {
            throw new BuildException("No url specified in http condition");
        }
        this.log("Checking for " + this.spec, 3);
        try {
            URL url = new URL(this.spec);
            try {
                URLConnection conn = url.openConnection();
                if (conn instanceof HttpURLConnection) {
                    int code = this.request((HttpURLConnection)conn, url);
                    this.log("Result code for " + this.spec + " was " + code, 3);
                    return code > 0 && code < this.errorsBeginAt;
                }
            }
            catch (ProtocolException pe) {
                throw new BuildException("Invalid HTTP protocol: " + this.requestMethod, pe);
            }
            catch (IOException e) {
                return false;
            }
        }
        catch (MalformedURLException e) {
            throw new BuildException("Badly formed URL: " + this.spec, e);
        }
        return true;
    }

    private int request(HttpURLConnection http, URL url) throws IOException {
        URLConnection newConn;
        String newLocation;
        URL newURL;
        http.setRequestMethod(this.requestMethod);
        http.setInstanceFollowRedirects(this.followRedirects);
        http.setReadTimeout(this.readTimeout);
        int firstStatusCode = http.getResponseCode();
        if (this.followRedirects && Get.isMoved(firstStatusCode) && this.redirectionAllowed(url, newURL = new URL(newLocation = http.getHeaderField("Location"))) && (newConn = newURL.openConnection()) instanceof HttpURLConnection) {
            this.log("Following redirect from " + url + " to " + newURL);
            return this.request((HttpURLConnection)newConn, newURL);
        }
        return firstStatusCode;
    }

    private boolean redirectionAllowed(URL from, URL to) {
        if (from.equals(to)) {
            return false;
        }
        if (!(from.getProtocol().equals(to.getProtocol()) || HTTP.equals(from.getProtocol()) && HTTPS.equals(to.getProtocol()))) {
            this.log("Redirection detected from " + from.getProtocol() + " to " + to.getProtocol() + ". Protocol switch unsafe, not allowed.");
            return false;
        }
        return true;
    }
}

