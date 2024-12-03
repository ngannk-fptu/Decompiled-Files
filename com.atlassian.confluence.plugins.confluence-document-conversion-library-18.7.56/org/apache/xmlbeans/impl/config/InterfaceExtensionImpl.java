/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.javaparser.ast.Node
 *  com.github.javaparser.ast.NodeList
 *  com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
 *  com.github.javaparser.ast.body.MethodDeclaration
 *  com.github.javaparser.ast.body.Parameter
 *  com.github.javaparser.ast.type.Type
 *  com.github.javaparser.ast.type.TypeParameter
 *  com.github.javaparser.resolution.MethodUsage
 *  com.github.javaparser.resolution.types.ResolvedType
 */
package org.apache.xmlbeans.impl.config;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.types.ResolvedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.config.BindingConfigImpl;
import org.apache.xmlbeans.impl.config.NameSet;
import org.apache.xmlbeans.impl.config.Parser;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

public class InterfaceExtensionImpl
implements InterfaceExtension {
    private NameSet _xbeanSet;
    private String _interfaceClassName;
    private String _delegateToClassName;
    private MethodSignatureImpl[] _methods;

    static InterfaceExtensionImpl newInstance(Parser loader, NameSet xbeanSet, Extensionconfig.Interface intfXO) {
        InterfaceExtensionImpl result = new InterfaceExtensionImpl();
        result._xbeanSet = xbeanSet;
        ClassOrInterfaceDeclaration interfaceJClass = InterfaceExtensionImpl.validateInterface(loader, intfXO.getName(), intfXO);
        if (interfaceJClass == null) {
            BindingConfigImpl.error("Interface '" + intfXO.getStaticHandler() + "' not found.", intfXO);
            return null;
        }
        result._interfaceClassName = (String)interfaceJClass.getFullyQualifiedName().get();
        result._delegateToClassName = intfXO.getStaticHandler();
        ClassOrInterfaceDeclaration delegateJClass = InterfaceExtensionImpl.validateClass(loader, result._delegateToClassName, intfXO);
        if (delegateJClass == null) {
            BindingConfigImpl.warning("Handler class '" + intfXO.getStaticHandler() + "' not found on classpath, skip validation.", intfXO);
            return result;
        }
        if (!result.validateMethods(loader, interfaceJClass, delegateJClass, intfXO)) {
            return null;
        }
        return result;
    }

    private static ClassOrInterfaceDeclaration validateInterface(Parser loader, String intfStr, XmlObject loc) {
        return InterfaceExtensionImpl.validateJava(loader, intfStr, true, loc);
    }

    static ClassOrInterfaceDeclaration validateClass(Parser loader, String clsStr, XmlObject loc) {
        return InterfaceExtensionImpl.validateJava(loader, clsStr, false, loc);
    }

    static ClassOrInterfaceDeclaration validateJava(Parser loader, String clsStr, boolean isInterface, XmlObject loc) {
        if (loader == null) {
            return null;
        }
        String ent = isInterface ? "Interface" : "Class";
        ClassOrInterfaceDeclaration cls = loader.loadSource(clsStr);
        if (cls == null) {
            BindingConfigImpl.error(ent + " '" + clsStr + "' not found.", loc);
            return null;
        }
        if (isInterface != cls.isInterface()) {
            BindingConfigImpl.error("'" + clsStr + "' must be " + (isInterface ? "an interface" : "a class") + ".", loc);
        }
        if (!cls.isPublic()) {
            BindingConfigImpl.error(ent + " '" + clsStr + "' is not public.", loc);
        }
        return cls;
    }

    private boolean validateMethods(Parser loader, ClassOrInterfaceDeclaration interfaceJClass, ClassOrInterfaceDeclaration delegateJClass, XmlObject loc) {
        this._methods = (MethodSignatureImpl[])interfaceJClass.resolve().getAllMethods().stream().filter(m -> !Object.class.getName().equals(m.declaringType().getQualifiedName())).map(m -> this.validateMethod(interfaceJClass, delegateJClass, (MethodUsage)m, loc)).map(m -> m == null ? null : new MethodSignatureImpl(this.getStaticHandler(), (MethodDeclaration)m)).toArray(MethodSignatureImpl[]::new);
        return Stream.of(this._methods).allMatch(Objects::nonNull);
    }

    private MethodDeclaration validateMethod(ClassOrInterfaceDeclaration interfaceJClass, ClassOrInterfaceDeclaration delegateJClass, MethodUsage ifMethod, XmlObject loc) {
        Object[] delEx;
        String methodName = ifMethod.getName();
        MethodDeclaration delMethod = delegateJClass.getMethodsByName(methodName).stream().filter(mDel -> InterfaceExtensionImpl.matchParams(ifMethod, mDel)).findFirst().orElse(null);
        String delegateFQN = delegateJClass.getFullyQualifiedName().orElse("");
        String methodFQN = methodName + "(" + ifMethod.getParamTypes().toString() + ")";
        String interfaceFQN = interfaceJClass.getFullyQualifiedName().orElse("");
        if (delMethod == null) {
            BindingConfigImpl.error("Handler class '" + delegateFQN + "' does not contain method " + methodFQN, loc);
            return null;
        }
        Object[] ifEx = (String[])ifMethod.getDeclaration().getSpecifiedExceptions().stream().map(ResolvedType::describe).sorted().toArray(String[]::new);
        if (!Arrays.equals(ifEx, delEx = (String[])delMethod.getThrownExceptions().stream().map(Type::resolve).map(ResolvedType::describe).sorted().toArray(String[]::new))) {
            BindingConfigImpl.error("Handler method '" + delegateFQN + "." + methodName + "' must declare the same exceptions as the interface method '" + interfaceFQN + "." + methodFQN, loc);
            return null;
        }
        if (!delMethod.isPublic() || !delMethod.isStatic()) {
            BindingConfigImpl.error("Method '" + delegateFQN + "." + methodFQN + "' must be declared public and static.", loc);
            return null;
        }
        if (!ifMethod.getDeclaration().getReturnType().equals(delMethod.resolve().getReturnType())) {
            String returnType = ifMethod.getDeclaration().getReturnType().describe();
            BindingConfigImpl.error("Return type for method '" + returnType + " " + delegateFQN + "." + methodName + "(...)' does not match the return type of the interface method :'" + returnType + "'.", loc);
            return null;
        }
        return delMethod;
    }

    static MethodDeclaration getMethod(ClassOrInterfaceDeclaration cls, String name, String[] paramTypes) {
        return cls.getMethodsByName(name).stream().filter(m -> InterfaceExtensionImpl.parameterMatches(InterfaceExtensionImpl.paramStrings(m.getParameters()), paramTypes)).findFirst().orElse(null);
    }

    private static String[] paramStrings(NodeList<?> params) {
        return (String[])params.stream().map(p -> {
            if (p instanceof Parameter) {
                return ((Parameter)p).getType().resolve().describe();
            }
            if (p instanceof TypeParameter) {
                return ((TypeParameter)p).getNameAsString();
            }
            return "unknown";
        }).toArray(String[]::new);
    }

    private static boolean matchParams(MethodUsage mIf, MethodDeclaration mDel) {
        List pIf = mIf.getParamTypes();
        NodeList pDel = mDel.getParameters();
        if (pDel.size() != pIf.size() + 1) {
            return false;
        }
        if (!XmlObject.class.getName().equals(((Parameter)pDel.get(0)).resolve().describeType())) {
            return false;
        }
        int idx = 1;
        for (ResolvedType rt : pIf) {
            if (rt.describe().equals(((Parameter)pDel.get(idx++)).resolve().describeType())) continue;
            return false;
        }
        return true;
    }

    private static boolean parameterMatches(String[] params1, String[] params2) {
        if (params1.length != params2.length) {
            return false;
        }
        for (int i = 0; i < params1.length; ++i) {
            String p1 = params1[i];
            String p2 = params2[i];
            if (p1.contains(".")) {
                String tmp = p1;
                p1 = p2;
                p2 = tmp;
            }
            if (p2.endsWith(p1)) continue;
            return false;
        }
        return true;
    }

    public boolean contains(String fullJavaName) {
        return this._xbeanSet.contains(fullJavaName);
    }

    @Override
    public String getStaticHandler() {
        return this._delegateToClassName;
    }

    @Override
    public String getInterface() {
        return this._interfaceClassName;
    }

    @Override
    public InterfaceExtension.MethodSignature[] getMethods() {
        return this._methods;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("  static handler: ").append(this._delegateToClassName).append("\n");
        buf.append("  interface: ").append(this._interfaceClassName).append("\n");
        buf.append("  name set: ").append(this._xbeanSet).append("\n");
        for (int i = 0; i < this._methods.length; ++i) {
            buf.append("  method[").append(i).append("]=").append(this._methods[i]).append("\n");
        }
        return buf.toString();
    }

    static class MethodSignatureImpl
    implements InterfaceExtension.MethodSignature {
        private final String _intfName;
        private final int NOTINITIALIZED = -1;
        private int _hashCode = -1;
        private String _signature;
        private final String _name;
        private final String _return;
        private final String[] _params;
        private final String[] _paramNames;
        private final String[] _exceptions;

        MethodSignatureImpl(String intfName, MethodDeclaration method) {
            if (intfName == null || method == null) {
                throw new IllegalArgumentException("Interface: " + intfName + " method: " + method);
            }
            this._intfName = intfName;
            this._signature = null;
            this._name = method.getName().asString();
            String typeParams = method.getTypeParameters().stream().map(Node::toString).collect(Collectors.joining(", "));
            this._return = (typeParams.length() == 0 ? "" : " <" + typeParams + "> ") + MethodSignatureImpl.replaceInner(method.getType().resolve().describe());
            this._params = (String[])method.getParameters().stream().map(p -> p.getType().resolve().describe()).map(MethodSignatureImpl::replaceInner).toArray(String[]::new);
            this._exceptions = (String[])method.getThrownExceptions().stream().map(e -> e.resolve().describe()).map(MethodSignatureImpl::replaceInner).toArray(String[]::new);
            this._paramNames = (String[])method.getParameters().stream().map(p -> p.getNameAsString()).toArray(String[]::new);
        }

        private static String replaceInner(String classname) {
            return classname.replace('$', '.');
        }

        String getInterfaceName() {
            return this._intfName;
        }

        @Override
        public String getName() {
            return this._name;
        }

        @Override
        public String getReturnType() {
            return this._return;
        }

        @Override
        public String[] getParameterTypes() {
            return this._params;
        }

        @Override
        public String[] getParameterNames() {
            return this._paramNames;
        }

        @Override
        public String[] getExceptionTypes() {
            return this._exceptions;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MethodSignatureImpl)) {
                return false;
            }
            MethodSignatureImpl ms = (MethodSignatureImpl)o;
            return ms.getName().equals(this.getName()) && this._intfName.equals(ms._intfName) && Arrays.equals(this.getParameterTypes(), ms.getParameterTypes());
        }

        public int hashCode() {
            return this._hashCode != -1 ? this._hashCode : (this._hashCode = Objects.hash(this.getName(), Arrays.hashCode(this.getParameterTypes()), this._intfName));
        }

        String getSignature() {
            return this._signature != null ? this._signature : (this._signature = this._name + "(" + String.join((CharSequence)" ,", this._params) + ")");
        }

        public String toString() {
            return this.getReturnType() + " " + this.getSignature();
        }
    }
}

