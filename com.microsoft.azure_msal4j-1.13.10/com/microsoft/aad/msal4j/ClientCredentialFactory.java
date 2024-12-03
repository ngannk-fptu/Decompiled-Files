/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ClientAssertion;
import com.microsoft.aad.msal4j.ClientCertificate;
import com.microsoft.aad.msal4j.ClientSecret;
import com.microsoft.aad.msal4j.IClientAssertion;
import com.microsoft.aad.msal4j.IClientCertificate;
import com.microsoft.aad.msal4j.IClientSecret;
import com.microsoft.aad.msal4j.ParameterValidationUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientCredentialFactory {
    public static IClientSecret createFromSecret(String secret) {
        return new ClientSecret(secret);
    }

    public static IClientCertificate createFromCertificate(InputStream pkcs12Certificate, String password) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException {
        return ClientCertificate.create(pkcs12Certificate, password);
    }

    public static IClientCertificate createFromCertificate(PrivateKey key, X509Certificate publicKeyCertificate) {
        ParameterValidationUtils.validateNotNull("publicKeyCertificate", publicKeyCertificate);
        return ClientCertificate.create(key, publicKeyCertificate);
    }

    public static IClientCertificate createFromCertificateChain(PrivateKey key, List<X509Certificate> publicKeyCertificateChain) {
        if (key == null || publicKeyCertificateChain == null || publicKeyCertificateChain.size() == 0) {
            throw new IllegalArgumentException("null or empty input parameter");
        }
        return new ClientCertificate(key, publicKeyCertificateChain);
    }

    public static IClientAssertion createFromClientAssertion(String clientAssertion) {
        return new ClientAssertion(clientAssertion);
    }

    public static IClientAssertion createFromCallback(Callable<String> callable) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(callable);
        return new ClientAssertion(future.get());
    }
}

