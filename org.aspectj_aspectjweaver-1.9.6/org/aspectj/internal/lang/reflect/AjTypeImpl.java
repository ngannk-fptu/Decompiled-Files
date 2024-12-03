/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;
import org.aspectj.internal.lang.annotation.ajcDeclareParents;
import org.aspectj.internal.lang.annotation.ajcDeclarePrecedence;
import org.aspectj.internal.lang.annotation.ajcDeclareSoft;
import org.aspectj.internal.lang.annotation.ajcITD;
import org.aspectj.internal.lang.annotation.ajcPrivileged;
import org.aspectj.internal.lang.reflect.AdviceImpl;
import org.aspectj.internal.lang.reflect.DeclareAnnotationImpl;
import org.aspectj.internal.lang.reflect.DeclareErrorOrWarningImpl;
import org.aspectj.internal.lang.reflect.DeclareParentsImpl;
import org.aspectj.internal.lang.reflect.DeclarePrecedenceImpl;
import org.aspectj.internal.lang.reflect.DeclareSoftImpl;
import org.aspectj.internal.lang.reflect.InterTypeConstructorDeclarationImpl;
import org.aspectj.internal.lang.reflect.InterTypeFieldDeclarationImpl;
import org.aspectj.internal.lang.reflect.InterTypeMethodDeclarationImpl;
import org.aspectj.internal.lang.reflect.PerClauseImpl;
import org.aspectj.internal.lang.reflect.PointcutBasedPerClauseImpl;
import org.aspectj.internal.lang.reflect.PointcutImpl;
import org.aspectj.internal.lang.reflect.TypePatternBasedPerClauseImpl;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.DeclareWarning;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;
import org.aspectj.lang.reflect.NoSuchAdviceException;
import org.aspectj.lang.reflect.NoSuchPointcutException;
import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;

