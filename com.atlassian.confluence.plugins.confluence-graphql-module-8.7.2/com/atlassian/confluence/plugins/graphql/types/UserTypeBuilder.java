/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.UnknownUser
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.graphql.json.types.JsonObjectTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLTypeBuilder
 *  com.atlassian.graphql.spi.GraphQLTypeBuilderContext
 *  com.atlassian.graphql.utils.ReflectionUtils
 *  graphql.schema.GraphQLType
 */
package com.atlassian.confluence.plugins.graphql.types;

import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.UnknownUser;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.graphql.json.types.JsonObjectTypeBuilder;
import com.atlassian.graphql.spi.GraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLTypeBuilder;
import com.atlassian.graphql.spi.GraphQLTypeBuilderContext;
import com.atlassian.graphql.utils.ReflectionUtils;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public class UserTypeBuilder
extends JsonObjectTypeBuilder {
    public UserTypeBuilder(GraphQLTypeBuilder typeBuilder, GraphQLExtensions extensions) {
        super(typeBuilder, extensions);
    }

    public boolean canBuildType(Type type, AnnotatedElement element) {
        return Person.class.isAssignableFrom(ReflectionUtils.getClazz((Type)type));
    }

    public GraphQLType buildType(String typeName, Type type, AnnotatedElement element, GraphQLTypeBuilderContext context) {
        return super.buildFromTypes(typeName, new Type[]{Person.class, User.class, KnownUser.class, UnknownUser.class}, element, context);
    }
}

