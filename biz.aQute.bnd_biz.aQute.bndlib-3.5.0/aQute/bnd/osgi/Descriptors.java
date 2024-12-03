/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Constants;
import aQute.libg.generics.Create;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;

public class Descriptors {
    Map<String, TypeRef> typeRefCache = Create.map();
    Map<String, Descriptor> descriptorCache = Create.map();
    Map<String, PackageRef> packageCache = Create.map();
    static final PackageRef DEFAULT_PACKAGE = new PackageRef();
    static final PackageRef PRIMITIVE_PACKAGE = new PackageRef();
    static final TypeRef VOID = new ConcreteRef("V", "void", PRIMITIVE_PACKAGE);
    static final TypeRef BOOLEAN = new ConcreteRef("Z", "boolean", PRIMITIVE_PACKAGE);
    static final TypeRef BYTE = new ConcreteRef("B", "byte", PRIMITIVE_PACKAGE);
    static final TypeRef CHAR = new ConcreteRef("C", "char", PRIMITIVE_PACKAGE);
    static final TypeRef SHORT = new ConcreteRef("S", "short", PRIMITIVE_PACKAGE);
    static final TypeRef INTEGER = new ConcreteRef("I", "int", PRIMITIVE_PACKAGE);
    static final TypeRef LONG = new ConcreteRef("J", "long", PRIMITIVE_PACKAGE);
    static final TypeRef DOUBLE = new ConcreteRef("D", "double", PRIMITIVE_PACKAGE);
    static final TypeRef FLOAT = new ConcreteRef("F", "float", PRIMITIVE_PACKAGE);

    public Descriptors() {
        this.packageCache.put("", DEFAULT_PACKAGE);
    }

    public TypeRef getTypeRef(String binaryClassName) {
        assert (!binaryClassName.endsWith(".class"));
        TypeRef ref = this.typeRefCache.get(binaryClassName);
        if (ref != null) {
            return ref;
        }
        if (binaryClassName.startsWith("[")) {
            ref = this.getTypeRef(binaryClassName.substring(1));
            ref = new ArrayRef(ref);
        } else {
            if (binaryClassName.length() == 1) {
                switch (binaryClassName.charAt(0)) {
                    case 'V': {
                        return VOID;
                    }
                    case 'B': {
                        return BYTE;
                    }
                    case 'C': {
                        return CHAR;
                    }
                    case 'I': {
                        return INTEGER;
                    }
                    case 'S': {
                        return SHORT;
                    }
                    case 'D': {
                        return DOUBLE;
                    }
                    case 'F': {
                        return FLOAT;
                    }
                    case 'J': {
                        return LONG;
                    }
                    case 'Z': {
                        return BOOLEAN;
                    }
                }
            }
            if (binaryClassName.startsWith("L") && binaryClassName.endsWith(";")) {
                binaryClassName = binaryClassName.substring(1, binaryClassName.length() - 1);
            }
            if ((ref = this.typeRefCache.get(binaryClassName)) != null) {
                return ref;
            }
            int n = binaryClassName.lastIndexOf(47);
            PackageRef pref = n < 0 ? DEFAULT_PACKAGE : this.getPackageRef(binaryClassName.substring(0, n));
            ref = new ConcreteRef(pref, binaryClassName);
        }
        this.typeRefCache.put(binaryClassName, ref);
        return ref;
    }

    public PackageRef getPackageRef(String binaryPackName) {
        PackageRef ref;
        if (binaryPackName.indexOf(46) >= 0) {
            binaryPackName = binaryPackName.replace('.', '/');
        }
        if ((ref = this.packageCache.get(binaryPackName)) != null) {
            return ref;
        }
        ref = new PackageRef(binaryPackName);
        this.packageCache.put(binaryPackName, ref);
        return ref;
    }

    public Descriptor getDescriptor(String descriptor) {
        Descriptor d = this.descriptorCache.get(descriptor);
        if (d != null) {
            return d;
        }
        d = new Descriptor(descriptor);
        this.descriptorCache.put(descriptor, d);
        return d;
    }

    public static String getShortName(String fqn) {
        assert (fqn.indexOf(47) < 0);
        int n = fqn.lastIndexOf(46);
        if (n >= 0) {
            return fqn.substring(n + 1);
        }
        return fqn;
    }

    public static String binaryToFQN(String binary) {
        StringBuilder sb = new StringBuilder();
        int l = binary.length();
        for (int i = 0; i < l; ++i) {
            char c = binary.charAt(i);
            if (c == '/') {
                sb.append('.');
                continue;
            }
            sb.append(c);
        }
        String result = sb.toString();
        assert (result.length() > 0);
        return result;
    }

    public static String fqnToBinary(String binary) {
        return binary.replace('.', '/');
    }

