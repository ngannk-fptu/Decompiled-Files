/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.differ;

import aQute.bnd.differ.Element;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Packages;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Type;
import aQute.bnd.version.Version;
import aQute.lib.collections.MultiMap;
import aQute.libg.generics.Create;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Manifest;

class JavaElement {
    static final EnumSet<Type> INHERITED = EnumSet.of(Type.FIELD, Type.METHOD, Type.EXTENDS, Type.IMPLEMENTS);
    private static final Element PROTECTED = new Element(Type.ACCESS, "protected", null, Delta.MAJOR, Delta.MINOR, null);
    private static final Element PROTECTED_PROVIDER = new Element(Type.ACCESS, "protected", null, Delta.MINOR, Delta.MINOR, null);
    private static final Element STATIC = new Element(Type.ACCESS, "static", null, Delta.MAJOR, Delta.MAJOR, null);
    private static final Element ABSTRACT = new Element(Type.ACCESS, "abstract", null, Delta.MAJOR, Delta.MINOR, null);
    private static final Element FINAL = new Element(Type.ACCESS, "final", null, Delta.MAJOR, Delta.MINOR, null);
    static final Element VOID_R = new Element(Type.RETURN, "void");
    static final Element BOOLEAN_R = new Element(Type.RETURN, "boolean");
    static final Element BYTE_R = new Element(Type.RETURN, "byte");
    static final Element SHORT_R = new Element(Type.RETURN, "short");
    static final Element CHAR_R = new Element(Type.RETURN, "char");
    static final Element INT_R = new Element(Type.RETURN, "int");
    static final Element LONG_R = new Element(Type.RETURN, "long");
    static final Element FLOAT_R = new Element(Type.RETURN, "float");
    static final Element DOUBLE_R = new Element(Type.RETURN, "double");
    static final Element OBJECT_R = new Element(Type.RETURN, "java.lang.Object");
    final Analyzer analyzer;
    final Map<Descriptors.PackageRef, Instructions> providerMatcher = Create.map();
    final Set<Descriptors.TypeRef> notAccessible = Create.set();
    final Map<Object, Element> cache = Create.map();
    final MultiMap<Descriptors.PackageRef, Element> packages;
    final Set<Clazz.JAVA> javas = Create.set();
    final Packages exports;

    JavaElement(Analyzer analyzer) throws Exception {
        this.analyzer = analyzer;
        Manifest manifest = analyzer.getJar().getManifest();
        if (manifest != null && manifest.getMainAttributes().getValue("Bundle-ManifestVersion") != null) {
            this.exports = new Packages();
            for (Map.Entry<Object, Attrs> entry : OSGiHeader.parseHeader(manifest.getMainAttributes().getValue("Export-Package")).entrySet()) {
                this.exports.put(analyzer.getPackageRef((String)entry.getKey()), entry.getValue());
            }
        } else {
            this.exports = analyzer.getContained();
        }
        for (Map.Entry<Descriptors.PackageRef, Attrs> entry : this.exports.entrySet()) {
            String value = entry.getValue().get("x-provider-type:");
            if (value == null) continue;
            this.providerMatcher.put(entry.getKey(), new Instructions(value));
        }
        this.packages = new MultiMap();
        for (Clazz clazz : analyzer.getClassspace().values()) {
            Descriptors.PackageRef packageName;
            if (clazz.isSynthetic() || !clazz.isPublic() && !clazz.isProtected() || !this.exports.containsKey(packageName = clazz.getClassName().getPackageRef())) continue;
            Element cdef = this.classElement(clazz);
            this.packages.add(packageName, cdef);
        }
    }

    static Element getAPI(Analyzer analyzer) throws Exception {
        analyzer.analyze();
        JavaElement te = new JavaElement(analyzer);
        return te.getLocalAPI();
    }

