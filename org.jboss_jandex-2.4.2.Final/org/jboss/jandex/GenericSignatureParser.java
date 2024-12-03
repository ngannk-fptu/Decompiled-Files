/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.NameTable;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.UnresolvedTypeVariable;
import org.jboss.jandex.VoidType;
import org.jboss.jandex.WildcardType;

class GenericSignatureParser {
    private static WildcardType UNBOUNDED_WILDCARD = new WildcardType(null, true);
    private String signature;
    private int pos;
    private NameTable names;
    private Map<String, TypeVariable> typeParameters;
    private Map<String, TypeVariable> elementTypeParameters = new HashMap<String, TypeVariable>();
    private Map<String, TypeVariable> classTypeParameters = new HashMap<String, TypeVariable>();

    GenericSignatureParser(NameTable names) {
        names.intern(DotName.OBJECT_NAME, '/');
        this.names = names;
    }

    ClassSignature parseClassSignature(String signature) {
        this.signature = signature;
        this.typeParameters = this.classTypeParameters;
        this.typeParameters.clear();
        this.pos = 0;
        Type[] parameters = this.parseTypeParameters();
        Type superClass = this.names.intern(this.parseClassTypeSignature());
        int end = signature.length();
        ArrayList<Type> interfaces = new ArrayList<Type>();
        while (this.pos < end) {
            interfaces.add(this.names.intern(this.parseClassTypeSignature()));
        }
        Type[] intfArray = this.names.intern(interfaces.toArray(new Type[interfaces.size()]));
        return new ClassSignature(parameters, superClass, intfArray);
    }

    private void expect(char c) {
        if (this.signature.charAt(this.pos++) != c) {
            throw new IllegalArgumentException("Expected character '" + c + "' at position " + (this.pos - 1));
        }
    }

    Type parseFieldSignature(String signature) {
        this.signature = signature;
        this.typeParameters = this.elementTypeParameters;
        this.typeParameters.clear();
        this.pos = 0;
        return this.parseReferenceType();
    }

    MethodSignature parseMethodSignature(String signature) {
        this.signature = signature;
        this.typeParameters = this.elementTypeParameters;
        this.typeParameters.clear();
        this.pos = 0;
        Type[] typeParameters = this.parseTypeParameters();
        this.expect('(');
        ArrayList<Type> parameters = new ArrayList<Type>();
        while (signature.charAt(this.pos) != ')') {
            Type type = this.parseJavaType();
            if (type == null) {
                throw new IllegalArgumentException("Corrupted argument, or unclosed brace at: " + this.pos);
            }
            parameters.add(type);
        }
        ++this.pos;
        Type returnType = this.parseReturnType();
        ArrayList<Type> exceptions = new ArrayList<Type>();
        while (this.pos < signature.length()) {
            this.expect('^');
            exceptions.add(this.parseReferenceType());
        }
        Type[] exceptionsArray = this.names.intern(exceptions.toArray(new Type[exceptions.size()]));
        Type[] types = this.names.intern(parameters.toArray(new Type[parameters.size()]));
        return new MethodSignature(typeParameters, types, returnType, exceptionsArray);
    }

    private Type parseClassTypeSignature() {
        String signature = this.signature;
        DotName name = this.parseName();
        Type[] types = this.parseTypeArguments();
        Type type = null;
        if (types.length > 0) {
            type = new ParameterizedType(name, types, null);
        }
        while (signature.charAt(this.pos) == '.') {
            int mark = ++this.pos;
            int suffixEnd = this.advanceNameEnd();
            name = this.names.wrap(name, signature.substring(mark, suffixEnd), true);
            types = this.parseTypeArguments();
            if (type == null && types.length > 0) {
                type = this.names.intern(new ClassType(name.prefix()));
            }
            if (type == null) continue;
            type = this.names.intern(new ParameterizedType(name, types, type));
        }
        ++this.pos;
        return type != null ? type : this.names.intern(new ClassType(name));
    }

    private Type[] parseTypeArguments() {
        return this.parseTypeList(true);
    }

    private Type[] parseTypeParameters() {
        return this.parseTypeList(false);
    }

