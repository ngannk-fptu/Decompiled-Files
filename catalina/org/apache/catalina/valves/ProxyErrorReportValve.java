/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  org.apache.coyote.ActionCode
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletOutputStream;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.coyote.ActionCode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;

public class ProxyErrorReportValve
extends ErrorReportValve {
    private static final Log log = LogFactory.getLog(ProxyErrorReportValve.class);
    protected boolean useRedirect = true;
    protected boolean usePropertiesFile = false;

    public boolean getUseRedirect() {
        return this.useRedirect;
    }

    public void setUseRedirect(boolean useRedirect) {
        this.useRedirect = useRedirect;
    }

    public boolean getUsePropertiesFile() {
        return this.usePropertiesFile;
    }

    public void setUsePropertiesFile(boolean usePropertiesFile) {
        this.usePropertiesFile = usePropertiesFile;
    }

    private String getRedirectUrl(Response response) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(this.getClass().getSimpleName(), response.getLocale());
        String redirectUrl = null;
        try {
            redirectUrl = resourceBundle.getString(Integer.toString(response.getStatus()));
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        if (redirectUrl == null) {
            try {
                redirectUrl = resourceBundle.getString(Integer.toString(0));
            }
            catch (MissingResourceException missingResourceException) {
                // empty catch block
            }
        }
        return redirectUrl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void report(Request request, Response response, Throwable throwable) {
        int statusCode = response.getStatus();
        if (statusCode < 400 || response.getContentWritten() > 0L) {
            return;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, (Object)result);
        if (!result.get()) {
            return;
        }
        String urlString = null;
        if (this.usePropertiesFile) {
            urlString = this.getRedirectUrl(response);
        } else {
            ErrorPage errorPage = this.findErrorPage(statusCode, throwable);
            if (errorPage != null) {
                urlString = errorPage.getLocation();
            }
        }
        if (urlString == null) {
            super.report(request, response, throwable);
            return;
        }
        if (!response.setErrorReported()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(urlString);
        if (urlString.indexOf("?") > -1) {
            stringBuilder.append('&');
        } else {
            stringBuilder.append('?');
        }
        try {
            stringBuilder.append("requestUri=");
            stringBuilder.append(URLEncoder.encode(request.getDecodedRequestURI(), request.getConnector().getURIEncoding()));
            stringBuilder.append("&statusCode=");
            stringBuilder.append(URLEncoder.encode(String.valueOf(statusCode), "UTF-8"));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        String reason = null;
        String description = null;
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.valves", request.getLocales());
        response.setLocale(smClient.getLocale());
        try {
            reason = smClient.getString("http." + statusCode + ".reason");
            description = smClient.getString("http." + statusCode + ".desc");
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
        }
        if (reason == null || description == null) {
            reason = smClient.getString("errorReportValve.unknownReason");
            description = smClient.getString("errorReportValve.noDescription");
        }
        try {
            stringBuilder.append("&statusDescription=");
            stringBuilder.append(URLEncoder.encode(description, "UTF-8"));
            stringBuilder.append("&statusReason=");
            stringBuilder.append(URLEncoder.encode(reason, "UTF-8"));
            String message = response.getMessage();
            if (message != null) {
                stringBuilder.append("&message=");
                stringBuilder.append(URLEncoder.encode(message, "UTF-8"));
            }
            if (throwable != null) {
                stringBuilder.append("&throwable=");
                stringBuilder.append(URLEncoder.encode(throwable.toString(), "UTF-8"));
            }
        }
        catch (UnsupportedEncodingException message) {
            // empty catch block
        }
        urlString = stringBuilder.toString();
        if (this.useRedirect) {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Redirecting error reporting to " + urlString));
            }
            try {
                response.sendRedirect(urlString);
            }
            catch (IOException message) {}
        } else {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Proxying error reporting to " + urlString));
            }
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URI(urlString).toURL();
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.connect();
                response.setContentType(httpURLConnection.getContentType());
                response.setContentLength(httpURLConnection.getContentLength());
                ServletOutputStream outputStream = response.getOutputStream();
                InputStream inputStream = url.openStream();
                IOTools.flow(inputStream, (OutputStream)outputStream);
            }
            catch (IOException | IllegalArgumentException | URISyntaxException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Proxy error to " + urlString), (Throwable)e);
                }
            }
            finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
    }
}

