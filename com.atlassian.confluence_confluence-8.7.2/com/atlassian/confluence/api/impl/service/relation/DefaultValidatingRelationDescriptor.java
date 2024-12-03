/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.ValidatingRelationDescriptor
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 */
package com.atlassian.confluence.api.impl.service.relation;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.ValidatingRelationDescriptor;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;

public class DefaultValidatingRelationDescriptor<S extends Relatable, T extends Relatable>
implements ValidatingRelationDescriptor<S, T> {
    private final String relationName;
    private Class<S> sourceClass;
    private Class<T> targetClass;

    protected DefaultValidatingRelationDescriptor(RelationDescriptor relationDescriptor) {
        this.relationName = relationDescriptor.getRelationName();
        this.sourceClass = relationDescriptor.getSourceClass();
        this.targetClass = relationDescriptor.getTargetClass();
    }

    public String getRelationName() {
        return this.relationName;
    }

    public ValidationResult canRelate(S source, T target) {
        SimpleValidationResult.Builder resultBuilder = SimpleValidationResult.builder().authorized(true);
        if ((source instanceof Space || source instanceof Content) && target instanceof User) {
            resultBuilder.addError("Unrecognised source / type combination", new Object[0]);
        }
        if (!this.sourceClass.isInstance(source)) {
            resultBuilder.addError(String.format("The source of a '%s' relation must be a %s", this.relationName, this.sourceClass.getSimpleName()), new Object[0]);
        }
        if (!this.targetClass.isInstance(target)) {
            resultBuilder.addError(String.format("The target of a '%s' relation must be a %s", this.relationName, this.targetClass.getSimpleName()), new Object[0]);
        }
        return resultBuilder.build();
    }

    public Class<S> getSourceClass() {
        return this.sourceClass;
    }

    public Class<T> getTargetClass() {
        return this.targetClass;
    }
}

