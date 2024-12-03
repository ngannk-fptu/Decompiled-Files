/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.agent.builder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class LambdaFactory {
    private static final String FIELD_NAME = "CLASS_FILE_TRANSFORMERS";
    @SuppressFBWarnings(value={"MS_MUTABLE_COLLECTION_PKGPROTECT"}, justification="The field must be accessible by different class loader instances.")
    public static final Map<ClassFileTransformer, LambdaFactory> CLASS_FILE_TRANSFORMERS = new ConcurrentHashMap<ClassFileTransformer, LambdaFactory>();
    private final Object target;
    private final Method dispatcher;

    public LambdaFactory(Object target, Method dispatcher) {
        this.target = target;
        this.dispatcher = dispatcher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean register(ClassFileTransformer classFileTransformer, Object classFileFactory) {
        try {
            Map classFileTransformers;
            TypeDescription typeDescription = TypeDescription.ForLoadedType.of(LambdaFactory.class);
            Class<?> lambdaFactory = ClassInjector.UsingReflection.ofSystemClassLoader().inject(Collections.singletonMap(typeDescription, ClassFileLocator.ForClassLoader.read(LambdaFactory.class))).get(typeDescription);
            Map map = classFileTransformers = (Map)lambdaFactory.getField(FIELD_NAME).get(null);
            synchronized (map) {
                boolean bl;
                try {
                    bl = classFileTransformers.isEmpty();
                    Object var8_9 = null;
                }
                catch (Throwable throwable) {
                    Object var8_10 = null;
                    classFileTransformers.put(classFileTransformer, lambdaFactory.getConstructor(Object.class, Method.class).newInstance(classFileFactory, classFileFactory.getClass().getMethod("make", Object.class, String.class, Object.class, Object.class, Object.class, Object.class, Boolean.TYPE, List.class, List.class, Collection.class)));
                    throw throwable;
                }
                classFileTransformers.put(classFileTransformer, lambdaFactory.getConstructor(Object.class, Method.class).newInstance(classFileFactory, classFileFactory.getClass().getMethod("make", Object.class, String.class, Object.class, Object.class, Object.class, Object.class, Boolean.TYPE, List.class, List.class, Collection.class)));
                return bl;
            }
        }
        catch (RuntimeException exception) {
            throw exception;
        }
        catch (Exception exception) {
            throw new IllegalStateException("Could not register class file transformer", exception);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean release(ClassFileTransformer classFileTransformer) {
        try {
            Map classFileTransformers;
            Map map = classFileTransformers = (Map)ClassLoader.getSystemClassLoader().loadClass(LambdaFactory.class.getName()).getField(FIELD_NAME).get(null);
            synchronized (map) {
                return classFileTransformers.remove(classFileTransformer) != null && classFileTransformers.isEmpty();
            }
        }
        catch (RuntimeException exception) {
            throw exception;
        }
        catch (Exception exception) {
            throw new IllegalStateException("Could not release class file transformer", exception);
        }
    }

    private byte[] invoke(Object caller, String invokedName, Object invokedType, Object samMethodType, Object implMethod, Object instantiatedMethodType, boolean serializable, List<Class<?>> markerInterfaces, List<?> additionalBridges, Collection<ClassFileTransformer> classFileTransformers) {
        try {
            return (byte[])this.dispatcher.invoke(this.target, caller, invokedName, invokedType, samMethodType, implMethod, instantiatedMethodType, serializable, markerInterfaces, additionalBridges, classFileTransformers);
        }
        catch (RuntimeException exception) {
            throw exception;
        }
        catch (Exception exception) {
            throw new IllegalStateException("Cannot create class for lambda expression", exception);
        }
    }

    public static byte[] make(Object caller, String invokedName, Object invokedType, Object samMethodType, Object implMethod, Object instantiatedMethodType, boolean serializable, List<Class<?>> markerInterfaces, List<?> additionalBridges) {
        return CLASS_FILE_TRANSFORMERS.values().iterator().next().invoke(caller, invokedName, invokedType, samMethodType, implMethod, instantiatedMethodType, serializable, markerInterfaces, additionalBridges, CLASS_FILE_TRANSFORMERS.keySet());
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        if (!this.target.equals(((LambdaFactory)object).target)) {
            return false;
        }
        return this.dispatcher.equals(((LambdaFactory)object).dispatcher);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.dispatcher.hashCode();
    }
}

