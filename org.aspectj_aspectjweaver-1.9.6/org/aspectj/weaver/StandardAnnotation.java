/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.aspectj.weaver.AbstractAnnotationAJ;
import org.aspectj.weaver.AnnotationNameValuePair;
import org.aspectj.weaver.AnnotationValue;
import org.aspectj.weaver.ArrayAnnotationValue;
import org.aspectj.weaver.EnumAnnotationValue;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;

public class StandardAnnotation
extends AbstractAnnotationAJ {
    private final boolean isRuntimeVisible;
    private List<AnnotationNameValuePair> nvPairs = null;

    public StandardAnnotation(ResolvedType type, boolean isRuntimeVisible) {
        super(type);
        this.isRuntimeVisible = isRuntimeVisible;
    }

    @Override
    public boolean isRuntimeVisible() {
        return this.isRuntimeVisible;
    }

    @Override
    public String stringify() {
        StringBuffer sb = new StringBuffer();
        sb.append("@").append(this.type.getClassName());
        if (this.hasNameValuePairs()) {
            sb.append("(");
            for (AnnotationNameValuePair nvPair : this.nvPairs) {
                sb.append(nvPair.stringify());
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Anno[" + this.getTypeSignature() + " " + (this.isRuntimeVisible ? "rVis" : "rInvis"));
        if (this.nvPairs != null) {
            sb.append(" ");
            Iterator<AnnotationNameValuePair> iter = this.nvPairs.iterator();
            while (iter.hasNext()) {
                AnnotationNameValuePair element = iter.next();
                sb.append(element.toString());
                if (!iter.hasNext()) continue;
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean hasNamedValue(String n) {
        if (this.nvPairs == null) {
            return false;
        }
        for (int i = 0; i < this.nvPairs.size(); ++i) {
            AnnotationNameValuePair pair = this.nvPairs.get(i);
            if (!pair.getName().equals(n)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasNameValuePair(String n, String v) {
        if (this.nvPairs == null) {
            return false;
        }
        for (int i = 0; i < this.nvPairs.size(); ++i) {
            AnnotationNameValuePair pair = this.nvPairs.get(i);
            if (!pair.getName().equals(n) || !pair.getValue().stringify().equals(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<String> getTargets() {
        if (!this.type.equals(UnresolvedType.AT_TARGET)) {
            return Collections.emptySet();
        }
        AnnotationNameValuePair nvp = this.nvPairs.get(0);
        ArrayAnnotationValue aav = (ArrayAnnotationValue)nvp.getValue();
        AnnotationValue[] avs = aav.getValues();
        HashSet<String> targets = new HashSet<String>();
        for (int i = 0; i < avs.length; ++i) {
            EnumAnnotationValue value = (EnumAnnotationValue)avs[i];
            targets.add(value.getValue());
        }
        return targets;
    }

    public List<AnnotationNameValuePair> getNameValuePairs() {
        return this.nvPairs;
    }

    public boolean hasNameValuePairs() {
        return this.nvPairs != null && this.nvPairs.size() != 0;
    }

    public void addNameValuePair(AnnotationNameValuePair pair) {
        if (this.nvPairs == null) {
            this.nvPairs = new ArrayList<AnnotationNameValuePair>();
        }
        this.nvPairs.add(pair);
    }

    @Override
    public String getStringFormOfValue(String name) {
        if (this.hasNameValuePairs()) {
            for (AnnotationNameValuePair nvPair : this.nvPairs) {
                if (!nvPair.getName().equals(name)) continue;
                return nvPair.getValue().stringify();
            }
        }
        return null;
    }
}

