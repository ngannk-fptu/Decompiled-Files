/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.dao.sso;

import com.atlassian.crowd.model.sso.idp.SAMLTrustEntity;
import java.util.List;
import java.util.Optional;

public interface SAMLTrustDAO {
    public SAMLTrustEntity addSamlTrustEntity(SAMLTrustEntity var1);

    public Optional<SAMLTrustEntity> findSamlTrustEntityById(long var1);

    public List<SAMLTrustEntity> findAll();
}