    private Element getLocalAPI() throws Exception {
        HashSet<Element> result = new HashSet<Element>();
        for (Map.Entry entry : this.packages.entrySet()) {
            List set = (List)entry.getValue();
            Iterator i = set.iterator();
            while (i.hasNext()) {
                if (!this.notAccessible.contains(this.analyzer.getTypeRefFromFQN(((Element)i.next()).getName()))) continue;
                i.remove();
            }
            String version = this.exports.get((Descriptors.PackageRef)entry.getKey()).get("version");
            if (version != null) {
                Version v = new Version(version);
                set.add(new Element(Type.VERSION, v.getWithoutQualifier().toString(), null, Delta.IGNORED, Delta.IGNORED, null));
            }
            Element pd = new Element(Type.PACKAGE, ((Descriptors.PackageRef)entry.getKey()).getFQN(), set, Delta.MINOR, Delta.MAJOR, null);
            result.add(pd);
        }
        for (Clazz.JAVA java : this.javas) {
            result.add(new Element(Type.CLASS_VERSION, java.toString(), null, Delta.CHANGED, Delta.CHANGED, null));
        }
        return new Element(Type.API, "<api>", result, Delta.CHANGED, Delta.CHANGED, null);
    }

    Element classElement(final Clazz clazz) throws Exception {
        Collection<Element> children;
        Delta remove;
        Delta add;
        Element e = this.cache.get(clazz);
        if (e != null) {
            return e;
        }
        final Set<Element> members = Create.set();
        final Set<Clazz.MethodDef> methods = Create.set();
        final Set<Clazz.FieldDef> fields = Create.set();
        final MultiMap annotations = new MultiMap();
        Descriptors.TypeRef name = clazz.getClassName();
        String fqn = name.getFQN();
        String shortName = name.getShortName();
        Instructions matchers = this.providerMatcher.get(name.getPackageRef());
        boolean p = matchers != null && matchers.matches(shortName);
        final AtomicBoolean provider = new AtomicBoolean(p);
        Element before = this.cache.get(clazz);
        if (before != null) {
            return before;
        }
        clazz.parseClassFileWithCollector(new ClassDataCollector(){
            boolean memberEnd;
            Clazz.FieldDef last;
            Set<Element> OBJECT = Create.set();

            @Override
            public void version(int minor, int major) {
                JavaElement.this.javas.add(Clazz.JAVA.getJava(major, minor));
            }

            @Override
            public void method(Clazz.MethodDef defined) {
                if (defined.isProtected() || defined.isPublic()) {
                    this.last = defined;
                    methods.add(defined);
                } else {
                    this.last = null;
                }
            }

            @Override
            public void deprecated() {
                if (this.memberEnd) {
                    clazz.setDeprecated(true);
                } else if (this.last != null) {
                    this.last.setDeprecated(true);
                }
            }

            @Override
            public void field(Clazz.FieldDef defined) {
                if (defined.isProtected() || defined.isPublic()) {
                    this.last = defined;
                    fields.add(defined);
                } else {
                    this.last = null;
                }
            }

            @Override
            public void constant(Object o) {
                if (this.last != null) {
                    this.last.setConstant(o);
                }
            }

            @Override
            public void extendsClass(Descriptors.TypeRef name) throws Exception {
                Clazz c;
                String comment = null;
                if (!clazz.isInterface()) {
                    comment = this.inherit(members, name);
                }
                if (((c = JavaElement.this.analyzer.findClass(name)) == null || c.isPublic()) && !name.isObject()) {
                    members.add(new Element(Type.EXTENDS, name.getFQN(), null, Delta.MICRO, Delta.MAJOR, comment));
                }
            }

            @Override
            public void implementsInterfaces(Descriptors.TypeRef[] names) throws Exception {
                Arrays.sort(names);
                for (Descriptors.TypeRef name : names) {
                    String comment = null;
                    if (clazz.isInterface() || clazz.isAbstract()) {
                        comment = this.inherit(members, name);
                    }
                    members.add(new Element(Type.IMPLEMENTS, name.getFQN(), null, Delta.MINOR, Delta.MAJOR, comment));
                }
            }

            public String inherit(Set<Element> members2, Descriptors.TypeRef name) throws Exception {
                if (name.isObject()) {
                    if (this.OBJECT.isEmpty()) {
                        Clazz c = JavaElement.this.analyzer.findClass(name);
                        if (c == null) {
                            return null;
                        }
                        Element s = JavaElement.this.classElement(c);
                        for (Element child : s.children) {
                            if (!INHERITED.contains((Object)child.type)) continue;
                            String n = child.getName();
                            if (child.type == Type.METHOD && (n.startsWith("<init>") || "getClass()".equals(child.getName()) || n.startsWith("wait(") || n.startsWith("notify(") || n.startsWith("notifyAll(")) || this.isStatic(child)) continue;
                            this.OBJECT.add(child);
                        }
                    }
                    members2.addAll(this.OBJECT);
                } else {
                    Clazz c = JavaElement.this.analyzer.findClass(name);
                    if (c == null) {
                        return this.inherit(members2, JavaElement.this.analyzer.getTypeRef("java/lang/Object"));
                    }
                    Element s = JavaElement.this.classElement(c);
                    for (Element child : s.children) {
                        if (this.isStatic(child) || !INHERITED.contains((Object)child.type) || child.name.startsWith("<")) continue;
                        members2.add(child);
                    }
                }
                return null;
            }

            private boolean isStatic(Element child) {
                boolean isStatic = child.get("static") != null;
                return isStatic;
            }

            @Override
            public void annotation(Annotation annotation) {
                if (Deprecated.class.getName().equals(annotation.getName().getFQN())) {
                    if (this.memberEnd) {
                        clazz.setDeprecated(true);
                    } else if (this.last != null) {
                        this.last.setDeprecated(true);
                    }
                    return;
                }
                Element e = this.annotatedToElement(annotation);
                if (this.memberEnd) {
                    members.add(e);
                    String name = annotation.getName().getFQN();
                    if ("aQute.bnd.annotation.ProviderType".equals(name) || "org.osgi.annotation.versioning.ProviderType".equals(name)) {
                        provider.set(true);
                    } else if ("aQute.bnd.annotation.ConsumerType".equals(name) || "org.osgi.annotation.versioning.ConsumerType".equals(name)) {
                        provider.set(false);
                    }
                } else if (this.last != null) {
                    annotations.add(this.last, e);
                }
            }

            private Element annotatedToElement(Annotation annotation) {
                Set<Element> properties = Create.set();
                for (String key : annotation.keySet()) {
                    this.addAnnotationMember(properties, key, annotation.get(key));
                }
                return new Element(Type.ANNOTATED, annotation.getName().getFQN(), properties, Delta.CHANGED, Delta.CHANGED, null);
            }

            private void addAnnotationMember(Collection<Element> properties, String key, Object member) {
                if (member instanceof Annotation) {
                    properties.add(this.annotatedToElement((Annotation)member));
                } else if (member.getClass().isArray()) {
                    int l = Array.getLength(member);
                    for (int i = 0; i < l; ++i) {
                        this.addAnnotationMember(properties, key + "." + i, Array.get(member, i));
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(key);
                    sb.append('=');
                    if (member instanceof String) {
                        sb.append("'");
                        sb.append(member);
                        sb.append("'");
                    } else {
                        sb.append(member);
                    }
                    properties.add(new Element(Type.PROPERTY, sb.toString(), null, Delta.CHANGED, Delta.CHANGED, null));
                }
            }

            @Override
            public void innerClass(Descriptors.TypeRef innerClass, Descriptors.TypeRef outerClass, String innerName, int innerClassAccessFlags) throws Exception {
                Clazz clazz2 = JavaElement.this.analyzer.findClass(innerClass);
                if (clazz2 != null) {
                    clazz2.setInnerAccess(innerClassAccessFlags);
                }
                if (Modifier.isProtected(innerClassAccessFlags) || Modifier.isPublic(innerClassAccessFlags)) {
                    return;
                }
                JavaElement.this.notAccessible.add(innerClass);
            }

            @Override
            public void memberEnd() {
                this.memberEnd = true;
            }
        });
        Type type = clazz.isInterface() ? (clazz.isAnnotation() ? Type.ANNOTATION : Type.INTERFACE) : (clazz.isEnum() ? Type.ENUM : Type.CLASS);
        if (type == Type.INTERFACE) {
            if (provider.get()) {
                add = Delta.MINOR;
                remove = Delta.MAJOR;
            } else {
                add = Delta.MAJOR;
                remove = Delta.MAJOR;
            }
        } else {
            add = Delta.MINOR;
            remove = Delta.MAJOR;
        }
        for (Clazz.MethodDef m : methods) {
            String signature;
            Element member;
            if (m.isSynthetic()) continue;
            children = (HashSet<Element>)annotations.get(m);
            if (children == null) {
                children = new HashSet<Element>();
            }
            JavaElement.access(children, m.getAccess(), m.isDeprecated(), provider.get());
            if (clazz.isFinal()) {
                children.remove(FINAL);
            }
            children.add(this.getReturn(m.getType()));
            if (clazz.isInterface() && !m.isAbstract()) {
                add = Delta.MINOR;
            }
            if (members.add(member = new Element(Type.METHOD, signature = m.getName() + this.toString(m.getPrototype()), children, add, provider.get() && !m.isPublic() ? Delta.MINOR : remove, null))) continue;
            members.remove(member);
            members.add(member);
        }
        for (Clazz.FieldDef f : fields) {
            if (f.isSynthetic()) continue;
            children = (Collection)annotations.get(f);
            if (children == null) {
                children = new HashSet();
            }
            if (f.getConstant() != null) {
                children.add(new Element(Type.CONSTANT, f.getConstant().toString(), null, Delta.CHANGED, Delta.CHANGED, null));
            }
            JavaElement.access(children, f.getAccess(), f.isDeprecated(), provider.get());
            children.add(this.getReturn(f.getType()));
            Element member = new Element(Type.FIELD, f.getName(), children, Delta.MINOR, provider.get() && !f.isPublic() ? Delta.MINOR : Delta.MAJOR, null);
            if (members.add(member)) continue;
            members.remove(member);
            members.add(member);
        }
        JavaElement.access(members, clazz.getAccess(), clazz.isDeprecated(), provider.get());
        Element s = new Element(type, fqn, members, Delta.MINOR, Delta.MAJOR, null);
        this.cache.put(clazz, s);
        return s;
    }

    private String toString(Descriptors.TypeRef[] prototype) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        String del = "";
        for (Descriptors.TypeRef ref : prototype) {
            sb.append(del);
            sb.append(ref.getFQN());
            del = ",";
        }
        sb.append(")");
        return sb.toString();
    }

