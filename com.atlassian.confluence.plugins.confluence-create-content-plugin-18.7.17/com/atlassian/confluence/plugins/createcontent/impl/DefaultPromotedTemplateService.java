/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.createcontent.SpaceBandanaContext;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.services.PromotedTemplateService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPromotedTemplateService
implements PromotedTemplateService {
    public static final String KEY_PROMOTED_TEMPLATES = "promotedTemplates";
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final BandanaManager bandanaManager;

    @Autowired
    public DefaultPromotedTemplateService(@ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, @ComponentImport BandanaManager bandanaManager) {
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.bandanaManager = bandanaManager;
    }

    @Override
    public void promoteTemplate(long templateId, @Nonnull String spaceKey) throws BlueprintIllegalArgumentException {
        this.toggleTemplatePromotion(templateId, spaceKey, true);
    }

    @Override
    public void demoteTemplate(long templateId, @Nonnull String spaceKey) throws BlueprintIllegalArgumentException {
        this.toggleTemplatePromotion(templateId, spaceKey, false);
    }

    public List<Long> getPromotedTemplates(@Nonnull Space space) {
        List promotedTemplateIds = (List)this.bandanaManager.getValue((BandanaContext)new SpaceBandanaContext(space), KEY_PROMOTED_TEMPLATES);
        if (promotedTemplateIds == null) {
            return Lists.newArrayList();
        }
        return promotedTemplateIds;
    }

    private void toggleTemplatePromotion(long templateId, @Nonnull String spaceKey, boolean promote) throws BlueprintIllegalArgumentException {
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            throw new BlueprintIllegalArgumentException("Space key is required to promote/demote template with id: " + templateId, ResourceErrorType.PARAMETER_MISSING, (Object)"spaceKey");
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new BlueprintIllegalArgumentException("Space with key '" + spaceKey + "' could not be found.", ResourceErrorType.NOT_FOUND_SPACE, (Object)spaceKey);
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, (Object)space)) {
            throw new BlueprintIllegalArgumentException("Only space administrators can enable / disable plugin modules per space", ResourceErrorType.PERMISSION_USER_ADMIN_SPACE, (Object)spaceKey);
        }
        SpaceBandanaContext spaceBandanaContext = new SpaceBandanaContext(space);
        List ids = (List)this.bandanaManager.getValue((BandanaContext)spaceBandanaContext, KEY_PROMOTED_TEMPLATES);
        HashSet promotedTemplatesIds = Sets.newHashSet();
        if (ids != null) {
            promotedTemplatesIds.addAll(ids);
        }
        if (promote) {
            promotedTemplatesIds.add(templateId);
        } else {
            promotedTemplatesIds.remove(templateId);
        }
        this.bandanaManager.setValue((BandanaContext)spaceBandanaContext, KEY_PROMOTED_TEMPLATES, (Object)Lists.newArrayList((Iterable)promotedTemplatesIds));
    }

    private long matchTemplateId(Long promotedTemplateId, Collection<PageTemplate> templatesInSpace) {
        for (PageTemplate templateInSpace : templatesInSpace) {
            long id = templateInSpace.getId();
            if (!promotedTemplateId.equals(id)) continue;
            return promotedTemplateId;
        }
        return 0L;
    }
}

