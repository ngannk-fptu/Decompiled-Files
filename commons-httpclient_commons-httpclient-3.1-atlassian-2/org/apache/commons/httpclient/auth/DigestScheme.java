/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.auth.RFC2617Scheme;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.util.ParameterFormatter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DigestScheme
extends RFC2617Scheme {
    private static final Log LOG = LogFactory.getLog(DigestScheme.class);
    private static final char[] HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private boolean complete = false;
    private static final String NC = "00000001";
    private static final int QOP_MISSING = 0;
    private static final int QOP_AUTH_INT = 1;
    private static final int QOP_AUTH = 2;
    private int qopVariant = 0;
    private String cnonce;
    private final ParameterFormatter formatter = new ParameterFormatter();

    public DigestScheme() {
    }

    @Override
    public String getID() {
        String id = this.getRealm();
        String nonce = this.getParameter("nonce");
        if (nonce != null) {
            id = id + "-" + nonce;
        }
        return id;
    }

    public DigestScheme(String challenge) throws MalformedChallengeException {
        this();
        this.processChallenge(challenge);
    }

    @Override
    public void processChallenge(String challenge) throws MalformedChallengeException {
        super.processChallenge(challenge);
        if (this.getParameter("realm") == null) {
            throw new MalformedChallengeException("missing realm in challange");
        }
        if (this.getParameter("nonce") == null) {
            throw new MalformedChallengeException("missing nonce in challange");
        }
        boolean unsupportedQop = false;
        String qop = this.getParameter("qop");
        if (qop != null) {
            StringTokenizer tok = new StringTokenizer(qop, ",");
            while (tok.hasMoreTokens()) {
                String variant = tok.nextToken().trim();
                if (variant.equals("auth")) {
                    this.qopVariant = 2;
                    break;
                }
                if (variant.equals("auth-int")) {
                    this.qopVariant = 1;
                    continue;
                }
                unsupportedQop = true;
                LOG.warn((Object)("Unsupported qop detected: " + variant));
            }
        }
        if (unsupportedQop && this.qopVariant == 0) {
            throw new MalformedChallengeException("None of the qop methods is supported");
        }
        this.cnonce = DigestScheme.createCnonce();
        this.complete = true;
    }

    @Override
    public boolean isComplete() {
        String s = this.getParameter("stale");
        if ("true".equalsIgnoreCase(s)) {
            return false;
        }
        return this.complete;
    }

    @Override
    public String getSchemeName() {
        return "digest";
    }

    @Override
    public boolean isConnectionBased() {
        return false;
    }

    @Override
    public String authenticate(Credentials credentials, String method, String uri) throws AuthenticationException {
        LOG.trace((Object)"enter DigestScheme.authenticate(Credentials, String, String)");
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for digest authentication: " + credentials.getClass().getName());
        }
        this.getParameters().put("methodname", method);
        this.getParameters().put("uri", uri);
        String digest = this.createDigest(usernamepassword.getUserName(), usernamepassword.getPassword());
        return "Digest " + this.createDigestHeader(usernamepassword.getUserName(), digest);
    }

    @Override
    public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
        LOG.trace((Object)"enter DigestScheme.authenticate(Credentials, HttpMethod)");
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for digest authentication: " + credentials.getClass().getName());
        }
        this.getParameters().put("methodname", method.getName());
        StringBuffer buffer = new StringBuffer(method.getPath());
        String query = method.getQueryString();
        if (query != null) {
            if (query.indexOf("?") != 0) {
                buffer.append("?");
            }
            buffer.append(method.getQueryString());
        }
        this.getParameters().put("uri", buffer.toString());
        String charset = this.getParameter("charset");
        if (charset == null) {
            this.getParameters().put("charset", method.getParams().getCredentialCharset());
        }
        String digest = this.createDigest(usernamepassword.getUserName(), usernamepassword.getPassword());
        return "Digest " + this.createDigestHeader(usernamepassword.getUserName(), digest);
    }

    private String createDigest(String uname, String pwd) throws AuthenticationException {
        String serverDigestValue;
        MessageDigest md5Helper;
        String charset;
        LOG.trace((Object)"enter DigestScheme.createDigest(String, String, Map)");
        String digAlg = "MD5";
        String uri = this.getParameter("uri");
        String realm = this.getParameter("realm");
        String nonce = this.getParameter("nonce");
        String qop = this.getParameter("qop");
        String method = this.getParameter("methodname");
        String algorithm = this.getParameter("algorithm");
        if (algorithm == null) {
            algorithm = "MD5";
        }
        if ((charset = this.getParameter("charset")) == null) {
            charset = "ISO-8859-1";
        }
        if (this.qopVariant == 1) {
            LOG.warn((Object)"qop=auth-int is not supported");
            throw new AuthenticationException("Unsupported qop in HTTP Digest authentication");
        }
        try {
            md5Helper = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
            throw new AuthenticationException("Unsupported algorithm in HTTP Digest authentication: MD5");
        }
        StringBuffer tmp = new StringBuffer(uname.length() + realm.length() + pwd.length() + 2);
        tmp.append(uname);
        tmp.append(':');
        tmp.append(realm);
        tmp.append(':');
        tmp.append(pwd);
        String a1 = tmp.toString();
        if (algorithm.equals("MD5-sess")) {
            String tmp2 = DigestScheme.encode(md5Helper.digest(EncodingUtil.getBytes(a1, charset)));
            StringBuffer tmp3 = new StringBuffer(tmp2.length() + nonce.length() + this.cnonce.length() + 2);
            tmp3.append(tmp2);
            tmp3.append(':');
            tmp3.append(nonce);
            tmp3.append(':');
            tmp3.append(this.cnonce);
            a1 = tmp3.toString();
        } else if (!algorithm.equals("MD5")) {
            LOG.warn((Object)("Unhandled algorithm " + algorithm + " requested"));
        }
        String md5a1 = DigestScheme.encode(md5Helper.digest(EncodingUtil.getBytes(a1, charset)));
        String a2 = null;
        if (this.qopVariant == 1) {
            LOG.error((Object)"Unhandled qop auth-int");
        } else {
            a2 = method + ":" + uri;
        }
        String md5a2 = DigestScheme.encode(md5Helper.digest(EncodingUtil.getAsciiBytes(a2)));
        if (this.qopVariant == 0) {
            LOG.debug((Object)"Using null qop method");
            StringBuffer tmp2 = new StringBuffer(md5a1.length() + nonce.length() + md5a2.length());
            tmp2.append(md5a1);
            tmp2.append(':');
            tmp2.append(nonce);
            tmp2.append(':');
            tmp2.append(md5a2);
            serverDigestValue = tmp2.toString();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Using qop method " + qop));
            }
            String qopOption = this.getQopVariantString();
            StringBuffer tmp2 = new StringBuffer(md5a1.length() + nonce.length() + NC.length() + this.cnonce.length() + qopOption.length() + md5a2.length() + 5);
            tmp2.append(md5a1);
            tmp2.append(':');
            tmp2.append(nonce);
            tmp2.append(':');
            tmp2.append(NC);
            tmp2.append(':');
            tmp2.append(this.cnonce);
            tmp2.append(':');
            tmp2.append(qopOption);
            tmp2.append(':');
            tmp2.append(md5a2);
            serverDigestValue = tmp2.toString();
        }
        String serverDigest = DigestScheme.encode(md5Helper.digest(EncodingUtil.getAsciiBytes(serverDigestValue)));
        return serverDigest;
    }

    private String createDigestHeader(String uname, String digest) throws AuthenticationException {
        LOG.trace((Object)"enter DigestScheme.createDigestHeader(String, Map, String)");
        String uri = this.getParameter("uri");
        String realm = this.getParameter("realm");
        String nonce = this.getParameter("nonce");
        String opaque = this.getParameter("opaque");
        String response = digest;
        String algorithm = this.getParameter("algorithm");
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(20);
        params.add(new NameValuePair("username", uname));
        params.add(new NameValuePair("realm", realm));
        params.add(new NameValuePair("nonce", nonce));
        params.add(new NameValuePair("uri", uri));
        params.add(new NameValuePair("response", response));
        if (this.qopVariant != 0) {
            params.add(new NameValuePair("qop", this.getQopVariantString()));
            params.add(new NameValuePair("nc", NC));
            params.add(new NameValuePair("cnonce", this.cnonce));
        }
        if (algorithm != null) {
            params.add(new NameValuePair("algorithm", algorithm));
        }
        if (opaque != null) {
            params.add(new NameValuePair("opaque", opaque));
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < params.size(); ++i) {
            NameValuePair param = (NameValuePair)params.get(i);
            if (i > 0) {
                buffer.append(", ");
            }
            boolean noQuotes = "nc".equals(param.getName()) || "qop".equals(param.getName());
            this.formatter.setAlwaysUseQuotes(!noQuotes);
            this.formatter.format(buffer, param);
        }
        return buffer.toString();
    }

    private String getQopVariantString() {
        String qopOption = this.qopVariant == 1 ? "auth-int" : "auth";
        return qopOption;
    }

    private static String encode(byte[] binaryData) {
        LOG.trace((Object)"enter DigestScheme.encode(byte[])");
        if (binaryData.length != 16) {
            return null;
        }
        char[] buffer = new char[32];
        for (int i = 0; i < 16; ++i) {
            int low = binaryData[i] & 0xF;
            int high = (binaryData[i] & 0xF0) >> 4;
            buffer[i * 2] = HEXADECIMAL[high];
            buffer[i * 2 + 1] = HEXADECIMAL[low];
        }
        return new String(buffer);
    }

    public static String createCnonce() {
        MessageDigest md5Helper;
        LOG.trace((Object)"enter DigestScheme.createCnonce()");
        String digAlg = "MD5";
        try {
            md5Helper = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new HttpClientError("Unsupported algorithm in HTTP Digest authentication: MD5");
        }
        String cnonce = Long.toString(System.currentTimeMillis());
        cnonce = DigestScheme.encode(md5Helper.digest(EncodingUtil.getAsciiBytes(cnonce)));
        return cnonce;
    }
}

