/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Supplier;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.DynamicAttribute;
import org.apache.tools.ant.DynamicAttributeNS;
import org.apache.tools.ant.DynamicElement;
import org.apache.tools.ant.DynamicElementNS;
import org.apache.tools.ant.DynamicObjectAttribute;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.UnsupportedAttributeException;
import org.apache.tools.ant.UnsupportedElementException;
import org.apache.tools.ant.taskdefs.PreSetDef;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.StringUtils;

public final class IntrospectionHelper {
    private static final Map<String, IntrospectionHelper> HELPERS = new Hashtable<String, IntrospectionHelper>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAP = new HashMap(8);
    private static final int MAX_REPORT_NESTED_TEXT = 20;
    private static final String ELLIPSIS = "...";
    private final Map<String, Class<?>> attributeTypes = new Hashtable();
    private final Map<String, AttributeSetter> attributeSetters = new Hashtable<String, AttributeSetter>();
    private final Map<String, Class<?>> nestedTypes = new Hashtable();
    private final Map<String, NestedCreator> nestedCreators = new Hashtable<String, NestedCreator>();
    private final List<Method> addTypeMethods = new ArrayList<Method>();
    private final Method addText;
    private final Class<?> bean;
    protected static final String NOT_SUPPORTED_CHILD_PREFIX = " doesn't support the nested \"";
    protected static final String NOT_SUPPORTED_CHILD_POSTFIX = "\" element.";

