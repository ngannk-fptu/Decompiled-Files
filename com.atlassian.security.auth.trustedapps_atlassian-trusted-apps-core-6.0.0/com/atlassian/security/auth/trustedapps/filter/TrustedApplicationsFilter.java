/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.bouncycastle.util.encoders.Base64
 */
package com.atlassian.security.auth.trustedapps.filter;

import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.UserResolver;
import com.atlassian.security.auth.trustedapps.filter.AuthenticationController;
import com.atlassian.security.auth.trustedapps.filter.AuthenticationListener;
import com.atlassian.security.auth.trustedapps.filter.Authenticator;
import com.atlassian.security.auth.trustedapps.filter.TrustedApplicationFilterAuthenticator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.PublicKey;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bouncycastle.util.encoders.Base64;

@Deprecated
public class TrustedApplicationsFilter
implements Filter {
    private final CertificateServer certificateServer;
    private final Authenticator authenticator;
    private FilterConfig filterConfig = null;
    private final AuthenticationController authenticationController;
    private final AuthenticationListener authenticationListener;

    public TrustedApplicationsFilter(TrustedApplicationsManager appManager, UserResolver resolver, AuthenticationController authenticationController, AuthenticationListener authenticationListener) {
        this(new CertificateServerImpl(appManager), new TrustedApplicationFilterAuthenticator(appManager, resolver, authenticationController), authenticationController, authenticationListener);
    }

    protected TrustedApplicationsFilter(CertificateServer certificateServer, Authenticator authenticator, AuthenticationController authenticationController, AuthenticationListener authenticationListener) {
        Null.not("certificateServer", certificateServer);
        Null.not("authenticator", authenticator);
        Null.not("authenticationController", authenticationController);
        Null.not("authenticationListener", authenticationListener);
        this.certificateServer = certificateServer;
        this.authenticator = authenticator;
        this.authenticationController = authenticationController;
        this.authenticationListener = authenticationListener;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        if (this.getPathInfo(request).endsWith("/admin/appTrustCertificate")) {
            response.setContentType("text/plain");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)response.getOutputStream());
            this.certificateServer.writeCertificate(outputStreamWriter);
            outputStreamWriter.flush();
            return;
        }
        boolean isTrustedAppCall = this.authenticate(request, response);
        try {
            chain.doFilter((ServletRequest)request, res);
        }
        finally {
            if (isTrustedAppCall && request.getSession(false) != null) {
                request.getSession().invalidate();
            }
        }
    }

    boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        if (this.authenticationController.shouldAttemptAuthentication(request)) {
            Authenticator.Result result = this.authenticator.authenticate(request, response);
            if (result.getStatus() == Authenticator.Result.Status.SUCCESS) {
                this.authenticationListener.authenticationSuccess(result, request, response);
                response.setHeader("X-Seraph-Trusted-App-Status", "OK");
                return true;
            }
            if (result.getStatus() == Authenticator.Result.Status.FAILED) {
                this.authenticationListener.authenticationFailure(result, request, response);
            } else {
                this.authenticationListener.authenticationError(result, request, response);
            }
        } else {
            this.authenticationListener.authenticationNotAttempted(request, response);
        }
        return false;
    }

    protected String getPathInfo(HttpServletRequest request) {
        String context = request.getContextPath();
        String uri = request.getRequestURI();
        if (context != null && context.length() > 0) {
            return uri.substring(context.length());
        }
        return uri;
    }

    public void init(FilterConfig config) {
        this.filterConfig = config;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        if (filterConfig != null) {
            this.init(filterConfig);
        }
    }

    public static class CertificateServerImpl
    implements CertificateServer {
        final TrustedApplicationsManager appManager;

        public CertificateServerImpl(TrustedApplicationsManager appManager) {
            this.appManager = appManager;
        }

        @Override
        public void writeCertificate(Writer writer) throws IOException {
            CurrentApplication currentApplication = this.appManager.getCurrentApplication();
            PublicKey publicKey = currentApplication.getPublicKey();
            try {
                writer.write(currentApplication.getID());
                writer.write("\n");
                byte[] key = publicKey.getEncoded();
                writer.write(new String(Base64.encode((byte[])key), "utf-8"));
                writer.write("\n");
                writer.write(TrustedApplicationUtils.getProtocolVersionInUse().toString());
                writer.write("\n");
                writer.write(TrustedApplicationUtils.Constant.MAGIC);
                writer.flush();
            }
            catch (UnsupportedEncodingException ex) {
                throw new AssertionError((Object)ex);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Deprecated
    public static interface CertificateServer {
        public void writeCertificate(Writer var1) throws IOException;
    }

    static final class Status {
        static final String ERROR = "ERROR";
        static final String OK = "OK";

        Status() {
        }
    }
}

