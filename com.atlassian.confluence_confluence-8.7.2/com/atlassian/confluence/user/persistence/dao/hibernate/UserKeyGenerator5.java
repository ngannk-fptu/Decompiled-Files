/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.id.IdentifierGenerator
 */
package com.atlassian.confluence.user.persistence.dao.hibernate;

import com.atlassian.confluence.user.persistence.dao.hibernate.ConfluenceHexUserKeyGenerator;
import com.atlassian.sal.api.user.UserKey;
import java.io.Serializable;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UserKeyGenerator5
implements IdentifierGenerator {
    private final ConfluenceHexUserKeyGenerator confluenceHexUserKeyGenerator = new ConfluenceHexUserKeyGenerator();

    public UserKey generate(SessionImplementor cache, Object obj) {
        return new UserKey((String)((Object)this.confluenceHexUserKeyGenerator.generate(null, obj)));
    }

    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return new UserKey((String)((Object)this.confluenceHexUserKeyGenerator.generate(null, object)));
    }
}