    private Element getReturn(Descriptors.TypeRef type) {
        if (!type.isPrimitive()) {
            return type.isObject() ? OBJECT_R : new Element(Type.RETURN, type.getFQN());
        }
        switch (type.getBinary().charAt(0)) {
            case 'V': {
                return VOID_R;
            }
            case 'Z': {
                return BOOLEAN_R;
            }
            case 'S': {
                return SHORT_R;
            }
            case 'I': {
                return INT_R;
            }
            case 'B': {
                return BYTE_R;
            }
            case 'C': {
                return CHAR_R;
            }
            case 'J': {
                return LONG_R;
            }
            case 'F': {
                return FLOAT_R;
            }
            case 'D': {
                return DOUBLE_R;
            }
        }
        throw new IllegalArgumentException("Unknown primitive " + type);
    }

    private static void access(Collection<Element> children, int access, boolean deprecated, boolean provider) {
        if (!Modifier.isPublic(access)) {
            children.add(provider ? PROTECTED_PROVIDER : PROTECTED);
        }
        if (Modifier.isAbstract(access)) {
            children.add(ABSTRACT);
        }
        if (Modifier.isFinal(access)) {
            children.add(FINAL);
        }
        if (Modifier.isStatic(access)) {
            children.add(STATIC);
        }
    }
}