    public static String getPackage(String binaryNameOrFqn) {
        int n = binaryNameOrFqn.lastIndexOf(47);
        if (n >= 0) {
            return binaryNameOrFqn.substring(0, n).replace('/', '.');
        }
        n = binaryNameOrFqn.lastIndexOf(46);
        if (n >= 0) {
            return binaryNameOrFqn.substring(0, n);
        }
        return ".";
    }

    public static String fqnToPath(String s) {
        return Descriptors.fqnToBinary(s) + ".class";
    }

    public TypeRef getTypeRefFromFQN(String fqn) {
        if (fqn.equals("boolean")) {
            return BOOLEAN;
        }
        if (fqn.equals("byte")) {
            return BOOLEAN;
        }
        if (fqn.equals("char")) {
            return CHAR;
        }
        if (fqn.equals("short")) {
            return SHORT;
        }
        if (fqn.equals("int")) {
            return INTEGER;
        }
        if (fqn.equals("long")) {
            return LONG;
        }
        if (fqn.equals("float")) {
            return FLOAT;
        }
        if (fqn.equals("double")) {
            return DOUBLE;
        }
        return this.getTypeRef(Descriptors.fqnToBinary(fqn));
    }

    public TypeRef getTypeRefFromPath(String path) {
        assert (path.endsWith(".class"));
        return this.getTypeRef(path.substring(0, path.length() - 6));
    }

    public class Descriptor {
        final TypeRef type;
        final TypeRef[] prototype;
        final String descriptor;

        Descriptor(String descriptor) {
            this.descriptor = descriptor;
            int index = 0;
            List<TypeRef> types = Create.list();
            if (descriptor.charAt(index) == '(') {
                ++index;
                while (descriptor.charAt(index) != ')') {
                    index = this.parse(types, descriptor, index);
                }
                ++index;
                this.prototype = types.toArray(new TypeRef[0]);
                types.clear();
            } else {
                this.prototype = null;
            }
            index = this.parse(types, descriptor, index);
            this.type = types.get(0);
        }

        int parse(List<TypeRef> types, String descriptor, int index) {
            char c;
            StringBuilder sb = new StringBuilder();
            while ((c = descriptor.charAt(index++)) == '[') {
                sb.append('[');
            }
            switch (c) {
                case 'L': {
                    while ((c = descriptor.charAt(index++)) != ';') {
                        sb.append(c);
                    }
                    break;
                }
                case 'B': 
                case 'C': 
                case 'D': 
                case 'F': 
                case 'I': 
                case 'J': 
                case 'S': 
                case 'V': 
                case 'Z': {
                    sb.append(c);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid type in descriptor: " + c + " from " + descriptor + "[" + index + "]");
                }
            }
            types.add(Descriptors.this.getTypeRef(sb.toString()));
            return index;
        }

        public TypeRef getType() {
            return this.type;
        }

        public TypeRef[] getPrototype() {
            return this.prototype;
        }

        public boolean equals(Object other) {
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            return Arrays.equals(this.prototype, ((Descriptor)other).prototype) && this.type == ((Descriptor)other).type;
        }

        public int hashCode() {
            return this.prototype == null ? this.type.hashCode() : this.type.hashCode() ^ Arrays.hashCode(this.prototype);
        }

        public String toString() {
            return this.descriptor;
        }
    }

    private static class ArrayRef
    implements TypeRef {
        final TypeRef component;

        ArrayRef(TypeRef component) {
            this.component = component;
        }

        @Override
        public String getBinary() {
            return "[" + this.component.getBinary();
        }

        @Override
        public String getFQN() {
            return this.component.getFQN() + "[]";
        }

        @Override
        public String getPath() {
            return this.component.getPath();
        }

        @Override
        public String getSourcePath() {
            return this.component.getSourcePath();
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }

        @Override
        public TypeRef getComponentTypeRef() {
            return this.component;
        }

        @Override
        public TypeRef getClassRef() {
            return this.component.getClassRef();
        }

        public boolean equals(Object other) {
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            return this.component.equals(((ArrayRef)other).component);
        }

        @Override
        public PackageRef getPackageRef() {
            return this.component.getPackageRef();
        }

        @Override
        public String getShortName() {
            return this.component.getShortName() + "[]";
        }

        @Override
        public boolean isJava() {
            return this.component.isJava();
        }

        public String toString() {
            return this.component.toString() + "[]";
        }

        @Override
        public boolean isObject() {
            return false;
        }

        @Override
        public String getDottedOnly() {
            return this.component.getDottedOnly();
        }

        @Override
        public int compareTo(TypeRef other) {
            if (this == other) {
                return 0;
            }
            return this.getFQN().compareTo(other.getFQN());
        }

        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String getShorterName() {
            String name = this.getShortName();
            int n = name.lastIndexOf(36);
            if (n <= 0) {
                return name;
            }
            return name.substring(n + 1);
        }
    }

