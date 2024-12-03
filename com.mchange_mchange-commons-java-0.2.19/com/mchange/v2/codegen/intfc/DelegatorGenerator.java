/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.intfc;

import com.mchange.v1.lang.ClassUtils;
import com.mchange.v2.codegen.CodegenUtils;
import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.intfc.ReflectiveDelegationPolicy;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DelegatorGenerator {
    int class_modifiers = 1025;
    int method_modifiers = 1;
    int wrapping_ctor_modifiers = 1;
    int default_ctor_modifiers = 1;
    boolean wrapping_constructor = true;
    boolean default_constructor = true;
    boolean inner_getter = true;
    boolean inner_setter = true;
    Class superclass = null;
    Class[] extraInterfaces = null;
    Method[] reflectiveDelegateMethods = null;
    ReflectiveDelegationPolicy reflectiveDelegationPolicy = ReflectiveDelegationPolicy.USE_MAIN_DELEGATE_INTERFACE;
    static final Comparator classComp = new Comparator(){

        public int compare(Object object, Object object2) {
            return ((Class)object).getName().compareTo(((Class)object2).getName());
        }
    };

    public void setGenerateInnerSetter(boolean bl) {
        this.inner_setter = bl;
    }

    public boolean isGenerateInnerSetter() {
        return this.inner_setter;
    }

    public void setGenerateInnerGetter(boolean bl) {
        this.inner_getter = bl;
    }

    public boolean isGenerateInnerGetter() {
        return this.inner_getter;
    }

    public void setGenerateNoArgConstructor(boolean bl) {
        this.default_constructor = bl;
    }

    public boolean isGenerateNoArgConstructor() {
        return this.default_constructor;
    }

    public void setGenerateWrappingConstructor(boolean bl) {
        this.wrapping_constructor = bl;
    }

    public boolean isGenerateWrappingConstructor() {
        return this.wrapping_constructor;
    }

    public void setWrappingConstructorModifiers(int n) {
        this.wrapping_ctor_modifiers = n;
    }

    public int getWrappingConstructorModifiers() {
        return this.wrapping_ctor_modifiers;
    }

    public void setNoArgConstructorModifiers(int n) {
        this.default_ctor_modifiers = n;
    }

    public int getNoArgConstructorModifiers() {
        return this.default_ctor_modifiers;
    }

    public void setMethodModifiers(int n) {
        this.method_modifiers = n;
    }

    public int getMethodModifiers() {
        return this.method_modifiers;
    }

    public void setClassModifiers(int n) {
        this.class_modifiers = n;
    }

    public int getClassModifiers() {
        return this.class_modifiers;
    }

    public void setSuperclass(Class clazz) {
        this.superclass = clazz;
    }

    public Class getSuperclass() {
        return this.superclass;
    }

    public void setExtraInterfaces(Class[] classArray) {
        this.extraInterfaces = classArray;
    }

    public Class[] getExtraInterfaces() {
        return this.extraInterfaces;
    }

    public Method[] getReflectiveDelegateMethods() {
        return this.reflectiveDelegateMethods;
    }

    public void setReflectiveDelegateMethods(Method[] methodArray) {
        this.reflectiveDelegateMethods = methodArray;
    }

    public ReflectiveDelegationPolicy getReflectiveDelegationPolicy() {
        return this.reflectiveDelegationPolicy;
    }

    public void setReflectiveDelegationPolicy(ReflectiveDelegationPolicy reflectiveDelegationPolicy) {
        this.reflectiveDelegationPolicy = reflectiveDelegationPolicy;
    }

    public void writeDelegator(Class clazz, String string, Writer writer) throws IOException {
        int n;
        int n2;
        GenericDeclaration genericDeclaration2;
        IndentedWriter indentedWriter = CodegenUtils.toIndentedWriter(writer);
        String string2 = string.substring(0, string.lastIndexOf(46));
        String string3 = CodegenUtils.fqcnLastElement(string);
        String string4 = this.superclass != null ? ClassUtils.simpleClassName(this.superclass) : null;
        String string5 = ClassUtils.simpleClassName(clazz);
        String[] stringArray = null;
        if (this.extraInterfaces != null) {
            stringArray = new String[this.extraInterfaces.length];
            int n3 = this.extraInterfaces.length;
            for (int i = 0; i < n3; ++i) {
                stringArray[i] = ClassUtils.simpleClassName(this.extraInterfaces[i]);
            }
        }
        TreeSet<Class> treeSet = new TreeSet<Class>(classComp);
        Method[] methodArray = clazz.getMethods();
        if (!CodegenUtils.inSamePackage(clazz.getName(), string)) {
            treeSet.add(clazz);
        }
        if (this.superclass != null && !CodegenUtils.inSamePackage(this.superclass.getName(), string)) {
            treeSet.add(this.superclass);
        }
        if (this.extraInterfaces != null) {
            for (GenericDeclaration genericDeclaration2 : this.extraInterfaces) {
                if (CodegenUtils.inSamePackage(genericDeclaration2.getName(), string)) continue;
                treeSet.add((Class)genericDeclaration2);
            }
        }
        this.ensureImports(string, treeSet, methodArray);
        if (this.reflectiveDelegateMethods != null) {
            this.ensureImports(string, treeSet, this.reflectiveDelegateMethods);
        }
        if (this.reflectiveDelegationPolicy.delegateClass != null && !CodegenUtils.inSamePackage(this.reflectiveDelegationPolicy.delegateClass.getName(), string)) {
            treeSet.add(this.reflectiveDelegationPolicy.delegateClass);
        }
        this.generateBannerComment(indentedWriter);
        indentedWriter.println("package " + string2 + ';');
        indentedWriter.println();
        Iterator iterator = treeSet.iterator();
        while (iterator.hasNext()) {
            indentedWriter.println("import " + ((Class)iterator.next()).getName() + ';');
        }
        this.generateExtraImports(indentedWriter);
        indentedWriter.println();
        this.generateClassJavaDocComment(indentedWriter);
        indentedWriter.print(CodegenUtils.getModifierString(this.class_modifiers) + " class " + string3);
        if (this.superclass != null) {
            indentedWriter.print(" extends " + string4);
        }
        indentedWriter.print(" implements " + string5);
        if (stringArray != null) {
            n2 = stringArray.length;
            for (int i = 0; i < n2; ++i) {
                indentedWriter.print(", " + stringArray[i]);
            }
        }
        indentedWriter.println();
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("protected " + string5 + " inner;");
        indentedWriter.println();
        if (this.reflectiveDelegateMethods != null) {
            indentedWriter.println("protected Class __delegateClass = null;");
        }
        indentedWriter.println();
        indentedWriter.println("private void __setInner( " + string5 + " inner )");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("this.inner = inner;");
        if (this.reflectiveDelegateMethods != null) {
            String string6 = this.reflectiveDelegationPolicy == ReflectiveDelegationPolicy.USE_MAIN_DELEGATE_INTERFACE ? string5 + ".class" : (this.reflectiveDelegationPolicy == ReflectiveDelegationPolicy.USE_RUNTIME_CLASS ? "inner.getClass()" : ClassUtils.simpleClassName(this.reflectiveDelegationPolicy.delegateClass) + ".class");
            indentedWriter.println("this.__delegateClass = inner == null ? null : " + string6 + ";");
        }
        indentedWriter.downIndent();
        indentedWriter.println("}");
        indentedWriter.println();
        if (this.wrapping_constructor) {
            indentedWriter.println(CodegenUtils.getModifierString(this.wrapping_ctor_modifiers) + ' ' + string3 + '(' + string5 + " inner)");
            indentedWriter.println("{ __setInner( inner ); }");
        }
        if (this.default_constructor) {
            indentedWriter.println();
            indentedWriter.println(CodegenUtils.getModifierString(this.default_ctor_modifiers) + ' ' + string3 + "()");
            indentedWriter.println("{}");
        }
        if (this.inner_setter) {
            indentedWriter.println();
            indentedWriter.println(CodegenUtils.getModifierString(this.method_modifiers) + " void setInner( " + string5 + " inner )");
            indentedWriter.println("{ __setInner( inner ); }");
        }
        if (this.inner_getter) {
            indentedWriter.println();
            indentedWriter.println(CodegenUtils.getModifierString(this.method_modifiers) + ' ' + string5 + " getInner()");
            indentedWriter.println("{ return inner; }");
        }
        indentedWriter.println();
        n2 = methodArray.length;
        for (n = 0; n < n2; ++n) {
            genericDeclaration2 = methodArray[n];
            if (n != 0) {
                indentedWriter.println();
            }
            indentedWriter.println(CodegenUtils.methodSignature(this.method_modifiers, (Method)genericDeclaration2, null));
            indentedWriter.println("{");
            indentedWriter.upIndent();
            this.generatePreDelegateCode(clazz, string, (Method)genericDeclaration2, indentedWriter);
            this.generateDelegateCode(clazz, string, (Method)genericDeclaration2, indentedWriter);
            this.generatePostDelegateCode(clazz, string, (Method)genericDeclaration2, indentedWriter);
            indentedWriter.downIndent();
            indentedWriter.println("}");
        }
        if (this.reflectiveDelegateMethods != null) {
            indentedWriter.println("// Methods not in core interface to be delegated via reflection");
            n2 = this.reflectiveDelegateMethods.length;
            for (n = 0; n < n2; ++n) {
                genericDeclaration2 = this.reflectiveDelegateMethods[n];
                if (n != 0) {
                    indentedWriter.println();
                }
                indentedWriter.println(CodegenUtils.methodSignature(this.method_modifiers, (Method)genericDeclaration2, null));
                indentedWriter.println("{");
                indentedWriter.upIndent();
                this.generatePreDelegateCode(clazz, string, (Method)genericDeclaration2, indentedWriter);
                this.generateReflectiveDelegateCode(clazz, string, (Method)genericDeclaration2, indentedWriter);
                this.generatePostDelegateCode(clazz, string, (Method)genericDeclaration2, indentedWriter);
                indentedWriter.downIndent();
                indentedWriter.println("}");
            }
        }
        indentedWriter.println();
        this.generateExtraDeclarations(clazz, string, indentedWriter);
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }

    private void ensureImports(String string, Set set, Method[] methodArray) {
        int n = methodArray.length;
        for (int i = 0; i < n; ++i) {
            Class<?>[] classArray = methodArray[i].getParameterTypes();
            int n2 = classArray.length;
            for (int j = 0; j < n2; ++j) {
                if (CodegenUtils.inSamePackage(classArray[j].getName(), string)) continue;
                set.add(CodegenUtils.unarrayClass(classArray[j]));
            }
            Class<?>[] classArray2 = methodArray[i].getExceptionTypes();
            int n3 = classArray2.length;
            for (n2 = 0; n2 < n3; ++n2) {
                if (CodegenUtils.inSamePackage(classArray2[n2].getName(), string)) continue;
                set.add(CodegenUtils.unarrayClass(classArray2[n2]));
            }
            if (CodegenUtils.inSamePackage(methodArray[i].getReturnType().getName(), string)) continue;
            set.add(CodegenUtils.unarrayClass(methodArray[i].getReturnType()));
        }
    }

    protected void generateDelegateCode(Class clazz, String string, Method method, IndentedWriter indentedWriter) throws IOException {
        Class<?> clazz2 = method.getReturnType();
        indentedWriter.println((clazz2 == Void.TYPE ? "" : "return ") + "inner." + CodegenUtils.methodCall(method) + ";");
    }

    protected void generateReflectiveDelegateCode(Class clazz, String string, Method method, IndentedWriter indentedWriter) throws IOException {
        Class<?> clazz2 = method.getReturnType();
        String string2 = CodegenUtils.reflectiveMethodParameterTypeArray(method);
        String string3 = CodegenUtils.reflectiveMethodObjectArray(method);
        Class<?>[] classArray = method.getExceptionTypes();
        HashSet hashSet = new HashSet();
        hashSet.addAll(Arrays.asList(classArray));
        indentedWriter.println("try");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("Method m = __delegateClass.getMethod(\"" + method.getName() + "\", " + string2 + ");");
        indentedWriter.println((clazz2 == Void.TYPE ? "" : "return (" + ClassUtils.simpleClassName(clazz2) + ") ") + "m.invoke( inner, " + string3 + " );");
        indentedWriter.downIndent();
        indentedWriter.println("}");
        if (!hashSet.contains(IllegalAccessException.class)) {
            indentedWriter.println("catch (IllegalAccessException iae)");
            indentedWriter.println("{");
            indentedWriter.upIndent();
            indentedWriter.println("throw new RuntimeException(\"A reflectively delegated method '" + method.getName() + "' cannot access the object to which the call is delegated\", iae);");
            indentedWriter.downIndent();
            indentedWriter.println("}");
        }
        indentedWriter.println("catch (InvocationTargetException ite)");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        indentedWriter.println("Throwable cause = ite.getCause();");
        indentedWriter.println("if (cause instanceof RuntimeException) throw (RuntimeException) cause;");
        indentedWriter.println("if (cause instanceof Error) throw (Error) cause;");
        int n = classArray.length;
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                String string4 = ClassUtils.simpleClassName(classArray[i]);
                indentedWriter.println("if (cause instanceof " + string4 + ") throw (" + string4 + ") cause;");
            }
        }
        indentedWriter.println("throw new RuntimeException(\"Target of reflectively delegated method '" + method.getName() + "' threw an Exception.\", cause);");
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }

    protected void generateBannerComment(IndentedWriter indentedWriter) throws IOException {
        indentedWriter.println("/*");
        indentedWriter.println(" * This class generated by " + this.getClass().getName());
        indentedWriter.println(" * " + new Date());
        indentedWriter.println(" * DO NOT HAND EDIT!!!!");
        indentedWriter.println(" */");
    }

    protected void generateClassJavaDocComment(IndentedWriter indentedWriter) throws IOException {
        indentedWriter.println("/**");
        indentedWriter.println(" * This class was generated by " + this.getClass().getName() + ".");
        indentedWriter.println(" */");
    }

    protected void generateExtraImports(IndentedWriter indentedWriter) throws IOException {
    }

    protected void generatePreDelegateCode(Class clazz, String string, Method method, IndentedWriter indentedWriter) throws IOException {
    }

    protected void generatePostDelegateCode(Class clazz, String string, Method method, IndentedWriter indentedWriter) throws IOException {
    }

    protected void generateExtraDeclarations(Class clazz, String string, IndentedWriter indentedWriter) throws IOException {
    }
}

