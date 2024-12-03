/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MetaProperty;
import groovy.lang.MissingPropertyException;
import groovy.util.AbstractFactory;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.codehaus.groovy.runtime.InvokerHelper;

public class ObjectGraphBuilder
extends FactoryBuilderSupport {
    public static final String NODE_CLASS = "_NODE_CLASS_";
    public static final String NODE_NAME = "_NODE_NAME_";
    public static final String OBJECT_ID = "_OBJECT_ID_";
    public static final String LAZY_REF = "_LAZY_REF_";
    public static final String CLASSNAME_RESOLVER_KEY = "name";
    public static final String CLASSNAME_RESOLVER_REFLECTION = "reflection";
    public static final String CLASSNAME_RESOLVER_REFLECTION_ROOT = "root";
    private static final Pattern PLURAL_IES_PATTERN = Pattern.compile(".*[^aeiouy]y", 2);
    private ChildPropertySetter childPropertySetter;
    private ClassNameResolver classNameResolver;
    private IdentifierResolver identifierResolver;
    private NewInstanceResolver newInstanceResolver;
    private ObjectFactory objectFactory = new ObjectFactory();
    private ObjectBeanFactory objectBeanFactory = new ObjectBeanFactory();
    private ObjectRefFactory objectRefFactory = new ObjectRefFactory();
    private ReferenceResolver referenceResolver;
    private RelationNameResolver relationNameResolver;
    private Map<String, Class> resolvedClasses = new HashMap<String, Class>();
    private ClassLoader classLoader;
    private boolean lazyReferencesAllowed = true;
    private List<NodeReference> lazyReferences = new ArrayList<NodeReference>();
    private String beanFactoryName = "bean";

    public ObjectGraphBuilder() {
        this.classNameResolver = new DefaultClassNameResolver();
        this.newInstanceResolver = new DefaultNewInstanceResolver();
        this.relationNameResolver = new DefaultRelationNameResolver();
        this.childPropertySetter = new DefaultChildPropertySetter();
        this.identifierResolver = new DefaultIdentifierResolver();
        this.referenceResolver = new DefaultReferenceResolver();
        this.addPostNodeCompletionDelegate(new Closure(this, this){

            public void doCall(ObjectGraphBuilder builder, Object parent, Object node) {
                if (parent == null) {
                    builder.resolveLazyReferences();
                    builder.dispose();
                }
            }
        });
    }

    public String getBeanFactoryName() {
        return this.beanFactoryName;
    }

    public ChildPropertySetter getChildPropertySetter() {
        return this.childPropertySetter;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public ClassNameResolver getClassNameResolver() {
        return this.classNameResolver;
    }

    public NewInstanceResolver getNewInstanceResolver() {
        return this.newInstanceResolver;
    }

    public RelationNameResolver getRelationNameResolver() {
        return this.relationNameResolver;
    }

    public boolean isLazyReferencesAllowed() {
        return this.lazyReferencesAllowed;
    }

    public void setBeanFactoryName(String beanFactoryName) {
        this.beanFactoryName = beanFactoryName;
    }

    public void setChildPropertySetter(final Object childPropertySetter) {
        if (childPropertySetter instanceof ChildPropertySetter) {
            this.childPropertySetter = (ChildPropertySetter)childPropertySetter;
        } else if (childPropertySetter instanceof Closure) {
            final ObjectGraphBuilder self = this;
            this.childPropertySetter = new ChildPropertySetter(){

                @Override
                public void setChild(Object parent, Object child, String parentName, String propertyName) {
                    Closure cls = (Closure)childPropertySetter;
                    cls.setDelegate(self);
                    cls.call(parent, child, parentName, propertyName);
                }
            };
        } else {
            this.childPropertySetter = new DefaultChildPropertySetter();
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void setClassNameResolver(final Object classNameResolver) {
        if (classNameResolver instanceof ClassNameResolver) {
            this.classNameResolver = (ClassNameResolver)classNameResolver;
            return;
        } else if (classNameResolver instanceof String) {
            this.classNameResolver = new ClassNameResolver(){

                @Override
                public String resolveClassname(String classname) {
                    return ObjectGraphBuilder.makeClassName((String)classNameResolver, classname);
                }
            };
            return;
        } else if (classNameResolver instanceof Closure) {
            final ObjectGraphBuilder self = this;
            this.classNameResolver = new ClassNameResolver(){

                @Override
                public String resolveClassname(String classname) {
                    Closure cls = (Closure)classNameResolver;
                    cls.setDelegate(self);
                    return (String)cls.call(new Object[]{classname});
                }
            };
            return;
        } else if (classNameResolver instanceof Map) {
            Map classNameResolverOptions = (Map)classNameResolver;
            String resolverName = (String)classNameResolverOptions.get(CLASSNAME_RESOLVER_KEY);
            if (resolverName == null) {
                throw new RuntimeException("key 'name' not defined");
            }
            if (!CLASSNAME_RESOLVER_REFLECTION.equals(resolverName)) throw new RuntimeException("unknown class name resolver " + resolverName);
            String root = (String)classNameResolverOptions.get(CLASSNAME_RESOLVER_REFLECTION_ROOT);
            if (root == null) {
                throw new RuntimeException("key 'root' not defined");
            }
            this.classNameResolver = new ReflectionClassNameResolver(root);
            return;
        } else {
            this.classNameResolver = new DefaultClassNameResolver();
        }
    }

    public void setIdentifierResolver(final Object identifierResolver) {
        if (identifierResolver instanceof IdentifierResolver) {
            this.identifierResolver = (IdentifierResolver)identifierResolver;
        } else if (identifierResolver instanceof String) {
            this.identifierResolver = new IdentifierResolver(){

                @Override
                public String getIdentifierFor(String nodeName) {
                    return (String)identifierResolver;
                }
            };
        } else if (identifierResolver instanceof Closure) {
            final ObjectGraphBuilder self = this;
            this.identifierResolver = new IdentifierResolver(){

                @Override
                public String getIdentifierFor(String nodeName) {
                    Closure cls = (Closure)identifierResolver;
                    cls.setDelegate(self);
                    return (String)cls.call(new Object[]{nodeName});
                }
            };
        } else {
            this.identifierResolver = new DefaultIdentifierResolver();
        }
    }

    public void setLazyReferencesAllowed(boolean lazyReferencesAllowed) {
        this.lazyReferencesAllowed = lazyReferencesAllowed;
    }

    public void setNewInstanceResolver(final Object newInstanceResolver) {
        if (newInstanceResolver instanceof NewInstanceResolver) {
            this.newInstanceResolver = (NewInstanceResolver)newInstanceResolver;
        } else if (newInstanceResolver instanceof Closure) {
            final ObjectGraphBuilder self = this;
            this.newInstanceResolver = new NewInstanceResolver(){

                @Override
                public Object newInstance(Class klass, Map attributes) throws InstantiationException, IllegalAccessException {
                    Closure cls = (Closure)newInstanceResolver;
                    cls.setDelegate(self);
                    return cls.call(klass, attributes);
                }
            };
        } else {
            this.newInstanceResolver = new DefaultNewInstanceResolver();
        }
    }

    public void setReferenceResolver(final Object referenceResolver) {
        if (referenceResolver instanceof ReferenceResolver) {
            this.referenceResolver = (ReferenceResolver)referenceResolver;
        } else if (referenceResolver instanceof String) {
            this.referenceResolver = new ReferenceResolver(){

                @Override
                public String getReferenceFor(String nodeName) {
                    return (String)referenceResolver;
                }
            };
        } else if (referenceResolver instanceof Closure) {
            final ObjectGraphBuilder self = this;
            this.referenceResolver = new ReferenceResolver(){

                @Override
                public String getReferenceFor(String nodeName) {
                    Closure cls = (Closure)referenceResolver;
                    cls.setDelegate(self);
                    return (String)cls.call(new Object[]{nodeName});
                }
            };
        } else {
            this.referenceResolver = new DefaultReferenceResolver();
        }
    }

    public void setRelationNameResolver(RelationNameResolver relationNameResolver) {
        this.relationNameResolver = relationNameResolver != null ? relationNameResolver : new DefaultRelationNameResolver();
    }

    @Override
    protected void postInstantiate(Object name, Map attributes, Object node) {
        super.postInstantiate(name, attributes, node);
        Map<String, Object> context = this.getContext();
        String objectId = (String)context.get(OBJECT_ID);
        if (objectId != null && node != null) {
            this.setVariable(objectId, node);
        }
    }

    @Override
    protected void preInstantiate(Object name, Map attributes, Object value) {
        super.preInstantiate(name, attributes, value);
        Map<String, Object> context = this.getContext();
        context.put(OBJECT_ID, attributes.remove(this.identifierResolver.getIdentifierFor((String)name)));
    }

    @Override
    protected Factory resolveFactory(Object name, Map attributes, Object value) {
        Factory factory = super.resolveFactory(name, attributes, value);
        if (factory != null) {
            return factory;
        }
        if (attributes.get(this.referenceResolver.getReferenceFor((String)name)) != null) {
            return this.objectRefFactory;
        }
        if (this.beanFactoryName != null && this.beanFactoryName.equals((String)name)) {
            return this.objectBeanFactory;
        }
        return this.objectFactory;
    }

    private void resolveLazyReferences() {
        if (!this.lazyReferencesAllowed) {
            return;
        }
        for (NodeReference ref : this.lazyReferences) {
            if (ref.parent == null) continue;
            Object child = null;
            try {
                child = this.getProperty(ref.refId);
            }
            catch (MissingPropertyException missingPropertyException) {
                // empty catch block
            }
            if (child == null) {
                throw new IllegalArgumentException("There is no valid node for reference " + ref.parentName + "." + ref.childName + "=" + ref.refId);
            }
            this.childPropertySetter.setChild(ref.parent, child, ref.parentName, this.relationNameResolver.resolveChildRelationName(ref.parentName, ref.parent, ref.childName, child));
            String propertyName = this.relationNameResolver.resolveParentRelationName(ref.parentName, ref.parent, ref.childName, child);
            MetaProperty metaProperty = InvokerHelper.getMetaClass(child).hasProperty(child, propertyName);
            if (metaProperty == null) continue;
            metaProperty.setProperty(child, ref.parent);
        }
    }

    private static String makeClassName(String root, String name) {
        return root + "." + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static final class NodeReference {
        private final Object parent;
        private final String parentName;
        private final String childName;
        private final String refId;

        private NodeReference(Object parent, String parentName, String childName, String refId) {
            this.parent = parent;
            this.parentName = parentName;
            this.childName = childName;
            this.refId = refId;
        }

        public String toString() {
            return "[parentName=" + this.parentName + ", childName=" + this.childName + ", refId=" + this.refId + "]";
        }
    }

    private static class ObjectRefFactory
    extends ObjectFactory {
        private ObjectRefFactory() {
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
            ObjectGraphBuilder ogbuilder = (ObjectGraphBuilder)builder;
            String refProperty = ogbuilder.referenceResolver.getReferenceFor((String)name);
            Object refId = properties.remove(refProperty);
            Object object = null;
            Boolean lazy = Boolean.FALSE;
            if (refId instanceof String) {
                try {
                    object = ogbuilder.getProperty((String)refId);
                }
                catch (MissingPropertyException missingPropertyException) {
                    // empty catch block
                }
                if (object == null) {
                    if (!ogbuilder.isLazyReferencesAllowed()) throw new IllegalArgumentException("There is no previous node with " + ogbuilder.identifierResolver.getIdentifierFor((String)name) + "=" + refId);
                    lazy = Boolean.TRUE;
                }
            } else {
                object = refId;
            }
            if (!properties.isEmpty()) {
                throw new IllegalArgumentException("You can not modify the properties of a referenced object.");
            }
            Map<String, Object> context = ogbuilder.getContext();
            context.put(ObjectGraphBuilder.NODE_NAME, name);
            context.put(ObjectGraphBuilder.LAZY_REF, lazy);
            if (lazy.booleanValue()) {
                Map parentContext = ogbuilder.getParentContext();
                Object parent = null;
                String parentName = null;
                String childName = (String)name;
                if (parentContext != null) {
                    parent = context.get("_CURRENT_NODE_");
                    parentName = (String)parentContext.get(ObjectGraphBuilder.NODE_NAME);
                }
                ogbuilder.lazyReferences.add(new NodeReference(parent, parentName, childName, (String)refId));
                return object;
            } else {
                context.put(ObjectGraphBuilder.NODE_CLASS, object.getClass());
            }
            return object;
        }

        @Override
        public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
            Boolean lazy = (Boolean)builder.getContext().get(ObjectGraphBuilder.LAZY_REF);
            if (!lazy.booleanValue()) {
                super.setChild(builder, parent, child);
            }
        }

        @Override
        public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
            Boolean lazy = (Boolean)builder.getContext().get(ObjectGraphBuilder.LAZY_REF);
            if (!lazy.booleanValue()) {
                super.setParent(builder, parent, child);
            }
        }
    }

    private static class ObjectBeanFactory
    extends ObjectFactory {
        private ObjectBeanFactory() {
        }

        @Override
        public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
            if (value == null) {
                return super.newInstance(builder, name, value, properties);
            }
            Object bean = null;
            Class<?> klass = null;
            Map<String, Object> context = builder.getContext();
            if (value instanceof String || value instanceof GString) {
                throw new IllegalArgumentException("ObjectGraphBuilder." + ((ObjectGraphBuilder)builder).getBeanFactoryName() + "() does not accept String nor GString as value.");
            }
            if (value instanceof Class) {
                klass = (Class<?>)value;
                bean = this.resolveInstance(builder, name, value, klass, properties);
            } else {
                klass = value.getClass();
                bean = value;
            }
            String nodename = klass.getSimpleName();
            nodename = nodename.length() > 1 ? nodename.substring(0, 1).toLowerCase() + nodename.substring(1) : nodename.toLowerCase();
            context.put(ObjectGraphBuilder.NODE_NAME, nodename);
            context.put(ObjectGraphBuilder.NODE_CLASS, klass);
            return bean;
        }
    }

    private static class ObjectFactory
    extends AbstractFactory {
        private ObjectFactory() {
        }

        @Override
        public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
            ObjectGraphBuilder ogbuilder = (ObjectGraphBuilder)builder;
            String classname = ogbuilder.classNameResolver.resolveClassname((String)name);
            Class klass = this.resolveClass(builder, classname, name, value, properties);
            Map<String, Object> context = builder.getContext();
            context.put(ObjectGraphBuilder.NODE_NAME, name);
            context.put(ObjectGraphBuilder.NODE_CLASS, klass);
            return this.resolveInstance(builder, name, value, klass, properties);
        }

        protected Class resolveClass(FactoryBuilderSupport builder, String classname, Object name, Object value, Map properties) {
            ObjectGraphBuilder ogbuilder = (ObjectGraphBuilder)builder;
            Class klass = (Class)ogbuilder.resolvedClasses.get(classname);
            if (klass == null) {
                klass = this.loadClass(ogbuilder.classLoader, classname);
                if (klass == null) {
                    klass = this.loadClass(ogbuilder.getClass().getClassLoader(), classname);
                }
                if (klass == null) {
                    try {
                        klass = Class.forName(classname);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        // empty catch block
                    }
                }
                if (klass == null) {
                    klass = this.loadClass(Thread.currentThread().getContextClassLoader(), classname);
                }
                if (klass == null) {
                    throw new RuntimeException(new ClassNotFoundException(classname));
                }
                ogbuilder.resolvedClasses.put(classname, klass);
            }
            return klass;
        }

        protected Object resolveInstance(FactoryBuilderSupport builder, Object name, Object value, Class klass, Map properties) throws InstantiationException, IllegalAccessException {
            ObjectGraphBuilder ogbuilder = (ObjectGraphBuilder)builder;
            if (value != null && klass.isAssignableFrom(value.getClass())) {
                return value;
            }
            return ogbuilder.newInstanceResolver.newInstance(klass, properties);
        }

        @Override
        public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
            if (child == null) {
                return;
            }
            ObjectGraphBuilder ogbuilder = (ObjectGraphBuilder)builder;
            if (parent != null) {
                Map<String, Object> context = ogbuilder.getContext();
                Map parentContext = ogbuilder.getParentContext();
                String parentName = null;
                String childName = (String)context.get(ObjectGraphBuilder.NODE_NAME);
                if (parentContext != null) {
                    parentName = (String)parentContext.get(ObjectGraphBuilder.NODE_NAME);
                }
                String propertyName = ogbuilder.relationNameResolver.resolveParentRelationName(parentName, parent, childName, child);
                MetaProperty metaProperty = InvokerHelper.getMetaClass(child).hasProperty(child, propertyName);
                if (metaProperty != null) {
                    metaProperty.setProperty(child, parent);
                }
            }
        }

        @Override
        public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
            if (child == null) {
                return;
            }
            ObjectGraphBuilder ogbuilder = (ObjectGraphBuilder)builder;
            if (parent != null) {
                Map<String, Object> context = ogbuilder.getContext();
                Map parentContext = ogbuilder.getParentContext();
                String parentName = null;
                String childName = (String)context.get(ObjectGraphBuilder.NODE_NAME);
                if (parentContext != null) {
                    parentName = (String)parentContext.get(ObjectGraphBuilder.NODE_NAME);
                }
                ogbuilder.childPropertySetter.setChild(parent, child, parentName, ogbuilder.relationNameResolver.resolveChildRelationName(parentName, parent, childName, child));
            }
        }

        protected Class loadClass(ClassLoader classLoader, String classname) {
            if (classLoader == null || classname == null) {
                return null;
            }
            try {
                return classLoader.loadClass(classname);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    public static interface RelationNameResolver {
        public String resolveChildRelationName(String var1, Object var2, String var3, Object var4);

        public String resolveParentRelationName(String var1, Object var2, String var3, Object var4);
    }

    public static interface ReferenceResolver {
        public String getReferenceFor(String var1);
    }

    public static interface NewInstanceResolver {
        public Object newInstance(Class var1, Map var2) throws InstantiationException, IllegalAccessException;
    }

    public static interface IdentifierResolver {
        public String getIdentifierFor(String var1);
    }

    public static class DefaultRelationNameResolver
    implements RelationNameResolver {
        @Override
        public String resolveChildRelationName(String parentName, Object parent, String childName, Object child) {
            boolean matchesIESRule = PLURAL_IES_PATTERN.matcher(childName).matches();
            String childNamePlural = matchesIESRule ? childName.substring(0, childName.length() - 1) + "ies" : childName + "s";
            MetaProperty metaProperty = InvokerHelper.getMetaClass(parent).hasProperty(parent, childNamePlural);
            return metaProperty != null ? childNamePlural : childName;
        }

        @Override
        public String resolveParentRelationName(String parentName, Object parent, String childName, Object child) {
            return parentName;
        }
    }

    public static class DefaultReferenceResolver
    implements ReferenceResolver {
        @Override
        public String getReferenceFor(String nodeName) {
            return "refId";
        }
    }

    public static class DefaultNewInstanceResolver
    implements NewInstanceResolver {
        @Override
        public Object newInstance(Class klass, Map attributes) throws InstantiationException, IllegalAccessException {
            return klass.newInstance();
        }
    }

    public static class DefaultIdentifierResolver
    implements IdentifierResolver {
        @Override
        public String getIdentifierFor(String nodeName) {
            return "id";
        }
    }

    public class ReflectionClassNameResolver
    implements ClassNameResolver {
        private final String root;

        public ReflectionClassNameResolver(String root) {
            this.root = root;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public String resolveClassname(String classname) {
            Object currentNode = ObjectGraphBuilder.this.getContext().get("_CURRENT_NODE_");
            if (currentNode == null) {
                return ObjectGraphBuilder.makeClassName(this.root, classname);
            }
            try {
                Class klass = currentNode.getClass().getDeclaredField(classname).getType();
                if (!Collection.class.isAssignableFrom(klass)) return klass.getName();
                Type type = currentNode.getClass().getDeclaredField(classname).getGenericType();
                if (!(type instanceof ParameterizedType)) throw new RuntimeException("collection field " + classname + " must be genericised");
                ParameterizedType ptype = (ParameterizedType)type;
                Type[] actualTypeArguments = ptype.getActualTypeArguments();
                if (actualTypeArguments.length != 1) {
                    throw new RuntimeException("can't determine class name for collection field " + classname + " with multiple generics");
                }
                Type typeArgument = actualTypeArguments[0];
                if (!(typeArgument instanceof Class)) throw new RuntimeException("can't instantiate collection field " + classname + " elements as they aren't a class");
                klass = (Class)actualTypeArguments[0];
                return klass.getName();
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException("can't find field " + classname + " for node class " + currentNode.getClass().getName(), e);
            }
        }
    }

    public static class DefaultClassNameResolver
    implements ClassNameResolver {
        @Override
        public String resolveClassname(String classname) {
            if (classname.length() == 1) {
                return classname.toUpperCase();
            }
            return classname.substring(0, 1).toUpperCase() + classname.substring(1);
        }
    }

    public static class DefaultChildPropertySetter
    implements ChildPropertySetter {
        @Override
        public void setChild(Object parent, Object child, String parentName, String propertyName) {
            try {
                Object property = InvokerHelper.getProperty(parent, propertyName);
                if (property != null && Collection.class.isAssignableFrom(property.getClass())) {
                    ((Collection)property).add(child);
                } else {
                    InvokerHelper.setProperty(parent, propertyName, child);
                }
            }
            catch (MissingPropertyException missingPropertyException) {
                // empty catch block
            }
        }
    }

    public static interface ClassNameResolver {
        public String resolveClassname(String var1);
    }

    public static interface ChildPropertySetter {
        public void setChild(Object var1, Object var2, String var3, String var4);
    }
}