    private Type[] parseTypeList(boolean argument) {
        String signature = this.signature;
        if (signature.charAt(this.pos) != '<') {
            return Type.EMPTY_ARRAY;
        }
        ++this.pos;
        ArrayList<Type> types = new ArrayList<Type>();
        while (true) {
            Type t;
            Type type = t = argument ? this.parseTypeArgument() : this.parseTypeParameter();
            if (t == null) break;
            types.add(t);
        }
        if (!argument) {
            this.resolveTypeList(types);
        }
        return this.names.intern(types.toArray(new Type[types.size()]));
    }

    private Type parseTypeArgument() {
        char c = this.signature.charAt(this.pos++);
        switch (c) {
            case '>': {
                return null;
            }
            case '*': {
                return UNBOUNDED_WILDCARD;
            }
            case '-': {
                return this.parseWildCard(false);
            }
            case '+': {
                return this.parseWildCard(true);
            }
        }
        --this.pos;
        return this.parseReferenceType();
    }

    private Type parseWildCard(boolean isExtends) {
        Type bound = this.parseReferenceType();
        return new WildcardType(bound, isExtends);
    }

    private Type parseTypeParameter() {
        int start;
        String signature = this.signature;
        if (signature.charAt(start = this.pos++) == '>') {
            return null;
        }
        int bound = this.advancePast(':');
        String name = this.names.intern(signature.substring(start, bound));
        ArrayList<Type> bounds = new ArrayList<Type>();
        if (signature.charAt(this.pos) != ':') {
            bounds.add(this.parseReferenceType());
        }
        boolean implicitObjectBound = false;
        while (signature.charAt(this.pos) == ':') {
            ++this.pos;
            if (bounds.size() == 0) {
                implicitObjectBound = true;
            }
            bounds.add(this.parseReferenceType());
        }
        TypeVariable type = new TypeVariable(name, bounds.toArray(new Type[bounds.size()]), null, implicitObjectBound);
        this.typeParameters.put(name, type);
        return type;
    }

    private Type parseReturnType() {
        if (this.signature.charAt(this.pos) == 'V') {
            ++this.pos;
            return VoidType.VOID;
        }
        return this.parseJavaType();
    }

    private Type parseReferenceType() {
        Type type;
        int mark = this.pos;
        char c = this.signature.charAt(mark);
        switch (c) {
            case 'T': {
                type = this.parseTypeVariable();
                break;
            }
            case 'L': {
                type = this.parseClassTypeSignature();
                break;
            }
            case '[': {
                type = this.parseArrayType();
                break;
            }
            default: {
                return null;
            }
        }
        return this.names.intern(type);
    }

    private Type parseArrayType() {
        int mark = this.pos;
        int last = this.advanceNot('[');
        return new ArrayType(this.parseJavaType(), last - mark);
    }

    private Type parseTypeVariable() {
        String name = this.names.intern(this.signature.substring(this.pos + 1, this.advancePast(';')));
        TypeVariable type = this.resolveType(name);
        return type == null ? new UnresolvedTypeVariable(name) : type;
    }