public class AjTypeImpl<T>
implements AjType<T> {
    private static final String ajcMagic = "ajc$";
    private Class<T> clazz;
    private org.aspectj.lang.reflect.Pointcut[] declaredPointcuts = null;
    private org.aspectj.lang.reflect.Pointcut[] pointcuts = null;
    private Advice[] declaredAdvice = null;
    private Advice[] advice = null;
    private InterTypeMethodDeclaration[] declaredITDMethods = null;
    private InterTypeMethodDeclaration[] itdMethods = null;
    private InterTypeFieldDeclaration[] declaredITDFields = null;
    private InterTypeFieldDeclaration[] itdFields = null;
    private InterTypeConstructorDeclaration[] itdCons = null;
    private InterTypeConstructorDeclaration[] declaredITDCons = null;

    public AjTypeImpl(Class<T> fromClass) {
        this.clazz = fromClass;
    }

    @Override
    public String getName() {
        return this.clazz.getName();
    }

    @Override
    public Package getPackage() {
        return this.clazz.getPackage();
    }

    @Override
    public AjType<?>[] getInterfaces() {
        Class<?>[] baseInterfaces = this.clazz.getInterfaces();
        return this.toAjTypeArray(baseInterfaces);
    }

    @Override
    public int getModifiers() {
        return this.clazz.getModifiers();
    }

    @Override
    public Class<T> getJavaClass() {
        return this.clazz;
    }

    @Override
    public AjType<? super T> getSupertype() {
        Class<T> superclass = this.clazz.getSuperclass();
        return superclass == null ? null : new AjTypeImpl<T>(superclass);
    }

    @Override
    public Type getGenericSupertype() {
        return this.clazz.getGenericSuperclass();
    }

    @Override
    public Method getEnclosingMethod() {
        return this.clazz.getEnclosingMethod();
    }

    @Override
    public Constructor getEnclosingConstructor() {
        return this.clazz.getEnclosingConstructor();
    }

    @Override
    public AjType<?> getEnclosingType() {
        Class<?> enc = this.clazz.getEnclosingClass();
        return enc != null ? new AjTypeImpl(enc) : null;
    }

    @Override
    public AjType<?> getDeclaringType() {
        Class<?> dec = this.clazz.getDeclaringClass();
        return dec != null ? new AjTypeImpl(dec) : null;
    }

    @Override
    public PerClause getPerClause() {
        if (this.isAspect()) {
            Aspect aspectAnn = this.clazz.getAnnotation(Aspect.class);
            String perClause = aspectAnn.value();
            if (perClause.equals("")) {
                if (this.getSupertype().isAspect()) {
                    return this.getSupertype().getPerClause();
                }
                return new PerClauseImpl(PerClauseKind.SINGLETON);
            }
            if (perClause.startsWith("perthis(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERTHIS, perClause.substring("perthis(".length(), perClause.length() - 1));
            }
            if (perClause.startsWith("pertarget(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERTARGET, perClause.substring("pertarget(".length(), perClause.length() - 1));
            }
            if (perClause.startsWith("percflow(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOW, perClause.substring("percflow(".length(), perClause.length() - 1));
            }
            if (perClause.startsWith("percflowbelow(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOWBELOW, perClause.substring("percflowbelow(".length(), perClause.length() - 1));
            }
            if (perClause.startsWith("pertypewithin")) {
                return new TypePatternBasedPerClauseImpl(PerClauseKind.PERTYPEWITHIN, perClause.substring("pertypewithin(".length(), perClause.length() - 1));
            }
            throw new IllegalStateException("Per-clause not recognized: " + perClause);
        }
        return null;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.clazz.isAnnotationPresent(annotationType);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return this.clazz.getAnnotation(annotationType);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.clazz.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.clazz.getDeclaredAnnotations();
    }

    @Override
    public AjType<?>[] getAjTypes() {
        Class<?>[] classes = this.clazz.getClasses();
        return this.toAjTypeArray(classes);
    }

    @Override
    public AjType<?>[] getDeclaredAjTypes() {
        Class<?>[] classes = this.clazz.getDeclaredClasses();
        return this.toAjTypeArray(classes);
    }

    @Override
    public Constructor getConstructor(AjType<?> ... parameterTypes) throws NoSuchMethodException {
        return this.clazz.getConstructor(this.toClassArray(parameterTypes));
    }

    @Override
    public Constructor[] getConstructors() {
        return this.clazz.getConstructors();
    }

    @Override
    public Constructor getDeclaredConstructor(AjType<?> ... parameterTypes) throws NoSuchMethodException {
        return this.clazz.getDeclaredConstructor(this.toClassArray(parameterTypes));
    }

    @Override
    public Constructor[] getDeclaredConstructors() {
        return this.clazz.getDeclaredConstructors();
    }

    @Override
    public Field getDeclaredField(String name) throws NoSuchFieldException {
        Field f = this.clazz.getDeclaredField(name);
        if (f.getName().startsWith(ajcMagic)) {
            throw new NoSuchFieldException(name);
        }
        return f;
    }

    @Override
    public Field[] getDeclaredFields() {
        Field[] fields = this.clazz.getDeclaredFields();
        ArrayList<Field> filteredFields = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getName().startsWith(ajcMagic) || field.isAnnotationPresent(DeclareWarning.class) || field.isAnnotationPresent(DeclareError.class)) continue;
            filteredFields.add(field);
        }
        Field[] ret = new Field[filteredFields.size()];
        filteredFields.toArray(ret);
        return ret;
    }

    @Override
    public Field getField(String name) throws NoSuchFieldException {
        Field f = this.clazz.getField(name);
        if (f.getName().startsWith(ajcMagic)) {
            throw new NoSuchFieldException(name);
        }
        return f;
    }

    @Override
    public Field[] getFields() {
        Field[] fields = this.clazz.getFields();
        ArrayList<Field> filteredFields = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getName().startsWith(ajcMagic) || field.isAnnotationPresent(DeclareWarning.class) || field.isAnnotationPresent(DeclareError.class)) continue;
            filteredFields.add(field);
        }
        Field[] ret = new Field[filteredFields.size()];
        filteredFields.toArray(ret);
        return ret;
    }

    @Override
    public Method getDeclaredMethod(String name, AjType<?> ... parameterTypes) throws NoSuchMethodException {
        Method m = this.clazz.getDeclaredMethod(name, this.toClassArray(parameterTypes));
        if (!this.isReallyAMethod(m)) {
            throw new NoSuchMethodException(name);
        }
        return m;
    }

    @Override
    public Method getMethod(String name, AjType<?> ... parameterTypes) throws NoSuchMethodException {
        Method m = this.clazz.getMethod(name, this.toClassArray(parameterTypes));
        if (!this.isReallyAMethod(m)) {
            throw new NoSuchMethodException(name);
        }
        return m;
    }

    @Override
    public Method[] getDeclaredMethods() {
        Method[] methods = this.clazz.getDeclaredMethods();
        ArrayList<Method> filteredMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (!this.isReallyAMethod(method)) continue;
            filteredMethods.add(method);
        }
        Method[] ret = new Method[filteredMethods.size()];
        filteredMethods.toArray(ret);
        return ret;
    }

    @Override
    public Method[] getMethods() {
        Method[] methods = this.clazz.getMethods();
        ArrayList<Method> filteredMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (!this.isReallyAMethod(method)) continue;
            filteredMethods.add(method);
        }
        Method[] ret = new Method[filteredMethods.size()];
        filteredMethods.toArray(ret);
        return ret;
    }

    private boolean isReallyAMethod(Method method) {
        if (method.getName().startsWith(ajcMagic)) {
            return false;
        }
        if (method.getAnnotations().length == 0) {
            return true;
        }
        if (method.isAnnotationPresent(Pointcut.class)) {
            return false;
        }
        if (method.isAnnotationPresent(Before.class)) {
            return false;
        }
        if (method.isAnnotationPresent(After.class)) {
            return false;
        }
        if (method.isAnnotationPresent(AfterReturning.class)) {
            return false;
        }
        if (method.isAnnotationPresent(AfterThrowing.class)) {
            return false;
        }
        return !method.isAnnotationPresent(Around.class);
    }

    @Override
    public org.aspectj.lang.reflect.Pointcut getDeclaredPointcut(String name) throws NoSuchPointcutException {
        org.aspectj.lang.reflect.Pointcut[] pcs;
        for (org.aspectj.lang.reflect.Pointcut pc : pcs = this.getDeclaredPointcuts()) {
            if (!pc.getName().equals(name)) continue;
            return pc;
        }
        throw new NoSuchPointcutException(name);
    }

    @Override
    public org.aspectj.lang.reflect.Pointcut getPointcut(String name) throws NoSuchPointcutException {
        org.aspectj.lang.reflect.Pointcut[] pcs;
        for (org.aspectj.lang.reflect.Pointcut pc : pcs = this.getPointcuts()) {
            if (!pc.getName().equals(name)) continue;
            return pc;
        }
        throw new NoSuchPointcutException(name);
    }

    @Override
    public org.aspectj.lang.reflect.Pointcut[] getDeclaredPointcuts() {
        Method[] methods;
        if (this.declaredPointcuts != null) {
            return this.declaredPointcuts;
        }
        ArrayList<org.aspectj.lang.reflect.Pointcut> pointcuts = new ArrayList<org.aspectj.lang.reflect.Pointcut>();
        for (Method method : methods = this.clazz.getDeclaredMethods()) {
            org.aspectj.lang.reflect.Pointcut pc = this.asPointcut(method);
            if (pc == null) continue;
            pointcuts.add(pc);
        }
        org.aspectj.lang.reflect.Pointcut[] ret = new org.aspectj.lang.reflect.Pointcut[pointcuts.size()];
        pointcuts.toArray(ret);
        this.declaredPointcuts = ret;
        return ret;
    }

    @Override
    public org.aspectj.lang.reflect.Pointcut[] getPointcuts() {
        Method[] methods;
        if (this.pointcuts != null) {
            return this.pointcuts;
        }
        ArrayList<org.aspectj.lang.reflect.Pointcut> pcuts = new ArrayList<org.aspectj.lang.reflect.Pointcut>();
        for (Method method : methods = this.clazz.getMethods()) {
            org.aspectj.lang.reflect.Pointcut pc = this.asPointcut(method);
            if (pc == null) continue;
            pcuts.add(pc);
        }
        org.aspectj.lang.reflect.Pointcut[] ret = new org.aspectj.lang.reflect.Pointcut[pcuts.size()];
        pcuts.toArray(ret);
        this.pointcuts = ret;
        return ret;
    }

    private org.aspectj.lang.reflect.Pointcut asPointcut(Method method) {
        Pointcut pcAnn = method.getAnnotation(Pointcut.class);
        if (pcAnn != null) {
            int nameStart;
            int nextDollar;
            String name = method.getName();
            if (name.startsWith(ajcMagic) && (nextDollar = (name = name.substring((nameStart = name.indexOf("$$")) + 2, name.length())).indexOf("$")) != -1) {
                name = name.substring(0, nextDollar);
            }
            return new PointcutImpl(name, pcAnn.value(), method, AjTypeSystem.getAjType(method.getDeclaringClass()), pcAnn.argNames());
        }
        return null;
    }

    @Override
    public Advice[] getDeclaredAdvice(AdviceKind ... ofType) {
        EnumSet<AdviceKind> types;
        if (ofType.length == 0) {
            types = EnumSet.allOf(AdviceKind.class);
        } else {
            types = EnumSet.noneOf(AdviceKind.class);
            types.addAll(Arrays.asList(ofType));
        }
        return this.getDeclaredAdvice(types);
    }

    @Override
    public Advice[] getAdvice(AdviceKind ... ofType) {
        EnumSet<AdviceKind> types;
        if (ofType.length == 0) {
            types = EnumSet.allOf(AdviceKind.class);
        } else {
            types = EnumSet.noneOf(AdviceKind.class);
            types.addAll(Arrays.asList(ofType));
        }
        return this.getAdvice(types);
    }

    private Advice[] getDeclaredAdvice(Set ofAdviceTypes) {
        if (this.declaredAdvice == null) {
            this.initDeclaredAdvice();
        }
        ArrayList<Advice> adviceList = new ArrayList<Advice>();
        for (Advice a : this.declaredAdvice) {
            if (!ofAdviceTypes.contains((Object)a.getKind())) continue;
            adviceList.add(a);
        }
        Advice[] ret = new Advice[adviceList.size()];
        adviceList.toArray(ret);
        return ret;
    }

    private void initDeclaredAdvice() {
        Method[] methods = this.clazz.getDeclaredMethods();
        ArrayList<Advice> adviceList = new ArrayList<Advice>();
        for (Method method : methods) {
            Advice advice = this.asAdvice(method);
            if (advice == null) continue;
            adviceList.add(advice);
        }
        this.declaredAdvice = new Advice[adviceList.size()];
        adviceList.toArray(this.declaredAdvice);
    }

    private Advice[] getAdvice(Set ofAdviceTypes) {
        if (this.advice == null) {
            this.initAdvice();
        }
        ArrayList<Advice> adviceList = new ArrayList<Advice>();
        for (Advice a : this.advice) {
            if (!ofAdviceTypes.contains((Object)a.getKind())) continue;
            adviceList.add(a);
        }
        Advice[] ret = new Advice[adviceList.size()];
        adviceList.toArray(ret);
        return ret;
    }

    private void initAdvice() {
        Method[] methods = this.clazz.getMethods();
        ArrayList<Advice> adviceList = new ArrayList<Advice>();
        for (Method method : methods) {
            Advice advice = this.asAdvice(method);
            if (advice == null) continue;
            adviceList.add(advice);
        }
        this.advice = new Advice[adviceList.size()];
        adviceList.toArray(this.advice);
    }

    @Override
    public Advice getAdvice(String name) throws NoSuchAdviceException {
        if (name.equals("")) {
            throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
        }
        if (this.advice == null) {
            this.initAdvice();
        }
        for (Advice a : this.advice) {
            if (!a.getName().equals(name)) continue;
            return a;
        }
        throw new NoSuchAdviceException(name);
    }

    @Override
    public Advice getDeclaredAdvice(String name) throws NoSuchAdviceException {
        if (name.equals("")) {
            throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
        }
        if (this.declaredAdvice == null) {
            this.initDeclaredAdvice();
        }
        for (Advice a : this.declaredAdvice) {
            if (!a.getName().equals(name)) continue;
            return a;
        }
        throw new NoSuchAdviceException(name);
    }

    private Advice asAdvice(Method method) {
        if (method.getAnnotations().length == 0) {
            return null;
        }
        Before beforeAnn = method.getAnnotation(Before.class);
        if (beforeAnn != null) {
            return new AdviceImpl(method, beforeAnn.value(), AdviceKind.BEFORE);
        }
        After afterAnn = method.getAnnotation(After.class);
        if (afterAnn != null) {
            return new AdviceImpl(method, afterAnn.value(), AdviceKind.AFTER);
        }
        AfterReturning afterReturningAnn = method.getAnnotation(AfterReturning.class);
        if (afterReturningAnn != null) {
            String pcExpr = afterReturningAnn.pointcut();
            if (pcExpr.equals("")) {
                pcExpr = afterReturningAnn.value();
            }
            return new AdviceImpl(method, pcExpr, AdviceKind.AFTER_RETURNING, afterReturningAnn.returning());
        }
        AfterThrowing afterThrowingAnn = method.getAnnotation(AfterThrowing.class);
        if (afterThrowingAnn != null) {
            String pcExpr = afterThrowingAnn.pointcut();
            if (pcExpr == null) {
                pcExpr = afterThrowingAnn.value();
            }
            return new AdviceImpl(method, pcExpr, AdviceKind.AFTER_THROWING, afterThrowingAnn.throwing());
        }
        Around aroundAnn = method.getAnnotation(Around.class);
        if (aroundAnn != null) {
            return new AdviceImpl(method, aroundAnn.value(), AdviceKind.AROUND);
        }
        return null;
    }

    @Override
    public InterTypeMethodDeclaration getDeclaredITDMethod(String name, AjType<?> target, AjType<?> ... parameterTypes) throws NoSuchMethodException {
        InterTypeMethodDeclaration[] itdms;
        block2: for (InterTypeMethodDeclaration itdm : itdms = this.getDeclaredITDMethods()) {
            try {
                AjType<?>[] ptypes;
                AjType<?> itdTarget;
                if (!itdm.getName().equals(name) || !(itdTarget = itdm.getTargetType()).equals(target) || (ptypes = itdm.getParameterTypes()).length != parameterTypes.length) continue;
                for (int i = 0; i < ptypes.length; ++i) {
                    if (!ptypes[i].equals(parameterTypes[i])) continue block2;
                }
                return itdm;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new NoSuchMethodException(name);
    }

    @Override
    public InterTypeMethodDeclaration[] getDeclaredITDMethods() {
        if (this.declaredITDMethods == null) {
            Method[] baseMethods;
            ArrayList<InterTypeMethodDeclaration> itdms = new ArrayList<InterTypeMethodDeclaration>();
            for (Method m : baseMethods = this.clazz.getDeclaredMethods()) {
                if (!m.getName().contains("ajc$interMethodDispatch1$") || !m.isAnnotationPresent(ajcITD.class)) continue;
                ajcITD ann = m.getAnnotation(ajcITD.class);
                InterTypeMethodDeclarationImpl itdm = new InterTypeMethodDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), m);
                itdms.add(itdm);
            }
            this.addAnnotationStyleITDMethods(itdms, false);
            this.declaredITDMethods = new InterTypeMethodDeclaration[itdms.size()];
            itdms.toArray(this.declaredITDMethods);
        }
        return this.declaredITDMethods;
    }

    @Override
    public InterTypeMethodDeclaration getITDMethod(String name, AjType<?> target, AjType<?> ... parameterTypes) throws NoSuchMethodException {
        InterTypeMethodDeclaration[] itdms;
        block2: for (InterTypeMethodDeclaration itdm : itdms = this.getITDMethods()) {
            try {
                AjType<?>[] ptypes;
                AjType<?> itdTarget;
                if (!itdm.getName().equals(name) || !(itdTarget = itdm.getTargetType()).equals(target) || (ptypes = itdm.getParameterTypes()).length != parameterTypes.length) continue;
                for (int i = 0; i < ptypes.length; ++i) {
                    if (!ptypes[i].equals(parameterTypes[i])) continue block2;
                }
                return itdm;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new NoSuchMethodException(name);
    }

    @Override
    public InterTypeMethodDeclaration[] getITDMethods() {
        if (this.itdMethods == null) {
            Method[] baseMethods;
            ArrayList<InterTypeMethodDeclaration> itdms = new ArrayList<InterTypeMethodDeclaration>();
            for (Method m : baseMethods = this.clazz.getDeclaredMethods()) {
                ajcITD ann;
                if (!m.getName().contains("ajc$interMethod$") || !m.isAnnotationPresent(ajcITD.class) || !Modifier.isPublic((ann = m.getAnnotation(ajcITD.class)).modifiers())) continue;
                InterTypeMethodDeclarationImpl itdm = new InterTypeMethodDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), m);
                itdms.add(itdm);
            }
            this.addAnnotationStyleITDMethods(itdms, true);
            this.itdMethods = new InterTypeMethodDeclaration[itdms.size()];
            itdms.toArray(this.itdMethods);
        }
        return this.itdMethods;
    }

    private void addAnnotationStyleITDMethods(List<InterTypeMethodDeclaration> toList, boolean publicOnly) {
        if (this.isAspect()) {
            for (Field f : this.clazz.getDeclaredFields()) {
                Class<DeclareParents> decPAnnClass;
                DeclareParents decPAnn;
                if (!f.getType().isInterface() || !f.isAnnotationPresent(DeclareParents.class) || (decPAnn = f.getAnnotation(decPAnnClass = DeclareParents.class)).defaultImpl() == decPAnnClass) continue;
                for (Method itdM : f.getType().getDeclaredMethods()) {
                    if (!Modifier.isPublic(itdM.getModifiers()) && publicOnly) continue;
                    InterTypeMethodDeclarationImpl itdm = new InterTypeMethodDeclarationImpl(this, AjTypeSystem.getAjType(f.getType()), itdM, 1);
                    toList.add(itdm);
                }
            }
        }
    }

    private void addAnnotationStyleITDFields(List<InterTypeFieldDeclaration> toList, boolean publicOnly) {
    }

    @Override
    public InterTypeConstructorDeclaration getDeclaredITDConstructor(AjType<?> target, AjType<?> ... parameterTypes) throws NoSuchMethodException {
        InterTypeConstructorDeclaration[] itdcs;
        block2: for (InterTypeConstructorDeclaration itdc : itdcs = this.getDeclaredITDConstructors()) {
            try {
                AjType<?>[] ptypes;
                AjType<?> itdTarget = itdc.getTargetType();
                if (!itdTarget.equals(target) || (ptypes = itdc.getParameterTypes()).length != parameterTypes.length) continue;
                for (int i = 0; i < ptypes.length; ++i) {
                    if (!ptypes[i].equals(parameterTypes[i])) continue block2;
                }
                return itdc;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new NoSuchMethodException();
    }

    @Override
    public InterTypeConstructorDeclaration[] getDeclaredITDConstructors() {
        if (this.declaredITDCons == null) {
            Method[] baseMethods;
            ArrayList<InterTypeConstructorDeclarationImpl> itdcs = new ArrayList<InterTypeConstructorDeclarationImpl>();
            for (Method m : baseMethods = this.clazz.getDeclaredMethods()) {
                if (!m.getName().contains("ajc$postInterConstructor") || !m.isAnnotationPresent(ajcITD.class)) continue;
                ajcITD ann = m.getAnnotation(ajcITD.class);
                InterTypeConstructorDeclarationImpl itdc = new InterTypeConstructorDeclarationImpl(this, ann.targetType(), ann.modifiers(), m);
                itdcs.add(itdc);
            }
            this.declaredITDCons = new InterTypeConstructorDeclaration[itdcs.size()];
            itdcs.toArray(this.declaredITDCons);
        }
        return this.declaredITDCons;
    }

    @Override
    public InterTypeConstructorDeclaration getITDConstructor(AjType<?> target, AjType<?> ... parameterTypes) throws NoSuchMethodException {
        InterTypeConstructorDeclaration[] itdcs;
        block2: for (InterTypeConstructorDeclaration itdc : itdcs = this.getITDConstructors()) {
            try {
                AjType<?>[] ptypes;
                AjType<?> itdTarget = itdc.getTargetType();
                if (!itdTarget.equals(target) || (ptypes = itdc.getParameterTypes()).length != parameterTypes.length) continue;
                for (int i = 0; i < ptypes.length; ++i) {
                    if (!ptypes[i].equals(parameterTypes[i])) continue block2;
                }
                return itdc;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new NoSuchMethodException();
    }

    @Override
    public InterTypeConstructorDeclaration[] getITDConstructors() {
        if (this.itdCons == null) {
            Method[] baseMethods;
            ArrayList<InterTypeConstructorDeclarationImpl> itdcs = new ArrayList<InterTypeConstructorDeclarationImpl>();
            for (Method m : baseMethods = this.clazz.getMethods()) {
                ajcITD ann;
                if (!m.getName().contains("ajc$postInterConstructor") || !m.isAnnotationPresent(ajcITD.class) || !Modifier.isPublic((ann = m.getAnnotation(ajcITD.class)).modifiers())) continue;
                InterTypeConstructorDeclarationImpl itdc = new InterTypeConstructorDeclarationImpl(this, ann.targetType(), ann.modifiers(), m);
                itdcs.add(itdc);
            }
            this.itdCons = new InterTypeConstructorDeclaration[itdcs.size()];
            itdcs.toArray(this.itdCons);
        }
        return this.itdCons;
    }

    @Override
    public InterTypeFieldDeclaration getDeclaredITDField(String name, AjType<?> target) throws NoSuchFieldException {
        InterTypeFieldDeclaration[] itdfs;
        for (InterTypeFieldDeclaration itdf : itdfs = this.getDeclaredITDFields()) {
            if (!itdf.getName().equals(name)) continue;
            try {
                AjType<?> itdTarget = itdf.getTargetType();
                if (!itdTarget.equals(target)) continue;
                return itdf;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new NoSuchFieldException(name);
    }

    @Override
    public InterTypeFieldDeclaration[] getDeclaredITDFields() {
        ArrayList<InterTypeFieldDeclaration> itdfs = new ArrayList<InterTypeFieldDeclaration>();
        if (this.declaredITDFields == null) {
            Method[] baseMethods;
            for (Method m : baseMethods = this.clazz.getDeclaredMethods()) {
                if (!m.isAnnotationPresent(ajcITD.class) || !m.getName().contains("ajc$interFieldInit")) continue;
                ajcITD ann = m.getAnnotation(ajcITD.class);
                String interFieldInitMethodName = m.getName();
                String interFieldGetDispatchMethodName = interFieldInitMethodName.replace("FieldInit", "FieldGetDispatch");
                try {
                    Method dispatch = this.clazz.getDeclaredMethod(interFieldGetDispatchMethodName, m.getParameterTypes());
                    InterTypeFieldDeclarationImpl itdf = new InterTypeFieldDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), AjTypeSystem.getAjType(dispatch.getReturnType()), dispatch.getGenericReturnType());
                    itdfs.add(itdf);
                }
                catch (NoSuchMethodException nsmEx) {
                    throw new IllegalStateException("Can't find field get dispatch method for " + m.getName());
                }
            }
            this.addAnnotationStyleITDFields(itdfs, false);
            this.declaredITDFields = new InterTypeFieldDeclaration[itdfs.size()];
            itdfs.toArray(this.declaredITDFields);
        }
        return this.declaredITDFields;
    }

    @Override
    public InterTypeFieldDeclaration getITDField(String name, AjType<?> target) throws NoSuchFieldException {
        InterTypeFieldDeclaration[] itdfs;
        for (InterTypeFieldDeclaration itdf : itdfs = this.getITDFields()) {
            if (!itdf.getName().equals(name)) continue;
            try {
                AjType<?> itdTarget = itdf.getTargetType();
                if (!itdTarget.equals(target)) continue;
                return itdf;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        throw new NoSuchFieldException(name);
    }

    @Override
    public InterTypeFieldDeclaration[] getITDFields() {
        ArrayList<InterTypeFieldDeclaration> itdfs = new ArrayList<InterTypeFieldDeclaration>();
        if (this.itdFields == null) {
            Method[] baseMethods;
            for (Method m : baseMethods = this.clazz.getMethods()) {
                if (!m.isAnnotationPresent(ajcITD.class)) continue;
                ajcITD ann = m.getAnnotation(ajcITD.class);
                if (!m.getName().contains("ajc$interFieldInit") || !Modifier.isPublic(ann.modifiers())) continue;
                String interFieldInitMethodName = m.getName();
                String interFieldGetDispatchMethodName = interFieldInitMethodName.replace("FieldInit", "FieldGetDispatch");
                try {
                    Method dispatch = m.getDeclaringClass().getDeclaredMethod(interFieldGetDispatchMethodName, m.getParameterTypes());
                    InterTypeFieldDeclarationImpl itdf = new InterTypeFieldDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), AjTypeSystem.getAjType(dispatch.getReturnType()), dispatch.getGenericReturnType());
                    itdfs.add(itdf);
                }
                catch (NoSuchMethodException nsmEx) {
                    throw new IllegalStateException("Can't find field get dispatch method for " + m.getName());
                }
            }
            this.addAnnotationStyleITDFields(itdfs, true);
            this.itdFields = new InterTypeFieldDeclaration[itdfs.size()];
            itdfs.toArray(this.itdFields);
        }
        return this.itdFields;
    }

    @Override
    public DeclareErrorOrWarning[] getDeclareErrorOrWarnings() {
        ArrayList<DeclareErrorOrWarningImpl> deows = new ArrayList<DeclareErrorOrWarningImpl>();
        for (Field field : this.clazz.getDeclaredFields()) {
            try {
                DeclareErrorOrWarningImpl deow;
                String message;
                if (field.isAnnotationPresent(DeclareWarning.class)) {
                    DeclareWarning dw = field.getAnnotation(DeclareWarning.class);
                    if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isStatic(field.getModifiers())) continue;
                    message = (String)field.get(null);
                    deow = new DeclareErrorOrWarningImpl(dw.value(), message, false, this);
                    deows.add(deow);
                    continue;
                }
                if (!field.isAnnotationPresent(DeclareError.class)) continue;
                DeclareError de = field.getAnnotation(DeclareError.class);
                if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isStatic(field.getModifiers())) continue;
                message = (String)field.get(null);
                deow = new DeclareErrorOrWarningImpl(de.value(), message, true, this);
                deows.add(deow);
            }
            catch (IllegalArgumentException de) {
            }
            catch (IllegalAccessException de) {
                // empty catch block
            }
        }
        for (AccessibleObject accessibleObject : this.clazz.getDeclaredMethods()) {
            if (!accessibleObject.isAnnotationPresent(ajcDeclareEoW.class)) continue;
            ajcDeclareEoW deowAnn = ((Method)accessibleObject).getAnnotation(ajcDeclareEoW.class);
            DeclareErrorOrWarningImpl deow = new DeclareErrorOrWarningImpl(deowAnn.pointcut(), deowAnn.message(), deowAnn.isError(), this);
            deows.add(deow);
        }
        DeclareErrorOrWarning[] ret = new DeclareErrorOrWarning[deows.size()];
        deows.toArray(ret);
        return ret;
    }

    @Override
    public org.aspectj.lang.reflect.DeclareParents[] getDeclareParents() {
        ArrayList<org.aspectj.lang.reflect.DeclareParents> decps = new ArrayList<org.aspectj.lang.reflect.DeclareParents>();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ajcDeclareParents.class)) continue;
            ajcDeclareParents decPAnn = method.getAnnotation(ajcDeclareParents.class);
            DeclareParentsImpl decp = new DeclareParentsImpl(decPAnn.targetTypePattern(), decPAnn.parentTypes(), decPAnn.isExtends(), this);
            decps.add(decp);
        }
        this.addAnnotationStyleDeclareParents(decps);
        if (this.getSupertype().isAspect()) {
            decps.addAll(Arrays.asList(this.getSupertype().getDeclareParents()));
        }
        org.aspectj.lang.reflect.DeclareParents[] ret = new org.aspectj.lang.reflect.DeclareParents[decps.size()];
        decps.toArray(ret);
        return ret;
    }

    private void addAnnotationStyleDeclareParents(List<org.aspectj.lang.reflect.DeclareParents> toList) {
        for (Field f : this.clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(DeclareParents.class) || !f.getType().isInterface()) continue;
            DeclareParents ann = f.getAnnotation(DeclareParents.class);
            String parentType = f.getType().getName();
            DeclareParentsImpl decp = new DeclareParentsImpl(ann.value(), parentType, false, this);
            toList.add(decp);
        }
    }

    @Override
    public DeclareSoft[] getDeclareSofts() {
        ArrayList<DeclareSoft> decs = new ArrayList<DeclareSoft>();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ajcDeclareSoft.class)) continue;
            ajcDeclareSoft decSAnn = method.getAnnotation(ajcDeclareSoft.class);
            DeclareSoftImpl ds = new DeclareSoftImpl(this, decSAnn.pointcut(), decSAnn.exceptionType());
            decs.add(ds);
        }
        if (this.getSupertype().isAspect()) {
            decs.addAll(Arrays.asList(this.getSupertype().getDeclareSofts()));
        }
        DeclareSoft[] ret = new DeclareSoft[decs.size()];
        decs.toArray(ret);
        return ret;
    }

    @Override
    public DeclareAnnotation[] getDeclareAnnotations() {
        ArrayList<DeclareAnnotation> decAs = new ArrayList<DeclareAnnotation>();
        for (Method method : this.clazz.getDeclaredMethods()) {
            Annotation[] anns;
            if (!method.isAnnotationPresent(ajcDeclareAnnotation.class)) continue;
            ajcDeclareAnnotation decAnn = method.getAnnotation(ajcDeclareAnnotation.class);
            Annotation targetAnnotation = null;
            for (Annotation ann : anns = method.getAnnotations()) {
                if (ann.annotationType() == ajcDeclareAnnotation.class) continue;
                targetAnnotation = ann;
                break;
            }
            DeclareAnnotationImpl da = new DeclareAnnotationImpl(this, decAnn.kind(), decAnn.pattern(), targetAnnotation, decAnn.annotation());
            decAs.add(da);
        }
        if (this.getSupertype().isAspect()) {
            decAs.addAll(Arrays.asList(this.getSupertype().getDeclareAnnotations()));
        }
        DeclareAnnotation[] ret = new DeclareAnnotation[decAs.size()];
        decAs.toArray(ret);
        return ret;
    }

    @Override
    public DeclarePrecedence[] getDeclarePrecedence() {
        ArrayList<DeclarePrecedence> decps = new ArrayList<DeclarePrecedence>();
        if (this.clazz.isAnnotationPresent(org.aspectj.lang.annotation.DeclarePrecedence.class)) {
            org.aspectj.lang.annotation.DeclarePrecedence ann = this.clazz.getAnnotation(org.aspectj.lang.annotation.DeclarePrecedence.class);
            DeclarePrecedenceImpl decp = new DeclarePrecedenceImpl(ann.value(), this);
            decps.add(decp);
        }
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ajcDeclarePrecedence.class)) continue;
            ajcDeclarePrecedence decPAnn = method.getAnnotation(ajcDeclarePrecedence.class);
            DeclarePrecedenceImpl decp = new DeclarePrecedenceImpl(decPAnn.value(), this);
            decps.add(decp);
        }
        if (this.getSupertype().isAspect()) {
            decps.addAll(Arrays.asList(this.getSupertype().getDeclarePrecedence()));
        }
        DeclarePrecedence[] ret = new DeclarePrecedence[decps.size()];
        decps.toArray(ret);
        return ret;
    }

    @Override
    public T[] getEnumConstants() {
        return this.clazz.getEnumConstants();
    }

    @Override
    public TypeVariable<Class<T>>[] getTypeParameters() {
        return this.clazz.getTypeParameters();
    }

    @Override
    public boolean isEnum() {
        return this.clazz.isEnum();
    }

    @Override
    public boolean isInstance(Object o) {
        return this.clazz.isInstance(o);
    }

    @Override
    public boolean isInterface() {
        return this.clazz.isInterface();
    }

    @Override
    public boolean isLocalClass() {
        return this.clazz.isLocalClass() && !this.isAspect();
    }

    @Override
    public boolean isMemberClass() {
        return this.clazz.isMemberClass() && !this.isAspect();
    }

    @Override
    public boolean isArray() {
        return this.clazz.isArray();
    }

    @Override
    public boolean isPrimitive() {
        return this.clazz.isPrimitive();
    }

    @Override
    public boolean isAspect() {
        return this.clazz.getAnnotation(Aspect.class) != null;
    }

    @Override
    public boolean isMemberAspect() {
        return this.clazz.isMemberClass() && this.isAspect();
    }

    @Override
    public boolean isPrivileged() {
        return this.isAspect() && this.clazz.isAnnotationPresent(ajcPrivileged.class);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AjTypeImpl)) {
            return false;
        }
        AjTypeImpl other = (AjTypeImpl)obj;
        return other.clazz.equals(this.clazz);
    }

    public int hashCode() {
        return this.clazz.hashCode();
    }

    private AjType<?>[] toAjTypeArray(Class<?>[] classes) {
        AjType[] ajtypes = new AjType[classes.length];
        for (int i = 0; i < ajtypes.length; ++i) {
            ajtypes[i] = AjTypeSystem.getAjType(classes[i]);
        }
        return ajtypes;
    }

    private Class<?>[] toClassArray(AjType<?>[] ajTypes) {
        Class[] classes = new Class[ajTypes.length];
        for (int i = 0; i < classes.length; ++i) {
            classes[i] = ajTypes[i].getJavaClass();
        }
        return classes;
    }

    public String toString() {
        return this.getName();
    }
}

