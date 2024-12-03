/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyConstructorDoc;
import org.codehaus.groovy.groovydoc.GroovyFieldDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.groovydoc.GroovyParameter;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.codehaus.groovy.groovydoc.GroovyType;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.tools.groovydoc.ArrayClassDocWrapper;
import org.codehaus.groovy.tools.groovydoc.ExternalGroovyClassDoc;
import org.codehaus.groovy.tools.groovydoc.LinkArgument;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyAbstractableElementDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyAnnotationRef;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyFieldDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyParameter;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyRootDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyType;

public class SimpleGroovyClassDoc
extends SimpleGroovyAbstractableElementDoc
implements GroovyClassDoc {
    public static final Pattern TAG_REGEX = Pattern.compile("(?sm)\\s*@([a-zA-Z.]+)\\s+(.*?)(?=\\s+@)");
    public static final String DOCROOT_PATTERN2 = "(?m)[{]@docRoot}/";
    public static final String DOCROOT_PATTERN = "(?m)[{]@docRoot}";
    public static final Pattern LINK_REGEX = Pattern.compile("(?m)[{]@(link)\\s+([^}]*)}");
    public static final Pattern LITERAL_REGEX = Pattern.compile("(?m)[{]@(literal)\\s+([^}]*)}");
    public static final Pattern CODE_REGEX = Pattern.compile("(?m)[{]@(code)\\s+([^}]*)}");
    public static final Pattern REF_LABEL_REGEX = Pattern.compile("([\\w.#\\$]*(\\(.*\\))?)(\\s(.*))?");
    public static final Pattern NAME_ARGS_REGEX = Pattern.compile("([^(]+)\\(([^)]*)\\)");
    public static final Pattern SPLIT_ARGS_REGEX = Pattern.compile(",\\s*");
    private static final List<String> PRIMITIVES = Arrays.asList("void", "boolean", "byte", "short", "char", "int", "long", "float", "double");
    private static final Map<String, String> TAG_TEXT = new LinkedHashMap<String, String>();
    private final List<GroovyConstructorDoc> constructors;
    private final List<GroovyFieldDoc> fields;
    private final List<GroovyFieldDoc> properties;
    private final List<GroovyFieldDoc> enumConstants;
    private final List<GroovyMethodDoc> methods;
    private final List<String> importedClassesAndPackages;
    private final Map<String, String> aliases;
    private final List<String> interfaceNames;
    private final List<GroovyClassDoc> interfaceClasses;
    private final List<GroovyClassDoc> nested;
    private final List<LinkArgument> links;
    private GroovyClassDoc superClass;
    private GroovyClassDoc outer;
    private String superClassName;
    private String fullPathName;
    private boolean isgroovy;
    private GroovyRootDoc savedRootDoc = null;
    private String nameWithTypeArgs;

    public SimpleGroovyClassDoc(List<String> importedClassesAndPackages, Map<String, String> aliases, String name, List<LinkArgument> links) {
        super(name);
        this.importedClassesAndPackages = importedClassesAndPackages;
        this.aliases = aliases;
        this.links = links;
        this.constructors = new ArrayList<GroovyConstructorDoc>();
        this.fields = new ArrayList<GroovyFieldDoc>();
        this.properties = new ArrayList<GroovyFieldDoc>();
        this.enumConstants = new ArrayList<GroovyFieldDoc>();
        this.methods = new ArrayList<GroovyMethodDoc>();
        this.interfaceNames = new ArrayList<String>();
        this.interfaceClasses = new ArrayList<GroovyClassDoc>();
        this.nested = new ArrayList<GroovyClassDoc>();
    }

    public SimpleGroovyClassDoc(List<String> importedClassesAndPackages, Map<String, String> aliases, String name) {
        this(importedClassesAndPackages, aliases, name, new ArrayList<LinkArgument>());
    }

    public SimpleGroovyClassDoc(List<String> importedClassesAndPackages, String name) {
        this(importedClassesAndPackages, new LinkedHashMap<String, String>(), name, new ArrayList<LinkArgument>());
    }

    @Override
    public GroovyConstructorDoc[] constructors() {
        Collections.sort(this.constructors);
        return this.constructors.toArray(new GroovyConstructorDoc[this.constructors.size()]);
    }

    public boolean add(GroovyConstructorDoc constructor) {
        return this.constructors.add(constructor);
    }

    public GroovyClassDoc getOuter() {
        return this.outer;
    }

    public void setOuter(GroovyClassDoc outer) {
        this.outer = outer;
    }

    public boolean isGroovy() {
        return this.isgroovy;
    }

    public void setGroovy(boolean isgroovy) {
        this.isgroovy = isgroovy;
    }

    @Override
    public GroovyClassDoc[] innerClasses() {
        Collections.sort(this.nested);
        return this.nested.toArray(new GroovyClassDoc[this.nested.size()]);
    }

    public boolean addNested(GroovyClassDoc nestedClass) {
        return this.nested.add(nestedClass);
    }

    @Override
    public GroovyFieldDoc[] fields() {
        Collections.sort(this.fields);
        return this.fields.toArray(new GroovyFieldDoc[this.fields.size()]);
    }

    public boolean add(GroovyFieldDoc field) {
        return this.fields.add(field);
    }

    @Override
    public GroovyFieldDoc[] properties() {
        Collections.sort(this.properties);
        return this.properties.toArray(new GroovyFieldDoc[this.properties.size()]);
    }

    public boolean addProperty(GroovyFieldDoc property) {
        return this.properties.add(property);
    }

    @Override
    public GroovyFieldDoc[] enumConstants() {
        Collections.sort(this.enumConstants);
        return this.enumConstants.toArray(new GroovyFieldDoc[this.enumConstants.size()]);
    }

    public boolean addEnumConstant(GroovyFieldDoc field) {
        return this.enumConstants.add(field);
    }

    @Override
    public GroovyMethodDoc[] methods() {
        Collections.sort(this.methods);
        return this.methods.toArray(new GroovyMethodDoc[this.methods.size()]);
    }

    public boolean add(GroovyMethodDoc method) {
        return this.methods.add(method);
    }

    public String getSuperClassName() {
        return this.superClassName;
    }

    public void setSuperClassName(String className) {
        this.superClassName = className;
    }

    @Override
    public GroovyClassDoc superclass() {
        return this.superClass;
    }

    public void setSuperClass(GroovyClassDoc doc) {
        this.superClass = doc;
    }

    @Override
    public String getFullPathName() {
        return this.fullPathName;
    }

    public void setFullPathName(String fullPathName) {
        this.fullPathName = fullPathName;
    }

    @Override
    public String getRelativeRootPath() {
        StringTokenizer tokenizer = new StringTokenizer(this.fullPathName, "/");
        StringBuilder sb = new StringBuilder();
        if (tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
        }
        while (tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
            sb.append("../");
        }
        return sb.toString();
    }

    public List<GroovyClassDoc> getParentClasses() {
        LinkedList<GroovyClassDoc> result = new LinkedList<GroovyClassDoc>();
        if (this.isInterface()) {
            return result;
        }
        result.add(0, this);
        GroovyClassDoc next = this;
        while (next.superclass() != null && !"java.lang.Object".equals(next.qualifiedTypeName())) {
            next = next.superclass();
            result.add(0, next);
        }
        GroovyClassDoc prev = next;
        Class nextClass = this.getClassOf(next.qualifiedTypeName());
        while (nextClass != null && nextClass.getSuperclass() != null && !Object.class.equals((Object)nextClass)) {
            nextClass = nextClass.getSuperclass();
            ExternalGroovyClassDoc nextDoc = new ExternalGroovyClassDoc(nextClass);
            if (prev instanceof SimpleGroovyClassDoc) {
                SimpleGroovyClassDoc parent = prev;
                parent.setSuperClass(nextDoc);
            }
            result.add(0, nextDoc);
            prev = nextDoc;
        }
        if (!((GroovyClassDoc)result.get(0)).qualifiedTypeName().equals("java.lang.Object")) {
            result.add(0, new ExternalGroovyClassDoc(Object.class));
        }
        return result;
    }

    public Set<GroovyClassDoc> getParentInterfaces() {
        LinkedHashSet<GroovyClassDoc> result = new LinkedHashSet<GroovyClassDoc>();
        result.add(this);
        Set<GroovyClassDoc> next = new LinkedHashSet<GroovyClassDoc>();
        next.addAll(Arrays.asList(this.interfaces()));
        while (!next.isEmpty()) {
            LinkedHashSet<GroovyClassDoc> temp = next;
            next = new LinkedHashSet();
            for (GroovyClassDoc t : temp) {
                if (t instanceof SimpleGroovyClassDoc) {
                    next.addAll(((SimpleGroovyClassDoc)t).getParentInterfaces());
                    continue;
                }
                if (!(t instanceof ExternalGroovyClassDoc)) continue;
                ExternalGroovyClassDoc d = (ExternalGroovyClassDoc)t;
                next.addAll(this.getJavaInterfaces(d));
            }
            next = DefaultGroovyMethods.minus(next, result);
            result.addAll(next);
        }
        return result;
    }

    private Set<GroovyClassDoc> getJavaInterfaces(ExternalGroovyClassDoc d) {
        LinkedHashSet<GroovyClassDoc> result = new LinkedHashSet<GroovyClassDoc>();
        Class<?>[] interfaces = d.externalClass().getInterfaces();
        if (interfaces != null) {
            for (Class<?> i : interfaces) {
                ExternalGroovyClassDoc doc = new ExternalGroovyClassDoc(i);
                result.add(doc);
                result.addAll(this.getJavaInterfaces(doc));
            }
        }
        return result;
    }

    private Class getClassOf(String next) {
        try {
            return Class.forName(next.replace("/", "."), false, this.getClass().getClassLoader());
        }
        catch (Throwable t) {
            return null;
        }
    }

    private void processAnnotationRefs(GroovyRootDoc rootDoc, GroovyAnnotationRef[] annotations) {
        for (GroovyAnnotationRef annotation : annotations) {
            SimpleGroovyAnnotationRef ref = (SimpleGroovyAnnotationRef)annotation;
            ref.setType(this.resolveClass(rootDoc, ref.name()));
        }
    }

    void resolve(GroovyRootDoc rootDoc) {
        this.savedRootDoc = rootDoc;
        Map<String, GroovyClassDoc> visibleClasses = rootDoc.getVisibleClasses(this.importedClassesAndPackages);
        for (GroovyConstructorDoc constructor : this.constructors) {
            for (GroovyParameter groovyParameter : constructor.parameters()) {
                SimpleGroovyParameter param = (SimpleGroovyParameter)groovyParameter;
                String paramTypeName = param.typeName();
                if (visibleClasses.containsKey(paramTypeName)) {
                    param.setType(visibleClasses.get(paramTypeName));
                } else {
                    GroovyClassDoc doc = this.resolveClass(rootDoc, paramTypeName);
                    if (doc != null) {
                        param.setType(doc);
                    }
                }
                this.processAnnotationRefs(rootDoc, param.annotations());
            }
            this.processAnnotationRefs(rootDoc, constructor.annotations());
        }
        for (GroovyFieldDoc field : this.fields) {
            SimpleGroovyFieldDoc mutableField = (SimpleGroovyFieldDoc)field;
            GroovyType fieldType = field.type();
            String typeName = fieldType.typeName();
            if (visibleClasses.containsKey(typeName)) {
                mutableField.setType(visibleClasses.get(typeName));
            } else {
                GroovyClassDoc doc = this.resolveClass(rootDoc, typeName);
                if (doc != null) {
                    mutableField.setType(doc);
                }
            }
            this.processAnnotationRefs(rootDoc, field.annotations());
        }
        for (GroovyMethodDoc method : this.methods) {
            GroovyType returnType = method.returnType();
            String typeName = returnType.typeName();
            if (visibleClasses.containsKey(typeName)) {
                method.setReturnType(visibleClasses.get(typeName));
            } else {
                GroovyParameter[] doc = this.resolveClass(rootDoc, typeName);
                if (doc != null) {
                    method.setReturnType((GroovyType)doc);
                }
            }
            for (GroovyParameter groovyParameter : method.parameters()) {
                SimpleGroovyParameter param = (SimpleGroovyParameter)groovyParameter;
                String paramTypeName = param.typeName();
                if (visibleClasses.containsKey(paramTypeName)) {
                    param.setType(visibleClasses.get(paramTypeName));
                } else {
                    GroovyClassDoc doc = this.resolveClass(rootDoc, paramTypeName);
                    if (doc != null) {
                        param.setType(doc);
                    }
                }
                this.processAnnotationRefs(rootDoc, param.annotations());
            }
            this.processAnnotationRefs(rootDoc, method.annotations());
        }
        for (GroovyFieldDoc property : this.properties) {
            SimpleGroovyType simpleGroovyType;
            GroovyClassDoc propertyTypeClassDoc;
            SimpleGroovyFieldDoc simpleGroovyFieldDoc;
            if (property instanceof SimpleGroovyFieldDoc && (simpleGroovyFieldDoc = (SimpleGroovyFieldDoc)property).type() instanceof SimpleGroovyType && (propertyTypeClassDoc = this.resolveClass(rootDoc, (simpleGroovyType = (SimpleGroovyType)simpleGroovyFieldDoc.type()).qualifiedTypeName())) != null) {
                simpleGroovyFieldDoc.setType(propertyTypeClassDoc);
            }
            this.processAnnotationRefs(rootDoc, property.annotations());
        }
        if (this.superClassName != null && this.superClass == null) {
            this.superClass = this.resolveClass(rootDoc, this.superClassName);
        }
        for (String name : this.interfaceNames) {
            this.interfaceClasses.add(this.resolveClass(rootDoc, name));
        }
        this.processAnnotationRefs(rootDoc, this.annotations());
    }

    public String getDocUrl(String type) {
        return this.getDocUrl(type, false);
    }

    public String getDocUrl(String type, boolean full) {
        return SimpleGroovyClassDoc.getDocUrl(type, full, this.links, this.getRelativeRootPath(), this.savedRootDoc, this);
    }

    private static String resolveMethodArgs(GroovyRootDoc rootDoc, SimpleGroovyClassDoc classDoc, String type) {
        if (!type.contains("(")) {
            return type;
        }
        Matcher m = NAME_ARGS_REGEX.matcher(type);
        if (m.matches()) {
            String name = m.group(1);
            String args = m.group(2);
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("(");
            String[] argParts = SPLIT_ARGS_REGEX.split(args);
            boolean first = true;
            for (String argPart : argParts) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                GroovyClassDoc doc = classDoc.resolveClass(rootDoc, argPart);
                sb.append(doc == null ? argPart : doc.qualifiedTypeName());
            }
            sb.append(")");
            return sb.toString();
        }
        return type;
    }

    public static String getDocUrl(String type, boolean full, List<LinkArgument> links, String relativePath, GroovyRootDoc rootDoc, SimpleGroovyClassDoc classDoc) {
        String slashedName;
        GroovyClassDoc doc;
        Matcher matcher;
        if (type == null) {
            return type;
        }
        if (SimpleGroovyClassDoc.isPrimitiveType(type = type.trim()) || type.length() == 1) {
            return type;
        }
        if (type.equals("def")) {
            type = "java.lang.Object def";
        }
        if (type.startsWith("<a href=")) {
            return type;
        }
        if (type.startsWith("? extends ")) {
            return "? extends " + SimpleGroovyClassDoc.getDocUrl(type.substring(10), full, links, relativePath, rootDoc, classDoc);
        }
        if (type.startsWith("? super ")) {
            return "? super " + SimpleGroovyClassDoc.getDocUrl(type.substring(8), full, links, relativePath, rootDoc, classDoc);
        }
        String label = null;
        int lt = type.indexOf("<");
        if (lt != -1) {
            String outerType = type.substring(0, lt);
            int gt = type.lastIndexOf(">");
            if (gt != -1) {
                if (gt > lt) {
                    String allTypeArgs = type.substring(lt + 1, gt);
                    ArrayList<String> typeArgs = new ArrayList<String>();
                    int nested = 0;
                    StringBuilder sb = new StringBuilder();
                    for (char ch : allTypeArgs.toCharArray()) {
                        if (ch == '<') {
                            ++nested;
                        } else if (ch == '>') {
                            --nested;
                        } else if (ch == ',' && nested == 0) {
                            typeArgs.add(sb.toString().trim());
                            sb = new StringBuilder();
                            continue;
                        }
                        sb.append(ch);
                    }
                    if (sb.length() > 0) {
                        typeArgs.add(sb.toString().trim());
                    }
                    ArrayList<String> typeUrls = new ArrayList<String>();
                    for (String typeArg : typeArgs) {
                        typeUrls.add(SimpleGroovyClassDoc.getDocUrl(typeArg, full, links, relativePath, rootDoc, classDoc));
                    }
                    sb = new StringBuilder(SimpleGroovyClassDoc.getDocUrl(outerType, full, links, relativePath, rootDoc, classDoc));
                    sb.append("&lt;");
                    sb.append(DefaultGroovyMethods.join(typeUrls, ", "));
                    sb.append("&gt;");
                    return sb.toString();
                }
                return type.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            }
        }
        if ((matcher = REF_LABEL_REGEX.matcher(type)).find()) {
            type = matcher.group(1);
            label = matcher.group(4);
        }
        if (type.startsWith("#")) {
            return "<a href='" + SimpleGroovyClassDoc.resolveMethodArgs(rootDoc, classDoc, type) + "'>" + (label == null ? type.substring(1) : label) + "</a>";
        }
        if (type.endsWith("[]")) {
            if (label != null) {
                return SimpleGroovyClassDoc.getDocUrl(type.substring(0, type.length() - 2) + " " + label, full, links, relativePath, rootDoc, classDoc);
            }
            return SimpleGroovyClassDoc.getDocUrl(type.substring(0, type.length() - 2), full, links, relativePath, rootDoc, classDoc) + "[]";
        }
        if (!type.contains(".") && classDoc != null) {
            String[] pieces = type.split("#");
            String candidate = pieces[0];
            Class c = classDoc.resolveExternalClassFromImport(candidate);
            if (c != null) {
                type = c.getName();
            }
            if (pieces.length > 1) {
                type = type + "#" + pieces[1];
            }
            type = SimpleGroovyClassDoc.resolveMethodArgs(rootDoc, classDoc, type);
        }
        String[] target = type.split("#");
        String shortClassName = target[0].replaceAll(".*\\.", "");
        shortClassName = shortClassName + (target.length > 1 ? "#" + target[1].split("\\(")[0] : "");
        String name = (full ? target[0] : shortClassName).replaceAll("#", ".").replace('$', '.');
        if (rootDoc != null && (doc = rootDoc.classNamed(classDoc, slashedName = target[0].replaceAll("\\.", "/"))) != null) {
            target[0] = doc.getFullPathName();
            return SimpleGroovyClassDoc.buildUrl(relativePath, target, label == null ? name : label);
        }
        if (type.indexOf(46) == -1) {
            return type;
        }
        if (links != null) {
            for (LinkArgument link : links) {
                StringTokenizer tokenizer = new StringTokenizer(link.getPackages(), ", ");
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (!type.startsWith(token)) continue;
                    return SimpleGroovyClassDoc.buildUrl(link.getHref(), target, label == null ? name : label);
                }
            }
        }
        return type;
    }

    private static String buildUrl(String relativeRoot, String[] target, String shortClassName) {
        if (relativeRoot.length() > 0 && !relativeRoot.endsWith("/")) {
            relativeRoot = relativeRoot + "/";
        }
        String url = relativeRoot + target[0].replace('.', '/').replace('$', '.') + ".html" + (target.length > 1 ? "#" + target[1] : "");
        return "<a href='" + url + "' title='" + shortClassName + "'>" + shortClassName + "</a>";
    }

    private GroovyClassDoc resolveClass(GroovyRootDoc rootDoc, String name) {
        String fullyQualifiedTypeName;
        GroovyClassDoc gcd;
        Class c;
        if (SimpleGroovyClassDoc.isPrimitiveType(name)) {
            return null;
        }
        if (name.endsWith("[]")) {
            GroovyClassDoc componentClass = this.resolveClass(rootDoc, name.substring(0, name.length() - 2));
            if (componentClass != null) {
                return new ArrayClassDocWrapper(componentClass);
            }
            return null;
        }
        GroovyClassDoc doc = ((SimpleGroovyRootDoc)rootDoc).classNamedExact(name);
        if (doc != null) {
            return doc;
        }
        int slashIndex = name.lastIndexOf("/");
        if (slashIndex < 1) {
            doc = this.resolveInternalClassDocFromImport(rootDoc, name);
            if (doc != null) {
                return doc;
            }
            for (GroovyClassDoc nestedDoc : this.nested) {
                if (!nestedDoc.name().endsWith("." + name)) continue;
                return nestedDoc;
            }
            doc = rootDoc.classNamed(this, name);
            if (doc != null) {
                return doc;
            }
        }
        String shortname = name;
        if (slashIndex > 0) {
            shortname = name.substring(slashIndex + 1);
            c = this.resolveExternalFullyQualifiedClass(name);
        } else {
            c = this.resolveExternalClassFromImport(name);
        }
        if (c == null) {
            c = this.resolveFromJavaLang(name);
        }
        if (c != null) {
            return new ExternalGroovyClassDoc(c);
        }
        if (name.contains("/") && slashIndex > 0) {
            String outerName = name.substring(0, slashIndex);
            gcd = this.resolveClass(rootDoc, outerName);
            if (gcd instanceof ExternalGroovyClassDoc) {
                ExternalGroovyClassDoc egcd = (ExternalGroovyClassDoc)gcd;
                String innerName = name.substring(slashIndex + 1);
                Class outerClass = egcd.externalClass();
                for (Class<?> inner : outerClass.getDeclaredClasses()) {
                    if (!inner.getName().equals(outerClass.getName() + "$" + innerName)) continue;
                    return new ExternalGroovyClassDoc(inner);
                }
            }
            if (gcd instanceof SimpleGroovyClassDoc) {
                String innerClassName = name.substring(slashIndex + 1);
                SimpleGroovyClassDoc innerClass = new SimpleGroovyClassDoc(this.importedClassesAndPackages, this.aliases, innerClassName);
                innerClass.setFullPathName(gcd.getFullPathName() + "." + innerClassName);
                return innerClass;
            }
        }
        if (this.hasAlias(name) && (gcd = this.resolveClass(rootDoc, fullyQualifiedTypeName = this.getFullyQualifiedTypeNameForAlias(name))) != null) {
            return gcd;
        }
        SimpleGroovyClassDoc placeholder = new SimpleGroovyClassDoc(null, shortname);
        placeholder.setFullPathName(name);
        return placeholder;
    }

    private Class resolveFromJavaLang(String name) {
        try {
            return Class.forName("java.lang." + name, false, this.getClass().getClassLoader());
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return null;
    }

    private static boolean isPrimitiveType(String name) {
        String type = name;
        if (name.endsWith("[]")) {
            type = name.substring(0, name.length() - 2);
        }
        return PRIMITIVES.contains(type);
    }

    private GroovyClassDoc resolveInternalClassDocFromImport(GroovyRootDoc rootDoc, String baseName) {
        if (SimpleGroovyClassDoc.isPrimitiveType(baseName)) {
            return null;
        }
        for (String importName : this.importedClassesAndPackages) {
            GroovyClassDoc doc;
            if (!(importName.endsWith("/" + baseName) ? (doc = ((SimpleGroovyRootDoc)rootDoc).classNamedExact(importName)) != null : importName.endsWith("/*") && (doc = ((SimpleGroovyRootDoc)rootDoc).classNamedExact(importName.substring(0, importName.length() - 2) + baseName)) != null)) continue;
            return doc;
        }
        return null;
    }

    private Class resolveExternalClassFromImport(String name) {
        if (SimpleGroovyClassDoc.isPrimitiveType(name)) {
            return null;
        }
        for (String importName : this.importedClassesAndPackages) {
            String candidate = null;
            if (importName.endsWith("/" + name)) {
                candidate = importName.replaceAll("/", ".");
            } else if (importName.endsWith("/*")) {
                candidate = importName.substring(0, importName.length() - 2).replace('/', '.') + "." + name;
            }
            if (candidate == null) continue;
            try {
                return Class.forName(candidate, false, this.getClass().getClassLoader());
            }
            catch (NoClassDefFoundError noClassDefFoundError) {
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        return null;
    }

    private Class resolveExternalFullyQualifiedClass(String name) {
        String candidate = name.replace('/', '.');
        try {
            return Class.forName(candidate, false, this.getClass().getClassLoader());
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return null;
    }

    private boolean hasAlias(String alias) {
        return this.aliases.containsKey(alias);
    }

    private String getFullyQualifiedTypeNameForAlias(String alias) {
        if (!this.hasAlias(alias)) {
            return "";
        }
        return this.aliases.get(alias);
    }

    @Override
    public GroovyConstructorDoc[] constructors(boolean filter) {
        return null;
    }

    @Override
    public boolean definesSerializableFields() {
        return false;
    }

    @Override
    public GroovyFieldDoc[] fields(boolean filter) {
        return null;
    }

    @Override
    public GroovyClassDoc findClass(String className) {
        return null;
    }

    @Override
    public GroovyClassDoc[] importedClasses() {
        return null;
    }

    @Override
    public GroovyPackageDoc[] importedPackages() {
        return null;
    }

    @Override
    public GroovyClassDoc[] innerClasses(boolean filter) {
        return null;
    }

    @Override
    public GroovyClassDoc[] interfaces() {
        Collections.sort(this.interfaceClasses);
        return this.interfaceClasses.toArray(new GroovyClassDoc[this.interfaceClasses.size()]);
    }

    @Override
    public GroovyType[] interfaceTypes() {
        return null;
    }

    @Override
    public boolean isExternalizable() {
        return false;
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

    @Override
    public GroovyMethodDoc[] methods(boolean filter) {
        return null;
    }

    @Override
    public GroovyFieldDoc[] serializableFields() {
        return null;
    }

    @Override
    public GroovyMethodDoc[] serializationMethods() {
        return null;
    }

    @Override
    public boolean subclassOf(GroovyClassDoc gcd) {
        return false;
    }

    @Override
    public GroovyType superclassType() {
        return null;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public String qualifiedTypeName() {
        String qtnWithSlashes = this.fullPathName.startsWith("DefaultPackage/") ? this.fullPathName.substring("DefaultPackage/".length()) : this.fullPathName;
        return qtnWithSlashes.replace('/', '.');
    }

    @Override
    public String simpleTypeName() {
        String typeName = this.qualifiedTypeName();
        int lastDot = typeName.lastIndexOf(46);
        if (lastDot < 0) {
            return typeName;
        }
        return typeName.substring(lastDot + 1);
    }

    @Override
    public String typeName() {
        return null;
    }

    public void addInterfaceName(String className) {
        this.interfaceNames.add(className);
    }

    @Override
    public String firstSentenceCommentText() {
        if (super.firstSentenceCommentText() == null) {
            this.setFirstSentenceCommentText(this.replaceTags(SimpleGroovyClassDoc.calculateFirstSentence(this.getRawCommentText())));
        }
        return super.firstSentenceCommentText();
    }

    @Override
    public String commentText() {
        if (super.commentText() == null) {
            this.setCommentText(this.replaceTags(this.getRawCommentText()));
        }
        return super.commentText();
    }

    public String replaceTags(String comment) {
        String result = comment.replaceAll("(?m)^\\s*\\*", "");
        String relativeRootPath = this.getRelativeRootPath();
        if (!relativeRootPath.endsWith("/")) {
            relativeRootPath = relativeRootPath + "/";
        }
        result = result.replaceAll(DOCROOT_PATTERN2, relativeRootPath);
        result = result.replaceAll(DOCROOT_PATTERN, relativeRootPath);
        result = this.replaceAllTags(result, "", "", LINK_REGEX);
        result = SimpleGroovyClassDoc.encodeAngleBracketsInTagBody(result, LITERAL_REGEX);
        result = this.replaceAllTags(result, "", "", LITERAL_REGEX);
        result = SimpleGroovyClassDoc.encodeAngleBracketsInTagBody(result, CODE_REGEX);
        result = this.replaceAllTags(result, "<CODE>", "</CODE>", CODE_REGEX);
        result = this.replaceAllTagsCollated(result, "<DL><DT><B>", ":</B></DT><DD>", "</DD><DD>", "</DD></DL>", TAG_REGEX);
        return SimpleGroovyClassDoc.decodeSpecialSymbols(result);
    }

    public String replaceAllTags(String self, String s1, String s2, Pattern regex) {
        return SimpleGroovyClassDoc.replaceAllTags(self, s1, s2, regex, this.links, this.getRelativeRootPath(), this.savedRootDoc, this);
    }

    public static String replaceAllTags(String self, String s1, String s2, Pattern regex, List<LinkArgument> links, String relPath, GroovyRootDoc rootDoc, SimpleGroovyClassDoc classDoc) {
        Matcher matcher = regex.matcher(self);
        if (matcher.find()) {
            matcher.reset();
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String tagname = matcher.group(1);
                if ("interface".equals(tagname)) continue;
                String content = SimpleGroovyClassDoc.encodeSpecialSymbols(matcher.group(2));
                if ("link".equals(tagname) || "see".equals(tagname)) {
                    content = SimpleGroovyClassDoc.getDocUrl(content, false, links, relPath, rootDoc, classDoc);
                }
                matcher.appendReplacement(sb, s1 + content + s2);
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        return self;
    }

    public String replaceAllTagsCollated(String self, String preKey, String postKey, String valueSeparator, String postValues, Pattern regex) {
        Matcher matcher = regex.matcher(self + "@endMarker");
        if (matcher.find()) {
            matcher.reset();
            LinkedHashMap<String, ArrayList<String>> savedTags = new LinkedHashMap<String, ArrayList<String>>();
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                int index;
                String tagname = matcher.group(1);
                if ("interface".equals(tagname)) continue;
                String content = SimpleGroovyClassDoc.encodeSpecialSymbols(matcher.group(2));
                if ("see".equals(tagname) || "link".equals(tagname)) {
                    content = this.getDocUrl(content);
                } else if ("param".equals(tagname) && (index = content.indexOf(" ")) >= 0) {
                    content = "<code>" + content.substring(0, index) + "</code> - " + content.substring(index);
                }
                if (TAG_TEXT.containsKey(tagname)) {
                    String text = TAG_TEXT.get(tagname);
                    ArrayList<String> contents = (ArrayList<String>)savedTags.get(text);
                    if (contents == null) {
                        contents = new ArrayList<String>();
                        savedTags.put(text, contents);
                    }
                    contents.add(content);
                    matcher.appendReplacement(sb, "");
                    continue;
                }
                matcher.appendReplacement(sb, preKey + tagname + postKey + content + postValues);
            }
            matcher.appendTail(sb);
            sb = new StringBuffer(sb.substring(0, sb.length() - 10));
            for (Map.Entry e : savedTags.entrySet()) {
                sb.append(preKey);
                sb.append((String)e.getKey());
                sb.append(postKey);
                sb.append(DefaultGroovyMethods.join((Collection)e.getValue(), valueSeparator));
                sb.append(postValues);
            }
            return sb.toString();
        }
        return self;
    }

    public static String encodeAngleBracketsInTagBody(String text, Pattern regex) {
        Matcher matcher = regex.matcher(text);
        if (matcher.find()) {
            matcher.reset();
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String tagName = matcher.group(1);
                String tagBody = matcher.group(2);
                String encodedBody = Matcher.quoteReplacement(SimpleGroovyClassDoc.encodeAngleBrackets(tagBody));
                String replacement = "{@" + tagName + " " + encodedBody + "}";
                matcher.appendReplacement(sb, replacement);
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        return text;
    }

    public static String encodeAngleBrackets(String text) {
        return text == null ? null : text.replace("<", "&lt;").replace(">", "&gt;");
    }

    public static String encodeSpecialSymbols(String text) {
        return Matcher.quoteReplacement(text.replaceAll("@", "&at;"));
    }

    public static String decodeSpecialSymbols(String text) {
        return text.replaceAll("&at;", "@");
    }

    public void setNameWithTypeArgs(String nameWithTypeArgs) {
        this.nameWithTypeArgs = nameWithTypeArgs;
    }

    public String getNameWithTypeArgs() {
        return this.nameWithTypeArgs;
    }

    static {
        TAG_TEXT.put("see", "See Also");
        TAG_TEXT.put("param", "Parameters");
        TAG_TEXT.put("throw", "Throws");
        TAG_TEXT.put("exception", "Throws");
        TAG_TEXT.put("return", "Returns");
        TAG_TEXT.put("since", "Since");
        TAG_TEXT.put("author", "Authors");
        TAG_TEXT.put("version", "Version");
        TAG_TEXT.put("default", "Default");
    }
}

