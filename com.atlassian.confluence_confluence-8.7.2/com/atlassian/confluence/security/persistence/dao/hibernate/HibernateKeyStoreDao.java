/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.transaction.annotation.Transactional
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.trust.KeyStore;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Transactional
public class HibernateKeyStoreDao
implements KeyStore {
    private final HibernateTemplate hibernateTemplate;

    public HibernateKeyStoreDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    @Transactional(readOnly=true)
    public PrivateKey getPrivateKey(String alias) {
        return (PrivateKey)this.getKey(alias, "private");
    }

    @Override
    @Transactional(readOnly=true)
    public PublicKey getPublicKey(String alias) {
        return (PublicKey)this.getKey(alias, "public");
    }

    @Override
    @Transactional(readOnly=true)
    public @Nullable KeyPair getKeyPair(String alias) {
        List keys = this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.ks_getOrphanedKeysByAlias", "alias", (Object)alias);
        if (CollectionUtils.isEmpty((Collection)keys)) {
            return null;
        }
        if (keys.size() < 2) {
            return null;
        }
        if (keys.size() > 2) {
            throw new IncorrectResultSizeDataAccessException(2, keys.size());
        }
        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        for (Object key : keys) {
            Key javaKey = ((AliasedKey)key).getKey();
            if (javaKey instanceof PublicKey) {
                publicKey = (PublicKey)javaKey;
                continue;
            }
            privateKey = (PrivateKey)javaKey;
        }
        return new KeyPair(publicKey, privateKey);
    }

    private Key getKey(String alias, String keyType) {
        List keys = this.hibernateTemplate.findByNamedQuery("confluence.ks_getKeyByAliasAndType", new Object[]{alias, keyType});
        AliasedKey aliasedKey = (AliasedKey)DataAccessUtils.requiredSingleResult((Collection)keys);
        return aliasedKey.getKey();
    }

    @Override
    public void storeKeyPair(String alias, KeyPair keyPair) {
        this.storePublicKey(alias, keyPair.getPublic());
        this.storeKey(alias, keyPair.getPrivate());
    }

    private void storeKey(String alias, @Nullable Key key) {
        AliasedKey aliasedKey = new AliasedKey();
        aliasedKey.setAlias(alias);
        aliasedKey.setKey(key);
        this.hibernateTemplate.save((Object)aliasedKey);
    }

    @Override
    public void storePublicKey(String alias, @Nullable PublicKey publicKey) {
        this.storeKey(alias, publicKey);
    }
}

