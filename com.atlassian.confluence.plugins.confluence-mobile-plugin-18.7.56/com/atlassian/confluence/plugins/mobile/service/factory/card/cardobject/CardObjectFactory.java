/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory.card.cardobject;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.helper.ContentHelper;
import com.atlassian.confluence.plugins.mobile.helper.TimeHelper;
import com.atlassian.confluence.plugins.mobile.model.card.CardObject;
import com.atlassian.confluence.plugins.mobile.model.card.ObjectId;
import com.atlassian.confluence.plugins.mobile.model.card.ObjectType;
import com.atlassian.confluence.plugins.mobile.model.card.PageCardObject;
import com.atlassian.confluence.plugins.mobile.service.factory.PersonFactory;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CardObjectFactory {
    private final PageManager pageManager;
    private final PersonFactory personFactory;
    private final TimeHelper timeHelper;
    private final PermissionManager permissionManager;

    @Autowired
    public CardObjectFactory(@Qualifier(value="pageManager") PageManager pageManager, PersonFactory personFactory, TimeHelper timeHelper, @ComponentImport PermissionManager permissionManager) {
        this.pageManager = pageManager;
        this.personFactory = personFactory;
        this.timeHelper = timeHelper;
        this.permissionManager = permissionManager;
    }

    public CardObject buildPageCardObject(Long contentId, List<SearchResult> results) {
        for (SearchResult result : results) {
            if (!ContentType.BLOG_POST.getValue().equals(result.getType()) && !ContentType.PAGE.getValue().equals(result.getType())) continue;
            return this.buildPageCardObject(contentId, result);
        }
        return this.buildPageCardObject(contentId);
    }

    public CardObject buildPageCardObject(Long contentId) {
        AbstractPage ceo = this.pageManager.getAbstractPage(contentId.longValue());
        return this.buildPageCardObject((ContentEntityObject)ceo);
    }

    public CardObject buildPageCardObject(ContentEntityObject ceo) {
        if (ceo == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)ceo)) {
            return null;
        }
        ObjectId objectId = ObjectId.of(ceo.getId(), ObjectType.valueOf(ceo.getType()));
        Person createdBy = this.personFactory.forUser(ceo.getCreator());
        PageCardObject.PageCardObjectBuilder builder = PageCardObject.builder().id(objectId).title(ceo.getDisplayTitle()).createBy(createdBy).createdDate(ceo.getCreationDate()).timeToRead(this.timeHelper.timeToRead(ceo.getBodyAsStringWithoutMarkup())).saved(ContentHelper.isSaved(ceo.getLabels()));
        if (ceo instanceof AbstractPage) {
            AbstractPage abstractPage = (AbstractPage)ceo;
            Space space = abstractPage.getSpace();
            builder.space(SpaceDto.builder().key(space.getKey()).name(space.getName()).build());
        }
        return builder.build();
    }

    private PageCardObject buildPageCardObject(Long contentId, SearchResult result) {
        ObjectId objectId = ObjectId.of(contentId, ObjectType.valueOf(result.getType()));
        Person createdBy = this.personFactory.forUser(result.getCreatorUser());
        return PageCardObject.builder().id(objectId).title(result.getDisplayTitle()).createBy(createdBy).createdDate(result.getCreationDate()).timeToRead(this.timeToRead(result)).saved(ContentHelper.isSaved(result.getPersonalLabels())).space(SpaceDto.builder().key(result.getSpaceKey()).name(result.getSpaceName()).build()).build();
    }

    private String timeToRead(SearchResult result) {
        if (result.getContent() == null) {
            return "";
        }
        return this.timeHelper.timeToReadWithMarkupContent(result.getContent(), result.getDisplayTitle());
    }
}

