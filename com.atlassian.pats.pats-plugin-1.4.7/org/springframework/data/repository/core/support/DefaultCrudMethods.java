/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class DefaultCrudMethods
implements CrudMethods {
    private static final String FIND_ONE = "findById";
    private static final String SAVE = "save";
    private static final String FIND_ALL = "findAll";
    private static final String DELETE = "delete";
    private static final String DELETE_BY_ID = "deleteById";
    private final Optional<Method> findAllMethod;
    private final Optional<Method> findOneMethod;
    private final Optional<Method> saveMethod;
    private final Optional<Method> deleteMethod;

    public DefaultCrudMethods(RepositoryMetadata metadata) {
        Assert.notNull((Object)metadata, (String)"RepositoryInformation must not be null!");
        this.findOneMethod = DefaultCrudMethods.selectMostSuitableFindOneMethod(metadata);
        this.findAllMethod = DefaultCrudMethods.selectMostSuitableFindAllMethod(metadata);
        this.deleteMethod = DefaultCrudMethods.selectMostSuitableDeleteMethod(metadata);
        this.saveMethod = DefaultCrudMethods.selectMostSuitableSaveMethod(metadata);
    }

    private static Optional<Method> selectMostSuitableSaveMethod(RepositoryMetadata metadata) {
        return Arrays.asList(metadata.getDomainType(), Object.class).stream().flatMap(it -> Optionals.toStream(DefaultCrudMethods.findMethod(metadata.getRepositoryInterface(), SAVE, it))).flatMap(it -> Optionals.toStream(DefaultCrudMethods.getMostSpecificMethod(it, metadata.getRepositoryInterface()))).findFirst();
    }

    private static Optional<Method> selectMostSuitableDeleteMethod(RepositoryMetadata metadata) {
        Stream<Pair> source = Stream.of(Pair.of(DELETE, metadata.getDomainType()), Pair.of(DELETE_BY_ID, metadata.getIdType()), Pair.of(DELETE, Object.class), Pair.of(DELETE_BY_ID, Object.class), Pair.of(DELETE, Iterable.class));
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        return source.flatMap(it -> Optionals.toStream(DefaultCrudMethods.findMethod(repositoryInterface, (String)it.getFirst(), (Class)it.getSecond()))).flatMap(it -> Optionals.toStream(DefaultCrudMethods.getMostSpecificMethod(it, repositoryInterface))).findFirst();
    }

    private static Optional<Method> selectMostSuitableFindAllMethod(RepositoryMetadata metadata) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        Supplier<Optional> withPageableOrSort = () -> Stream.of(Pageable.class, Sort.class).flatMap(it -> Optionals.toStream(DefaultCrudMethods.findMethod(repositoryInterface, FIND_ALL, it))).flatMap(it -> Optionals.toStream(DefaultCrudMethods.getMostSpecificMethod(it, repositoryInterface))).findFirst();
        Supplier<Optional> withoutParameter = () -> DefaultCrudMethods.findMethod(repositoryInterface, FIND_ALL, new Class[0]).flatMap(it -> DefaultCrudMethods.getMostSpecificMethod(it, repositoryInterface));
        return Optionals.firstNonEmpty(withPageableOrSort, withoutParameter);
    }

    private static Optional<Method> selectMostSuitableFindOneMethod(RepositoryMetadata metadata) {
        return Arrays.asList(metadata.getIdType(), Object.class).stream().flatMap(it -> Optionals.toStream(DefaultCrudMethods.findMethod(metadata.getRepositoryInterface(), FIND_ONE, it))).flatMap(it -> Optionals.toStream(DefaultCrudMethods.getMostSpecificMethod(it, metadata.getRepositoryInterface()))).findFirst();
    }

    private static Optional<Method> getMostSpecificMethod(Method method, Class<?> type) {
        return Optionals.toStream(Optional.ofNullable(ClassUtils.getMostSpecificMethod((Method)method, type))).map(BridgeMethodResolver::findBridgedMethod).peek(ReflectionUtils::makeAccessible).findFirst();
    }

    @Override
    public Optional<Method> getSaveMethod() {
        return this.saveMethod;
    }

    @Override
    public boolean hasSaveMethod() {
        return this.saveMethod.isPresent();
    }

    @Override
    public Optional<Method> getFindAllMethod() {
        return this.findAllMethod;
    }

    @Override
    public boolean hasFindAllMethod() {
        return this.findAllMethod.isPresent();
    }

    @Override
    public Optional<Method> getFindOneMethod() {
        return this.findOneMethod;
    }

    @Override
    public boolean hasFindOneMethod() {
        return this.findOneMethod.isPresent();
    }

    @Override
    public boolean hasDelete() {
        return this.deleteMethod.isPresent();
    }

    @Override
    public Optional<Method> getDeleteMethod() {
        return this.deleteMethod;
    }

    private static Optional<Method> findMethod(Class<?> type, String name, Class<?> ... parameterTypes) {
        return Optional.ofNullable(ReflectionUtils.findMethod(type, (String)name, (Class[])parameterTypes));
    }
}

