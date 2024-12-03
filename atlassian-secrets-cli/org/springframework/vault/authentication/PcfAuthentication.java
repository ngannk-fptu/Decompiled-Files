/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.AsymmetricBlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.CryptoException
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.digests.SHA256Digest
 *  org.bouncycastle.crypto.engines.RSAEngine
 *  org.bouncycastle.crypto.params.RSAKeyParameters
 *  org.bouncycastle.crypto.signers.PSSSigner
 */
package org.springframework.vault.authentication;

import java.nio.charset.StandardCharsets;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.PcfAuthenticationOptions;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.PemObject;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class PcfAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(PcfAuthentication.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final int SALT_LENGTH = 222;
    private final PcfAuthenticationOptions options;
    private final RestOperations restOperations;

    public PcfAuthentication(PcfAuthenticationOptions options, RestOperations restOperations) {
        Assert.notNull((Object)options, "PcfAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, "RestOperations must not be null");
        this.options = options;
        this.restOperations = restOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(PcfAuthenticationOptions options) {
        Assert.notNull((Object)options, "PcfAuthenticationOptions must not be null");
        AuthenticationSteps.Node<String> cert = AuthenticationSteps.fromSupplier(options.getInstanceCertSupplier());
        AuthenticationSteps.Node<String> key = AuthenticationSteps.fromSupplier(options.getInstanceKeySupplier());
        return cert.zipWith(key).map(credentials -> PcfAuthentication.getPcfLogin(options.getRole(), options.getClock(), (String)credentials.getLeft(), (String)credentials.getRight())).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    @Override
    public VaultToken login() throws VaultException {
        Map<String, String> login = PcfAuthentication.getPcfLogin(this.options.getRole(), this.options.getClock(), this.options.getInstanceCertSupplier().get(), this.options.getInstanceKeySupplier().get());
        try {
            VaultResponse response = this.restOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), login, VaultResponse.class, new Object[0]);
            Assert.state(response != null && response.getAuth() != null, "Auth field must not be null");
            logger.debug("Login successful using PCF authentication");
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("PCF", e);
        }
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return PcfAuthentication.createAuthenticationSteps(this.options);
    }

    private static Map<String, String> getPcfLogin(String role, Clock clock, String instanceCert, String instanceKey) {
        Assert.hasText(role, "Role must not be empty");
        String signingTime = TIME_FORMAT.format(LocalDateTime.now(clock));
        String message = PcfAuthentication.getMessage(role, signingTime, instanceCert);
        String signature = PcfAuthentication.sign(message, instanceKey);
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("role", role);
        login.put("cf_instance_cert", instanceCert);
        login.put("signing_time", signingTime);
        login.put("signature", signature);
        return login;
    }

    private static String sign(String message, String privateKeyPem) {
        try {
            return PcfAuthentication.doSign(message.getBytes(StandardCharsets.US_ASCII), privateKeyPem);
        }
        catch (CryptoException e) {
            throw new VaultException("Cannot sign PCF login", e);
        }
    }

    private static String getMessage(String role, String signingTime, String instanceCertPem) {
        return signingTime + instanceCertPem + role;
    }

    private static String doSign(byte[] message, String instanceKeyPem) throws CryptoException {
        RSAPrivateCrtKeySpec privateKey = PemObject.fromKey(instanceKeyPem).getRSAKeySpec();
        PSSSigner signer = new PSSSigner((AsymmetricBlockCipher)new RSAEngine(), (Digest)new SHA256Digest(), 222);
        signer.init(true, (CipherParameters)new RSAKeyParameters(true, privateKey.getModulus(), privateKey.getPrivateExponent()));
        signer.update(message, 0, message.length);
        byte[] signature = signer.generateSignature();
        return Base64Utils.encodeToUrlSafeString(signature);
    }
}

