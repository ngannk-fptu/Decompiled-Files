/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.ClassElementValue;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.apache.bcel.classfile.annotation.SimpleElementValue;
import org.aspectj.apache.bcel.generic.FieldGen;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.LocalVariableTag;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.Message;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.GeneratedReferenceTypeDelegate;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelPerClauseAspectAdder;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.patterns.BasicTokenSource;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.ISignaturePattern;
import org.aspectj.weaver.patterns.ITokenSource;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.TypePattern;

public class ConcreteAspectCodeGen {
    private static final String[] EMPTY_STRINGS = new String[0];
    private static final Type[] EMPTY_TYPES = new Type[0];
    private final Definition.ConcreteAspect concreteAspect;
    private final World world;
    private boolean isValid = false;
    private ResolvedType parent;
    private PerClause perclause;
    private byte[] bytes;

    ConcreteAspectCodeGen(Definition.ConcreteAspect concreteAspect, World world) {
        this.concreteAspect = concreteAspect;
        this.world = world;
    }

    public boolean validate() {
        String perclauseString;
        Object fixedNameChars;
        if (!(this.world instanceof BcelWorld)) {
            this.reportError("Internal error: world must be of type BcelWorld");
            return false;
        }
        ReferenceType current = this.world.lookupBySignature(UnresolvedType.forName(this.concreteAspect.name).getSignature());
        if (current != null && !current.isMissing()) {
            this.reportError("Attempt to concretize but chosen aspect name already defined: " + this.stringify());
            return false;
        }
        if (this.concreteAspect.pointcutsAndAdvice.size() != 0) {
            this.isValid = true;
            return true;
        }
        if (this.concreteAspect.declareAnnotations.size() != 0) {
            this.isValid = true;
            return true;
        }
        if (this.concreteAspect.extend == null && this.concreteAspect.precedence != null) {
            if (this.concreteAspect.pointcuts.isEmpty()) {
                this.isValid = true;
                this.parent = null;
                return true;
            }
            this.reportError("Attempt to use nested pointcuts without extends clause: " + this.stringify());
            return false;
        }
        String parentAspectName = this.concreteAspect.extend;
        if (parentAspectName.indexOf("<") != -1) {
            this.parent = this.world.resolve(UnresolvedType.forName(parentAspectName), true);
            if (this.parent.isMissing()) {
                this.reportError("Unable to resolve type reference: " + this.stringify());
                return false;
            }
            if (this.parent.isParameterizedType()) {
                UnresolvedType[] typeParameters = this.parent.getTypeParameters();
                for (int i = 0; i < typeParameters.length; ++i) {
                    UnresolvedType typeParameter = typeParameters[i];
                    if (!(typeParameter instanceof ResolvedType) || !((ResolvedType)typeParameter).isMissing()) continue;
                    this.reportError("Unablet to resolve type parameter '" + typeParameter.getName() + "' from " + this.stringify());
                    return false;
                }
            }
        } else {
            this.parent = this.world.resolve(this.concreteAspect.extend, true);
        }
        if (this.parent.isMissing()) {
            String fixedName = this.concreteAspect.extend;
            int hasDot = fixedName.lastIndexOf(46);
            while (hasDot > 0) {
                fixedNameChars = fixedName.toCharArray();
                fixedNameChars[hasDot] = 36;
                fixedName = new String((char[])fixedNameChars);
                hasDot = fixedName.lastIndexOf(46);
                this.parent = this.world.resolve(UnresolvedType.forName(fixedName), true);
                if (this.parent.isMissing()) continue;
                break;
            }
        }
        if (this.parent.isMissing()) {
            this.reportError("Cannot find parent aspect for: " + this.stringify());
            return false;
        }
        if (!this.parent.isAbstract() && !this.parent.equals(ResolvedType.OBJECT)) {
            this.reportError("Attempt to concretize a non-abstract aspect: " + this.stringify());
            return false;
        }
        if (!this.parent.isAspect() && !this.parent.equals(ResolvedType.OBJECT)) {
            this.reportError("Attempt to concretize a non aspect: " + this.stringify());
            return false;
        }
        ArrayList<String> elligibleAbstractions = new ArrayList<String>();
        Collection<ResolvedMember> abstractMethods = this.getOutstandingAbstractMethods(this.parent);
        fixedNameChars = abstractMethods.iterator();
        while (fixedNameChars.hasNext()) {
            ResolvedMember method = (ResolvedMember)fixedNameChars.next();
            if ("()V".equals(method.getSignature())) {
                String n = method.getName();
                if (n.startsWith("ajc$pointcut")) {
                    n = n.substring(14);
                    n = n.substring(0, n.indexOf("$"));
                    elligibleAbstractions.add(n);
                    continue;
                }
                if (this.hasPointcutAnnotation(method)) {
                    elligibleAbstractions.add(method.getName());
                    continue;
                }
                this.reportError("Abstract method '" + method.toString() + "' cannot be concretized in XML: " + this.stringify());
                return false;
            }
            if (method.getName().startsWith("ajc$pointcut") || this.hasPointcutAnnotation(method)) {
                this.reportError("Abstract method '" + method.toString() + "' cannot be concretized as a pointcut (illegal signature, must have no arguments, must return void): " + this.stringify());
                return false;
            }
            this.reportError("Abstract method '" + method.toString() + "' cannot be concretized in XML: " + this.stringify());
            return false;
        }
        ArrayList<String> pointcutNames = new ArrayList<String>();
        for (Definition.Pointcut abstractPc : this.concreteAspect.pointcuts) {
            pointcutNames.add(abstractPc.name);
        }
        for (String elligiblePc : elligibleAbstractions) {
            if (pointcutNames.contains(elligiblePc)) continue;
            this.reportError("Abstract pointcut '" + elligiblePc + "' not configured: " + this.stringify());
            return false;
        }
        if (!(this.concreteAspect.perclause == null || (perclauseString = this.concreteAspect.perclause).startsWith("persingleton") || perclauseString.startsWith("percflow") || perclauseString.startsWith("pertypewithin") || perclauseString.startsWith("perthis") || perclauseString.startsWith("pertarget") || perclauseString.startsWith("percflowbelow"))) {
            this.reportError("Unrecognized per clause specified " + this.stringify());
            return false;
        }
        this.isValid = true;
        return this.isValid;
    }

