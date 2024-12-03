/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.reference.Reference
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;

public class VersionFactory {
    private final PersonFactory personFactory;

    public VersionFactory(PersonFactory personFactory) {
        this.personFactory = personFactory;
    }

    public Version build(ContentEntityObject entity, Expansions subExpansions, ContentFactory contentFactory) {
        if (entity == null) {
            return null;
        }
        Person by = this.personFactory.forUser(entity.getLastModifier(), subExpansions.getSubExpansions("by"));
        String message = entity.getVersionComment();
        boolean minorEdit = false;
        if (entity instanceof Attachment) {
            minorEdit = ((Attachment)entity).isMinorEdit();
        }
        boolean hidden = false;
        if (entity instanceof Attachment) {
            hidden = ((Attachment)entity).isHidden();
        }
        return Version.builder().by(by).when(entity.getLastModificationDate()).message(message).number(entity.getVersion()).minorEdit(minorEdit).hidden(hidden).content(contentFactory.buildRef(entity, Fauxpansions.fauxpansions(subExpansions, "content"))).build();
    }

    public Reference<Version> buildRef(ContentEntityObject entity, Fauxpansions fauxpansions, ContentFactory contentFactory) {
        if (!fauxpansions.canExpand()) {
            if (entity == null) {
                return Reference.collapsed(Version.class);
            }
            return Version.buildReference((int)entity.getVersion());
        }
        if (entity == null) {
            return Reference.empty(Version.class);
        }
        return Reference.to((Object)this.build(entity, fauxpansions.getSubExpansions(), contentFactory));
    }
}

