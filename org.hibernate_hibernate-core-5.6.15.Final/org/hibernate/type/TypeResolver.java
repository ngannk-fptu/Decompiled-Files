/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.type.BasicType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

@Deprecated
public class TypeResolver
implements Serializable {
    private final TypeFactory typeFactory;
    private final TypeConfiguration typeConfiguration;

    public TypeResolver(TypeConfiguration typeConfiguration, TypeFactory typeFactory) {
        this.typeConfiguration = typeConfiguration;
        this.typeFactory = typeFactory;
    }

    public void registerTypeOverride(BasicType type) {
        this.typeConfiguration.getBasicTypeRegistry().register(type);
    }

    public void registerTypeOverride(UserType type, String[] keys) {
        this.typeConfiguration.getBasicTypeRegistry().register(type, keys);
    }

    public void registerTypeOverride(CompositeUserType type, String[] keys) {
        this.typeConfiguration.getBasicTypeRegistry().register(type, keys);
    }

    public TypeFactory getTypeFactory() {
        return this.typeFactory;
    }

    public BasicType basic(String name) {
        return this.typeConfiguration.getBasicTypeRegistry().getRegisteredType(name);
    }

    public Type heuristicType(String typeName) throws MappingException {
        return this.heuristicType(typeName, null);
    }

    public Type heuristicType(String typeName, Properties parameters) throws MappingException {
        BasicType type = this.basic(typeName);
        if (type != null) {
            return type;
        }
        try {
            ClassLoaderService classLoaderService = this.typeConfiguration.getServiceRegistry().getService(ClassLoaderService.class);
            Class typeClass = classLoaderService.classForName(typeName);
            if (typeClass != null) {
                return this.typeFactory.byClass(typeClass, parameters);
            }
        }
        catch (ClassLoadingException classLoadingException) {
            // empty catch block
        }
        return null;
    }
}