    private Collection<ResolvedMember> getOutstandingAbstractMethods(ResolvedType type) {
        HashMap<String, ResolvedMember> collector = new HashMap<String, ResolvedMember>();
        this.getOutstandingAbstractMethodsHelper(type, collector);
        return collector.values();
    }

    private void getOutstandingAbstractMethodsHelper(ResolvedType type, Map<String, ResolvedMember> collector) {
        ResolvedMember[] rms;
        if (type == null) {
            return;
        }
        if (!type.equals(ResolvedType.OBJECT) && type.getSuperclass() != null) {
            this.getOutstandingAbstractMethodsHelper(type.getSuperclass(), collector);
        }
        if ((rms = type.getDeclaredMethods()) != null) {
            for (int i = 0; i < rms.length; ++i) {
                ResolvedMember member = rms[i];
                String key = member.getName() + member.getSignature();
                if (member.isAbstract()) {
                    collector.put(key, member);
                    continue;
                }
                collector.remove(key);
            }
        }
    }

    private String stringify() {
        StringBuffer sb = new StringBuffer("<concrete-aspect name='");
        sb.append(this.concreteAspect.name);
        sb.append("' extends='");
        sb.append(this.concreteAspect.extend);
        sb.append("' perclause='");
        sb.append(this.concreteAspect.perclause);
        sb.append("'/> in aop.xml");
        return sb.toString();
    }

