/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistryChangeEventListener;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaClass;

public interface MetaClassRegistry {
    public MetaClass getMetaClass(Class var1);

    public void setMetaClass(Class var1, MetaClass var2);

    public void removeMetaClass(Class var1);

    public MetaClassCreationHandle getMetaClassCreationHandler();

    public void setMetaClassCreationHandle(MetaClassCreationHandle var1);

    public void addMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener var1);

    public void addNonRemovableMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener var1);

    public void removeMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener var1);

    public MetaClassRegistryChangeEventListener[] getMetaClassRegistryChangeEventListeners();

    public Iterator iterator();

    public static class MetaClassCreationHandle {
        private boolean disableCustomMetaClassLookup;

        public final MetaClass create(Class theClass, MetaClassRegistry registry) {
            if (this.disableCustomMetaClassLookup) {
                return this.createNormalMetaClass(theClass, registry);
            }
            return this.createWithCustomLookup(theClass, registry);
        }

        private MetaClass createWithCustomLookup(Class theClass, MetaClassRegistry registry) {
            try {
                Class<?> customMetaClass = Class.forName("groovy.runtime.metaclass." + theClass.getName() + "MetaClass");
                if (DelegatingMetaClass.class.isAssignableFrom(customMetaClass)) {
                    Constructor<?> customMetaClassConstructor = customMetaClass.getConstructor(MetaClass.class);
                    MetaClass normalMetaClass = this.createNormalMetaClass(theClass, registry);
                    return (MetaClass)customMetaClassConstructor.newInstance(normalMetaClass);
                }
                Constructor<?> customMetaClassConstructor = customMetaClass.getConstructor(MetaClassRegistry.class, Class.class);
                return (MetaClass)customMetaClassConstructor.newInstance(registry, theClass);
            }
            catch (ClassNotFoundException e) {
                return this.createNormalMetaClass(theClass, registry);
            }
            catch (Exception e) {
                throw new GroovyRuntimeException("Could not instantiate custom Metaclass for class: " + theClass.getName() + ". Reason: " + e, e);
            }
        }

        protected MetaClass createNormalMetaClass(Class theClass, MetaClassRegistry registry) {
            if (GeneratedClosure.class.isAssignableFrom(theClass)) {
                return new ClosureMetaClass(registry, theClass);
            }
            return new MetaClassImpl(registry, theClass);
        }

        public boolean isDisableCustomMetaClassLookup() {
            return this.disableCustomMetaClassLookup;
        }

        public void setDisableCustomMetaClassLookup(boolean disableCustomMetaClassLookup) {
            this.disableCustomMetaClassLookup = disableCustomMetaClassLookup;
        }
    }
}

