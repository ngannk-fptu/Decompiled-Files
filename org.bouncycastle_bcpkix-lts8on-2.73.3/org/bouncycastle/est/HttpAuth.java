/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.encoders.Base64
 *  org.bouncycastle.util.encoders.Hex
 */
package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.est.ESTAuth;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTHijacker;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTResponse;
import org.bouncycastle.est.HttpUtil;
import org.bouncycastle.est.Source;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class HttpAuth
implements ESTAuth {
    private static final DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder = new DefaultDigestAlgorithmIdentifierFinder();
    private final String realm;
    private final String username;
    private final char[] password;
    private final SecureRandom nonceGenerator;
    private final DigestCalculatorProvider digestCalculatorProvider;
    private static final Set<String> validParts;

    public HttpAuth(String username, char[] password) {
        this(null, username, password, null, null);
    }

    public HttpAuth(String realm, String username, char[] password) {
        this(realm, username, password, null, null);
    }

    public HttpAuth(String username, char[] password, SecureRandom nonceGenerator, DigestCalculatorProvider digestCalculatorProvider) {
        this(null, username, password, nonceGenerator, digestCalculatorProvider);
    }

    public HttpAuth(String realm, String username, char[] password, SecureRandom nonceGenerator, DigestCalculatorProvider digestCalculatorProvider) {
        this.realm = realm;
        this.username = username;
        this.password = password;
        this.nonceGenerator = nonceGenerator;
        this.digestCalculatorProvider = digestCalculatorProvider;
    }

    @Override
    public void applyAuth(ESTRequestBuilder reqBldr) {
        reqBldr.withHijacker(new ESTHijacker(){

            @Override
            public ESTResponse hijack(ESTRequest req, Source sock) throws IOException {
                ESTResponse res = new ESTResponse(req, sock);
                if (res.getStatusCode() == 401) {
                    String authHeader = res.getHeader("WWW-Authenticate");
                    if (authHeader == null) {
                        throw new ESTException("Status of 401 but no WWW-Authenticate header");
                    }
                    if ((authHeader = Strings.toLowerCase((String)authHeader)).startsWith("digest")) {
                        res = HttpAuth.this.doDigestFunction(res);
                    } else if (authHeader.startsWith("basic")) {
                        res.close();
                        Map<String, String> s = HttpUtil.splitCSL("Basic", res.getHeader("WWW-Authenticate"));
                        if (HttpAuth.this.realm != null && !HttpAuth.this.realm.equals(s.get("realm"))) {
                            throw new ESTException("Supplied realm '" + HttpAuth.this.realm + "' does not match server realm '" + s.get("realm") + "'", null, 401, null);
                        }
                        ESTRequestBuilder answer = new ESTRequestBuilder(req).withHijacker(null);
                        if (HttpAuth.this.realm != null && HttpAuth.this.realm.length() > 0) {
                            answer.setHeader("WWW-Authenticate", "Basic realm=\"" + HttpAuth.this.realm + "\"");
                        }
                        if (HttpAuth.this.username.contains(":")) {
                            throw new IllegalArgumentException("User must not contain a ':'");
                        }
                        char[] userPass = new char[HttpAuth.this.username.length() + 1 + HttpAuth.this.password.length];
                        System.arraycopy(HttpAuth.this.username.toCharArray(), 0, userPass, 0, HttpAuth.this.username.length());
                        userPass[((HttpAuth)HttpAuth.this).username.length()] = 58;
                        System.arraycopy(HttpAuth.this.password, 0, userPass, HttpAuth.this.username.length() + 1, HttpAuth.this.password.length);
                        answer.setHeader("Authorization", "Basic " + Base64.toBase64String((byte[])Strings.toByteArray((char[])userPass)));
                        res = req.getClient().doRequest(answer.build());
                        Arrays.fill((char[])userPass, (char)'\u0000');
                    } else {
                        throw new ESTException("Unknown auth mode: " + authHeader);
                    }
                    return res;
                }
                return res;
            }
        });
    }

    private ESTResponse doDigestFunction(ESTResponse res) throws IOException {
        res.close();
        ESTRequest req = res.getOriginalRequest();
        Map<String, String> parts = null;
        try {
            parts = HttpUtil.splitCSL("Digest", res.getHeader("WWW-Authenticate"));
        }
        catch (Throwable t) {
            throw new ESTException("Parsing WWW-Authentication header: " + t.getMessage(), t, res.getStatusCode(), new ByteArrayInputStream(res.getHeader("WWW-Authenticate").getBytes()));
        }
        String uri = null;
        try {
            uri = req.getURL().toURI().getPath();
        }
        catch (Exception e) {
            throw new IOException("unable to process URL in request: " + e.getMessage());
        }
        for (String k : parts.keySet()) {
            if (validParts.contains(k)) continue;
            throw new ESTException("Unrecognised entry in WWW-Authenticate header: '" + k + "'");
        }
        String method = req.getMethod();
        String realm = parts.get("realm");
        String nonce = parts.get("nonce");
        String opaque = parts.get("opaque");
        String algorithm = parts.get("algorithm");
        String qop = parts.get("qop");
        ArrayList<String> qopMods = new ArrayList<String>();
        if (this.realm != null && !this.realm.equals(realm)) {
            throw new ESTException("Supplied realm '" + this.realm + "' does not match server realm '" + realm + "'", null, 401, null);
        }
        if (algorithm == null) {
            algorithm = "MD5";
        }
        if (algorithm.length() == 0) {
            throw new ESTException("WWW-Authenticate no algorithm defined.");
        }
        algorithm = Strings.toUpperCase((String)algorithm);
        if (qop != null) {
            if (qop.length() == 0) {
                throw new ESTException("QoP value is empty.");
            }
            qop = Strings.toLowerCase((String)qop);
            String[] s = qop.split(",");
            for (int j = 0; j != s.length; ++j) {
                if (!s[j].equals("auth") && !s[j].equals("auth-int")) {
                    throw new ESTException("QoP value unknown: '" + j + "'");
                }
                String jt = s[j].trim();
                if (qopMods.contains(jt)) continue;
                qopMods.add(jt);
            }
        } else {
            throw new ESTException("Qop is not defined in WWW-Authenticate header.");
        }
        AlgorithmIdentifier digestAlg = this.lookupDigest(algorithm);
        if (digestAlg == null || digestAlg.getAlgorithm() == null) {
            throw new IOException("auth digest algorithm unknown: " + algorithm);
        }
        DigestCalculator dCalc = this.getDigestCalculator(algorithm, digestAlg);
        OutputStream dOut = dCalc.getOutputStream();
        String crnonce = this.makeNonce(10);
        this.update(dOut, this.username);
        this.update(dOut, ":");
        this.update(dOut, realm);
        this.update(dOut, ":");
        this.update(dOut, this.password);
        dOut.close();
        byte[] ha1 = dCalc.getDigest();
        if (algorithm.endsWith("-SESS")) {
            DigestCalculator sessCalc = this.getDigestCalculator(algorithm, digestAlg);
            OutputStream sessOut = sessCalc.getOutputStream();
            String cs = Hex.toHexString((byte[])ha1);
            this.update(sessOut, cs);
            this.update(sessOut, ":");
            this.update(sessOut, nonce);
            this.update(sessOut, ":");
            this.update(sessOut, crnonce);
            sessOut.close();
            ha1 = sessCalc.getDigest();
        }
        String hashHa1 = Hex.toHexString((byte[])ha1);
        DigestCalculator authCalc = this.getDigestCalculator(algorithm, digestAlg);
        OutputStream authOut = authCalc.getOutputStream();
        if (((String)qopMods.get(0)).equals("auth-int")) {
            DigestCalculator reqCalc = this.getDigestCalculator(algorithm, digestAlg);
            OutputStream reqOut = reqCalc.getOutputStream();
            req.writeData(reqOut);
            reqOut.close();
            byte[] b = reqCalc.getDigest();
            this.update(authOut, method);
            this.update(authOut, ":");
            this.update(authOut, uri);
            this.update(authOut, ":");
            this.update(authOut, Hex.toHexString((byte[])b));
        } else if (((String)qopMods.get(0)).equals("auth")) {
            this.update(authOut, method);
            this.update(authOut, ":");
            this.update(authOut, uri);
        }
        authOut.close();
        String hashHa2 = Hex.toHexString((byte[])authCalc.getDigest());
        DigestCalculator responseCalc = this.getDigestCalculator(algorithm, digestAlg);
        OutputStream responseOut = responseCalc.getOutputStream();
        if (qopMods.contains("missing")) {
            this.update(responseOut, hashHa1);
            this.update(responseOut, ":");
            this.update(responseOut, nonce);
            this.update(responseOut, ":");
            this.update(responseOut, hashHa2);
        } else {
            this.update(responseOut, hashHa1);
            this.update(responseOut, ":");
            this.update(responseOut, nonce);
            this.update(responseOut, ":");
            this.update(responseOut, "00000001");
            this.update(responseOut, ":");
            this.update(responseOut, crnonce);
            this.update(responseOut, ":");
            if (((String)qopMods.get(0)).equals("auth-int")) {
                this.update(responseOut, "auth-int");
            } else {
                this.update(responseOut, "auth");
            }
            this.update(responseOut, ":");
            this.update(responseOut, hashHa2);
        }
        responseOut.close();
        String digest = Hex.toHexString((byte[])responseCalc.getDigest());
        HashMap<String, String> hdr = new HashMap<String, String>();
        hdr.put("username", this.username);
        hdr.put("realm", realm);
        hdr.put("nonce", nonce);
        hdr.put("uri", uri);
        hdr.put("response", digest);
        if (((String)qopMods.get(0)).equals("auth-int")) {
            hdr.put("qop", "auth-int");
            hdr.put("nc", "00000001");
            hdr.put("cnonce", crnonce);
        } else if (((String)qopMods.get(0)).equals("auth")) {
            hdr.put("qop", "auth");
            hdr.put("nc", "00000001");
            hdr.put("cnonce", crnonce);
        }
        hdr.put("algorithm", algorithm);
        if (opaque == null || opaque.length() == 0) {
            hdr.put("opaque", this.makeNonce(20));
        }
        ESTRequestBuilder answer = new ESTRequestBuilder(req).withHijacker(null);
        answer.setHeader("Authorization", HttpUtil.mergeCSL("Digest", hdr));
        return req.getClient().doRequest(answer.build());
    }

    private DigestCalculator getDigestCalculator(String algorithm, AlgorithmIdentifier digestAlg) throws IOException {
        DigestCalculator dCalc;
        try {
            dCalc = this.digestCalculatorProvider.get(digestAlg);
        }
        catch (OperatorCreationException e) {
            throw new IOException("cannot create digest calculator for " + algorithm + ": " + e.getMessage());
        }
        return dCalc;
    }

    private AlgorithmIdentifier lookupDigest(String algorithm) {
        if (algorithm.endsWith("-SESS")) {
            algorithm = algorithm.substring(0, algorithm.length() - "-SESS".length());
        }
        if (algorithm.equals("SHA-512-256")) {
            return digestAlgorithmIdentifierFinder.find(NISTObjectIdentifiers.id_sha512_256);
        }
        return digestAlgorithmIdentifierFinder.find(algorithm);
    }

    private void update(OutputStream dOut, char[] value) throws IOException {
        dOut.write(Strings.toUTF8ByteArray((char[])value));
    }

    private void update(OutputStream dOut, String value) throws IOException {
        dOut.write(Strings.toUTF8ByteArray((String)value));
    }

    private String makeNonce(int len) {
        byte[] b = new byte[len];
        this.nonceGenerator.nextBytes(b);
        return Hex.toHexString((byte[])b);
    }

    static {
        HashSet<String> s = new HashSet<String>();
        s.add("realm");
        s.add("nonce");
        s.add("opaque");
        s.add("algorithm");
        s.add("qop");
        validParts = Collections.unmodifiableSet(s);
    }
}