    private boolean hasPointcutAnnotation(ResolvedMember member) {
        AnnotationAJ[] as = member.getAnnotations();
        if (as == null || as.length == 0) {
            return false;
        }
        for (int i = 0; i < as.length; ++i) {
            if (!as[i].getTypeSignature().equals("Lorg/aspectj/lang/annotation/Pointcut;")) continue;
            return true;
        }
        return false;
    }

    public String getClassName() {
        return this.concreteAspect.name;
    }

    public byte[] getBytes() {
        PerClause parentPerClause;
        if (!this.isValid) {
            throw new RuntimeException("Must validate first");
        }
        if (this.bytes != null) {
            return this.bytes;
        }
        PerClause.Kind perclauseKind = PerClause.SINGLETON;
        PerClause perClause = parentPerClause = this.parent != null ? this.parent.getPerClause() : null;
        if (parentPerClause != null) {
            perclauseKind = parentPerClause.getKind();
        }
        String perclauseString = null;
        if (this.concreteAspect.perclause != null) {
            perclauseString = this.concreteAspect.perclause;
            if (perclauseString.startsWith("persingleton")) {
                perclauseKind = PerClause.SINGLETON;
            } else if (perclauseString.startsWith("percflow")) {
                perclauseKind = PerClause.PERCFLOW;
            } else if (perclauseString.startsWith("pertypewithin")) {
                perclauseKind = PerClause.PERTYPEWITHIN;
            } else if (perclauseString.startsWith("perthis")) {
                perclauseKind = PerClause.PEROBJECT;
            } else if (perclauseString.startsWith("pertarget")) {
                perclauseKind = PerClause.PEROBJECT;
            } else if (perclauseString.startsWith("percflowbelow")) {
                perclauseKind = PerClause.PERCFLOW;
            }
        }
        String parentName = "java/lang/Object";
        if (this.parent != null) {
            parentName = this.parent.isParameterizedType() ? this.parent.getGenericType().getName().replace('.', '/') : this.parent.getName().replace('.', '/');
        }
        LazyClassGen cg = new LazyClassGen(this.concreteAspect.name.replace('.', '/'), parentName, null, 33, EMPTY_STRINGS, this.world);
        if (this.parent != null && this.parent.isParameterizedType()) {
            cg.setSuperClass(this.parent);
        }
        if (perclauseString == null) {
            AnnotationGen ag = new AnnotationGen(new ObjectType("org/aspectj/lang/annotation/Aspect"), Collections.emptyList(), true, cg.getConstantPool());
            cg.addAnnotation(ag);
        } else {
            ArrayList<NameValuePair> elems = new ArrayList<NameValuePair>();
            elems.add(new NameValuePair("value", (ElementValue)new SimpleElementValue(115, cg.getConstantPool(), perclauseString), cg.getConstantPool()));
            AnnotationGen ag = new AnnotationGen(new ObjectType("org/aspectj/lang/annotation/Aspect"), elems, true, cg.getConstantPool());
            cg.addAnnotation(ag);
        }
        if (this.concreteAspect.precedence != null) {
            SimpleElementValue svg = new SimpleElementValue(115, cg.getConstantPool(), this.concreteAspect.precedence);
            ArrayList<NameValuePair> elems = new ArrayList<NameValuePair>();
            elems.add(new NameValuePair("value", (ElementValue)svg, cg.getConstantPool()));
            AnnotationGen agprec = new AnnotationGen(new ObjectType("org/aspectj/lang/annotation/DeclarePrecedence"), elems, true, cg.getConstantPool());
            cg.addAnnotation(agprec);
        }
        LazyMethodGen init = new LazyMethodGen(1, Type.VOID, "<init>", EMPTY_TYPES, EMPTY_STRINGS, cg);
        InstructionList cbody = init.getBody();
        cbody.append(InstructionConstants.ALOAD_0);
        cbody.append(cg.getFactory().createInvoke(parentName, "<init>", Type.VOID, EMPTY_TYPES, (short)183));
        cbody.append(InstructionConstants.RETURN);
        cg.addMethodGen(init);
        for (Definition.Pointcut pointcut : this.concreteAspect.pointcuts) {
            LazyMethodGen mg = new LazyMethodGen(1, Type.VOID, pointcut.name, EMPTY_TYPES, EMPTY_STRINGS, cg);
            SimpleElementValue svg = new SimpleElementValue(115, cg.getConstantPool(), pointcut.expression);
            ArrayList<NameValuePair> elems = new ArrayList<NameValuePair>();
            elems.add(new NameValuePair("value", (ElementValue)svg, cg.getConstantPool()));
            AnnotationGen mag = new AnnotationGen(new ObjectType("org/aspectj/lang/annotation/Pointcut"), elems, true, cg.getConstantPool());
            BcelAnnotation max = new BcelAnnotation(mag, this.world);
            mg.addAnnotation(max);
            InstructionList body = mg.getBody();
            body.append(InstructionConstants.RETURN);
            cg.addMethodGen(mg);
        }
        if (this.concreteAspect.deows.size() > 0) {
            int counter = 1;
            for (Definition.DeclareErrorOrWarning deow : this.concreteAspect.deows) {
                FieldGen field = new FieldGen(16, ObjectType.STRING, "rule" + counter++, cg.getConstantPool());
                SimpleElementValue svg = new SimpleElementValue(115, cg.getConstantPool(), deow.pointcut);
                ArrayList<NameValuePair> elems = new ArrayList<NameValuePair>();
                elems.add(new NameValuePair("value", (ElementValue)svg, cg.getConstantPool()));
                AnnotationGen mag = new AnnotationGen(new ObjectType("org/aspectj/lang/annotation/Declare" + (deow.isError ? "Error" : "Warning")), elems, true, cg.getConstantPool());
                field.addAnnotation(mag);
                field.setValue(deow.message);
                cg.addField(field, null);
            }
        }
        if (this.concreteAspect.pointcutsAndAdvice.size() > 0) {
            int adviceCounter = 1;
            for (Definition.PointcutAndAdvice paa : this.concreteAspect.pointcutsAndAdvice) {
                this.generateAdviceMethod(paa, adviceCounter, cg);
                ++adviceCounter;
            }
        }
        if (this.concreteAspect.declareAnnotations.size() > 0) {
            int decCounter = 1;
            for (Definition.DeclareAnnotation da : this.concreteAspect.declareAnnotations) {
                this.generateDeclareAnnotation(da, decCounter++, cg);
            }
        }
        ReferenceType rt = new ReferenceType(ResolvedType.forName(this.concreteAspect.name).getSignature(), this.world);
        GeneratedReferenceTypeDelegate generatedReferenceTypeDelegate = new GeneratedReferenceTypeDelegate(rt);
        generatedReferenceTypeDelegate.setSuperclass(this.parent);
        rt.setDelegate(generatedReferenceTypeDelegate);
        BcelPerClauseAspectAdder perClauseMunger = new BcelPerClauseAspectAdder(rt, perclauseKind);
        perClauseMunger.forceMunge(cg, false);
        JavaClass jc = cg.getJavaClass((BcelWorld)this.world);
        ((BcelWorld)this.world).addSourceObjectType(jc, true);
        this.bytes = jc.getBytes();
        return this.bytes;
    }

