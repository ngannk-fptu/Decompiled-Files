/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.type.PrimitiveWrapperHelper;
import org.hibernate.loader.custom.JdbcResultMetadata;
import org.hibernate.loader.custom.ResultColumnProcessor;
import org.hibernate.loader.custom.ScalarResultColumnProcessor;
import org.hibernate.type.Type;

public class ConstructorResultColumnProcessor
implements ResultColumnProcessor {
    private final Class targetClass;
    private final ScalarResultColumnProcessor[] scalarProcessors;
    private Constructor constructor;

    public ConstructorResultColumnProcessor(Class targetClass, ScalarResultColumnProcessor[] scalarProcessors) {
        this.targetClass = targetClass;
        this.scalarProcessors = scalarProcessors;
    }

    @Override
    public void performDiscovery(JdbcResultMetadata metadata, List<Type> types, List<String> aliases) throws SQLException {
        ArrayList<Type> localTypes = new ArrayList<Type>();
        for (ScalarResultColumnProcessor scalar : this.scalarProcessors) {
            scalar.performDiscovery(metadata, localTypes, aliases);
        }
        types.addAll(localTypes);
        this.constructor = ConstructorResultColumnProcessor.resolveConstructor(this.targetClass, localTypes);
    }

    @Override
    public Object extract(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        if (this.constructor == null) {
            throw new IllegalStateException("Constructor to call was null");
        }
        Object[] args = new Object[this.scalarProcessors.length];
        for (int i = 0; i < this.scalarProcessors.length; ++i) {
            args[i] = this.scalarProcessors[i].extract(data, resultSet, session);
        }
        try {
            return this.constructor.newInstance(args);
        }
        catch (Exception e) {
            throw new HibernateException(String.format("Unable to call %s constructor", this.constructor.getDeclaringClass()), e);
        }
    }

    private static Constructor resolveConstructor(Class targetClass, List<Type> types) {
        for (Constructor<?> constructor : targetClass.getConstructors()) {
            Class<?>[] argumentTypes = constructor.getParameterTypes();
            if (argumentTypes.length != types.size()) continue;
            boolean allMatched = true;
            for (int i = 0; i < argumentTypes.length; ++i) {
                if (ConstructorResultColumnProcessor.areAssignmentCompatible(argumentTypes[i], types.get(i).getReturnedClass())) continue;
                allMatched = false;
                break;
            }
            if (!allMatched) continue;
            return constructor;
        }
        throw new IllegalArgumentException("Could not locate appropriate constructor on class : " + targetClass.getName());
    }

    private static boolean areAssignmentCompatible(Class argumentType, Class typeReturnedClass) {
        return argumentType.isAssignableFrom(typeReturnedClass) || PrimitiveWrapperHelper.arePrimitiveWrapperEquivalents(argumentType, typeReturnedClass);
    }
}

