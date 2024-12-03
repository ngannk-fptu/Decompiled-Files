/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.projectcreate.spi.AggregateRoot
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootSubType
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability
 *  com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.projectcreate.crud;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.plugins.projectcreate.crud.exception.CreateSpaceFailureException;
import com.atlassian.confluence.plugins.projectcreate.crud.service.CompositeSpaceCreator;
import com.atlassian.confluence.plugins.projectcreate.crud.service.SpaceCreator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.projectcreate.spi.AggregateRoot;
import com.atlassian.plugins.projectcreate.spi.AggregateRootSubType;
import com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability;
import com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={AggregateRootTypeCapability.class})
@Component
public class ConfluenceAggregateRootTypeCapability
implements AggregateRootTypeCapability {
    private static final int SPACE_LOAD_LIMIT = 15;
    private final SpaceManager spaceManager;
    private final SpaceService spaceService;
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final Function<com.atlassian.confluence.api.model.content.Space, AggregateRoot> spaceToAggregateRootFunction;
    private final SpaceCreator spaceCreator;

    @Autowired
    public ConfluenceAggregateRootTypeCapability(@ComponentImport SpaceService spaceService, @ComponentImport UserAccessor userAccessor, @ComponentImport PermissionManager permissionManager, @ComponentImport ApplicationProperties applicationProperties, CompositeSpaceCreator spaceCreator, @ComponentImport SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
        this.spaceService = spaceService;
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.spaceCreator = spaceCreator;
        this.spaceToAggregateRootFunction = input -> AggregateRoot.makeAggregateRoot((String)input.getKey(), (String)input.getName(), (String)(applicationProperties.getBaseUrl(UrlMode.CANONICAL) + "/display/"));
    }

    public String getType() {
        return "confluence.space";
    }

    public String getLabelI18nKey() {
        return "confluence.projectcreate.space.label";
    }

    public String getDescriptionI18nKey() {
        return "confluence.projectcreate.space.description";
    }

    public boolean isAvailable() {
        return true;
    }

    public Iterable<AggregateRootSubType> getSubTypes() {
        return Collections.emptyList();
    }

    public Iterable<AggregateRoot> getExistingRoots() {
        boolean hasMore;
        ArrayList spaces = new ArrayList();
        int start = 0;
        do {
            PageResponse results = this.spaceService.find(new Expansion[0]).fetchMany((PageRequest)new SimplePageRequest(start, 15));
            spaces.addAll(results.getResults());
            hasMore = results.hasMore();
            start += 15;
        } while (hasMore);
        return spaces.stream().map(this.spaceToAggregateRootFunction).collect(Collectors.toList());
    }

    public Either<ResponseStatusWithMessage, AggregateRoot> createRoot(String username, String key, String name, Option<String> subtypeKey, Map<String, String> context) {
        return (Either)subtypeKey.fold((java.util.function.Supplier)((Supplier)() -> {
            ConfluenceUser user = this.userAccessor.getUserByName(username);
            Option<ResponseStatusWithMessage> maybeError = this.validateNewSpace(user, key, name, context);
            return (Either)maybeError.fold((java.util.function.Supplier)((Supplier)() -> {
                try {
                    return Either.right((Object)this.spaceToAggregateRootFunction.apply(this.spaceCreator.createSpace(user, key, name, context)));
                }
                catch (CreateSpaceFailureException e) {
                    return Either.left((Object)new ResponseStatusWithMessage(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), new String[0]));
                }
            }), Either::left);
        }), input -> Either.left((Object)new ResponseStatusWithMessage(Response.Status.NOT_FOUND, "atlassian.project.create.unknown.subtype", new String[]{input})));
    }

    public boolean canUserCreateRoot(String username) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        return this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, com.atlassian.confluence.api.model.content.Space.class);
    }

    public Option<AggregateRoot> getRootByKey(Option<String> maybeUsername, String entityKey) {
        return Option.option(this.spaceService.find(new Expansion[0]).withKeys(new String[]{entityKey}).fetch().map(this.spaceToAggregateRootFunction).orElse(null));
    }

    public Either<ResponseStatusWithMessage, ResponseStatusWithMessage> deleteRoot(String username, String entityKey) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        Space space = this.spaceManager.getSpace(entityKey);
        if (space == null || !this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)space)) {
            return Either.left((Object)new ResponseStatusWithMessage(Response.Status.NOT_FOUND, "confluence.projectcreate.space.delete.doesnt.exist", new String[0]));
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.REMOVE, (Object)space)) {
            return Either.left((Object)new ResponseStatusWithMessage(Response.Status.FORBIDDEN, "confluence.projectcreate.space.delete.permission.denied", new String[0]));
        }
        Boolean removed = this.spaceManager.removeSpace(space);
        if (!removed.booleanValue()) {
            return Either.left((Object)new ResponseStatusWithMessage(Response.Status.INTERNAL_SERVER_ERROR, "", new String[0]));
        }
        return Either.right((Object)new ResponseStatusWithMessage(Response.Status.NO_CONTENT, "", new String[0]));
    }

    private Option<ResponseStatusWithMessage> validateNewSpace(ConfluenceUser user, String key, String name, Map<String, String> context) {
        Option<String> results = this.spaceCreator.validateCreateSpace(user, key, name, context);
        if (results.isEmpty()) {
            return Option.none();
        }
        return Option.some((Object)new ResponseStatusWithMessage(Response.Status.BAD_REQUEST, (String)results.get(), new String[0]));
    }

    public void flushPluginSettings() {
    }
}

