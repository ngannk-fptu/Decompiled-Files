/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.util.List;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.classfile.annotation.EnumElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.apache.bcel.classfile.annotation.SimpleElementValue;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.bcel.AnnotationAccessVar;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelVar;

class AnnotationAccessFieldVar
extends BcelVar {
    private AnnotationAccessVar annoAccessor;
    private ResolvedType annoFieldOfInterest;
    private String name;
    private int elementValueType;

    public AnnotationAccessFieldVar(AnnotationAccessVar aav, ResolvedType annoFieldOfInterest, String name) {
        block8: {
            String sig;
            block7: {
                super(annoFieldOfInterest, 0);
                this.annoAccessor = aav;
                this.name = name;
                sig = annoFieldOfInterest.getSignature();
                if (sig.length() != 1) break block7;
                switch (sig.charAt(0)) {
                    case 'I': {
                        this.elementValueType = 73;
                        break block8;
                    }
                    default: {
                        throw new IllegalStateException(sig);
                    }
                }
            }
            if (sig.equals("Ljava/lang/String;")) {
                this.elementValueType = 115;
            } else if (annoFieldOfInterest.isEnum()) {
                this.elementValueType = 101;
            } else {
                throw new IllegalStateException(sig);
            }
        }
        this.annoFieldOfInterest = annoFieldOfInterest;
    }

    @Override
    public void appendLoadAndConvert(InstructionList il, InstructionFactory fact, ResolvedType toType) {
        AnnotationAJ[] annos;
        if (this.annoAccessor.getKind() != Shadow.MethodExecution) {
            return;
        }
        String annotationOfInterestSignature = this.annoAccessor.getType().getSignature();
        Member holder = this.annoAccessor.getMember();
        for (AnnotationAJ anno : annos = holder.getAnnotations()) {
            AnnotationGen annotation = ((BcelAnnotation)anno).getBcelAnnotation();
            boolean foundValueInAnnotationUsage = false;
            if (annotation.getTypeSignature().equals(annotationOfInterestSignature)) {
                ResolvedMember[] annotationFields = toType.getWorld().resolve(UnresolvedType.forSignature(annotation.getTypeSignature())).getDeclaredMethods();
                int countOfType = 0;
                for (ResolvedMember annotationField : annotationFields) {
                    if (!annotationField.getType().equals(this.annoFieldOfInterest)) continue;
                    ++countOfType;
                }
                List<NameValuePair> nvps = annotation.getValues();
                for (NameValuePair nvp : nvps) {
                    ElementValue v;
                    ElementValue o;
                    if (countOfType > 1 && !nvp.getNameString().equals(this.name) || (o = nvp.getValue()).getElementValueType() != this.elementValueType) continue;
                    if (o instanceof EnumElementValue) {
                        v = (EnumElementValue)o;
                        String s = ((EnumElementValue)v).getEnumTypeString();
                        ResolvedType rt = toType.getWorld().resolve(UnresolvedType.forSignature(s));
                        if (rt.equals(toType)) {
                            il.append(fact.createGetStatic(rt.getName(), ((EnumElementValue)v).getEnumValueString(), Type.getType(rt.getSignature())));
                            foundValueInAnnotationUsage = true;
                        }
                    } else if (o instanceof SimpleElementValue) {
                        v = (SimpleElementValue)o;
                        switch (v.getElementValueType()) {
                            case 73: {
                                il.append(fact.createConstant(((SimpleElementValue)v).getValueInt()));
                                foundValueInAnnotationUsage = true;
                                break;
                            }
                            case 115: {
                                il.append(fact.createConstant(((SimpleElementValue)v).getValueString()));
                                foundValueInAnnotationUsage = true;
                                break;
                            }
                            default: {
                                throw new IllegalStateException("NYI: Unsupported annotation value binding for " + o);
                            }
                        }
                    }
                    if (!foundValueInAnnotationUsage) continue;
                    break;
                }
                if (!foundValueInAnnotationUsage) {
                    for (ResolvedMember annotationField : annotationFields) {
                        if (countOfType > 1 && !annotationField.getName().equals(this.name) || !annotationField.getType().getSignature().equals(this.annoFieldOfInterest.getSignature())) continue;
                        if (annotationField.getType().getSignature().equals("I")) {
                            int ivalue = Integer.parseInt(annotationField.getAnnotationDefaultValue());
                            il.append(fact.createConstant(ivalue));
                            foundValueInAnnotationUsage = true;
                            break;
                        }
                        if (annotationField.getType().getSignature().equals("Ljava/lang/String;")) {
                            String svalue = annotationField.getAnnotationDefaultValue();
                            il.append(fact.createConstant(svalue));
                            foundValueInAnnotationUsage = true;
                            break;
                        }
                        String dvalue = annotationField.getAnnotationDefaultValue();
                        String typename = dvalue.substring(0, dvalue.lastIndexOf(59) + 1);
                        String field = dvalue.substring(dvalue.lastIndexOf(59) + 1);
                        ResolvedType rt = toType.getWorld().resolve(UnresolvedType.forSignature(typename));
                        il.append(fact.createGetStatic(rt.getName(), field, Type.getType(rt.getSignature())));
                        foundValueInAnnotationUsage = true;
                        break;
                    }
                }
            }
            if (foundValueInAnnotationUsage) break;
        }
    }

    @Override
    public void insertLoad(InstructionList il, InstructionFactory fact) {
        if (this.annoAccessor.getKind() != Shadow.MethodExecution) {
            return;
        }
        this.appendLoadAndConvert(il, fact, this.annoFieldOfInterest);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

