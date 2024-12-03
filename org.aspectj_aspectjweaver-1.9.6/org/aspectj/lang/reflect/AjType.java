/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.DeclareParents;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;
import org.aspectj.lang.reflect.NoSuchAdviceException;
import org.aspectj.lang.reflect.NoSuchPointcutException;
import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.Pointcut;

public interface AjType<T>
extends Type,
AnnotatedElement {
    public String getName();

    public Package getPackage();

    public AjType<?>[] getInterfaces();

    public int getModifiers();

    public Class<T> getJavaClass();

    public AjType<?> getSupertype();

    public Type getGenericSupertype();

    public Method getEnclosingMethod();

    public Constructor getEnclosingConstructor();

    public AjType<?> getEnclosingType();

    public AjType<?> getDeclaringType();

    public PerClause getPerClause();

    public AjType<?>[] getAjTypes();

    public AjType<?>[] getDeclaredAjTypes();

    public Constructor getConstructor(AjType<?> ... var1) throws NoSuchMethodException;

    public Constructor[] getConstructors();

    public Constructor getDeclaredConstructor(AjType<?> ... var1) throws NoSuchMethodException;

    public Constructor[] getDeclaredConstructors();

    public Field getDeclaredField(String var1) throws NoSuchFieldException;

    public Field[] getDeclaredFields();

    public Field getField(String var1) throws NoSuchFieldException;

    public Field[] getFields();

    public Method getDeclaredMethod(String var1, AjType<?> ... var2) throws NoSuchMethodException;

    public Method getMethod(String var1, AjType<?> ... var2) throws NoSuchMethodException;

    public Method[] getDeclaredMethods();

    public Method[] getMethods();

    public Pointcut getDeclaredPointcut(String var1) throws NoSuchPointcutException;

    public Pointcut getPointcut(String var1) throws NoSuchPointcutException;

    public Pointcut[] getDeclaredPointcuts();

    public Pointcut[] getPointcuts();

    public Advice[] getDeclaredAdvice(AdviceKind ... var1);

    public Advice[] getAdvice(AdviceKind ... var1);

    public Advice getAdvice(String var1) throws NoSuchAdviceException;

    public Advice getDeclaredAdvice(String var1) throws NoSuchAdviceException;

    public InterTypeMethodDeclaration getDeclaredITDMethod(String var1, AjType<?> var2, AjType<?> ... var3) throws NoSuchMethodException;

    public InterTypeMethodDeclaration[] getDeclaredITDMethods();

    public InterTypeMethodDeclaration getITDMethod(String var1, AjType<?> var2, AjType<?> ... var3) throws NoSuchMethodException;

    public InterTypeMethodDeclaration[] getITDMethods();

    public InterTypeConstructorDeclaration getDeclaredITDConstructor(AjType<?> var1, AjType<?> ... var2) throws NoSuchMethodException;

    public InterTypeConstructorDeclaration[] getDeclaredITDConstructors();

    public InterTypeConstructorDeclaration getITDConstructor(AjType<?> var1, AjType<?> ... var2) throws NoSuchMethodException;

    public InterTypeConstructorDeclaration[] getITDConstructors();

    public InterTypeFieldDeclaration getDeclaredITDField(String var1, AjType<?> var2) throws NoSuchFieldException;

    public InterTypeFieldDeclaration[] getDeclaredITDFields();

    public InterTypeFieldDeclaration getITDField(String var1, AjType<?> var2) throws NoSuchFieldException;

    public InterTypeFieldDeclaration[] getITDFields();

    public DeclareErrorOrWarning[] getDeclareErrorOrWarnings();

    public DeclareParents[] getDeclareParents();

    public DeclareSoft[] getDeclareSofts();

    public DeclareAnnotation[] getDeclareAnnotations();

    public DeclarePrecedence[] getDeclarePrecedence();

    public T[] getEnumConstants();

    public TypeVariable<Class<T>>[] getTypeParameters();

    public boolean isEnum();

    public boolean isInstance(Object var1);

    public boolean isInterface();

    public boolean isLocalClass();

    public boolean isMemberClass();

    public boolean isArray();

    public boolean isPrimitive();

    public boolean isAspect();

    public boolean isMemberAspect();

    public boolean isPrivileged();
}