    private IntrospectionHelper(Class<?> bean) {
        this.bean = bean;
        Method addTextMethod = null;
        for (Method m : bean.getMethods()) {
            String propName;
            Constructor<?> constructor2;
            String propName2;
            String name = m.getName();
            Class<?> returnType = m.getReturnType();
            Class<?>[] args = m.getParameterTypes();
            if (args.length == 1 && Void.TYPE.equals(returnType) && ("add".equals(name) || "addConfigured".equals(name))) {
                this.insertAddTypeMethod(m);
                continue;
            }
            if (ProjectComponent.class.isAssignableFrom(bean) && args.length == 1 && this.isHiddenSetMethod(name, args[0]) || this.isContainer() && args.length == 1 && "addTask".equals(name) && Task.class.equals(args[0])) continue;
            if ("addText".equals(name) && Void.TYPE.equals(returnType) && args.length == 1 && String.class.equals(args[0])) {
                addTextMethod = m;
                continue;
            }
            if (name.startsWith("set") && Void.TYPE.equals(returnType) && args.length == 1 && !args[0].isArray()) {
                propName2 = IntrospectionHelper.getPropertyName(name, "set");
                AttributeSetter as = this.attributeSetters.get(propName2);
                if (as != null && (String.class.equals(args[0]) || File.class.equals(args[0]) && (Resource.class.equals((Object)as.type) || FileProvider.class.equals((Object)as.type))) || (as = this.createAttributeSetter(m, args[0], propName2)) == null) continue;
                this.attributeTypes.put(propName2, args[0]);
                this.attributeSetters.put(propName2, as);
                continue;
            }
            if (name.startsWith("create") && !returnType.isArray() && !returnType.isPrimitive() && args.length == 0) {
                propName2 = IntrospectionHelper.getPropertyName(name, "create");
                if (this.nestedCreators.get(propName2) != null) continue;
                this.nestedTypes.put(propName2, returnType);
                this.nestedCreators.put(propName2, new CreateNestedCreator(m));
                continue;
            }
            if (name.startsWith("addConfigured") && Void.TYPE.equals(returnType) && args.length == 1 && !String.class.equals(args[0]) && !args[0].isArray() && !args[0].isPrimitive()) {
                try {
                    constructor2 = null;
                    try {
                        constructor2 = args[0].getConstructor(new Class[0]);
                    }
                    catch (NoSuchMethodException ex) {
                        constructor2 = args[0].getConstructor(Project.class);
                    }
                    propName = IntrospectionHelper.getPropertyName(name, "addConfigured");
                    this.nestedTypes.put(propName, args[0]);
                    this.nestedCreators.put(propName, new AddNestedCreator(m, constructor2, 2));
                }
                catch (NoSuchMethodException constructor2) {}
                continue;
            }
            if (!name.startsWith("add") || !Void.TYPE.equals(returnType) || args.length != 1 || String.class.equals(args[0]) || args[0].isArray() || args[0].isPrimitive()) continue;
            try {
                constructor2 = null;
                try {
                    constructor2 = args[0].getConstructor(new Class[0]);
                }
                catch (NoSuchMethodException ex) {
                    constructor2 = args[0].getConstructor(Project.class);
                }
                propName = IntrospectionHelper.getPropertyName(name, "add");
                if (this.nestedTypes.get(propName) != null) continue;
                this.nestedTypes.put(propName, args[0]);
                this.nestedCreators.put(propName, new AddNestedCreator(m, constructor2, 1));
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        this.addText = addTextMethod;
    }

    private boolean isHiddenSetMethod(String name, Class<?> type) {
        return "setLocation".equals(name) && Location.class.equals(type) || "setTaskType".equals(name) && String.class.equals(type);
    }

    public static IntrospectionHelper getHelper(Class<?> c) {
        return IntrospectionHelper.getHelper(null, c);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static IntrospectionHelper getHelper(Project p, Class<?> c) {
        if (p == null) {
            return new IntrospectionHelper(c);
        }
        IntrospectionHelper ih = HELPERS.get(c.getName());
        if (ih != null && ih.bean == c) {
            return ih;
        }
        ih = new IntrospectionHelper(c);
        Map<String, IntrospectionHelper> map = HELPERS;
        synchronized (map) {
            IntrospectionHelper cached = HELPERS.get(c.getName());
            if (cached != null && cached.bean == c) {
                return cached;
            }
            HELPERS.put(c.getName(), ih);
            return ih;
        }
    }

    public void setAttribute(Project p, Object element, String attributeName, Object value) throws BuildException {
        AttributeSetter as = this.attributeSetters.get(attributeName.toLowerCase(Locale.ENGLISH));
        if (as == null && value != null) {
            if (element instanceof DynamicAttributeNS) {
                DynamicAttributeNS dc = (DynamicAttributeNS)element;
                String uriPlusPrefix = ProjectHelper.extractUriFromComponentName(attributeName);
                String uri = ProjectHelper.extractUriFromComponentName(uriPlusPrefix);
                String localName = ProjectHelper.extractNameFromComponentName(attributeName);
                String qName = uri.isEmpty() ? localName : uri + ":" + localName;
                dc.setDynamicAttribute(uri, localName, qName, value.toString());
                return;
            }
            if (element instanceof DynamicObjectAttribute) {
                DynamicObjectAttribute dc = (DynamicObjectAttribute)element;
                dc.setDynamicAttribute(attributeName.toLowerCase(Locale.ENGLISH), value);
                return;
            }
            if (element instanceof DynamicAttribute) {
                DynamicAttribute dc = (DynamicAttribute)element;
                dc.setDynamicAttribute(attributeName.toLowerCase(Locale.ENGLISH), value.toString());
                return;
            }
            if (attributeName.contains(":")) {
                return;
            }
            String msg = this.getElementName(p, element) + " doesn't support the \"" + attributeName + "\" attribute.";
            throw new UnsupportedAttributeException(msg, attributeName);
        }
        if (as != null) {
            try {
                as.setObject(p, element, value);
            }
            catch (IllegalAccessException ie) {
                throw new BuildException(ie);
            }
            catch (InvocationTargetException ite) {
                throw IntrospectionHelper.extractBuildException(ite);
            }
        }
    }

    public void setAttribute(Project p, Object element, String attributeName, String value) throws BuildException {
        this.setAttribute(p, element, attributeName, (Object)value);
    }

    public void addText(Project project, Object element, String text) throws BuildException {
        if (this.addText == null) {
            if ((text = text.trim()).isEmpty()) {
                return;
            }
            throw new BuildException(project.getElementName(element) + " doesn't support nested text data (\"" + this.condenseText(text) + "\").");
        }
        try {
            this.addText.invoke(element, text);
        }
        catch (IllegalAccessException ie) {
            throw new BuildException(ie);
        }
        catch (InvocationTargetException ite) {
            throw IntrospectionHelper.extractBuildException(ite);
        }
    }

    public void throwNotSupported(Project project, Object parent, String elementName) {
        String msg = project.getElementName(parent) + NOT_SUPPORTED_CHILD_PREFIX + elementName + NOT_SUPPORTED_CHILD_POSTFIX;
        throw new UnsupportedElementException(msg, elementName);
    }

    private NestedCreator getNestedCreator(Project project, String parentUri, Object parent, String elementName, UnknownElement child) throws BuildException {
        String qName;
        Object nestedElement;
        String uri = ProjectHelper.extractUriFromComponentName(elementName);
        String name = ProjectHelper.extractNameFromComponentName(elementName);
        if (uri.equals("antlib:org.apache.tools.ant")) {
            uri = "";
        }
        if (parentUri.equals("antlib:org.apache.tools.ant")) {
            parentUri = "";
        }
        NestedCreator nc = null;
        if (uri.equals(parentUri) || uri.isEmpty()) {
            nc = this.nestedCreators.get(name.toLowerCase(Locale.ENGLISH));
        }
        if (nc == null) {
            nc = this.createAddTypeCreator(project, parent, elementName);
        }
        if (nc == null && (parent instanceof DynamicElementNS || parent instanceof DynamicElement) && (nestedElement = this.createDynamicElement(parent, child == null ? "" : child.getNamespace(), name, qName = child == null ? name : child.getQName())) != null) {
            nc = new NestedCreator(null){

                @Override
                Object create(Project project, Object parent, Object ignore) {
                    return nestedElement;
                }
            };
        }
        if (nc == null) {
            this.throwNotSupported(project, parent, elementName);
        }
        return nc;
    }

    private Object createDynamicElement(Object parent, String ns, String localName, String qName) {
        Object dc;
        Object nestedElement = null;
        if (parent instanceof DynamicElementNS) {
            dc = (DynamicElementNS)parent;
            nestedElement = dc.createDynamicElement(ns, localName, qName);
        }
        if (nestedElement == null && parent instanceof DynamicElement) {
            dc = (DynamicElement)parent;
            nestedElement = dc.createDynamicElement(localName.toLowerCase(Locale.ENGLISH));
        }
        return nestedElement;
    }

    @Deprecated
    public Object createElement(Project project, Object parent, String elementName) throws BuildException {
        NestedCreator nc = this.getNestedCreator(project, "", parent, elementName, null);
        try {
            Object nestedElement = nc.create(project, parent, null);
            if (project != null) {
                project.setProjectReference(nestedElement);
            }
            return nestedElement;
        }
        catch (IllegalAccessException | InstantiationException ie) {
            throw new BuildException(ie);
        }
        catch (InvocationTargetException ite) {
            throw IntrospectionHelper.extractBuildException(ite);
        }
    }

    public Creator getElementCreator(Project project, String parentUri, Object parent, String elementName, UnknownElement ue) {
        NestedCreator nc = this.getNestedCreator(project, parentUri, parent, elementName, ue);
        return new Creator(project, parent, nc);
    }

    public boolean isDynamic() {
        return DynamicElement.class.isAssignableFrom(this.bean) || DynamicElementNS.class.isAssignableFrom(this.bean);
    }

    public boolean isContainer() {
        return TaskContainer.class.isAssignableFrom(this.bean);
    }

    public boolean supportsNestedElement(String elementName) {
        return this.supportsNestedElement("", elementName);
    }

    public boolean supportsNestedElement(String parentUri, String elementName) {
        return this.isDynamic() || !this.addTypeMethods.isEmpty() || this.supportsReflectElement(parentUri, elementName);
    }

    public boolean supportsNestedElement(String parentUri, String elementName, Project project, Object parent) {
        return !this.addTypeMethods.isEmpty() && this.createAddTypeCreator(project, parent, elementName) != null || this.isDynamic() || this.supportsReflectElement(parentUri, elementName);
    }

    public boolean supportsReflectElement(String parentUri, String elementName) {
        String name = ProjectHelper.extractNameFromComponentName(elementName);
        if (!this.nestedCreators.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return false;
        }
        String uri = ProjectHelper.extractUriFromComponentName(elementName);
        if (uri.equals("antlib:org.apache.tools.ant") || uri.isEmpty()) {
            return true;
        }
        if (parentUri.equals("antlib:org.apache.tools.ant")) {
            parentUri = "";
        }
        return uri.equals(parentUri);
    }

    public void storeElement(Project project, Object parent, Object child, String elementName) throws BuildException {
        if (elementName == null) {
            return;
        }
        NestedCreator ns = this.nestedCreators.get(elementName.toLowerCase(Locale.ENGLISH));
        if (ns == null) {
            return;
        }
        try {
            ns.store(parent, child);
        }
        catch (IllegalAccessException | InstantiationException ie) {
            throw new BuildException(ie);
        }
        catch (InvocationTargetException ite) {
            throw IntrospectionHelper.extractBuildException(ite);
        }
    }

    private static BuildException extractBuildException(InvocationTargetException ite) {
        Throwable t = ite.getTargetException();
        if (t instanceof BuildException) {
            return (BuildException)t;
        }
        return new BuildException(t);
    }

    public Class<?> getElementType(String elementName) throws BuildException {
        Class<?> nt = this.nestedTypes.get(elementName);
        if (nt == null) {
            throw new UnsupportedElementException("Class " + this.bean.getName() + NOT_SUPPORTED_CHILD_PREFIX + elementName + NOT_SUPPORTED_CHILD_POSTFIX, elementName);
        }
        return nt;
    }

    public Class<?> getAttributeType(String attributeName) throws BuildException {
        Class<?> at = this.attributeTypes.get(attributeName);
        if (at == null) {
            throw new UnsupportedAttributeException("Class " + this.bean.getName() + " doesn't support the \"" + attributeName + "\" attribute.", attributeName);
        }
        return at;
    }

    public Method getAddTextMethod() throws BuildException {
        if (!this.supportsCharacters()) {
            throw new BuildException("Class " + this.bean.getName() + " doesn't support nested text data.");
        }
        return this.addText;
    }

    public Method getElementMethod(String elementName) throws BuildException {
        NestedCreator creator = this.nestedCreators.get(elementName);
        if (creator == null) {
            throw new UnsupportedElementException("Class " + this.bean.getName() + NOT_SUPPORTED_CHILD_PREFIX + elementName + NOT_SUPPORTED_CHILD_POSTFIX, elementName);
        }
        return creator.method;
    }

    public Method getAttributeMethod(String attributeName) throws BuildException {
        AttributeSetter setter = this.attributeSetters.get(attributeName);
        if (setter == null) {
            throw new UnsupportedAttributeException("Class " + this.bean.getName() + " doesn't support the \"" + attributeName + "\" attribute.", attributeName);
        }
        return setter.method;
    }

    public boolean supportsCharacters() {
        return this.addText != null;
    }

    public Enumeration<String> getAttributes() {
        return Collections.enumeration(this.attributeSetters.keySet());
    }

    public Map<String, Class<?>> getAttributeMap() {
        return this.attributeTypes.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.attributeTypes);
    }

    public Enumeration<String> getNestedElements() {
        return Collections.enumeration(this.nestedTypes.keySet());
    }

    public Map<String, Class<?>> getNestedElementMap() {
        return this.nestedTypes.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.nestedTypes);
    }

    public List<Method> getExtensionPoints() {
        return this.addTypeMethods.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.addTypeMethods);
    }

