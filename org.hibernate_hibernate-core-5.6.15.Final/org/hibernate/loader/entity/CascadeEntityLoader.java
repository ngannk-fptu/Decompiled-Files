/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.entity.AbstractEntityLoader;
import org.hibernate.loader.entity.CascadeEntityJoinWalker;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class CascadeEntityLoader
extends AbstractEntityLoader {
    public CascadeEntityLoader(OuterJoinLoadable persister, CascadingAction action, SessionFactoryImplementor factory) throws MappingException {
        super(persister, persister.getIdentifierType(), factory, LoadQueryInfluencers.NONE);
        CascadeEntityJoinWalker walker = new CascadeEntityJoinWalker(persister, action, factory);
        this.initFromWalker(walker);
        this.postInstantiate();
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static select for action %s on entity %s: %s", action, this.entityName, this.getSQLString());
        }
    }
}

