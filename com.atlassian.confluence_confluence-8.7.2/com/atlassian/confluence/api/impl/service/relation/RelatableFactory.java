/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 */
package com.atlassian.confluence.api.impl.service.relation;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.impl.service.content.factory.SpaceFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.ConfluenceUser;

public class RelatableFactory<S extends RelatableEntity, T extends Relatable>
extends ModelFactory<S, T> {
    private ContentFactory contentFactory;
    private SpaceFactory spaceFactory;
    private PersonFactory personFactory;

    public RelatableFactory(ContentFactory contentFactory, SpaceFactory spaceFactory, PersonFactory personFactory) {
        this.contentFactory = contentFactory;
        this.spaceFactory = spaceFactory;
        this.personFactory = personFactory;
    }

    @Override
    public T buildFrom(S hibernateEntity, Expansions expansions) {
        if (hibernateEntity instanceof SpaceDescription) {
            return (T)this.spaceFactory.buildFrom(((SpaceDescription)hibernateEntity).getSpace(), expansions);
        }
        if (hibernateEntity instanceof ContentEntityObject) {
            return (T)this.contentFactory.buildFrom((ContentEntityObject)hibernateEntity, expansions);
        }
        if (hibernateEntity instanceof ConfluenceUser) {
            return (T)this.personFactory.fromUser((ConfluenceUser)hibernateEntity, expansions);
        }
        throw new NotImplementedServiceException("Unknown target type : " + hibernateEntity.getClass());
    }
}

