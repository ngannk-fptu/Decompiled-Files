/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWTParser
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AccountCacheEntity;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.ITenantProfile;
import com.microsoft.aad.msal4j.IdToken;
import com.microsoft.aad.msal4j.JsonHelper;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.StringHelper;
import com.microsoft.aad.msal4j.TenantProfile;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

final class AuthenticationResult
implements IAuthenticationResult {
    private static final long serialVersionUID = 1L;
    private final String accessToken;
    private final long expiresOn;
    private final long extExpiresOn;
    private final String refreshToken;
    private final Long refreshOn;
    private final String familyId;
    private final String idToken;
    private final AtomicReference<Object> idTokenObject = new AtomicReference();
    private final AccountCacheEntity accountCacheEntity;
    private final AtomicReference<Object> account = new AtomicReference();
    private final AtomicReference<Object> tenantProfile = new AtomicReference();
    private String environment;
    private final AtomicReference<Object> expiresOnDate = new AtomicReference();
    private final String scopes;

    private IdToken getIdTokenObj() {
        if (StringHelper.isBlank(this.idToken)) {
            return null;
        }
        try {
            String idTokenJson = JWTParser.parse((String)this.idToken).getParsedParts()[1].decodeToString();
            return JsonHelper.convertJsonToObject(idTokenJson, IdToken.class);
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private IAccount getAccount() {
        if (this.accountCacheEntity == null) {
            return null;
        }
        return this.accountCacheEntity.toAccount();
    }

    private ITenantProfile getTenantProfile() {
        if (StringHelper.isBlank(this.idToken)) {
            return null;
        }
        try {
            return new TenantProfile(JWTParser.parse((String)this.idToken).getJWTClaimsSet().getClaims(), this.getAccount().environment());
        }
        catch (ParseException e) {
            throw new MsalClientException("Cached JWT could not be parsed: " + e.getMessage(), "invalid_jwt");
        }
    }

    AuthenticationResult(String accessToken, long expiresOn, long extExpiresOn, String refreshToken, Long refreshOn, String familyId, String idToken, AccountCacheEntity accountCacheEntity, String environment, String scopes) {
        this.accessToken = accessToken;
        this.expiresOn = expiresOn;
        this.extExpiresOn = extExpiresOn;
        this.refreshToken = refreshToken;
        this.refreshOn = refreshOn;
        this.familyId = familyId;
        this.idToken = idToken;
        this.accountCacheEntity = accountCacheEntity;
        this.environment = environment;
        this.scopes = scopes;
    }

    public static AuthenticationResultBuilder builder() {
        return new AuthenticationResultBuilder();
    }

    @Override
    public String accessToken() {
        return this.accessToken;
    }

    public String refreshToken() {
        return this.refreshToken;
    }

    public Long refreshOn() {
        return this.refreshOn;
    }

    @Override
    public String idToken() {
        return this.idToken;
    }

    @Override
    public String environment() {
        return this.environment;
    }

    @Override
    public String scopes() {
        return this.scopes;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthenticationResult)) {
            return false;
        }
        AuthenticationResult other = (AuthenticationResult)o;
        String this$accessToken = this.accessToken();
        String other$accessToken = other.accessToken();
        if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) {
            return false;
        }
        if (this.expiresOn() != other.expiresOn()) {
            return false;
        }
        if (this.extExpiresOn() != other.extExpiresOn()) {
            return false;
        }
        String this$refreshToken = this.refreshToken();
        String other$refreshToken = other.refreshToken();
        if (this$refreshToken == null ? other$refreshToken != null : !this$refreshToken.equals(other$refreshToken)) {
            return false;
        }
        Long this$refreshOn = this.refreshOn();
        Long other$refreshOn = other.refreshOn();
        if (this$refreshOn == null ? other$refreshOn != null : !((Object)this$refreshOn).equals(other$refreshOn)) {
            return false;
        }
        String this$familyId = this.familyId();
        String other$familyId = other.familyId();
        if (this$familyId == null ? other$familyId != null : !this$familyId.equals(other$familyId)) {
            return false;
        }
        String this$idToken = this.idToken();
        String other$idToken = other.idToken();
        if (this$idToken == null ? other$idToken != null : !this$idToken.equals(other$idToken)) {
            return false;
        }
        IdToken this$idTokenObject = this.idTokenObject();
        IdToken other$idTokenObject = other.idTokenObject();
        if (this$idTokenObject == null ? other$idTokenObject != null : !this$idTokenObject.equals(other$idTokenObject)) {
            return false;
        }
        AccountCacheEntity this$accountCacheEntity = this.accountCacheEntity();
        AccountCacheEntity other$accountCacheEntity = other.accountCacheEntity();
        if (this$accountCacheEntity == null ? other$accountCacheEntity != null : !((Object)this$accountCacheEntity).equals(other$accountCacheEntity)) {
            return false;
        }
        IAccount this$account = this.account();
        IAccount other$account = other.account();
        if (this$account == null ? other$account != null : !this$account.equals(other$account)) {
            return false;
        }
        ITenantProfile this$tenantProfile = this.tenantProfile();
        ITenantProfile other$tenantProfile = other.tenantProfile();
        if (this$tenantProfile == null ? other$tenantProfile != null : !this$tenantProfile.equals(other$tenantProfile)) {
            return false;
        }
        String this$environment = this.environment();
        String other$environment = other.environment();
        if (this$environment == null ? other$environment != null : !this$environment.equals(other$environment)) {
            return false;
        }
        Date this$expiresOnDate = this.expiresOnDate();
        Date other$expiresOnDate = other.expiresOnDate();
        if (this$expiresOnDate == null ? other$expiresOnDate != null : !((Object)this$expiresOnDate).equals(other$expiresOnDate)) {
            return false;
        }
        String this$scopes = this.scopes();
        String other$scopes = other.scopes();
        return !(this$scopes == null ? other$scopes != null : !this$scopes.equals(other$scopes));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $accessToken = this.accessToken();
        result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
        long $expiresOn = this.expiresOn();
        result = result * 59 + (int)($expiresOn >>> 32 ^ $expiresOn);
        long $extExpiresOn = this.extExpiresOn();
        result = result * 59 + (int)($extExpiresOn >>> 32 ^ $extExpiresOn);
        String $refreshToken = this.refreshToken();
        result = result * 59 + ($refreshToken == null ? 43 : $refreshToken.hashCode());
        Long $refreshOn = this.refreshOn();
        result = result * 59 + ($refreshOn == null ? 43 : ((Object)$refreshOn).hashCode());
        String $familyId = this.familyId();
        result = result * 59 + ($familyId == null ? 43 : $familyId.hashCode());
        String $idToken = this.idToken();
        result = result * 59 + ($idToken == null ? 43 : $idToken.hashCode());
        IdToken $idTokenObject = this.idTokenObject();
        result = result * 59 + ($idTokenObject == null ? 43 : $idTokenObject.hashCode());
        AccountCacheEntity $accountCacheEntity = this.accountCacheEntity();
        result = result * 59 + ($accountCacheEntity == null ? 43 : ((Object)$accountCacheEntity).hashCode());
        IAccount $account = this.account();
        result = result * 59 + ($account == null ? 43 : $account.hashCode());
        ITenantProfile $tenantProfile = this.tenantProfile();
        result = result * 59 + ($tenantProfile == null ? 43 : $tenantProfile.hashCode());
        String $environment = this.environment();
        result = result * 59 + ($environment == null ? 43 : $environment.hashCode());
        Date $expiresOnDate = this.expiresOnDate();
        result = result * 59 + ($expiresOnDate == null ? 43 : ((Object)$expiresOnDate).hashCode());
        String $scopes = this.scopes();
        result = result * 59 + ($scopes == null ? 43 : $scopes.hashCode());
        return result;
    }

    long expiresOn() {
        return this.expiresOn;
    }

    long extExpiresOn() {
        return this.extExpiresOn;
    }

    String familyId() {
        return this.familyId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    IdToken idTokenObject() {
        Object value = this.idTokenObject.get();
        if (value == null) {
            AtomicReference<Object> atomicReference = this.idTokenObject;
            synchronized (atomicReference) {
                value = this.idTokenObject.get();
                if (value == null) {
                    IdToken actualValue = this.getIdTokenObj();
                    value = actualValue == null ? this.idTokenObject : actualValue;
                    this.idTokenObject.set(value);
                }
            }
        }
        return (IdToken)(value == this.idTokenObject ? null : value);
    }

    AccountCacheEntity accountCacheEntity() {
        return this.accountCacheEntity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IAccount account() {
        Object value = this.account.get();
        if (value == null) {
            AtomicReference<Object> atomicReference = this.account;
            synchronized (atomicReference) {
                value = this.account.get();
                if (value == null) {
                    IAccount actualValue = this.getAccount();
                    value = actualValue == null ? this.account : actualValue;
                    this.account.set(value);
                }
            }
        }
        return (IAccount)(value == this.account ? null : value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ITenantProfile tenantProfile() {
        Object value = this.tenantProfile.get();
        if (value == null) {
            AtomicReference<Object> atomicReference = this.tenantProfile;
            synchronized (atomicReference) {
                value = this.tenantProfile.get();
                if (value == null) {
                    ITenantProfile actualValue = this.getTenantProfile();
                    value = actualValue == null ? this.tenantProfile : actualValue;
                    this.tenantProfile.set(value);
                }
            }
        }
        return (ITenantProfile)(value == this.tenantProfile ? null : value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Date expiresOnDate() {
        Object value = this.expiresOnDate.get();
        if (value == null) {
            AtomicReference<Object> atomicReference = this.expiresOnDate;
            synchronized (atomicReference) {
                value = this.expiresOnDate.get();
                if (value == null) {
                    Date actualValue = new Date(this.expiresOn * 1000L);
                    value = actualValue == null ? this.expiresOnDate : actualValue;
                    this.expiresOnDate.set(value);
                }
            }
        }
        return (Date)(value == this.expiresOnDate ? null : value);
    }

    public static class AuthenticationResultBuilder {
        private String accessToken;
        private long expiresOn;
        private long extExpiresOn;
        private String refreshToken;
        private Long refreshOn;
        private String familyId;
        private String idToken;
        private AccountCacheEntity accountCacheEntity;
        private String environment;
        private String scopes;

        AuthenticationResultBuilder() {
        }

        public AuthenticationResultBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AuthenticationResultBuilder expiresOn(long expiresOn) {
            this.expiresOn = expiresOn;
            return this;
        }

        public AuthenticationResultBuilder extExpiresOn(long extExpiresOn) {
            this.extExpiresOn = extExpiresOn;
            return this;
        }

        public AuthenticationResultBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthenticationResultBuilder refreshOn(Long refreshOn) {
            this.refreshOn = refreshOn;
            return this;
        }

        public AuthenticationResultBuilder familyId(String familyId) {
            this.familyId = familyId;
            return this;
        }

        public AuthenticationResultBuilder idToken(String idToken) {
            this.idToken = idToken;
            return this;
        }

        public AuthenticationResultBuilder accountCacheEntity(AccountCacheEntity accountCacheEntity) {
            this.accountCacheEntity = accountCacheEntity;
            return this;
        }

        public AuthenticationResultBuilder environment(String environment) {
            this.environment = environment;
            return this;
        }

        public AuthenticationResultBuilder scopes(String scopes) {
            this.scopes = scopes;
            return this;
        }

        public AuthenticationResult build() {
            return new AuthenticationResult(this.accessToken, this.expiresOn, this.extExpiresOn, this.refreshToken, this.refreshOn, this.familyId, this.idToken, this.accountCacheEntity, this.environment, this.scopes);
        }

        public String toString() {
            return "AuthenticationResult.AuthenticationResultBuilder(accessToken=" + this.accessToken + ", expiresOn=" + this.expiresOn + ", extExpiresOn=" + this.extExpiresOn + ", refreshToken=" + this.refreshToken + ", refreshOn=" + this.refreshOn + ", familyId=" + this.familyId + ", idToken=" + this.idToken + ", accountCacheEntity=" + this.accountCacheEntity + ", environment=" + this.environment + ", scopes=" + this.scopes + ")";
        }
    }
}

