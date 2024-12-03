/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.aad.msal4j.ClientCredentialFactory
 *  com.microsoft.aad.msal4j.ClientCredentialParameters
 *  com.microsoft.aad.msal4j.ConfidentialClientApplication
 *  com.microsoft.aad.msal4j.ConfidentialClientApplication$Builder
 *  com.microsoft.aad.msal4j.IAccount
 *  com.microsoft.aad.msal4j.IAuthenticationResult
 *  com.microsoft.aad.msal4j.IClientCertificate
 *  com.microsoft.aad.msal4j.IClientCredential
 *  com.microsoft.aad.msal4j.IClientSecret
 *  com.microsoft.aad.msal4j.ITokenCacheAccessAspect
 *  com.microsoft.aad.msal4j.IntegratedWindowsAuthenticationParameters
 *  com.microsoft.aad.msal4j.InteractiveRequestParameters
 *  com.microsoft.aad.msal4j.MsalInteractionRequiredException
 *  com.microsoft.aad.msal4j.PublicClientApplication
 *  com.microsoft.aad.msal4j.PublicClientApplication$Builder
 *  com.microsoft.aad.msal4j.SilentParameters
 *  com.microsoft.aad.msal4j.SystemBrowserOptions
 *  com.microsoft.aad.msal4j.UserNamePasswordParameters
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCertificate;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.IClientSecret;
import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.IntegratedWindowsAuthenticationParameters;
import com.microsoft.aad.msal4j.InteractiveRequestParameters;
import com.microsoft.aad.msal4j.MsalInteractionRequiredException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.SystemBrowserOptions;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import com.microsoft.sqlserver.jdbc.PersistentTokenCacheAccessAspect;
import com.microsoft.sqlserver.jdbc.SQLServerCertificateUtils;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import com.microsoft.sqlserver.jdbc.SqlAuthenticationToken;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.kerberos.KerberosPrincipal;

class SQLServerMSAL4JUtils {
    static final String REDIRECTURI = "http://localhost";
    static final String SLASH_DEFAULT = "/.default";
    static final String ACCESS_TOKEN_EXPIRE = "access token expires: ";
    private static final String LOGCONTEXT = "MSAL version " + PublicClientApplication.class.getPackage().getImplementationVersion() + ": ";
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerMSAL4JUtils");

