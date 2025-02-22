/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import org.aspectj.util.GenericSignature;
import org.aspectj.util.GenericSignatureParser;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeFactory;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableDeclaringElement;
import org.aspectj.weaver.UnresolvedTypeVariableReferenceType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.tools.Traceable;

public class UnresolvedType
implements Traceable,
TypeVariableDeclaringElement {
    public static final UnresolvedType[] NONE = new UnresolvedType[0];
    public static final UnresolvedType OBJECT = UnresolvedType.forSignature("Ljava/lang/Object;");
    public static final UnresolvedType OBJECTARRAY = UnresolvedType.forSignature("[Ljava/lang/Object;");
    public static final UnresolvedType CLONEABLE = UnresolvedType.forSignature("Ljava/lang/Cloneable;");
    public static final UnresolvedType SERIALIZABLE = UnresolvedType.forSignature("Ljava/io/Serializable;");
    public static final UnresolvedType THROWABLE = UnresolvedType.forSignature("Ljava/lang/Throwable;");
    public static final UnresolvedType RUNTIME_EXCEPTION = UnresolvedType.forSignature("Ljava/lang/RuntimeException;");
    public static final UnresolvedType ERROR = UnresolvedType.forSignature("Ljava/lang/Error;");
    public static final UnresolvedType AT_INHERITED = UnresolvedType.forSignature("Ljava/lang/annotation/Inherited;");
    public static final UnresolvedType AT_RETENTION = UnresolvedType.forSignature("Ljava/lang/annotation/Retention;");
    public static final UnresolvedType ENUM = UnresolvedType.forSignature("Ljava/lang/Enum;");
    public static final UnresolvedType ANNOTATION = UnresolvedType.forSignature("Ljava/lang/annotation/Annotation;");
    public static final UnresolvedType JL_CLASS = UnresolvedType.forSignature("Ljava/lang/Class;");
    public static final UnresolvedType JAVA_LANG_CLASS_ARRAY = UnresolvedType.forSignature("[Ljava/lang/Class;");
    public static final UnresolvedType JL_STRING = UnresolvedType.forSignature("Ljava/lang/String;");
    public static final UnresolvedType JL_EXCEPTION = UnresolvedType.forSignature("Ljava/lang/Exception;");
    public static final UnresolvedType JAVA_LANG_REFLECT_METHOD = UnresolvedType.forSignature("Ljava/lang/reflect/Method;");
    public static final UnresolvedType JAVA_LANG_REFLECT_FIELD = UnresolvedType.forSignature("Ljava/lang/reflect/Field;");
    public static final UnresolvedType JAVA_LANG_REFLECT_CONSTRUCTOR = UnresolvedType.forSignature("Ljava/lang/reflect/Constructor;");
    public static final UnresolvedType JAVA_LANG_ANNOTATION = UnresolvedType.forSignature("Ljava/lang/annotation/Annotation;");
    public static final UnresolvedType SUPPRESS_AJ_WARNINGS = UnresolvedType.forSignature("Lorg/aspectj/lang/annotation/SuppressAjWarnings;");
    public static final UnresolvedType AT_TARGET = UnresolvedType.forSignature("Ljava/lang/annotation/Target;");
    public static final UnresolvedType SOMETHING = new UnresolvedType("?");
    public static final UnresolvedType[] ARRAY_WITH_JUST_OBJECT = new UnresolvedType[]{OBJECT};
    public static final UnresolvedType JOINPOINT_STATICPART = UnresolvedType.forSignature("Lorg/aspectj/lang/JoinPoint$StaticPart;");
    public static final UnresolvedType JOINPOINT_ENCLOSINGSTATICPART = UnresolvedType.forSignature("Lorg/aspectj/lang/JoinPoint$EnclosingStaticPart;");
    public static final UnresolvedType AJC_PRIVILEGED = UnresolvedType.forSignature("Lorg/aspectj/internal/lang/annotation/ajcPrivileged;");
    public static final UnresolvedType PROCEEDING_JOINPOINT = UnresolvedType.forSignature("Lorg/aspectj/lang/ProceedingJoinPoint;");
    public static final UnresolvedType BOOLEAN = UnresolvedType.forPrimitiveType("Z");
    public static final UnresolvedType BYTE = UnresolvedType.forPrimitiveType("B");
    public static final UnresolvedType CHAR = UnresolvedType.forPrimitiveType("C");
    public static final UnresolvedType DOUBLE = UnresolvedType.forPrimitiveType("D");
    public static final UnresolvedType FLOAT = UnresolvedType.forPrimitiveType("F");
    public static final UnresolvedType INT = UnresolvedType.forPrimitiveType("I");
    public static final UnresolvedType LONG = UnresolvedType.forPrimitiveType("J");
    public static final UnresolvedType SHORT = UnresolvedType.forPrimitiveType("S");
    public static final UnresolvedType VOID = UnresolvedType.forPrimitiveType("V");
    public static final String MISSING_NAME = "@missing@";
    protected TypeKind typeKind = TypeKind.SIMPLE;
    protected String signature;
    protected String signatureErasure;
    private String packageName;
    private String className;
    protected UnresolvedType[] typeParameters;
    protected TypeVariable[] typeVariables;
    private int size = 1;
    private boolean needsModifiableDelegate = false;

    public boolean isPrimitiveType() {
        return this.typeKind == TypeKind.PRIMITIVE;
    }

    public boolean isVoid() {
        return this.signature.equals("V");
    }

    public boolean isSimpleType() {
        return this.typeKind == TypeKind.SIMPLE;
    }

    public boolean isRawType() {
        return this.typeKind == TypeKind.RAW;
    }

    public boolean isGenericType() {
        return this.typeKind == TypeKind.GENERIC;
    }

    public boolean isParameterizedType() {
        return this.typeKind == TypeKind.PARAMETERIZED;
    }

    public boolean isParameterizedOrGenericType() {
        return this.typeKind == TypeKind.GENERIC || this.typeKind == TypeKind.PARAMETERIZED;
    }

    public boolean isParameterizedOrRawType() {
        return this.typeKind == TypeKind.PARAMETERIZED || this.typeKind == TypeKind.RAW;
    }

    public boolean isTypeVariableReference() {
        return this.typeKind == TypeKind.TYPE_VARIABLE;
    }

    public boolean isGenericWildcard() {
        return this.typeKind == TypeKind.WILDCARD;
    }

    public TypeKind getTypekind() {
        return this.typeKind;
    }

    public final boolean isArray() {
        return this.signature.length() > 0 && this.signature.charAt(0) == '[';
    }

    public boolean equals(Object other) {
        if (!(other instanceof UnresolvedType)) {
            return false;
        }
        return this.signature.equals(((UnresolvedType)other).signature);
    }

    public int hashCode() {
        return this.signature.hashCode();
    }

    protected UnresolvedType(String signature) {
        this.signature = signature;
        this.signatureErasure = signature;
    }

    protected UnresolvedType(String signature, String signatureErasure) {
        this.signature = signature;
        this.signatureErasure = signatureErasure;
    }

    public UnresolvedType(String signature, String signatureErasure, UnresolvedType[] typeParams) {
        this.signature = signature;
        this.signatureErasure = signatureErasure;
        this.typeParameters = typeParams;
        if (typeParams != null) {
            this.typeKind = TypeKind.PARAMETERIZED;
        }
    }

    public int getSize() {
        return this.size;
    }

    public static UnresolvedType forName(String name) {
        return UnresolvedType.forSignature(UnresolvedType.nameToSignature(name));
    }

    public static UnresolvedType[] forNames(String[] names) {
        UnresolvedType[] ret = new UnresolvedType[names.length];
        int len = names.length;
        for (int i = 0; i < len; ++i) {
            ret[i] = UnresolvedType.forName(names[i]);
        }
        return ret;
    }

    public static UnresolvedType forGenericType(String name, TypeVariable[] tvbs, String genericSig) {
        String sig = UnresolvedType.nameToSignature(name);
        UnresolvedType ret = UnresolvedType.forSignature(sig);
        ret.typeKind = TypeKind.GENERIC;
        ret.typeVariables = tvbs;
        ret.signatureErasure = sig;
        return ret;
    }

    public static UnresolvedType forGenericTypeSignature(String sig, String declaredGenericSig) {
        UnresolvedType ret = UnresolvedType.forSignature(sig);
        ret.typeKind = TypeKind.GENERIC;
        GenericSignature.ClassSignature csig = new GenericSignatureParser().parseAsClassSignature(declaredGenericSig);
        GenericSignature.FormalTypeParameter[] ftps = csig.formalTypeParameters;
        ret.typeVariables = new TypeVariable[ftps.length];
        for (int i = 0; i < ftps.length; ++i) {
            GenericSignature.FormalTypeParameter parameter = ftps[i];
            if (parameter.classBound instanceof GenericSignature.ClassTypeSignature) {
                GenericSignature.ClassTypeSignature cts = (GenericSignature.ClassTypeSignature)parameter.classBound;
                ret.typeVariables[i] = new TypeVariable(ftps[i].identifier, UnresolvedType.forSignature(cts.outerType.identifier + ";"));
                continue;
            }
            if (parameter.classBound instanceof GenericSignature.TypeVariableSignature) {
                GenericSignature.TypeVariableSignature tvs = (GenericSignature.TypeVariableSignature)parameter.classBound;
                UnresolvedTypeVariableReferenceType utvrt = new UnresolvedTypeVariableReferenceType(new TypeVariable(tvs.typeVariableName));
                ret.typeVariables[i] = new TypeVariable(ftps[i].identifier, utvrt);
                continue;
            }
            throw new BCException("UnresolvedType.forGenericTypeSignature(): Do not know how to process type variable bound of type '" + parameter.classBound.getClass() + "'.  Full signature is '" + sig + "'");
        }
        ret.signature = ret.signatureErasure = sig;
        return ret;
    }

    public static UnresolvedType forGenericTypeVariables(String sig, TypeVariable[] tVars) {
        UnresolvedType ret = UnresolvedType.forSignature(sig);
        ret.typeKind = TypeKind.GENERIC;
        ret.typeVariables = tVars;
        ret.signature = ret.signatureErasure = sig;
        return ret;
    }

    public static UnresolvedType forRawTypeName(String name) {
        UnresolvedType ret = UnresolvedType.forName(name);
        ret.typeKind = TypeKind.RAW;
        return ret;
    }

    public static UnresolvedType forPrimitiveType(String signature) {
        UnresolvedType ret = new UnresolvedType(signature);
        ret.typeKind = TypeKind.PRIMITIVE;
        if (signature.equals("J") || signature.equals("D")) {
            ret.size = 2;
        } else if (signature.equals("V")) {
            ret.size = 0;
        }
        return ret;
    }

    public static UnresolvedType[] add(UnresolvedType[] types, UnresolvedType end) {
        int len = types.length;
        UnresolvedType[] ret = new UnresolvedType[len + 1];
        System.arraycopy(types, 0, ret, 0, len);
        ret[len] = end;
        return ret;
    }

    public static UnresolvedType[] insert(UnresolvedType start, UnresolvedType[] types) {
        int len = types.length;
        UnresolvedType[] ret = new UnresolvedType[len + 1];
        ret[0] = start;
        System.arraycopy(types, 0, ret, 1, len);
        return ret;
    }

    public static UnresolvedType forSignature(String signature) {
        assert (!signature.startsWith("L") || signature.indexOf("<") == -1);
        switch (signature.charAt(0)) {
            case 'B': {
                return BYTE;
            }
            case 'C': {
                return CHAR;
            }
            case 'D': {
                return DOUBLE;
            }
            case 'F': {
                return FLOAT;
            }
            case 'I': {
                return INT;
            }
            case 'J': {
                return LONG;
            }
            case 'L': {
                return TypeFactory.createTypeFromSignature(signature);
            }
            case 'P': {
                return TypeFactory.createTypeFromSignature(signature);
            }
            case 'S': {
                return SHORT;
            }
            case 'V': {
                return VOID;
            }
            case 'Z': {
                return BOOLEAN;
            }
            case '[': {
                return TypeFactory.createTypeFromSignature(signature);
            }
            case '+': {
                return TypeFactory.createTypeFromSignature(signature);
            }
            case '-': {
                return TypeFactory.createTypeFromSignature(signature);
            }
            case '?': {
                return TypeFactory.createTypeFromSignature(signature);
            }
            case 'T': {
                return TypeFactory.createTypeFromSignature(signature);
            }
        }
        throw new BCException("Bad type signature " + signature);
    }

    public static UnresolvedType[] forSignatures(String[] sigs) {
        UnresolvedType[] ret = new UnresolvedType[sigs.length];
        int len = sigs.length;
        for (int i = 0; i < len; ++i) {
            ret[i] = UnresolvedType.forSignature(sigs[i]);
        }
        return ret;
    }

    public String getName() {
        return UnresolvedType.signatureToName(this.signature);
    }

    public String getSimpleName() {
        String name = this.getRawName();
        int lastDot = name.lastIndexOf(46);
        if (lastDot != -1) {
            name = name.substring(lastDot + 1);
        }
        if (this.isParameterizedType()) {
            StringBuffer sb = new StringBuffer(name);
            sb.append("<");
            for (int i = 0; i < this.typeParameters.length - 1; ++i) {
                sb.append(this.typeParameters[i].getSimpleName());
                sb.append(",");
            }
            sb.append(this.typeParameters[this.typeParameters.length - 1].getSimpleName());
            sb.append(">");
            name = sb.toString();
        }
        return name;
    }

    public String getRawName() {
        return UnresolvedType.signatureToName(this.signatureErasure == null ? this.signature : this.signatureErasure);
    }

    public String getBaseName() {
        String name = this.getName();
        if (this.isParameterizedType() || this.isGenericType()) {
            if (this.typeParameters == null) {
                return name;
            }
            return name.substring(0, name.indexOf("<"));
        }
        return name;
    }

    public String getSimpleBaseName() {
        String name = this.getBaseName();
        int lastDot = name.lastIndexOf(46);
        if (lastDot != -1) {
            name = name.substring(lastDot + 1);
        }
        return name;
    }

    public static String[] getNames(UnresolvedType[] types) {
        String[] ret = new String[types.length];
        int len = types.length;
        for (int i = 0; i < len; ++i) {
            ret[i] = types[i].getName();
        }
        return ret;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getErasureSignature() {
        if (this.signatureErasure == null) {
            return this.signature;
        }
        return this.signatureErasure;
    }

    public boolean needsModifiableDelegate() {
        return this.needsModifiableDelegate;
    }

    public void setNeedsModifiableDelegate(boolean b) {
        this.needsModifiableDelegate = b;
    }

    public UnresolvedType getRawType() {
        return UnresolvedType.forSignature(this.getErasureSignature());
    }

    public UnresolvedType getOutermostType() {
        if (this.isArray() || this.isPrimitiveType()) {
            return this;
        }
        String sig = this.getErasureSignature();
        int dollar = sig.indexOf(36);
        if (dollar != -1) {
            return UnresolvedType.forSignature(sig.substring(0, dollar) + ';');
        }
        return this;
    }

    public UnresolvedType getComponentType() {
        if (this.isArray()) {
            return UnresolvedType.forSignature(this.signature.substring(1));
        }
        return null;
    }

    public String toString() {
        return this.getName();
    }

    public String toDebugString() {
        return this.getName();
    }

    public ResolvedType resolve(World world) {
        return world.resolve(this);
    }

    private static String signatureToName(String signature) {
        switch (signature.charAt(0)) {
            case 'B': {
                return "byte";
            }
            case 'C': {
                return "char";
            }
            case 'D': {
                return "double";
            }
            case 'F': {
                return "float";
            }
            case 'I': {
                return "int";
            }
            case 'J': {
                return "long";
            }
            case 'L': {
                String name = signature.substring(1, signature.length() - 1).replace('/', '.');
                return name;
            }
            case 'T': {
                StringBuffer nameBuff2 = new StringBuffer();
                int colon = signature.indexOf(";");
                String tvarName = signature.substring(1, colon);
                nameBuff2.append(tvarName);
                return nameBuff2.toString();
            }
            case 'P': {
                StringBuffer nameBuff = new StringBuffer();
                int paramNestLevel = 0;
                block23: for (int i = 1; i < signature.length(); ++i) {
                    char c = signature.charAt(i);
                    switch (c) {
                        case '/': {
                            nameBuff.append('.');
                            continue block23;
                        }
                        case '<': {
                            nameBuff.append("<");
                            ++paramNestLevel;
                            StringBuffer innerBuff = new StringBuffer();
                            while (paramNestLevel > 0) {
                                if ((c = signature.charAt(++i)) == '<') {
                                    ++paramNestLevel;
                                }
                                if (c == '>') {
                                    --paramNestLevel;
                                }
                                if (paramNestLevel > 0) {
                                    innerBuff.append(c);
                                }
                                if (c != ';' || paramNestLevel != 1) continue;
                                nameBuff.append(UnresolvedType.signatureToName(innerBuff.toString()));
                                if (signature.charAt(i + 1) != '>') {
                                    nameBuff.append(',');
                                }
                                innerBuff = new StringBuffer();
                            }
                            nameBuff.append(">");
                            continue block23;
                        }
                        case ';': {
                            continue block23;
                        }
                        default: {
                            nameBuff.append(c);
                        }
                    }
                }
                return nameBuff.toString();
            }
            case 'S': {
                return "short";
            }
            case 'V': {
                return "void";
            }
            case 'Z': {
                return "boolean";
            }
            case '[': {
                return UnresolvedType.signatureToName(signature.substring(1, signature.length())) + "[]";
            }
            case '+': {
                return "? extends " + UnresolvedType.signatureToName(signature.substring(1, signature.length()));
            }
            case '-': {
                return "? super " + UnresolvedType.signatureToName(signature.substring(1, signature.length()));
            }
            case '*': {
                return "?";
            }
        }
        throw new BCException("Bad type signature: " + signature);
    }

    private static String nameToSignature(String name) {
        int len = name.length();
        if (len < 8) {
            if (name.equals("int")) {
                return "I";
            }
            if (name.equals("void")) {
                return "V";
            }
            if (name.equals("long")) {
                return "J";
            }
            if (name.equals("boolean")) {
                return "Z";
            }
            if (name.equals("double")) {
                return "D";
            }
            if (name.equals("float")) {
                return "F";
            }
            if (name.equals("byte")) {
                return "B";
            }
            if (name.equals("short")) {
                return "S";
            }
            if (name.equals("char")) {
                return "C";
            }
            if (name.equals("?")) {
                return name;
            }
        }
        if (len == 0) {
            throw new BCException("Bad type name: " + name);
        }
        if (name.endsWith("[]")) {
            return "[" + UnresolvedType.nameToSignature(name.substring(0, name.length() - 2));
        }
        if (name.charAt(0) == '[') {
            return name.replace('.', '/');
        }
        if (name.indexOf("<") == -1) {
            return "L" + name.replace('.', '/') + ';';
        }
        StringBuffer nameBuff = new StringBuffer();
        int nestLevel = 0;
        nameBuff.append("P");
        block4: for (int i = 0; i < len; ++i) {
            char c = name.charAt(i);
            switch (c) {
                case '.': {
                    nameBuff.append('/');
                    continue block4;
                }
                case '<': {
                    nameBuff.append("<");
                    ++nestLevel;
                    StringBuffer innerBuff = new StringBuffer();
                    while (nestLevel > 0) {
                        if ((c = name.charAt(++i)) == '<') {
                            ++nestLevel;
                        } else if (c == '>') {
                            --nestLevel;
                        }
                        if (c == ',' && nestLevel == 1) {
                            nameBuff.append(UnresolvedType.nameToSignature(innerBuff.toString()));
                            innerBuff = new StringBuffer();
                            continue;
                        }
                        if (nestLevel <= 0) continue;
                        innerBuff.append(c);
                    }
                    nameBuff.append(UnresolvedType.nameToSignature(innerBuff.toString()));
                    nameBuff.append('>');
                    continue block4;
                }
                default: {
                    nameBuff.append(c);
                }
            }
        }
        nameBuff.append(";");
        return nameBuff.toString();
    }

    public final void write(CompressingDataOutputStream s) throws IOException {
        s.writeUTF(this.getSignature());
    }

    public static UnresolvedType read(DataInputStream s) throws IOException {
        String sig = s.readUTF();
        if (sig.equals(MISSING_NAME)) {
            return ResolvedType.MISSING;
        }
        return UnresolvedType.forSignature(sig);
    }

    public String getNameAsIdentifier() {
        return this.getName().replace('.', '_');
    }

    public String getPackageNameAsIdentifier() {
        String name = this.getName();
        int index = name.lastIndexOf(46);
        if (index == -1) {
            return "";
        }
        return name.substring(0, index).replace('.', '_');
    }

    public UnresolvedType[] getTypeParameters() {
        return this.typeParameters == null ? NONE : this.typeParameters;
    }

    public TypeVariable[] getTypeVariables() {
        return this.typeVariables;
    }

    @Override
    public TypeVariable getTypeVariableNamed(String name) {
        TypeVariable[] vars = this.getTypeVariables();
        if (vars == null || vars.length == 0) {
            return null;
        }
        for (int i = 0; i < vars.length; ++i) {
            TypeVariable aVar = vars[i];
            if (!aVar.getName().equals(name)) continue;
            return aVar;
        }
        return null;
    }

    @Override
    public String toTraceString() {
        return this.getClass().getName() + "[" + this.getName() + "]";
    }

    public UnresolvedType parameterize(Map<String, UnresolvedType> typeBindings) {
        throw new UnsupportedOperationException("unable to parameterize unresolved type: " + this.signature);
    }

    public String getClassName() {
        if (this.className == null) {
            int index;
            String name = this.getName();
            if (name.indexOf("<") != -1) {
                name = name.substring(0, name.indexOf("<"));
            }
            this.className = (index = name.lastIndexOf(46)) == -1 ? name : name.substring(index + 1);
        }
        return this.className;
    }

    public String getPackageName() {
        if (this.packageName == null) {
            int index;
            String name = this.getName();
            int angly = name.indexOf(60);
            if (angly != -1) {
                name = name.substring(0, angly);
            }
            this.packageName = (index = name.lastIndexOf(46)) == -1 ? "" : name.substring(0, index);
        }
        return this.packageName;
    }

    public static void writeArray(UnresolvedType[] types, CompressingDataOutputStream stream) throws IOException {
        int len = types.length;
        stream.writeShort(len);
        for (UnresolvedType type : types) {
            type.write(stream);
        }
    }

    public static UnresolvedType[] readArray(DataInputStream s) throws IOException {
        int len = s.readShort();
        if (len == 0) {
            return NONE;
        }
        UnresolvedType[] types = new UnresolvedType[len];
        for (int i = 0; i < len; ++i) {
            types[i] = UnresolvedType.read(s);
        }
        return types;
    }

    public static UnresolvedType makeArray(UnresolvedType base, int dims) {
        StringBuffer sig = new StringBuffer();
        for (int i = 0; i < dims; ++i) {
            sig.append("[");
        }
        sig.append(base.getSignature());
        return UnresolvedType.forSignature(sig.toString());
    }

    public static class TypeKind {
        public static final TypeKind PRIMITIVE = new TypeKind("primitive");
        public static final TypeKind SIMPLE = new TypeKind("simple");
        public static final TypeKind RAW = new TypeKind("raw");
        public static final TypeKind GENERIC = new TypeKind("generic");
        public static final TypeKind PARAMETERIZED = new TypeKind("parameterized");
        public static final TypeKind TYPE_VARIABLE = new TypeKind("type_variable");
        public static final TypeKind WILDCARD = new TypeKind("wildcard");
        private final String type;

        public String toString() {
            return this.type;
        }

        private TypeKind(String type) {
            this.type = type;
        }
    }
}

