/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.Application
 *  com.atlassian.security.auth.trustedapps.ApplicationRetriever$RetrievalException
 *  com.atlassian.security.auth.trustedapps.CurrentApplication
 *  com.atlassian.security.auth.trustedapps.DefaultTrustedApplication
 *  com.atlassian.security.auth.trustedapps.EncryptedCertificate
 *  com.atlassian.security.auth.trustedapps.EncryptionProvider
 *  com.atlassian.security.auth.trustedapps.RequestConditions
 *  com.atlassian.security.auth.trustedapps.TrustedApplication
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  com.google.common.collect.Collections2
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.security.persistence.dao.TrustedApplicationDao;
import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.trust.AsymmetricKeyFactory;
import com.atlassian.confluence.security.trust.ConfluenceApplication;
import com.atlassian.confluence.security.trust.ConfluenceTrustedApplication;
import com.atlassian.confluence.security.trust.CurrentApplicationIdProvider;
import com.atlassian.confluence.security.trust.KeyPairInitialiser;
import com.atlassian.confluence.security.trust.KeyStore;
import com.atlassian.confluence.security.trust.TrustedApplicationIpRestriction;
import com.atlassian.confluence.security.trust.TrustedApplicationRestriction;
import com.atlassian.confluence.security.trust.TrustedApplicationUrlRestriction;
import com.atlassian.confluence.security.trust.TrustedApplicationsManager;
import com.atlassian.confluence.security.trust.TrustedToken;
import com.atlassian.confluence.security.trust.TrustedTokenFactory;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultTrustedApplication;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.google.common.collect.Collections2;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultTrustedApplicationsManager
implements TrustedApplicationsManager,
KeyPairInitialiser,
TrustedTokenFactory,
TrustedApplicationsConfigurationManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultTrustedApplicationsManager.class);
    private EncryptionProvider encryptionProvider;
    private AsymmetricKeyFactory asymmetricKeyFactory;
    private KeyStore keyStoreDao;
    private TrustedApplicationDao trustedApplicationDao;
    private CurrentApplicationIdProvider currentApplicationIdProvider;

    @Override
    public void saveTrustedApplication(ConfluenceTrustedApplication trustedApplication) {
        this.trustedApplicationDao.saveHibernateTrustedApplication(trustedApplication);
    }

    @Override
    public void deleteTrustedApplication(ConfluenceTrustedApplication trustedApplication) {
        this.trustedApplicationDao.deleteHibernateTrustedApplication(trustedApplication);
    }

    @Override
    @Transactional(readOnly=true)
    public ConfluenceTrustedApplication getTrustedApplicationByAlias(String alias) {
        return this.trustedApplicationDao.findByKeyAlias(alias);
    }

    @Override
    @Transactional(readOnly=true)
    public TrustedToken getToken(String url) {
        String userName = AuthenticatedUserThreadLocal.getUsername();
        if (userName == null) {
            return null;
        }
        return new EncodedTrustedToken(userName, this.getCurrentApplication().encode(userName, url));
    }

    @Override
    @Transactional(readOnly=true)
    public Collection<ConfluenceTrustedApplication> getAllTrustedApplications() {
        return this.trustedApplicationDao.findAll();
    }

    @Override
    @Transactional(readOnly=true)
    public CurrentApplication getCurrentApplication() {
        String currentApplicationId = this.currentApplicationIdProvider.getCurrentApplicationId();
        KeyPair keyPair = this.keyStoreDao.getKeyPair(currentApplicationId);
        if (keyPair == null) {
            return null;
        }
        return new ConfluenceApplication(keyPair, currentApplicationId);
    }

    @Override
    public void initConfluenceKey() throws NoSuchProviderException, NoSuchAlgorithmException {
        CurrentApplication application = this.getCurrentApplication();
        if (application != null) {
            if (log.isInfoEnabled()) {
                log.info("Not initialising key pair as one already exists");
            }
            return;
        }
        this.storeCurrentApplication(this.generateNewConfluenceApplication());
        if (log.isInfoEnabled()) {
            log.info("Generated key pair with application id " + this.getCurrentApplication().getID());
        }
    }

    private ConfluenceApplication generateNewConfluenceApplication() throws NoSuchAlgorithmException, NoSuchProviderException {
        return new ConfluenceApplication(this.asymmetricKeyFactory.getNewKeyPair(), this.currentApplicationIdProvider.getCurrentApplicationId());
    }

    private void storeCurrentApplication(ConfluenceApplication application) {
        if (this.getCurrentApplication() != null) {
            throw new IllegalStateException("Key pair already exists for this instance");
        }
        this.keyStoreDao.storeKeyPair(application.getID(), application.getKeyPair());
    }

    public void setKeyStoreDao(KeyStore keyStoreDao) {
        this.keyStoreDao = keyStoreDao;
    }

    public void setTrustedApplicationDao(TrustedApplicationDao trustedApplicationDao) {
        this.trustedApplicationDao = trustedApplicationDao;
    }

    public void setAsymmetricKeyFactory(AsymmetricKeyFactory asymmetricKeyFactory) {
        this.asymmetricKeyFactory = asymmetricKeyFactory;
    }

    public void setEncryptionProvider(EncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
    }

    public void setCurrentApplicationIdProvider(CurrentApplicationIdProvider currentApplicationIdProvider) {
        this.currentApplicationIdProvider = currentApplicationIdProvider;
    }

    @Override
    @Transactional(readOnly=true)
    public ConfluenceTrustedApplication getTrustedApplication(long id) {
        return this.trustedApplicationDao.findById(id);
    }

    @Override
    @Transactional(readOnly=true)
    public ConfluenceTrustedApplication getTrustedApplicationByName(String applicationName) {
        return this.trustedApplicationDao.findByName(applicationName);
    }

    @Transactional(readOnly=true)
    public Application getApplicationCertificate(String baseUrl) throws ApplicationRetriever.RetrievalException {
        return this.encryptionProvider.getApplicationCertificate(baseUrl);
    }

    public TrustedApplication addTrustedApplication(Application in, RequestConditions conditions) {
        AliasedKey key = new AliasedKey();
        key.setAlias(in.getID());
        key.setKey(in.getPublicKey());
        ConfluenceTrustedApplication app = this.trustedApplicationDao.findByName(in.getID());
        if (app == null && (app = this.trustedApplicationDao.findByKeyAlias(in.getID())) == null) {
            app = new ConfluenceTrustedApplication();
        }
        app.setName(in.getID());
        app.setPublicKey(key);
        if (null != conditions) {
            app.setRequestTimeout((int)Math.min(Integer.MAX_VALUE, conditions.getCertificateTimeout()));
            if (conditions.getCertificateTimeout() > Integer.MAX_VALUE) {
                log.warn("The certificate timeout for the trusted application is invalid. Using Integer.MAX_VALUE instead of {}. Trusted App to be added: {}", (Object)conditions.getCertificateTimeout(), (Object)in.toString());
            }
            HashSet<TrustedApplicationRestriction> restrictions = new HashSet<TrustedApplicationRestriction>();
            for (String ipPattern : conditions.getIPPatterns()) {
                restrictions.add(new TrustedApplicationIpRestriction(ipPattern));
            }
            app.setRestrictions(restrictions);
            for (String urlPattern : conditions.getURLPatterns()) {
                app.addRestriction(new TrustedApplicationUrlRestriction(urlPattern));
            }
        }
        this.trustedApplicationDao.saveHibernateTrustedApplication(app);
        return new DefaultTrustedApplication(this.encryptionProvider, in.getPublicKey(), in.getID(), conditions);
    }

    public boolean deleteApplication(String id) {
        ConfluenceTrustedApplication cta = this.trustedApplicationDao.findByKeyAlias(id);
        if (null == cta) {
            return false;
        }
        this.trustedApplicationDao.deleteHibernateTrustedApplication(cta);
        return true;
    }

    @Transactional(readOnly=true)
    public Iterable<TrustedApplication> getTrustedApplications() {
        Collection<ConfluenceTrustedApplication> confluenceTrustedApplications = this.trustedApplicationDao.findAll();
        return Collections2.transform(confluenceTrustedApplications, from -> {
            if (null == from) {
                return null;
            }
            return from.toDefaultTrustedApplication(this.encryptionProvider);
        });
    }

    private static class EncodedTrustedToken
    implements TrustedToken {
        private final String userName;
        private final String encodedKey;
        private final String encodedToken;
        private final String applicationId;
        private final String magicNumber;
        private final Integer protocolVersion;
        private final String signature;

        private EncodedTrustedToken(String userName, EncryptedCertificate certificate) {
            this.userName = userName;
            this.encodedToken = certificate.getCertificate();
            this.applicationId = certificate.getID();
            this.encodedKey = certificate.getSecretKey();
            this.magicNumber = certificate.getMagicNumber();
            this.protocolVersion = certificate.getProtocolVersion();
            this.signature = certificate.getSignature();
        }

        @Override
        public String getUserName() {
            return this.userName;
        }

        @Override
        public String getApplicationId() {
            return this.applicationId;
        }

        @Override
        public String getEncodedToken() {
            return this.encodedToken;
        }

        @Override
        public String getEncodedKey() {
            return this.encodedKey;
        }

        @Override
        public String getMagicNumber() {
            return this.magicNumber;
        }

        @Override
        public Integer getProtocolVersion() {
            return this.protocolVersion;
        }

        @Override
        public String getSignature() {
            return this.signature;
        }

        public String toString() {
            return "EncodedTrustedToken: userName: " + this.userName + "; appId: " + this.applicationId;
        }
    }
}

