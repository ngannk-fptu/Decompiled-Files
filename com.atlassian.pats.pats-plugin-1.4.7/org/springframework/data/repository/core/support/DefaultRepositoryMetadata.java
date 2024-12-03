/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

public class DefaultRepositoryMetadata
extends AbstractRepositoryMetadata {
    private static final String MUST_BE_A_REPOSITORY = String.format("Given type must be assignable to %s!", Repository.class);
    private final Class<?> idType;
    private final Class<?> domainType;

    public DefaultRepositoryMetadata(Class<?> repositoryInterface) {
        super(repositoryInterface);
        Assert.isTrue((boolean)Repository.class.isAssignableFrom(repositoryInterface), (String)MUST_BE_A_REPOSITORY);
        List<TypeInformation<?>> arguments = ClassTypeInformation.from(repositoryInterface).getRequiredSuperTypeInformation(Repository.class).getTypeArguments();
        this.domainType = DefaultRepositoryMetadata.resolveTypeParameter(arguments, 0, () -> String.format("Could not resolve domain type of %s!", repositoryInterface));
        this.idType = DefaultRepositoryMetadata.resolveTypeParameter(arguments, 1, () -> String.format("Could not resolve id type of %s!", repositoryInterface));
    }

    private static Class<?> resolveTypeParameter(List<TypeInformation<?>> arguments, int index, Supplier<String> exceptionMessage) {
        if (arguments.size() <= index || arguments.get(index) == null) {
            throw new IllegalArgumentException(exceptionMessage.get());
        }
        return arguments.get(index).getType();
    }

    @Override
    public Class<?> getIdType() {
        return this.idType;
    }

    @Override
    public Class<?> getDomainType() {
        return this.domainType;
    }
}

