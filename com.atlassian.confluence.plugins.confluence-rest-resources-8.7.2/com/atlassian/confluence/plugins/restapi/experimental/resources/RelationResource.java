/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationDescriptors
 *  com.atlassian.confluence.api.model.relations.RelationInstance
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.relations.RelationService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.restapi.experimental.resources;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptors;
import com.atlassian.confluence.api.model.relations.RelationInstance;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@ExperimentalApi
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/relation/{sourceType}/{sourceKey}/{relationName}/to{targetType}/{targetKey}")
public class RelationResource {
    private final RelationService relationService;
    private final PersonService personService;

    public RelationResource(@ComponentImport RelationService relationService, @ComponentImport PersonService personService) {
        Preconditions.checkNotNull((Object)relationService, (Object)"relationService must not be null");
        Preconditions.checkNotNull((Object)personService, (Object)"personFactory must not be null");
        this.relationService = relationService;
        this.personService = personService;
    }

    @GET
    public Response isRelated(@PathParam(value="sourceType") String sourceType, @PathParam(value="sourceKey") String sourceKey, @PathParam(value="relationName") String relationName, @PathParam(value="targetType") String targetType, @PathParam(value="targetKey") String targetKey, @QueryParam(value="sourceStatus") ContentStatus sourceStatus, @QueryParam(value="targetStatus") ContentStatus targetStatus, @QueryParam(value="sourceVersion") Integer sourceVersion, @QueryParam(value="targetVersion") Integer targetVersion) throws NotFoundException, PermissionException {
        Class sourceClass = RelationResource.getRelatableClass(sourceType, "sourceType");
        Class targetClass = RelationResource.getRelatableClass(targetType, "targetType");
        if (!this.relationService.isRelated(this.buildRelatable(sourceClass, sourceKey, sourceStatus, sourceVersion, "source"), this.getRelationDescriptor(sourceClass, relationName, targetClass), this.buildRelatable(targetClass, targetKey, targetStatus, targetVersion, "target"))) {
            throw new NotFoundException("Relationship does not exist", SimpleValidationResult.VALID);
        }
        return Response.ok().entity(Collections.emptyMap()).build();
    }

    @PUT
    public Response create(@PathParam(value="sourceType") String sourceType, @PathParam(value="sourceKey") String sourceKey, @PathParam(value="relationName") String relationName, @PathParam(value="targetType") String targetType, @PathParam(value="targetKey") String targetKey, @QueryParam(value="sourceStatus") ContentStatus sourceStatus, @QueryParam(value="targetStatus") ContentStatus targetStatus, @QueryParam(value="sourceVersion") Integer sourceVersion, @QueryParam(value="targetVersion") Integer targetVersion) throws NotFoundException, PermissionException {
        this.relationService.create(this.getRelationInstance(sourceType, sourceKey, sourceStatus, sourceVersion, relationName, targetType, targetKey, targetStatus, targetVersion));
        return Response.ok().entity(Collections.emptyMap()).build();
    }

    @DELETE
    public Response delete(@PathParam(value="sourceType") String sourceType, @PathParam(value="sourceKey") String sourceKey, @PathParam(value="relationName") String relationName, @PathParam(value="targetType") String targetType, @PathParam(value="targetKey") String targetKey, @QueryParam(value="sourceStatus") ContentStatus sourceStatus, @QueryParam(value="targetStatus") ContentStatus targetStatus, @QueryParam(value="sourceVersion") Integer sourceVersion, @QueryParam(value="targetVersion") Integer targetVersion) throws NotFoundException, PermissionException {
        this.relationService.delete(this.getRelationInstance(sourceType, sourceKey, sourceStatus, sourceVersion, relationName, targetType, targetKey, targetStatus, targetVersion));
        return Response.noContent().build();
    }

    private RelationInstance getRelationInstance(String sourceType, String sourceKey, ContentStatus sourceStatus, Integer sourceVersion, String relationName, String targetType, String targetKey, ContentStatus targetStatus, Integer targetVersion) {
        Class sourceClass = RelationResource.getRelatableClass(sourceType, "sourceType");
        Class targetClass = RelationResource.getRelatableClass(targetType, "targetType");
        return this.getRelationInstance(sourceClass, sourceKey, sourceStatus, sourceVersion, relationName, targetClass, targetKey, targetStatus, targetVersion);
    }

    private RelationInstance getRelationInstance(Class sourceType, String sourceKey, ContentStatus sourceStatus, Integer sourceVersion, String relationName, Class targetType, String targetKey, ContentStatus targetStatus, Integer targetVersion) {
        return RelationInstance.builder((Relatable)this.buildRelatable(sourceType, sourceKey, sourceStatus, sourceVersion, "source"), (RelationDescriptor)this.getRelationDescriptor(sourceType, relationName, targetType), (Relatable)this.buildRelatable(targetType, targetKey, targetStatus, targetVersion, "target")).build();
    }

    private RelationDescriptor getRelationDescriptor(Class sourceType, String relationName, Class targetType) {
        if (Strings.isNullOrEmpty((String)relationName)) {
            throw new BadRequestException("relationName parameter is required but was empty");
        }
        return RelationDescriptors.lookupBuiltinOrCreate((Class)sourceType, (String)relationName, (Class)targetType);
    }

    private Relatable buildRelatable(Class type, String key, ContentStatus status, Integer version, String side) {
        if (Strings.isNullOrEmpty((String)key)) {
            throw new BadRequestException(side + " key parameter is required but was empty");
        }
        if (type.equals(User.class)) {
            return this.buildLookupUser(key);
        }
        if (type.equals(Space.class)) {
            return Space.builder().key(key).build();
        }
        if (type.equals(Content.class)) {
            Content.ContentBuilder contentBuilder = Content.builder().id(ContentId.deserialise((String)key)).status(status);
            if (version != null) {
                contentBuilder.version(Version.builder().number(version.intValue()).build());
            }
            return contentBuilder.build();
        }
        throw new BadRequestException("Expected a user, space or content as the " + side);
    }

    private static Class getRelatableClass(String type, String side) {
        if (type.equalsIgnoreCase("user")) {
            return User.class;
        }
        if (type.equalsIgnoreCase("space")) {
            return Space.class;
        }
        if (type.equalsIgnoreCase("content")) {
            return Content.class;
        }
        throw new BadRequestException("Expected user, space or content as the " + side);
    }

    private User buildLookupUser(String userKey) {
        if (Strings.isNullOrEmpty((String)userKey)) {
            throw new BadRequestException("userKey parameter is required but was empty");
        }
        if (userKey.equals("current")) {
            Person person = this.personService.getCurrentUser(new Expansion[0]);
            if (person instanceof Anonymous) {
                throw new PermissionException("Client must be authenticated to access this resource");
            }
            if (!(person instanceof User)) {
                throw new BadRequestException("Expected userKey to describe a User, but was " + person.getClass().getName());
            }
            return (User)person;
        }
        return new User(null, null, null, new UserKey(userKey));
    }
}