    private void generateDeclareAnnotation(Definition.DeclareAnnotation da, int decCounter, LazyClassGen cg) {
        AnnotationAJ constructedAnnotation = this.buildDeclareAnnotation_actualAnnotation(cg, da);
        if (constructedAnnotation == null) {
            return;
        }
        String nameComponent = da.declareAnnotationKind.name().toLowerCase();
        String declareName = "ajc$declare_at_" + nameComponent + "_" + decCounter;
        LazyMethodGen declareMethod = new LazyMethodGen(1, Type.VOID, declareName, Type.NO_ARGS, EMPTY_STRINGS, cg);
        InstructionList declareMethodBody = declareMethod.getBody();
        declareMethodBody.append(InstructionFactory.RETURN);
        declareMethod.addAnnotation(constructedAnnotation);
        DeclareAnnotation deca = null;
        ITokenSource tokenSource = BasicTokenSource.makeTokenSource(da.pattern, null);
        PatternParser pp = new PatternParser(tokenSource);
        if (da.declareAnnotationKind == Definition.DeclareAnnotationKind.Method || da.declareAnnotationKind == Definition.DeclareAnnotationKind.Field) {
            ISignaturePattern isp = da.declareAnnotationKind == Definition.DeclareAnnotationKind.Method ? pp.parseCompoundMethodOrConstructorSignaturePattern(true) : pp.parseCompoundFieldSignaturePattern();
            deca = new DeclareAnnotation(da.declareAnnotationKind == Definition.DeclareAnnotationKind.Method ? DeclareAnnotation.AT_METHOD : DeclareAnnotation.AT_FIELD, isp);
        } else if (da.declareAnnotationKind == Definition.DeclareAnnotationKind.Type) {
            TypePattern tp = pp.parseTypePattern();
            deca = new DeclareAnnotation(DeclareAnnotation.AT_TYPE, tp);
        }
        deca.setAnnotationMethod(declareName);
        deca.setAnnotationString(da.annotation);
        AjAttribute.DeclareAttribute attribute = new AjAttribute.DeclareAttribute(deca);
        cg.addAttribute(attribute);
        cg.addMethodGen(declareMethod);
    }

