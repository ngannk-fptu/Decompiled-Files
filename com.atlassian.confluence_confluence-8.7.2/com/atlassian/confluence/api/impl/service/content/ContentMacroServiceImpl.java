/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.MacroInstance
 *  com.atlassian.confluence.api.model.content.id.AttachmentContentId
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentMacroService
 *  com.atlassian.confluence.api.service.content.ContentMacroService$MacroInstanceFinder
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.MacroInstance;
import com.atlassian.confluence.api.model.content.id.AttachmentContentId;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentMacroService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.storage.MacroDefinitionTransformer;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.HashSet;
import java.util.Optional;

public class ContentMacroServiceImpl
implements ContentMacroService {
    private final PermissionManager permissionManager;
    private final ContentEntityManagerInternal contentEntityManager;
    private final MacroDefinitionTransformer macroDefinitionTransformer;

    public ContentMacroServiceImpl(ContentEntityManagerInternal contentEntityManager, MacroDefinitionTransformer macroDefinitionTransformer, PermissionManager permissionManager) {
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.macroDefinitionTransformer = macroDefinitionTransformer;
    }

    public ContentMacroService.MacroInstanceFinder findInContent(ContentId contentId, Expansion ... expansion) {
        return new MacroInstanceFinderImpl(contentId, expansion);
    }

    private Optional<MacroDefinition> handleMacroDefinitionsRecursively(String body, String macroId) {
        if (Strings.isNullOrEmpty((String)body)) {
            return Optional.empty();
        }
        try {
            HashSet nonMatchedMacrosToCheck = new HashSet();
            MacroDefinition[] macroDefinitionReference = new MacroDefinition[1];
            this.macroDefinitionTransformer.handleMacroDefinitions(body, new DefaultConversionContext(new RenderContext()), macroDefinition -> {
                if (this.foundMatchingMacro(macroDefinition, macroId)) {
                    macroDefinitionReference[0] = macroDefinition;
                } else {
                    nonMatchedMacrosToCheck.add(macroDefinition);
                }
            });
            if (macroDefinitionReference[0] != null) {
                return Optional.of(macroDefinitionReference[0]);
            }
            for (MacroDefinition macroDefinition2 : nonMatchedMacrosToCheck) {
                Optional<MacroDefinition> foundMacro;
                if (!Macro.BodyType.RICH_TEXT.equals((Object)macroDefinition2.getBodyType()) || !(foundMacro = this.handleMacroDefinitionsRecursively(macroDefinition2.getBodyText(), macroId)).isPresent()) continue;
                return foundMacro;
            }
            return Optional.empty();
        }
        catch (XhtmlException e) {
            throw new ServiceException((Throwable)e);
        }
    }

    private boolean foundMatchingMacro(MacroDefinition macro, String macroId) {
        Optional<MacroId> foundMacroId = macro.getMacroIdentifier();
        if (foundMacroId.isPresent() && foundMacroId.get().getId().equals(macroId)) {
            return true;
        }
        String bodyHash = macro.macroHash();
        return bodyHash != null && bodyHash.equals(macroId);
    }

    private boolean canView(ContentEntityObject entity) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, entity);
    }

    private class MacroInstanceFinderImpl
    extends AbstractFinder<MacroInstance>
    implements ContentMacroService.MacroInstanceFinder {
        private final ContentId contentId;
        private String withMacroId;
        private int version;

        public MacroInstanceFinderImpl(ContentId contentId, Expansion[] expansions) {
            super(expansions);
            this.contentId = (ContentId)Preconditions.checkNotNull((Object)contentId);
        }

        public ContentMacroService.MacroInstanceFinder withHash(String hash) {
            this.withMacroId(hash);
            return this;
        }

        public ContentMacroService.MacroInstanceFinder withMacroId(String withMacroId) {
            this.withMacroId = withMacroId;
            return this;
        }

        public ContentMacroService.MacroInstanceFinder withContentVersion(int version) {
            this.version = version;
            return this;
        }

        public Optional<MacroInstance> fetch() {
            if (this.contentId instanceof AttachmentContentId) {
                return Optional.empty();
            }
            ContentEntityObject content = ContentMacroServiceImpl.this.contentEntityManager.getById(this.contentId);
            if (content == null) {
                return Optional.empty();
            }
            if (!ContentMacroServiceImpl.this.canView(content)) {
                return Optional.empty();
            }
            if (this.withMacroId == null) {
                throw new NotImplementedServiceException("Currently can only fetch macro by id or hash");
            }
            return this.internalFetchByMacroId(content, this.withMacroId);
        }

        private Optional<MacroInstance> internalFetchByMacroId(ContentEntityObject content, String macroId) {
            int expectedVersion = this.version;
            if (expectedVersion != 0 && content.getVersion() != expectedVersion) {
                content = ContentMacroServiceImpl.this.contentEntityManager.getOtherVersion(content, expectedVersion);
            }
            if (content == null) {
                return Optional.empty();
            }
            Optional<MacroDefinition> maybeMacro = ContentMacroServiceImpl.this.handleMacroDefinitionsRecursively(content.getBodyAsString(), macroId);
            return maybeMacro.map(macroDefinition -> MacroInstance.builder().name(macroDefinition.getName()).body(Streamables.writeToString(macroDefinition.getStorageBodyStream())).parameters(macroDefinition.getParameters()).build());
        }
    }
}