    private AttributeSetter createAttributeSetter(Method m, Class<?> arg, final String attrName) {
        boolean includeProject;
        Constructor<?> c;
        if (Optional.class.equals(arg)) {
            Type gpt = m.getGenericParameterTypes()[0];
            Class payload = Object.class;
            if (gpt instanceof ParameterizedType) {
                Type ata = ((ParameterizedType)gpt).getActualTypeArguments()[0];
                if (ata instanceof Class) {
                    payload = (Class)ata;
                } else if (ata instanceof ParameterizedType) {
                    payload = (Class)((ParameterizedType)ata).getRawType();
                }
            }
            final AttributeSetter wrapped = this.createAttributeSetter(m, payload, attrName);
            return new AttributeSetter(m, arg, Optional::empty){

                @Override
                Optional<?> toTargetType(Project project, String value) throws BuildException {
                    return Optional.ofNullable(wrapped.toTargetType(project, value));
                }
            };
        }
        if (OptionalInt.class.equals(arg)) {
            final AttributeSetter wrapped = this.createAttributeSetter(m, Integer.class, attrName);
            return new AttributeSetter(m, arg, OptionalInt::empty){

                @Override
                OptionalInt toTargetType(Project project, String value) throws BuildException {
                    return Optional.ofNullable((Integer)wrapped.toTargetType(project, value)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
                }
            };
        }
        if (OptionalLong.class.equals(arg)) {
            final AttributeSetter wrapped = this.createAttributeSetter(m, Long.class, attrName);
            return new AttributeSetter(m, arg, OptionalLong::empty){

                @Override
                OptionalLong toTargetType(Project project, String value) throws BuildException {
                    return Optional.ofNullable((Long)wrapped.toTargetType(project, value)).map(OptionalLong::of).orElseGet(OptionalLong::empty);
                }
            };
        }
        if (OptionalDouble.class.equals(arg)) {
            final AttributeSetter wrapped = this.createAttributeSetter(m, Double.class, attrName);
            return new AttributeSetter(m, arg, OptionalDouble::empty){

                @Override
                Object toTargetType(Project project, String value) throws BuildException {
                    return Optional.ofNullable((Double)wrapped.toTargetType(project, value)).map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
                }
            };
        }
        final Class<?> reflectedArg = PRIMITIVE_TYPE_MAP.getOrDefault(arg, arg);
        if (Object.class == reflectedArg) {
            return new AttributeSetter(m, arg){

                @Override
                Object toTargetType(Project project, String value) throws BuildException {
                    throw new BuildException("Internal ant problem - this should not get called");
                }
            };
        }
        if (String.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                public String toTargetType(Project project, String t) {
                    return t;
                }
            };
        }
        if (Character.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                public Character toTargetType(Project project, String value) {
                    if (value.isEmpty()) {
                        throw new BuildException("The value \"\" is not a legal value for attribute \"" + attrName + "\"");
                    }
                    return Character.valueOf(value.charAt(0));
                }
            };
        }
        if (Boolean.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                public Boolean toTargetType(Project project, String value) {
                    return Project.toBoolean(value);
                }
            };
        }
        if (Class.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                public Class<?> toTargetType(Project project, String value) {
                    try {
                        return Class.forName(value);
                    }
                    catch (ClassNotFoundException e) {
                        throw new BuildException(e);
                    }
                }
            };
        }
        if (File.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                Object toTargetType(Project project, String value) throws BuildException {
                    return project.resolveFile(value);
                }
            };
        }
        if (Path.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                Object toTargetType(Project project, String value) throws BuildException {
                    return project.resolveFile(value).toPath();
                }
            };
        }
        if (Resource.class.equals(reflectedArg) || FileProvider.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                Object toTargetType(Project project, String value) throws BuildException {
                    return new FileResource(project.resolveFile(value));
                }
            };
        }
        if (EnumeratedAttribute.class.isAssignableFrom(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                public EnumeratedAttribute toTargetType(Project project, String value) {
                    EnumeratedAttribute ea;
                    try {
                        ea = (EnumeratedAttribute)reflectedArg.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    }
                    catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                        throw BuildException.of(e);
                    }
                    ea.setValue(value);
                    return ea;
                }
            };
        }
        AttributeSetter setter = this.getEnumSetter(reflectedArg, m, arg);
        if (setter != null) {
            return setter;
        }
        if (Long.class.equals(reflectedArg)) {
            return new AttributeSetter(m, arg){

                @Override
                public Long toTargetType(Project project, String value) {
                    try {
                        return StringUtils.parseHumanSizes(value);
                    }
                    catch (NumberFormatException e) {
                        throw new BuildException(String.format("Can't assign non-numeric value '%s' to attribute %s", value, attrName));
                    }
                    catch (Exception e) {
                        throw new BuildException(e);
                    }
                }
            };
        }
        try {
            c = reflectedArg.getConstructor(Project.class, String.class);
            includeProject = true;
        }
        catch (NoSuchMethodException nme) {
            try {
                c = reflectedArg.getConstructor(String.class);
                includeProject = false;
            }
            catch (NoSuchMethodException nme2) {
                return null;
            }
        }
        final boolean finalIncludeProject = includeProject;
        final Constructor<?> finalConstructor = c;
        return new AttributeSetter(m, arg){

            @Override
            public Object toTargetType(Project project, String value) {
                try {
                    Object[] objectArray;
                    if (finalIncludeProject) {
                        Object[] objectArray2 = new Object[2];
                        objectArray2[0] = project;
                        objectArray = objectArray2;
                        objectArray2[1] = value;
                    } else {
                        Object[] objectArray3 = new Object[1];
                        objectArray = objectArray3;
                        objectArray3[0] = value;
                    }
                    Object[] args = objectArray;
                    Object attribute = finalConstructor.newInstance(args);
                    if (project != null) {
                        project.setProjectReference(attribute);
                    }
                    return attribute;
                }
                catch (Exception e) {
                    Throwable thw = e;
                    while (true) {
                        if (thw instanceof IllegalArgumentException) {
                            throw new BuildException(String.format("Can't convert value '%s' to type %s, reason: %s with message '%s'", value, reflectedArg, thw.getClass(), thw.getMessage()));
                        }
                        Exception _thw = thw;
                        Optional<Throwable> next = Optional.of(thw).map(Throwable::getCause).filter(t -> t != _thw);
                        if (!next.isPresent()) break;
                        thw = next.get();
                    }
                    throw BuildException.of(e);
                }
            }
        };
    }

    private AttributeSetter getEnumSetter(final Class<?> reflectedArg, Method m, Class<?> arg) {
        if (reflectedArg.isEnum()) {
            return new AttributeSetter(m, arg){

                @Override
                public Enum<?> toTargetType(Project project, String value) {
                    try {
                        Object result = Enum.valueOf(reflectedArg, value);
                        return result;
                    }
                    catch (IllegalArgumentException e) {
                        throw new BuildException("'" + value + "' is not a permitted value for " + reflectedArg.getName());
                    }
                }
            };
        }
        return null;
    }

    private String getElementName(Project project, Object element) {
        return project.getElementName(element);
    }

    private static String getPropertyName(String methodName, String prefix) {
        return methodName.substring(prefix.length()).toLowerCase(Locale.ENGLISH);
    }

    public static void clearCache() {
        HELPERS.clear();
    }

    private NestedCreator createAddTypeCreator(Project project, Object parent, String elementName) throws BuildException {
        if (this.addTypeMethods.isEmpty()) {
            return null;
        }
        ComponentHelper helper = ComponentHelper.getComponentHelper(project);
        MethodAndObject restricted = this.createRestricted(helper, elementName, this.addTypeMethods);
        MethodAndObject topLevel = this.createTopLevel(helper, elementName, this.addTypeMethods);
        if (restricted == null && topLevel == null) {
            return null;
        }
        if (restricted != null && topLevel != null) {
            throw new BuildException("ambiguous: type and component definitions for " + elementName);
        }
        MethodAndObject methodAndObject = restricted == null ? topLevel : restricted;
        Object rObject = methodAndObject.object;
        if (methodAndObject.object instanceof PreSetDef.PreSetDefinition) {
            rObject = ((PreSetDef.PreSetDefinition)methodAndObject.object).createObject(project);
        }
        final Object nestedObject = methodAndObject.object;
        final Object realObject = rObject;
        return new NestedCreator(methodAndObject.method){

            @Override
            Object create(Project project, Object parent, Object ignore) throws InvocationTargetException, IllegalAccessException {
                if (!this.getMethod().getName().endsWith("Configured")) {
                    this.getMethod().invoke(parent, realObject);
                }
                return nestedObject;
            }

            @Override
            Object getRealObject() {
                return realObject;
            }

            @Override
            void store(Object parent, Object child) throws InvocationTargetException, IllegalAccessException, InstantiationException {
                if (this.getMethod().getName().endsWith("Configured")) {
                    this.getMethod().invoke(parent, realObject);
                }
            }
        };
    }

    private void insertAddTypeMethod(Method method) {
        Class<?> argClass = method.getParameterTypes()[0];
        int size = this.addTypeMethods.size();
        for (int c = 0; c < size; ++c) {
            Method current = this.addTypeMethods.get(c);
            if (current.getParameterTypes()[0].equals(argClass)) {
                if ("addConfigured".equals(method.getName())) {
                    this.addTypeMethods.set(c, method);
                }
                return;
            }
            if (!current.getParameterTypes()[0].isAssignableFrom(argClass)) continue;
            this.addTypeMethods.add(c, method);
            return;
        }
        this.addTypeMethods.add(method);
    }

    private Method findMatchingMethod(Class<?> paramClass, List<Method> methods) {
        if (paramClass == null) {
            return null;
        }
        Class<?> matchedClass = null;
        Method matchedMethod = null;
        for (Method method : methods) {
            Class<?> methodClass = method.getParameterTypes()[0];
            if (!methodClass.isAssignableFrom(paramClass)) continue;
            if (matchedClass == null) {
                matchedClass = methodClass;
                matchedMethod = method;
                continue;
            }
            if (methodClass.isAssignableFrom(matchedClass)) continue;
            throw new BuildException("ambiguous: types " + matchedClass.getName() + " and " + methodClass.getName() + " match " + paramClass.getName());
        }
        return matchedMethod;
    }

    private String condenseText(String text) {
        if (text.length() <= 20) {
            return text;
        }
        int ends = (20 - ELLIPSIS.length()) / 2;
        return new StringBuffer(text).replace(ends, text.length() - ends, ELLIPSIS).toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AntTypeDefinition findRestrictedDefinition(ComponentHelper helper, String componentName, List<Method> methods) {
        AntTypeDefinition definition = null;
        Class<?> matchedDefinitionClass = null;
        List<AntTypeDefinition> definitions = helper.getRestrictedDefinitions(componentName);
        if (definitions == null) {
            return null;
        }
        List<AntTypeDefinition> list = definitions;
        synchronized (list) {
            for (AntTypeDefinition d : definitions) {
                Method method;
                Class<?> exposedClass = d.getExposedClass(helper.getProject());
                if (exposedClass == null || (method = this.findMatchingMethod(exposedClass, methods)) == null) continue;
                if (matchedDefinitionClass != null) {
                    throw new BuildException("ambiguous: restricted definitions for " + componentName + " " + matchedDefinitionClass + " and " + exposedClass);
                }
                matchedDefinitionClass = exposedClass;
                definition = d;
            }
        }
        return definition;
    }

    private MethodAndObject createRestricted(ComponentHelper helper, String elementName, List<Method> addTypeMethods) {
        Project project = helper.getProject();
        AntTypeDefinition restrictedDefinition = this.findRestrictedDefinition(helper, elementName, addTypeMethods);
        if (restrictedDefinition == null) {
            return null;
        }
        Method addMethod = this.findMatchingMethod(restrictedDefinition.getExposedClass(project), addTypeMethods);
        if (addMethod == null) {
            throw new BuildException("Ant Internal Error - contract mismatch for " + elementName);
        }
        Object addedObject = restrictedDefinition.create(project);
        if (addedObject == null) {
            throw new BuildException("Failed to create object " + elementName + " of type " + restrictedDefinition.getTypeClass(project));
        }
        return new MethodAndObject(addMethod, addedObject);
    }

    private MethodAndObject createTopLevel(ComponentHelper helper, String elementName, List<Method> methods) {
        Class<?> clazz = helper.getComponentClass(elementName);
        if (clazz == null) {
            return null;
        }
        Method addMethod = this.findMatchingMethod(clazz, this.addTypeMethods);
        if (addMethod == null) {
            return null;
        }
        Object addedObject = helper.createComponent(elementName);
        return new MethodAndObject(addMethod, addedObject);
    }

    static {
        Class[] primitives = new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};
        Class[] wrappers = new Class[]{Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
        for (int i = 0; i < primitives.length; ++i) {
            PRIMITIVE_TYPE_MAP.put(primitives[i], wrappers[i]);
        }
    }

    private static abstract class AttributeSetter {
        private final Method method;
        private final Class<?> type;
        private final Supplier<?> supplyWhenNull;

        protected AttributeSetter(Method m, Class<?> type) {
            this(m, type, () -> null);
        }

        protected AttributeSetter(Method method, Class<?> type, Supplier<?> supplyWhenNull) {
            this.method = method;
            this.type = type;
            this.supplyWhenNull = supplyWhenNull;
        }

        final void setObject(Project p, Object parent, Object value) throws InvocationTargetException, IllegalAccessException, BuildException {
            if (this.type != null) {
                Class useType = this.type;
                if (this.type.isPrimitive()) {
                    if (value == null) {
                        throw new BuildException("Attempt to set primitive %s to null on %s", IntrospectionHelper.getPropertyName(this.method.getName(), "set"), parent);
                    }
                    useType = (Class)PRIMITIVE_TYPE_MAP.get(this.type);
                }
                if (value == null) {
                    value = this.supplyWhenNull.get();
                }
                if (value == null || useType.isInstance(value)) {
                    this.method.invoke(parent, value);
                    return;
                }
            }
            this.method.invoke(parent, this.toTargetType(p, value.toString()));
        }

        Object toTargetType(Project project, String value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class CreateNestedCreator
    extends NestedCreator {
        CreateNestedCreator(Method m) {
            super(m);
        }

        @Override
        Object create(Project project, Object parent, Object ignore) throws InvocationTargetException, IllegalAccessException {
            return this.getMethod().invoke(parent, new Object[0]);
        }
    }

    private static class AddNestedCreator
    extends NestedCreator {
        static final int ADD = 1;
        static final int ADD_CONFIGURED = 2;
        private final Constructor<?> constructor;
        private final int behavior;

        AddNestedCreator(Method m, Constructor<?> c, int behavior) {
            super(m);
            this.constructor = c;
            this.behavior = behavior;
        }

        @Override
        boolean isPolyMorphic() {
            return true;
        }

        @Override
        Object create(Project project, Object parent, Object child) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            if (child == null) {
                Object[] objectArray;
                if (this.constructor.getParameterTypes().length == 0) {
                    objectArray = new Object[]{};
                } else {
                    Object[] objectArray2 = new Object[1];
                    objectArray = objectArray2;
                    objectArray2[0] = project;
                }
                child = this.constructor.newInstance(objectArray);
            }
            if (child instanceof PreSetDef.PreSetDefinition) {
                child = ((PreSetDef.PreSetDefinition)child).createObject(project);
            }
            if (this.behavior == 1) {
                this.istore(parent, child);
            }
            return child;
        }

        @Override
        void store(Object parent, Object child) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            if (this.behavior == 2) {
                this.istore(parent, child);
            }
        }

        private void istore(Object parent, Object child) throws InvocationTargetException, IllegalAccessException {
            this.getMethod().invoke(parent, child);
        }
    }

    private static abstract class NestedCreator {
        private final Method method;

        protected NestedCreator(Method m) {
            this.method = m;
        }

        Method getMethod() {
            return this.method;
        }

        boolean isPolyMorphic() {
            return false;
        }

        Object getRealObject() {
            return null;
        }

        abstract Object create(Project var1, Object var2, Object var3) throws InvocationTargetException, IllegalAccessException, InstantiationException;

        void store(Object parent, Object child) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        }
    }

    public static final class Creator {
        private final NestedCreator nestedCreator;
        private final Object parent;
        private final Project project;
        private Object nestedObject;
        private String polyType;

        private Creator(Project project, Object parent, NestedCreator nestedCreator) {
            this.project = project;
            this.parent = parent;
            this.nestedCreator = nestedCreator;
        }

        public void setPolyType(String polyType) {
            this.polyType = polyType;
        }

        public Object create() {
            if (this.polyType != null) {
                if (!this.nestedCreator.isPolyMorphic()) {
                    throw new BuildException("Not allowed to use the polymorphic form for this element");
                }
                ComponentHelper helper = ComponentHelper.getComponentHelper(this.project);
                this.nestedObject = helper.createComponent(this.polyType);
                if (this.nestedObject == null) {
                    throw new BuildException("Unable to create object of type " + this.polyType);
                }
            }
            try {
                this.nestedObject = this.nestedCreator.create(this.project, this.parent, this.nestedObject);
                if (this.project != null) {
                    this.project.setProjectReference(this.nestedObject);
                }
                return this.nestedObject;
            }
            catch (IllegalAccessException | InstantiationException ex) {
                throw new BuildException(ex);
            }
            catch (IllegalArgumentException ex) {
                if (this.polyType == null) {
                    throw ex;
                }
                throw new BuildException("Invalid type used " + this.polyType);
            }
            catch (InvocationTargetException ex) {
                throw IntrospectionHelper.extractBuildException(ex);
            }
        }

        public Object getRealObject() {
            return this.nestedCreator.getRealObject();
        }

        public void store() {
            try {
                this.nestedCreator.store(this.parent, this.nestedObject);
            }
            catch (IllegalAccessException | InstantiationException ex) {
                throw new BuildException(ex);
            }
            catch (IllegalArgumentException ex) {
                if (this.polyType == null) {
                    throw ex;
                }
                throw new BuildException("Invalid type used " + this.polyType);
            }
            catch (InvocationTargetException ex) {
                throw IntrospectionHelper.extractBuildException(ex);
            }
        }
    }

    private static class MethodAndObject {
        private final Method method;
        private final Object object;

        public MethodAndObject(Method method, Object object) {
            this.method = method;
            this.object = object;
        }
    }
}

