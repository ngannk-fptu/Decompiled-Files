/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.ArrayElementValue;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.classfile.annotation.EnumElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.weaver.AbstractAnnotationAJ;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class BcelAnnotation
extends AbstractAnnotationAJ {
    private final AnnotationGen bcelAnnotation;

    public BcelAnnotation(AnnotationGen theBcelAnnotation, World world) {
        super(UnresolvedType.forSignature(theBcelAnnotation.getTypeSignature()).resolve(world));
        this.bcelAnnotation = theBcelAnnotation;
    }

    public BcelAnnotation(AnnotationGen theBcelAnnotation, ResolvedType resolvedAnnotationType) {
        super(resolvedAnnotationType);
        this.bcelAnnotation = theBcelAnnotation;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        List<NameValuePair> nvPairs = this.bcelAnnotation.getValues();
        sb.append("Anno[" + this.getTypeSignature() + " " + (this.isRuntimeVisible() ? "rVis" : "rInvis"));
        if (nvPairs.size() > 0) {
            sb.append(" ");
            int i = 0;
            for (NameValuePair element : nvPairs) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(element.getNameString()).append("=").append(element.getValue().toString());
                ++i;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Set<String> getTargets() {
        if (!this.type.equals(UnresolvedType.AT_TARGET)) {
            return Collections.emptySet();
        }
        List<NameValuePair> values = this.bcelAnnotation.getValues();
        NameValuePair envp = values.get(0);
        ArrayElementValue aev = (ArrayElementValue)envp.getValue();
        ElementValue[] evs = aev.getElementValuesArray();
        HashSet<String> targets = new HashSet<String>();
        for (int i = 0; i < evs.length; ++i) {
            EnumElementValue ev = (EnumElementValue)evs[i];
            targets.add(ev.getEnumValueString());
        }
        return targets;
    }

    @Override
    public boolean hasNameValuePair(String name, String value) {
        return this.bcelAnnotation.hasNameValuePair(name, value);
    }

    @Override
    public boolean hasNamedValue(String name) {
        return this.bcelAnnotation.hasNamedValue(name);
    }

    @Override
    public String stringify() {
        StringBuffer sb = new StringBuffer();
        sb.append("@").append(this.type.getClassName());
        List<NameValuePair> values = this.bcelAnnotation.getValues();
        if (values != null && values.size() != 0) {
            sb.append("(");
            for (NameValuePair nvPair : values) {
                sb.append(nvPair.getNameString()).append("=").append(nvPair.getValue().stringifyValue());
            }
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean isRuntimeVisible() {
        return this.bcelAnnotation.isRuntimeVisible();
    }

    public AnnotationGen getBcelAnnotation() {
        return this.bcelAnnotation;
    }

    @Override
    public String getStringFormOfValue(String name) {
        List<NameValuePair> annotationValues = this.bcelAnnotation.getValues();
        if (annotationValues == null || annotationValues.size() == 0) {
            return null;
        }
        for (NameValuePair nvPair : annotationValues) {
            if (!nvPair.getNameString().equals(name)) continue;
            return nvPair.getValue().stringifyValue();
        }
        return null;
    }
}

