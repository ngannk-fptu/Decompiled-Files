/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.node.ObjectNode
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.aad.msal4j.AadInstanceDiscoveryProvider;
import com.microsoft.aad.msal4j.AccessTokenCacheEntity;
import com.microsoft.aad.msal4j.AccountCacheEntity;
import com.microsoft.aad.msal4j.AppMetadataCacheEntity;
import com.microsoft.aad.msal4j.AuthenticationResult;
import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.Credential;
import com.microsoft.aad.msal4j.CredentialTypeEnum;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.ITokenCache;
import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;
import com.microsoft.aad.msal4j.IUserAssertion;
import com.microsoft.aad.msal4j.IdTokenCacheEntity;
import com.microsoft.aad.msal4j.JsonHelper;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.OnBehalfOfRequest;
import com.microsoft.aad.msal4j.RefreshTokenCacheEntity;
import com.microsoft.aad.msal4j.StringHelper;
import com.microsoft.aad.msal4j.TokenCacheAccessContext;
import com.microsoft.aad.msal4j.TokenRequestExecutor;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TokenCache
implements ITokenCache {
    protected static final int MIN_ACCESS_TOKEN_EXPIRE_IN_SEC = 300;
    private transient ReadWriteLock lock = new ReentrantReadWriteLock();
    @JsonProperty(value="AccessToken")
    Map<String, AccessTokenCacheEntity> accessTokens = new LinkedHashMap<String, AccessTokenCacheEntity>();
    @JsonProperty(value="RefreshToken")
    Map<String, RefreshTokenCacheEntity> refreshTokens = new LinkedHashMap<String, RefreshTokenCacheEntity>();
    @JsonProperty(value="IdToken")
    Map<String, IdTokenCacheEntity> idTokens = new LinkedHashMap<String, IdTokenCacheEntity>();
    @JsonProperty(value="Account")
    Map<String, AccountCacheEntity> accounts = new LinkedHashMap<String, AccountCacheEntity>();
    @JsonProperty(value="AppMetadata")
    Map<String, AppMetadataCacheEntity> appMetadata = new LinkedHashMap<String, AppMetadataCacheEntity>();
    transient ITokenCacheAccessAspect tokenCacheAccessAspect;
    private transient String serializedCachedSnapshot;

    public TokenCache(ITokenCacheAccessAspect tokenCacheAccessAspect) {
        this();
        this.tokenCacheAccessAspect = tokenCacheAccessAspect;
    }

    public TokenCache() {
    }

    @Override
    public void deserialize(String data) {
        if (StringHelper.isBlank(data)) {
            return;
        }
        this.serializedCachedSnapshot = data;
        TokenCache deserializedCache = JsonHelper.convertJsonToObject(data, TokenCache.class);
        this.lock.writeLock().lock();
        try {
            this.accounts = deserializedCache.accounts;
            this.accessTokens = deserializedCache.accessTokens;
            this.refreshTokens = deserializedCache.refreshTokens;
            this.idTokens = deserializedCache.idTokens;
            this.appMetadata = deserializedCache.appMetadata;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private static void mergeJsonObjects(JsonNode old, JsonNode update) {
        TokenCache.mergeRemovals(old, update);
        TokenCache.mergeUpdates(old, update);
    }

    private static void mergeUpdates(JsonNode old, JsonNode update) {
        Iterator fieldNames = update.fieldNames();
        while (fieldNames.hasNext()) {
            String uKey = (String)fieldNames.next();
            JsonNode uValue = update.get(uKey);
            if (!old.has(uKey)) {
                if (uValue.isNull() || uValue.isObject() && uValue.size() == 0) continue;
                ((ObjectNode)old).set(uKey, uValue);
                continue;
            }
            JsonNode oValue = old.get(uKey);
            if (uValue.isObject()) {
                TokenCache.mergeUpdates(oValue, uValue);
                continue;
            }
            ((ObjectNode)old).set(uKey, uValue);
        }
    }

    private static void mergeRemovals(JsonNode old, JsonNode update) {
        HashSet<String> msalEntities = new HashSet<String>(Arrays.asList("Account", "AccessToken", "RefreshToken", "IdToken", "AppMetadata"));
        for (String msalEntity : msalEntities) {
            JsonNode oldEntries = old.get(msalEntity);
            JsonNode newEntries = update.get(msalEntity);
            if (oldEntries == null) continue;
            Iterator iterator = oldEntries.fields();
            while (iterator.hasNext()) {
                Map.Entry oEntry = (Map.Entry)iterator.next();
                String key = (String)oEntry.getKey();
                if (newEntries != null && newEntries.has(key)) continue;
                iterator.remove();
            }
        }
    }

    @Override
    public String serialize() {
        this.lock.readLock().lock();
        try {
            if (!StringHelper.isBlank(this.serializedCachedSnapshot)) {
                JsonNode cache = JsonHelper.mapper.readTree(this.serializedCachedSnapshot);
                JsonNode update = JsonHelper.mapper.valueToTree((Object)this);
                TokenCache.mergeJsonObjects(cache, update);
                String string = JsonHelper.mapper.writeValueAsString((Object)cache);
                return string;
            }
            String cache = JsonHelper.mapper.writeValueAsString((Object)this);
            return cache;
        }
        catch (IOException e) {
            throw new MsalClientException(e);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void saveTokens(TokenRequestExecutor tokenRequestExecutor, AuthenticationResult authenticationResult, String environment) {
        try (CacheAspect cacheAspect = new CacheAspect(TokenCacheAccessContext.builder().clientId(tokenRequestExecutor.getMsalRequest().application().clientId()).tokenCache(this).hasCacheChanged(true).build());){
            try {
                this.lock.writeLock().lock();
                if (!StringHelper.isBlank(authenticationResult.accessToken())) {
                    AccessTokenCacheEntity atEntity = TokenCache.createAccessTokenCacheEntity(tokenRequestExecutor, authenticationResult, environment);
                    this.accessTokens.put(atEntity.getKey(), atEntity);
                }
                if (!StringHelper.isBlank(authenticationResult.familyId())) {
                    AppMetadataCacheEntity appMetadataCacheEntity = TokenCache.createAppMetadataCacheEntity(tokenRequestExecutor, authenticationResult, environment);
                    this.appMetadata.put(appMetadataCacheEntity.getKey(), appMetadataCacheEntity);
                }
                if (!StringHelper.isBlank(authenticationResult.refreshToken())) {
                    RefreshTokenCacheEntity rtEntity = TokenCache.createRefreshTokenCacheEntity(tokenRequestExecutor, authenticationResult, environment);
                    rtEntity.family_id(authenticationResult.familyId());
                    this.refreshTokens.put(rtEntity.getKey(), rtEntity);
                }
                if (!StringHelper.isBlank(authenticationResult.idToken())) {
                    IdTokenCacheEntity idTokenEntity = TokenCache.createIdTokenCacheEntity(tokenRequestExecutor, authenticationResult, environment);
                    this.idTokens.put(idTokenEntity.getKey(), idTokenEntity);
                    AccountCacheEntity accountCacheEntity = authenticationResult.accountCacheEntity();
                    if (accountCacheEntity != null) {
                        accountCacheEntity.environment(environment);
                        this.accounts.put(accountCacheEntity.getKey(), accountCacheEntity);
                    }
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    private static RefreshTokenCacheEntity createRefreshTokenCacheEntity(TokenRequestExecutor tokenRequestExecutor, AuthenticationResult authenticationResult, String environmentAlias) {
        RefreshTokenCacheEntity rt = new RefreshTokenCacheEntity();
        rt.credentialType(CredentialTypeEnum.REFRESH_TOKEN.value());
        if (authenticationResult.account() != null) {
            rt.homeAccountId(authenticationResult.account().homeAccountId());
        }
        rt.environment(environmentAlias);
        rt.clientId(tokenRequestExecutor.getMsalRequest().application().clientId());
        rt.secret(authenticationResult.refreshToken());
        if (tokenRequestExecutor.getMsalRequest() instanceof OnBehalfOfRequest) {
            OnBehalfOfRequest onBehalfOfRequest = (OnBehalfOfRequest)tokenRequestExecutor.getMsalRequest();
            rt.userAssertionHash(onBehalfOfRequest.parameters.userAssertion().getAssertionHash());
        }
        return rt;
    }

    private static AccessTokenCacheEntity createAccessTokenCacheEntity(TokenRequestExecutor tokenRequestExecutor, AuthenticationResult authenticationResult, String environmentAlias) {
        AccessTokenCacheEntity at = new AccessTokenCacheEntity();
        at.credentialType(CredentialTypeEnum.ACCESS_TOKEN.value());
        if (authenticationResult.account() != null) {
            at.homeAccountId(authenticationResult.account().homeAccountId());
        }
        at.environment(environmentAlias);
        at.clientId(tokenRequestExecutor.getMsalRequest().application().clientId());
        at.secret(authenticationResult.accessToken());
        at.realm(tokenRequestExecutor.requestAuthority.tenant());
        String scopes = !StringHelper.isBlank(authenticationResult.scopes()) ? authenticationResult.scopes() : tokenRequestExecutor.getMsalRequest().msalAuthorizationGrant().getScopes();
        at.target(scopes);
        if (tokenRequestExecutor.getMsalRequest() instanceof OnBehalfOfRequest) {
            OnBehalfOfRequest onBehalfOfRequest = (OnBehalfOfRequest)tokenRequestExecutor.getMsalRequest();
            at.userAssertionHash(onBehalfOfRequest.parameters.userAssertion().getAssertionHash());
        }
        long currTimestampSec = System.currentTimeMillis() / 1000L;
        at.cachedAt(Long.toString(currTimestampSec));
        at.expiresOn(Long.toString(authenticationResult.expiresOn()));
        if (authenticationResult.refreshOn() > 0L) {
            at.refreshOn(Long.toString(authenticationResult.refreshOn()));
        }
        if (authenticationResult.extExpiresOn() > 0L) {
            at.extExpiresOn(Long.toString(authenticationResult.extExpiresOn()));
        }
        return at;
    }

    private static IdTokenCacheEntity createIdTokenCacheEntity(TokenRequestExecutor tokenRequestExecutor, AuthenticationResult authenticationResult, String environmentAlias) {
        IdTokenCacheEntity idToken = new IdTokenCacheEntity();
        idToken.credentialType(CredentialTypeEnum.ID_TOKEN.value());
        if (authenticationResult.account() != null) {
            idToken.homeAccountId(authenticationResult.account().homeAccountId());
        }
        idToken.environment(environmentAlias);
        idToken.clientId(tokenRequestExecutor.getMsalRequest().application().clientId());
        idToken.secret(authenticationResult.idToken());
        idToken.realm(tokenRequestExecutor.requestAuthority.tenant());
        if (tokenRequestExecutor.getMsalRequest() instanceof OnBehalfOfRequest) {
            OnBehalfOfRequest onBehalfOfRequest = (OnBehalfOfRequest)tokenRequestExecutor.getMsalRequest();
            idToken.userAssertionHash(onBehalfOfRequest.parameters.userAssertion().getAssertionHash());
        }
        return idToken;
    }

    private static AppMetadataCacheEntity createAppMetadataCacheEntity(TokenRequestExecutor tokenRequestExecutor, AuthenticationResult authenticationResult, String environmentAlias) {
        AppMetadataCacheEntity appMetadataCacheEntity = new AppMetadataCacheEntity();
        appMetadataCacheEntity.clientId(tokenRequestExecutor.getMsalRequest().application().clientId());
        appMetadataCacheEntity.environment(environmentAlias);
        appMetadataCacheEntity.familyId(authenticationResult.familyId());
        return appMetadataCacheEntity;
    }

    /*
     * Exception decompiling
     */
    Set<IAccount> getAccounts(String clientId) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private String getIdTokenKey(String homeAccountId, String environment, String clientId, String realm) {
        return String.join((CharSequence)"-", Arrays.asList(homeAccountId, environment, "idtoken", clientId, realm, "")).toLowerCase();
    }

    private String getApplicationFamilyId(String clientId, Set<String> environmentAliases) {
        for (AppMetadataCacheEntity data : this.appMetadata.values()) {
            if (!data.clientId().equals(clientId) || !environmentAliases.contains(data.environment()) || StringHelper.isBlank(data.familyId())) continue;
            return data.familyId();
        }
        return null;
    }

    private Set<String> getFamilyClientIds(String familyId, Set<String> environmentAliases) {
        return this.appMetadata.values().stream().filter(appMetadata -> environmentAliases.contains(appMetadata.environment()) && familyId.equals(appMetadata.familyId())).map(AppMetadataCacheEntity::clientId).collect(Collectors.toSet());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeAccount(String clientId, IAccount account) {
        try (CacheAspect cacheAspect = new CacheAspect(TokenCacheAccessContext.builder().clientId(clientId).tokenCache(this).hasCacheChanged(true).build());){
            try {
                this.lock.writeLock().lock();
                this.removeAccount(account);
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    private void removeAccount(IAccount account) {
        Predicate<Map.Entry> credentialToRemovePredicate = e -> !StringHelper.isBlank(((Credential)e.getValue()).homeAccountId()) && !StringHelper.isBlank(((Credential)e.getValue()).environment()) && ((Credential)e.getValue()).homeAccountId().equals(account.homeAccountId());
        this.accessTokens.entrySet().removeIf(credentialToRemovePredicate);
        this.refreshTokens.entrySet().removeIf(credentialToRemovePredicate);
        this.idTokens.entrySet().removeIf(credentialToRemovePredicate);
        this.accounts.entrySet().removeIf(e -> !StringHelper.isBlank(((AccountCacheEntity)e.getValue()).homeAccountId()) && !StringHelper.isBlank(((AccountCacheEntity)e.getValue()).environment()) && ((AccountCacheEntity)e.getValue()).homeAccountId().equals(account.homeAccountId()));
    }

    private boolean isMatchingScopes(AccessTokenCacheEntity accessTokenCacheEntity, Set<String> scopes) {
        TreeSet<String> accessTokenCacheEntityScopes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        accessTokenCacheEntityScopes.addAll(Arrays.asList(accessTokenCacheEntity.target().split(" ")));
        return accessTokenCacheEntityScopes.containsAll(scopes);
    }

    private boolean userAssertionHashMatches(Credential credential, String userAssertionHash) {
        if (userAssertionHash == null) {
            return true;
        }
        return credential.userAssertionHash() != null && credential.userAssertionHash().equalsIgnoreCase(userAssertionHash);
    }

    private boolean userAssertionHashMatches(AccountCacheEntity accountCacheEntity, String userAssertionHash) {
        if (userAssertionHash == null) {
            return true;
        }
        return accountCacheEntity.userAssertionHash() != null && accountCacheEntity.userAssertionHash().equalsIgnoreCase(userAssertionHash);
    }

    private Optional<AccessTokenCacheEntity> getAccessTokenCacheEntity(IAccount account, Authority authority, Set<String> scopes, String clientId, Set<String> environmentAliases) {
        long currTimeStampSec = new Date().getTime() / 1000L;
        return this.accessTokens.values().stream().filter(accessToken -> accessToken.homeAccountId.equals(account.homeAccountId()) && environmentAliases.contains(accessToken.environment) && Long.parseLong(accessToken.expiresOn()) > currTimeStampSec + 300L && accessToken.realm.equals(authority.tenant()) && accessToken.clientId.equals(clientId) && this.isMatchingScopes((AccessTokenCacheEntity)accessToken, scopes)).findAny();
    }

    private Optional<AccessTokenCacheEntity> getApplicationAccessTokenCacheEntity(Authority authority, Set<String> scopes, String clientId, Set<String> environmentAliases, String userAssertionHash) {
        long currTimeStampSec = new Date().getTime() / 1000L;
        return this.accessTokens.values().stream().filter(accessToken -> this.userAssertionHashMatches((Credential)accessToken, userAssertionHash) && environmentAliases.contains(accessToken.environment) && Long.parseLong(accessToken.expiresOn()) > currTimeStampSec + 300L && accessToken.realm.equals(authority.tenant()) && accessToken.clientId.equals(clientId) && this.isMatchingScopes((AccessTokenCacheEntity)accessToken, scopes)).findAny();
    }

    private Optional<IdTokenCacheEntity> getIdTokenCacheEntity(IAccount account, Authority authority, String clientId, Set<String> environmentAliases) {
        return this.idTokens.values().stream().filter(idToken -> idToken.homeAccountId.equals(account.homeAccountId()) && environmentAliases.contains(idToken.environment) && idToken.realm.equals(authority.tenant()) && idToken.clientId.equals(clientId)).findAny();
    }

    private Optional<IdTokenCacheEntity> getIdTokenCacheEntity(Authority authority, String clientId, Set<String> environmentAliases, String userAssertionHash) {
        return this.idTokens.values().stream().filter(idToken -> this.userAssertionHashMatches((Credential)idToken, userAssertionHash) && environmentAliases.contains(idToken.environment) && idToken.realm.equals(authority.tenant()) && idToken.clientId.equals(clientId)).findAny();
    }

    private Optional<RefreshTokenCacheEntity> getRefreshTokenCacheEntity(String clientId, Set<String> environmentAliases, String userAssertionHash) {
        return this.refreshTokens.values().stream().filter(refreshToken -> this.userAssertionHashMatches((Credential)refreshToken, userAssertionHash) && environmentAliases.contains(refreshToken.environment) && refreshToken.clientId.equals(clientId)).findAny();
    }

    private Optional<RefreshTokenCacheEntity> getRefreshTokenCacheEntity(IAccount account, String clientId, Set<String> environmentAliases) {
        return this.refreshTokens.values().stream().filter(refreshToken -> refreshToken.homeAccountId.equals(account.homeAccountId()) && environmentAliases.contains(refreshToken.environment) && refreshToken.clientId.equals(clientId)).findAny();
    }

    private Optional<AccountCacheEntity> getAccountCacheEntity(IAccount account, Set<String> environmentAliases) {
        return this.accounts.values().stream().filter(acc -> acc.homeAccountId.equals(account.homeAccountId()) && environmentAliases.contains(acc.environment)).findAny();
    }

    private Optional<AccountCacheEntity> getAccountCacheEntity(Set<String> environmentAliases, String userAssertionHash) {
        return this.accounts.values().stream().filter(acc -> this.userAssertionHashMatches((AccountCacheEntity)acc, userAssertionHash) && environmentAliases.contains(acc.environment)).findAny();
    }

    private Optional<RefreshTokenCacheEntity> getAnyFamilyRefreshTokenCacheEntity(IAccount account, Set<String> environmentAliases) {
        return this.refreshTokens.values().stream().filter(refreshToken -> refreshToken.homeAccountId.equals(account.homeAccountId()) && environmentAliases.contains(refreshToken.environment) && refreshToken.isFamilyRT()).findAny();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    AuthenticationResult getCachedAuthenticationResult(IAccount account, Authority authority, Set<String> scopes, String clientId) {
        AuthenticationResult.AuthenticationResultBuilder builder = AuthenticationResult.builder();
        Set<String> environmentAliases = AadInstanceDiscoveryProvider.getAliases(account.environment());
        try (CacheAspect cacheAspect = new CacheAspect(TokenCacheAccessContext.builder().clientId(clientId).tokenCache(this).account(account).build());){
            try {
                Optional<RefreshTokenCacheEntity> rtCacheEntity;
                this.lock.readLock().lock();
                Optional<AccountCacheEntity> accountCacheEntity = this.getAccountCacheEntity(account, environmentAliases);
                Optional<AccessTokenCacheEntity> atCacheEntity = this.getAccessTokenCacheEntity(account, authority, scopes, clientId, environmentAliases);
                Optional<IdTokenCacheEntity> idTokenCacheEntity = this.getIdTokenCacheEntity(account, authority, clientId, environmentAliases);
                if (!StringHelper.isBlank(this.getApplicationFamilyId(clientId, environmentAliases))) {
                    rtCacheEntity = this.getAnyFamilyRefreshTokenCacheEntity(account, environmentAliases);
                    if (!rtCacheEntity.isPresent()) {
                        rtCacheEntity = this.getRefreshTokenCacheEntity(account, clientId, environmentAliases);
                    }
                } else {
                    rtCacheEntity = this.getRefreshTokenCacheEntity(account, clientId, environmentAliases);
                    if (!rtCacheEntity.isPresent()) {
                        rtCacheEntity = this.getAnyFamilyRefreshTokenCacheEntity(account, environmentAliases);
                    }
                }
                if (atCacheEntity.isPresent()) {
                    builder.environment(atCacheEntity.get().environment).accessToken(atCacheEntity.get().secret).expiresOn(Long.parseLong(atCacheEntity.get().expiresOn()));
                    if (atCacheEntity.get().refreshOn() != null) {
                        builder.refreshOn(Long.parseLong(atCacheEntity.get().refreshOn()));
                    }
                } else {
                    builder.environment(authority.host());
                }
                idTokenCacheEntity.ifPresent(tokenCacheEntity -> builder.idToken(tokenCacheEntity.secret));
                rtCacheEntity.ifPresent(refreshTokenCacheEntity -> builder.refreshToken(refreshTokenCacheEntity.secret));
                accountCacheEntity.ifPresent(builder::accountCacheEntity);
            }
            finally {
                this.lock.readLock().unlock();
            }
        }
        return builder.build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    AuthenticationResult getCachedAuthenticationResult(Authority authority, Set<String> scopes, String clientId, IUserAssertion assertion) {
        AuthenticationResult.AuthenticationResultBuilder builder = AuthenticationResult.builder();
        Set<String> environmentAliases = AadInstanceDiscoveryProvider.getAliases(authority.host);
        builder.environment(authority.host());
        try (CacheAspect cacheAspect = new CacheAspect(TokenCacheAccessContext.builder().clientId(clientId).tokenCache(this).build());){
            try {
                this.lock.readLock().lock();
                String userAssertionHash = assertion == null ? null : assertion.getAssertionHash();
                Optional<AccountCacheEntity> accountCacheEntity = this.getAccountCacheEntity(environmentAliases, userAssertionHash);
                accountCacheEntity.ifPresent(builder::accountCacheEntity);
                Optional<AccessTokenCacheEntity> atCacheEntity = this.getApplicationAccessTokenCacheEntity(authority, scopes, clientId, environmentAliases, userAssertionHash);
                if (atCacheEntity.isPresent()) {
                    builder.accessToken(atCacheEntity.get().secret).expiresOn(Long.parseLong(atCacheEntity.get().expiresOn()));
                    if (atCacheEntity.get().refreshOn() != null) {
                        builder.refreshOn(Long.parseLong(atCacheEntity.get().refreshOn()));
                    }
                }
                Optional<IdTokenCacheEntity> idTokenCacheEntity = this.getIdTokenCacheEntity(authority, clientId, environmentAliases, userAssertionHash);
                idTokenCacheEntity.ifPresent(tokenCacheEntity -> builder.idToken(tokenCacheEntity.secret));
                Optional<RefreshTokenCacheEntity> rtCacheEntity = this.getRefreshTokenCacheEntity(clientId, environmentAliases, userAssertionHash);
                rtCacheEntity.ifPresent(refreshTokenCacheEntity -> builder.refreshToken(refreshTokenCacheEntity.secret));
            }
            finally {
                this.lock.readLock().unlock();
            }
            AuthenticationResult authenticationResult = builder.build();
            return authenticationResult;
        }
    }

    private class CacheAspect
    implements AutoCloseable {
        ITokenCacheAccessContext context;

        CacheAspect(ITokenCacheAccessContext context) {
            if (TokenCache.this.tokenCacheAccessAspect != null) {
                this.context = context;
                TokenCache.this.tokenCacheAccessAspect.beforeCacheAccess(context);
            }
        }

        @Override
        public void close() {
            if (TokenCache.this.tokenCacheAccessAspect != null) {
                TokenCache.this.tokenCacheAccessAspect.afterCacheAccess(this.context);
            }
        }
    }
}