    private SQLServerMSAL4JUtils() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static SqlAuthenticationToken getSqlFedAuthToken(SQLServerConnection.SqlFedAuthInfo fedAuthInfo, String user, String password, String authenticationString) throws SQLServerException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(LOGCONTEXT + authenticationString + ": get FedAuth token for user: " + user);
        }
        try {
            PublicClientApplication pca = ((PublicClientApplication.Builder)((PublicClientApplication.Builder)((PublicClientApplication.Builder)PublicClientApplication.builder((String)"7f98cb04-cd1e-40df-9140-3bf7e2cea4db").executorService(executorService)).setTokenCacheAccessAspect((ITokenCacheAccessAspect)PersistentTokenCacheAccessAspect.getInstance())).authority(fedAuthInfo.stsurl)).build();
            CompletableFuture future = pca.acquireToken(UserNamePasswordParameters.builder(Collections.singleton(fedAuthInfo.spn + SLASH_DEFAULT), (String)user, (char[])password.toCharArray()).build());
            IAuthenticationResult authenticationResult = (IAuthenticationResult)future.get();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(LOGCONTEXT + (authenticationResult.account() != null ? authenticationResult.account().username() + ": " : ACCESS_TOKEN_EXPIRE + authenticationResult.expiresOnDate()));
            }
            SqlAuthenticationToken sqlAuthenticationToken = new SqlAuthenticationToken(authenticationResult.accessToken(), authenticationResult.expiresOnDate());
            return sqlAuthenticationToken;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLServerException(e.getMessage(), e);
        }
        catch (MalformedURLException | ExecutionException e) {
            throw SQLServerMSAL4JUtils.getCorrectedException(e, user, authenticationString);
        }
        finally {
            executorService.shutdown();
        }
    }

    static SqlAuthenticationToken getSqlFedAuthTokenPrincipal(SQLServerConnection.SqlFedAuthInfo fedAuthInfo, String aadPrincipalID, String aadPrincipalSecret, String authenticationString) throws SQLServerException {
        String defaultScopeSuffix;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(LOGCONTEXT + authenticationString + ": get FedAuth token for principal: " + aadPrincipalID);
        }
        String scope = fedAuthInfo.spn.endsWith(defaultScopeSuffix = SLASH_DEFAULT) ? fedAuthInfo.spn : fedAuthInfo.spn + defaultScopeSuffix;
        HashSet<String> scopes = new HashSet<String>();
        scopes.add(scope);
        try {
            IClientSecret credential = ClientCredentialFactory.createFromSecret((String)aadPrincipalSecret);
            ConfidentialClientApplication clientApplication = ((ConfidentialClientApplication.Builder)((ConfidentialClientApplication.Builder)((ConfidentialClientApplication.Builder)ConfidentialClientApplication.builder((String)aadPrincipalID, (IClientCredential)credential).executorService(executorService)).setTokenCacheAccessAspect((ITokenCacheAccessAspect)PersistentTokenCacheAccessAspect.getInstance())).authority(fedAuthInfo.stsurl)).build();
            CompletableFuture future = clientApplication.acquireToken(ClientCredentialParameters.builder(scopes).build());
            IAuthenticationResult authenticationResult = (IAuthenticationResult)future.get();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(LOGCONTEXT + (authenticationResult.account() != null ? authenticationResult.account().username() + ": " : ACCESS_TOKEN_EXPIRE + authenticationResult.expiresOnDate()));
            }
            SqlAuthenticationToken sqlAuthenticationToken = new SqlAuthenticationToken(authenticationResult.accessToken(), authenticationResult.expiresOnDate());
            return sqlAuthenticationToken;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLServerException(e.getMessage(), e);
        }
        catch (MalformedURLException | ExecutionException e) {
            throw SQLServerMSAL4JUtils.getCorrectedException(e, aadPrincipalID, authenticationString);
        }
        finally {
            executorService.shutdown();
        }
    }

    static SqlAuthenticationToken getSqlFedAuthTokenPrincipalCertificate(SQLServerConnection.SqlFedAuthInfo fedAuthInfo, String aadPrincipalID, String certFile, String certPassword, String certKey, String certKeyPassword, String authenticationString) throws SQLServerException {
        String defaultScopeSuffix;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(LOGCONTEXT + authenticationString + ": get FedAuth token for principal certificate: " + aadPrincipalID);
        }
        String scope = fedAuthInfo.spn.endsWith(defaultScopeSuffix = SLASH_DEFAULT) ? fedAuthInfo.spn : fedAuthInfo.spn + defaultScopeSuffix;
        HashSet<String> scopes = new HashSet<String>();
        scopes.add(scope);
        try {
            IClientCertificate credential;
            ConfidentialClientApplication clientApplication;
            block21: {
                clientApplication = null;
                try (FileInputStream is = new FileInputStream(certFile);){
                    KeyStore keyStore = SQLServerCertificateUtils.loadPKCS12KeyStore(certFile, certPassword);
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(LOGCONTEXT + "certificate type: " + keyStore.getType());
                        Enumeration<String> enumeration = keyStore.aliases();
                        while (enumeration.hasMoreElements()) {
                            String alias = enumeration.nextElement();
                            X509Certificate cert = (X509Certificate)keyStore.getCertificate(alias);
                            cert.checkValidity();
                            logger.finest(LOGCONTEXT + "certificate: " + cert.toString());
                        }
                    }
                    credential = ClientCredentialFactory.createFromCertificate((InputStream)is, (String)certPassword);
                    clientApplication = ((ConfidentialClientApplication.Builder)((ConfidentialClientApplication.Builder)((ConfidentialClientApplication.Builder)ConfidentialClientApplication.builder((String)aadPrincipalID, (IClientCredential)credential).executorService(executorService)).setTokenCacheAccessAspect((ITokenCacheAccessAspect)PersistentTokenCacheAccessAspect.getInstance())).authority(fedAuthInfo.stsurl)).build();
                }
                catch (FileNotFoundException e) {
                    throw new SQLServerException(SQLServerException.getErrString("R_readCertError") + e.getMessage(), null, 0, null);
                }
                catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                    if (!logger.isLoggable(Level.FINEST)) break block21;
                    logger.finest(LOGCONTEXT + "Error loading PKCS12 certificate: " + e.getMessage());
                }
            }
            if (clientApplication == null) {
                X509Certificate cert = (X509Certificate)SQLServerCertificateUtils.loadCertificate(certFile);
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(LOGCONTEXT + "certificate type: " + cert.getType());
                    cert.checkValidity();
                    logger.finest(LOGCONTEXT + "certificate: " + cert.toString());
                }
                PrivateKey privateKey = SQLServerCertificateUtils.loadPrivateKey(certKey, certKeyPassword);
                credential = ClientCredentialFactory.createFromCertificate((PrivateKey)privateKey, (X509Certificate)cert);
                clientApplication = ((ConfidentialClientApplication.Builder)((ConfidentialClientApplication.Builder)((ConfidentialClientApplication.Builder)ConfidentialClientApplication.builder((String)aadPrincipalID, (IClientCredential)credential).executorService(executorService)).setTokenCacheAccessAspect((ITokenCacheAccessAspect)PersistentTokenCacheAccessAspect.getInstance())).authority(fedAuthInfo.stsurl)).build();
            }
            CompletableFuture future = clientApplication.acquireToken(ClientCredentialParameters.builder(scopes).build());
            IAuthenticationResult authenticationResult = (IAuthenticationResult)future.get();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(LOGCONTEXT + (authenticationResult.account() != null ? authenticationResult.account().username() + ": " : ACCESS_TOKEN_EXPIRE + authenticationResult.expiresOnDate()));
            }
            SqlAuthenticationToken sqlAuthenticationToken = new SqlAuthenticationToken(authenticationResult.accessToken(), authenticationResult.expiresOnDate());
            return sqlAuthenticationToken;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLServerException(e.getMessage(), e);
        }
        catch (GeneralSecurityException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_readCertError") + e.getMessage(), null, 0, null);
        }
        catch (Exception e) {
            throw SQLServerMSAL4JUtils.getCorrectedException(e, aadPrincipalID, authenticationString);
        }
        finally {
            executorService.shutdown();
        }
    }

    static SqlAuthenticationToken getSqlFedAuthTokenIntegrated(SQLServerConnection.SqlFedAuthInfo fedAuthInfo, String authenticationString) throws SQLServerException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        KerberosPrincipal kerberosPrincipal = new KerberosPrincipal("username");
        String user = kerberosPrincipal.getName();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(LOGCONTEXT + authenticationString + ": get FedAuth token integrated, user: " + user + "realm name:" + kerberosPrincipal.getRealm());
        }
        try {
            PublicClientApplication pca = ((PublicClientApplication.Builder)((PublicClientApplication.Builder)((PublicClientApplication.Builder)PublicClientApplication.builder((String)"7f98cb04-cd1e-40df-9140-3bf7e2cea4db").executorService(executorService)).setTokenCacheAccessAspect((ITokenCacheAccessAspect)PersistentTokenCacheAccessAspect.getInstance())).authority(fedAuthInfo.stsurl)).build();
            CompletableFuture future = pca.acquireToken(IntegratedWindowsAuthenticationParameters.builder(Collections.singleton(fedAuthInfo.spn + SLASH_DEFAULT), (String)user).build());
            IAuthenticationResult authenticationResult = (IAuthenticationResult)future.get();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(LOGCONTEXT + (authenticationResult.account() != null ? authenticationResult.account().username() + ": " : ACCESS_TOKEN_EXPIRE + authenticationResult.expiresOnDate()));
            }
            SqlAuthenticationToken sqlAuthenticationToken = new SqlAuthenticationToken(authenticationResult.accessToken(), authenticationResult.expiresOnDate());
            return sqlAuthenticationToken;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLServerException(e.getMessage(), e);
        }
        catch (IOException | ExecutionException e) {
            throw SQLServerMSAL4JUtils.getCorrectedException(e, user, authenticationString);
        }
        finally {
            executorService.shutdown();
        }
    }

    static SqlAuthenticationToken getSqlFedAuthTokenInteractive(SQLServerConnection.SqlFedAuthInfo fedAuthInfo, String user, String authenticationString) throws SQLServerException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(LOGCONTEXT + authenticationString + ": get FedAuth token interactive for user: " + user);
        }
        try {
            IAuthenticationResult authenticationResult;
            CompletableFuture future;
            PublicClientApplication pca;
            block19: {
                pca = ((PublicClientApplication.Builder)((PublicClientApplication.Builder)((PublicClientApplication.Builder)PublicClientApplication.builder((String)"7f98cb04-cd1e-40df-9140-3bf7e2cea4db").executorService(executorService)).setTokenCacheAccessAspect((ITokenCacheAccessAspect)PersistentTokenCacheAccessAspect.getInstance())).authority(fedAuthInfo.stsurl)).build();
                future = null;
                authenticationResult = null;
                try {
                    IAccount account;
                    Set accountsInCache = (Set)pca.getAccounts().join();
                    if (logger.isLoggable(Level.FINEST)) {
                        StringBuilder acc = new StringBuilder();
                        if (accountsInCache != null) {
                            for (IAccount account2 : accountsInCache) {
                                if (acc.length() != 0) {
                                    acc.append(", ");
                                }
                                acc.append(account2.username());
                            }
                        }
                        logger.finest(LOGCONTEXT + "Accounts in cache = " + acc + ", size = " + (accountsInCache == null ? null : Integer.valueOf(accountsInCache.size())) + ", user = " + user);
                    }
                    if (null != accountsInCache && !accountsInCache.isEmpty() && null != user && !user.isEmpty() && null != (account = SQLServerMSAL4JUtils.getAccountByUsername(accountsInCache, user))) {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest(LOGCONTEXT + "Silent authentication for user:" + user);
                        }
                        SilentParameters silentParameters = SilentParameters.builder(Collections.singleton(fedAuthInfo.spn + SLASH_DEFAULT), (IAccount)account).build();
                        future = pca.acquireTokenSilently(silentParameters);
                    }
                }
                catch (MsalInteractionRequiredException e) {
                    if (!logger.isLoggable(Level.FINEST)) break block19;
                    logger.log(Level.FINEST, e, () -> LOGCONTEXT + "Need to get token interactively: " + e.reason().toString());
                }
            }
            if (null != future) {
                authenticationResult = (IAuthenticationResult)future.get();
            } else {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest(LOGCONTEXT + "Interactive authentication");
                }
                InteractiveRequestParameters parameters = InteractiveRequestParameters.builder((URI)new URI(REDIRECTURI)).systemBrowserOptions(SystemBrowserOptions.builder().htmlMessageSuccess(SQLServerResource.getResource("R_MSALAuthComplete")).build()).loginHint(user).scopes(Collections.singleton(fedAuthInfo.spn + SLASH_DEFAULT)).build();
                future = pca.acquireToken(parameters);
                authenticationResult = (IAuthenticationResult)future.get();
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(LOGCONTEXT + (authenticationResult.account() != null ? authenticationResult.account().username() + ": " : ACCESS_TOKEN_EXPIRE + authenticationResult.expiresOnDate()));
            }
            SqlAuthenticationToken sqlAuthenticationToken = new SqlAuthenticationToken(authenticationResult.accessToken(), authenticationResult.expiresOnDate());
            return sqlAuthenticationToken;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLServerException(e.getMessage(), e);
        }
        catch (MalformedURLException | URISyntaxException | ExecutionException e) {
            throw SQLServerMSAL4JUtils.getCorrectedException(e, user, authenticationString);
        }
        finally {
            executorService.shutdown();
        }
    }

    private static IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
        if (!accounts.isEmpty()) {
            for (IAccount account : accounts) {
                if (!account.username().equalsIgnoreCase(username)) continue;
                return account;
            }
        }
        return null;
    }

    private static SQLServerException getCorrectedException(Exception e, String user, String authenticationString) {
        Object[] msgArgs = new Object[]{user, authenticationString};
        if (null == e.getCause() || null == e.getCause().getMessage()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_MSALExecution") + " " + e.getMessage());
            return new SQLServerException(form.format(msgArgs), null);
        }
        String correctedErrorMessage = e.getCause().getMessage().replaceAll("\\\\r\\\\n", "\r\n").replaceAll("\\{", "\"").replaceAll("\\}", "\"");
        RuntimeException correctedAuthenticationException = new RuntimeException(correctedErrorMessage);
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_MSALExecution") + " " + correctedErrorMessage);
        ExecutionException correctedExecutionException = new ExecutionException(correctedAuthenticationException);
        return new SQLServerException(form.format(msgArgs), null, 0, (Throwable)correctedExecutionException);
    }
}