    private void resolveTypeList(ArrayList<Type> list) {
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            Type type = this.resolveType(list.get(i));
            if (type == null) continue;
            list.set(i, type);
            this.typeParameters.put(type.asTypeVariable().identifier(), type.asTypeVariable());
        }
    }

    private Type resolveType(Type type) {
        if (type instanceof TypeVariable) {
            TypeVariable typeVariable = this.resolveBounds(type);
            return typeVariable != type ? typeVariable : null;
        }
        if (!(type instanceof UnresolvedTypeVariable)) {
            return null;
        }
        return this.resolveType(((UnresolvedTypeVariable)type).identifier());
    }

    private TypeVariable resolveBounds(Type type) {
        TypeVariable typeVariable = type.asTypeVariable();
        Type[] bounds = typeVariable.boundArray();
        for (int i = 0; i < bounds.length; ++i) {
            Type newType = this.resolveType(bounds[i]);
            if (newType == null || newType == bounds[i]) continue;
            typeVariable = typeVariable.copyType(i, newType);
        }
        return typeVariable;
    }

    private TypeVariable resolveType(String identifier) {
        TypeVariable ret = this.elementTypeParameters.get(identifier);
        return ret == null ? this.classTypeParameters.get(identifier) : ret;
    }

    private Type parseJavaType() {
        PrimitiveType type = PrimitiveType.decode(this.signature.charAt(this.pos));
        if (type != null) {
            ++this.pos;
            return type;
        }
        return this.parseReferenceType();
    }

    private int advancePast(char c) {
        int pos = this.signature.indexOf(c, this.pos);
        if (pos == -1) {
            throw new IllegalStateException("Corruption");
        }
        this.pos = pos + 1;
        return pos;
    }

    private int advanceNot(char c) {
        while (this.signature.charAt(this.pos) == c) {
            ++this.pos;
        }
        return this.pos;
    }

    private DotName parseName() {
        int start = this.pos;
        int end = this.advanceNameEnd();
        if (this.signature.charAt(start++) != 'L') {
            throw new IllegalArgumentException("Invalid signature, invalid class designator");
        }
        return this.names.convertToName(this.signature.substring(start, end), '/');
    }

    private int advanceNameEnd() {
        String signature = this.signature;
        for (int end = this.pos; end < signature.length(); ++end) {
            char c = signature.charAt(end);
            if (c != '.' && c != '<' && c != ';') continue;
            this.pos = end;
            return this.pos;
        }
        throw new IllegalStateException("Corrupted name");
    }

    public static void main(String[] args) throws IOException {
        GenericSignatureParser parser = new GenericSignatureParser(new NameTable());
        MethodSignature sig1 = parser.parseMethodSignature("<U:Ljava/lang/Foo;>(Ljava/lang/Class<TU;>;TU;)Ljava/lang/Class<+TU;>;");
    }

    static class MethodSignature {
        private final Type[] typeParameters;
        private final Type[] methodParameters;
        private final Type returnType;
        private final Type[] throwables;

        private MethodSignature(Type[] typeParameters, Type[] methodParameters, Type returnType, Type[] throwables) {
            this.typeParameters = typeParameters;
            this.methodParameters = methodParameters;
            this.returnType = returnType;
            this.throwables = throwables;
        }

        public Type[] typeParameters() {
            return this.typeParameters;
        }

        public Type returnType() {
            return this.returnType;
        }

        public Type[] methodParameters() {
            return this.methodParameters;
        }

        public Type[] throwables() {
            return this.throwables;
        }

        public String toString() {
            int i;
            StringBuilder builder = new StringBuilder();
            if (this.typeParameters.length > 0) {
                builder.append("<");
                builder.append(this.typeParameters[0]);
                for (i = 1; i < this.typeParameters.length; ++i) {
                    builder.append(", ").append(this.typeParameters[i]);
                }
                builder.append("> ");
            }
            builder.append(this.returnType).append(" (");
            if (this.methodParameters.length > 0) {
                builder.append(this.methodParameters[0]);
                for (i = 1; i < this.methodParameters.length; ++i) {
                    builder.append(", ").append(this.methodParameters[i]);
                }
            }
            builder.append(')');
            if (this.throwables.length > 0) {
                builder.append(" throws ").append(this.throwables[0]);
                for (i = 1; i < this.throwables.length; ++i) {
                    builder.append(", ").append(this.throwables[i]);
                }
            }
            return builder.toString();
        }
    }

    static class ClassSignature {
        private final Type[] parameters;
        private final Type superClass;
        private final Type[] interfaces;

        private ClassSignature(Type[] parameters, Type superClass, Type[] interfaces) {
            this.parameters = parameters;
            this.superClass = superClass;
            this.interfaces = interfaces;
        }

        Type[] parameters() {
            return this.parameters;
        }

        Type superClass() {
            return this.superClass;
        }

        Type[] interfaces() {
            return this.interfaces;
        }

        public String toString() {
            int i;
            StringBuilder builder = new StringBuilder();
            if (this.parameters.length > 0) {
                builder.append('<');
                builder.append(this.parameters[0]);
                for (i = 1; i < this.parameters.length; ++i) {
                    builder.append(", ").append(this.parameters[i]);
                }
                builder.append('>');
            }
            if (this.superClass.name() != DotName.OBJECT_NAME) {
                builder.append(" extends ").append(this.superClass);
            }
            if (this.interfaces.length > 0) {
                builder.append(" implements ").append(this.interfaces[0]);
                for (i = 1; i < this.interfaces.length; ++i) {
                    builder.append(", ").append(this.interfaces[i]);
                }
            }
            return builder.toString();
        }
    }
}