    private AnnotationAJ buildDeclareAnnotation_actualAnnotation(LazyClassGen cg, Definition.DeclareAnnotation da) {
        AnnotationGen anno = this.buildAnnotationFromString(cg.getConstantPool(), cg.getWorld(), da.annotation);
        if (anno == null) {
            return null;
        }
        BcelAnnotation bcelAnnotation = new BcelAnnotation(anno, this.world);
        return bcelAnnotation;
    }

    private AnnotationGen buildAnnotationFromString(ConstantPool cp, World w, String annotationString) {
        AnnotationGen aaj;
        char ch;
        int pos;
        int paren = annotationString.indexOf(40);
        if (paren == -1) {
            AnnotationGen aaj2 = this.buildBaseAnnotationType(cp, this.world, annotationString);
            return aaj2;
        }
        String name = annotationString.substring(0, paren);
        ArrayList<String> values = new ArrayList<String>();
        int depth = 0;
        int len = annotationString.length();
        int start = pos;
        for (pos = paren + 1; pos < len && ((ch = annotationString.charAt(pos)) != ')' || depth != 0); ++pos) {
            if (ch == '(' || ch == '[') {
                ++depth;
            } else if (ch == ')' || ch == ']') {
                --depth;
            }
            if (ch != ',' || depth != 0) continue;
            values.add(annotationString.substring(start, pos).trim());
            start = pos + 1;
        }
        if (start != pos) {
            values.add(annotationString.substring(start, pos).trim());
        }
        if ((aaj = this.buildBaseAnnotationType(cp, this.world, name)) == null) {
            return null;
        }
        String typename = aaj.getTypeName();
        ResolvedType type = UnresolvedType.forName(typename).resolve(this.world);
        ResolvedMember[] rms = type.getDeclaredMethods();
        for (String value : values) {
            int equalsIndex = value.indexOf("=");
            String key = "value";
            if (value.charAt(0) != '\"' && equalsIndex != -1) {
                key = value.substring(0, equalsIndex).trim();
                value = value.substring(equalsIndex + 1).trim();
            }
            boolean keyIsOk = false;
            for (int m = 0; m < rms.length; ++m) {
                NameValuePair nvp;
                block41: {
                    UnresolvedType rt;
                    block42: {
                        nvp = null;
                        if (!rms[m].getName().equals(key)) break block41;
                        keyIsOk = true;
                        rt = rms[m].getReturnType();
                        if (!rt.isPrimitiveType()) break block42;
                        switch (rt.getSignature().charAt(0)) {
                            case 'J': {
                                try {
                                    long longValue = Long.parseLong(value);
                                    nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(74, cp, longValue), cp);
                                    break block41;
                                }
                                catch (NumberFormatException nfe) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as a long");
                                    return null;
                                }
                            }
                            case 'S': {
                                try {
                                    short shortValue = Short.parseShort(value);
                                    nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(83, cp, shortValue), cp);
                                    break block41;
                                }
                                catch (NumberFormatException nfe) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as a short");
                                    return null;
                                }
                            }
                            case 'F': {
                                try {
                                    float floatValue = Float.parseFloat(value);
                                    nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(70, cp, floatValue), cp);
                                    break block41;
                                }
                                catch (NumberFormatException nfe) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as a float");
                                    return null;
                                }
                            }
                            case 'D': {
                                try {
                                    double doubleValue = Double.parseDouble(value);
                                    nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(68, cp, doubleValue), cp);
                                    break block41;
                                }
                                catch (NumberFormatException nfe) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as a double");
                                    return null;
                                }
                            }
                            case 'I': {
                                try {
                                    int intValue = Integer.parseInt(value);
                                    nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(73, cp, intValue), cp);
                                    break block41;
                                }
                                catch (NumberFormatException nfe) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as an integer");
                                    return null;
                                }
                            }
                            case 'B': {
                                try {
                                    byte byteValue = Byte.parseByte(value);
                                    nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(66, cp, byteValue), cp);
                                    break block41;
                                }
                                catch (NumberFormatException nfe) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as a byte");
                                    return null;
                                }
                            }
                            case 'C': {
                                if (value.length() < 2) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as a char");
                                    return null;
                                }
                                nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(67, cp, value.charAt(1)), cp);
                                break block41;
                            }
                            case 'Z': {
                                try {
                                    boolean booleanValue = Boolean.parseBoolean(value);
                                    nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(90, cp, booleanValue), cp);
                                    break block41;
                                }
                                catch (NumberFormatException nfe) {
                                    this.reportError("unable to interpret annotation value '" + value + "' as a boolean");
                                    return null;
                                }
                            }
                            default: {
                                this.reportError("not yet supporting XML setting of annotation values of type " + rt.getName());
                                return null;
                            }
                        }
                    }
                    if (UnresolvedType.JL_STRING.equals(rt)) {
                        if (value.length() < 2) {
                            this.reportError("Invalid string value specified in annotation string: " + annotationString);
                            return null;
                        }
                        value = value.substring(1, value.length() - 1);
                        nvp = new NameValuePair(key, (ElementValue)new SimpleElementValue(115, cp, value), cp);
                    } else if (UnresolvedType.JL_CLASS.equals(rt)) {
                        boolean qualified;
                        if (value.length() < 6) {
                            this.reportError("Not a well formed class value for an annotation '" + value + "'");
                            return null;
                        }
                        String clazz = value.substring(0, value.length() - 6);
                        boolean bl = qualified = clazz.indexOf(".") != -1;
                        if (!qualified) {
                            clazz = "java.lang." + clazz;
                        }
                        nvp = new NameValuePair(key, (ElementValue)new ClassElementValue(new ObjectType(clazz), cp), cp);
                    }
                }
                if (nvp == null) continue;
                aaj.addElementNameValuePair(nvp);
            }
            if (keyIsOk) continue;
            this.reportError("annotation @" + typename + " does not have a value named " + key);
            return null;
        }
        return aaj;
    }

    private AnnotationGen buildBaseAnnotationType(ConstantPool cp, World world, String typename) {
        ResolvedType annotationType;
        String annoname = typename;
        if (annoname.startsWith("@")) {
            annoname = annoname.substring(1);
        }
        if (!(annotationType = UnresolvedType.forName(annoname).resolve(world)).isAnnotation()) {
            this.reportError("declare is not specifying an annotation type :" + typename);
            return null;
        }
        if (!annotationType.isAnnotationWithRuntimeRetention()) {
            this.reportError("declare is using an annotation type that does not have runtime retention: " + typename);
            return null;
        }
        ArrayList<NameValuePair> elems = new ArrayList<NameValuePair>();
        return new AnnotationGen(new ObjectType(annoname), elems, true, cp);
    }

    private void generateAdviceMethod(Definition.PointcutAndAdvice paa, int adviceCounter, LazyClassGen cg) {
        ResolvedType delegateClass = this.world.resolve(UnresolvedType.forName(paa.adviceClass));
        if (delegateClass.isMissing()) {
            this.reportError("Class to invoke cannot be found: '" + paa.adviceClass + "'");
            return;
        }
        String adviceName = "generated$" + paa.adviceKind.toString().toLowerCase() + "$advice$" + adviceCounter;
        AnnotationAJ aaj = this.buildAdviceAnnotation(cg, paa);
        String method = paa.adviceMethod;
        int paren = method.indexOf("(");
        String methodName = method.substring(0, paren);
        String signature = method.substring(paren);
        if (signature.charAt(0) != '(' || !signature.endsWith(")")) {
            this.reportError("Badly formatted parameter signature: '" + method + "'");
            return;
        }
        ArrayList<Type> paramTypes = new ArrayList<Type>();
        ArrayList<String> paramNames = new ArrayList<String>();
        if (signature.charAt(1) != ')') {
            StringBuilder convertedSignature = new StringBuilder("(");
            boolean paramsBroken = false;
            int pos = 1;
            while (pos < signature.length() && signature.charAt(pos) != ')' && !paramsBroken) {
                int nextChunkEndPos = signature.indexOf(44, pos);
                if (nextChunkEndPos == -1) {
                    nextChunkEndPos = signature.indexOf(41, pos);
                }
                String nextChunk = signature.substring(pos, nextChunkEndPos).trim();
                int space = nextChunk.indexOf(" ");
                ResolvedType resolvedParamType = null;
                if (space == -1) {
                    if (nextChunk.equals("JoinPoint")) {
                        nextChunk = "org.aspectj.lang.JoinPoint";
                    } else if (nextChunk.equals("JoinPoint.StaticPart")) {
                        nextChunk = "org.aspectj.lang.JoinPoint$StaticPart";
                    } else if (nextChunk.equals("ProceedingJoinPoint")) {
                        nextChunk = "org.aspectj.lang.ProceedingJoinPoint";
                    }
                    UnresolvedType unresolvedParamType = UnresolvedType.forName(nextChunk);
                    resolvedParamType = this.world.resolve(unresolvedParamType);
                } else {
                    String typename = nextChunk.substring(0, space);
                    if (typename.equals("JoinPoint")) {
                        typename = "org.aspectj.lang.JoinPoint";
                    } else if (typename.equals("JoinPoint.StaticPart")) {
                        typename = "org.aspectj.lang.JoinPoint$StaticPart";
                    } else if (typename.equals("ProceedingJoinPoint")) {
                        typename = "org.aspectj.lang.ProceedingJoinPoint";
                    }
                    UnresolvedType unresolvedParamType = UnresolvedType.forName(typename);
                    resolvedParamType = this.world.resolve(unresolvedParamType);
                    String paramname = nextChunk.substring(space).trim();
                    paramNames.add(paramname);
                }
                if (resolvedParamType.isMissing()) {
                    this.reportError("Cannot find type specified as parameter: '" + nextChunk + "' from signature '" + signature + "'");
                    paramsBroken = true;
                }
                paramTypes.add(Type.getType(resolvedParamType.getSignature()));
                convertedSignature.append(resolvedParamType.getSignature());
                pos = nextChunkEndPos + 1;
            }
            convertedSignature.append(")");
            signature = convertedSignature.toString();
            if (paramsBroken) {
                return;
            }
        }
        Type returnType = Type.VOID;
        if (paa.adviceKind == Definition.AdviceKind.Around) {
            ResolvedMember[] methods = delegateClass.getDeclaredMethods();
            Member found = null;
            for (ResolvedMember candidate : methods) {
                UnresolvedType[] cparms;
                if (!candidate.getName().equals(methodName) || (cparms = candidate.getParameterTypes()).length != paramTypes.size()) continue;
                boolean paramsMatch = true;
                for (int i = 0; i < cparms.length; ++i) {
                    if (cparms[i].getSignature().equals(((Type)paramTypes.get(i)).getSignature())) continue;
                    paramsMatch = false;
                    break;
                }
                if (!paramsMatch) continue;
                found = candidate;
                break;
            }
            if (found != null) {
                returnType = Type.getType(found.getReturnType().getSignature());
            } else {
                this.reportError("Unable to find method to invoke.  In class: " + delegateClass.getName() + " cant find " + paa.adviceMethod);
                return;
            }
        }
        LazyMethodGen advice = new LazyMethodGen(1, returnType, adviceName, paramTypes.toArray(new Type[paramTypes.size()]), EMPTY_STRINGS, cg);
        InstructionList adviceBody = advice.getBody();
        int pos = 1;
        for (int i = 0; i < paramTypes.size(); ++i) {
            adviceBody.append(InstructionFactory.createLoad((Type)paramTypes.get(i), pos));
            pos += ((Type)paramTypes.get(i)).getSize();
        }
        adviceBody.append(cg.getFactory().createInvoke(paa.adviceClass, methodName, signature + returnType.getSignature(), (short)184));
        if (returnType == Type.VOID) {
            adviceBody.append(InstructionConstants.RETURN);
        } else if (returnType.getSignature().length() < 2) {
            String sig = returnType.getSignature();
            if (sig.equals("F")) {
                adviceBody.append(InstructionConstants.FRETURN);
            } else if (sig.equals("D")) {
                adviceBody.append(InstructionConstants.DRETURN);
            } else if (sig.equals("J")) {
                adviceBody.append(InstructionConstants.LRETURN);
            } else {
                adviceBody.append(InstructionConstants.IRETURN);
            }
        } else {
            adviceBody.append(InstructionConstants.ARETURN);
        }
        advice.addAnnotation(aaj);
        InstructionHandle start = adviceBody.getStart();
        String sig = this.concreteAspect.name.replace('.', '/');
        start.addTargeter(new LocalVariableTag("L" + sig + ";", "this", 0, start.getPosition()));
        if (paramNames.size() > 0) {
            for (int i = 0; i < paramNames.size(); ++i) {
                start.addTargeter(new LocalVariableTag(((Type)paramTypes.get(i)).getSignature(), (String)paramNames.get(i), i + 1, start.getPosition()));
            }
        }
        cg.addMethodGen(advice);
    }

    private AnnotationAJ buildAdviceAnnotation(LazyClassGen cg, Definition.PointcutAndAdvice paa) {
        SimpleElementValue svg = new SimpleElementValue(115, cg.getConstantPool(), paa.pointcut);
        ArrayList<NameValuePair> elems = new ArrayList<NameValuePair>();
        elems.add(new NameValuePair("value", (ElementValue)svg, cg.getConstantPool()));
        AnnotationGen mag = new AnnotationGen(new ObjectType("org/aspectj/lang/annotation/" + paa.adviceKind.toString()), elems, true, cg.getConstantPool());
        BcelAnnotation aaj = new BcelAnnotation(mag, this.world);
        return aaj;
    }

    private void reportError(String message) {
        this.world.getMessageHandler().handleMessage(new Message(message, IMessage.ERROR, null, null));
    }
}