    private static class ConcreteRef
    implements TypeRef {
        final String binaryName;
        final String fqn;
        final boolean primitive;
        final PackageRef packageRef;

        ConcreteRef(PackageRef packageRef, String binaryName) {
            this.binaryName = binaryName;
            this.fqn = Descriptors.binaryToFQN(binaryName);
            this.primitive = false;
            this.packageRef = packageRef;
        }

        ConcreteRef(String binaryName, String fqn, PackageRef pref) {
            this.binaryName = binaryName;
            this.fqn = fqn;
            this.primitive = true;
            this.packageRef = pref;
        }

        @Override
        public String getBinary() {
            return this.binaryName;
        }

        @Override
        public String getPath() {
            return this.binaryName + ".class";
        }

        @Override
        public String getSourcePath() {
            return this.binaryName + ".java";
        }

        @Override
        public String getFQN() {
            return this.fqn;
        }

        @Override
        public String getDottedOnly() {
            return this.fqn.replace('$', '.');
        }

        @Override
        public boolean isPrimitive() {
            return this.primitive;
        }

        @Override
        public TypeRef getComponentTypeRef() {
            return null;
        }

        @Override
        public TypeRef getClassRef() {
            return this;
        }

        @Override
        public PackageRef getPackageRef() {
            return this.packageRef;
        }

        @Override
        public String getShortName() {
            int n = this.binaryName.lastIndexOf(47);
            return this.binaryName.substring(n + 1);
        }

        @Override
        public String getShorterName() {
            String name = this.getShortName();
            int n = name.lastIndexOf(36);
            if (n <= 0) {
                return name;
            }
            return name.substring(n + 1);
        }

        @Override
        public boolean isJava() {
            return this.packageRef.isJava();
        }

        public String toString() {
            return this.fqn;
        }

        @Override
        public boolean isObject() {
            return this.fqn.equals("java.lang.Object");
        }

        public boolean equals(Object other) {
            assert (other instanceof TypeRef);
            return this == other;
        }

        @Override
        public int compareTo(TypeRef other) {
            if (this == other) {
                return 0;
            }
            return this.fqn.compareTo(other.getFQN());
        }

        public int hashCode() {
            return super.hashCode();
        }
    }

    public static class PackageRef
    implements Comparable<PackageRef> {
        final String binaryName;
        final String fqn;
        final boolean java;

        PackageRef(String binaryName) {
            this.binaryName = Descriptors.fqnToBinary(binaryName);
            this.fqn = Descriptors.binaryToFQN(binaryName);
            this.java = this.fqn.startsWith("java.");
        }

        PackageRef() {
            this.binaryName = "";
            this.fqn = ".";
            this.java = false;
        }

        public PackageRef getDuplicate() {
            return new PackageRef(this.binaryName + '~');
        }

        public String getFQN() {
            return this.fqn;
        }

        public String getBinary() {
            return this.binaryName;
        }

        public String getPath() {
            return this.binaryName;
        }

        public boolean isJava() {
            return this.java;
        }

        public String toString() {
            return this.fqn;
        }

        boolean isDefaultPackage() {
            return this.fqn.equals(".");
        }

        boolean isPrimitivePackage() {
            return this == PRIMITIVE_PACKAGE;
        }

        @Override
        public int compareTo(PackageRef other) {
            return this.fqn.compareTo(other.fqn);
        }

        public boolean equals(Object o) {
            assert (o instanceof PackageRef);
            return o == this;
        }

        public int hashCode() {
            return super.hashCode();
        }

        public boolean isMetaData() {
            if (this.isDefaultPackage()) {
                return true;
            }
            for (int i = 0; i < Constants.METAPACKAGES.length; ++i) {
                if (!this.fqn.startsWith(Constants.METAPACKAGES[i])) continue;
                return true;
            }
            return false;
        }
    }

    @ProviderType
    public static interface TypeRef
    extends Comparable<TypeRef> {
        public String getBinary();

        public String getShorterName();

        public String getFQN();

        public String getPath();

        public boolean isPrimitive();

        public TypeRef getComponentTypeRef();

        public TypeRef getClassRef();

        public PackageRef getPackageRef();

        public String getShortName();

        public boolean isJava();

        public boolean isObject();

        public String getSourcePath();

        public String getDottedOnly();
    }

    public class Signature {
        public Map<String, Signature> typevariables = new HashMap<String, Signature>();
        public Signature type;
        public List<Signature> parameters;
    }

    public static enum SignatureType {
        TYPEVAR,
        METHOD,
        FIELD;

    }
}

