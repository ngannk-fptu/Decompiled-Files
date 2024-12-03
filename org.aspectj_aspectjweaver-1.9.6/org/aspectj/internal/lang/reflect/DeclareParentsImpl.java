/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Type;
import org.aspectj.internal.lang.reflect.StringToType;
import org.aspectj.internal.lang.reflect.TypePatternImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareParents;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareParentsImpl
implements DeclareParents {
    private AjType<?> declaringType;
    private TypePattern targetTypesPattern;
    private Type[] parents;
    private String parentsString;
    private String firstMissingTypeName;
    private boolean isExtends;
    private boolean parentsError = false;

    public DeclareParentsImpl(String targets, String parentsAsString, boolean isExtends, AjType<?> declaring) {
        this.targetTypesPattern = new TypePatternImpl(targets);
        this.isExtends = isExtends;
        this.declaringType = declaring;
        this.parentsString = parentsAsString;
        try {
            this.parents = StringToType.commaSeparatedListToTypeArray(parentsAsString, declaring.getJavaClass());
        }
        catch (ClassNotFoundException cnfEx) {
            this.parentsError = true;
            this.firstMissingTypeName = cnfEx.getMessage();
        }
    }

    @Override
    public AjType getDeclaringType() {
        return this.declaringType;
    }

    @Override
    public TypePattern getTargetTypesPattern() {
        return this.targetTypesPattern;
    }

    @Override
    public boolean isExtends() {
        return this.isExtends;
    }

    @Override
    public boolean isImplements() {
        return !this.isExtends;
    }

    @Override
    public Type[] getParentTypes() throws ClassNotFoundException {
        if (this.parentsError) {
            throw new ClassNotFoundException(this.firstMissingTypeName);
        }
        return this.parents;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare parents : ");
        sb.append(this.getTargetTypesPattern().asString());
        sb.append(this.isExtends() ? " extends " : " implements ");
        sb.append(this.parentsString);
        return sb.toString();
    }
}

