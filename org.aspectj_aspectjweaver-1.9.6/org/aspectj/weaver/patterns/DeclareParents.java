/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;
import org.aspectj.weaver.patterns.WildChildFinder;

public class DeclareParents
extends Declare {
    protected TypePattern child;
    protected TypePatternList parents;
    private boolean isWildChild = false;
    protected boolean isExtends = true;

    public DeclareParents(TypePattern child, List<TypePattern> parents, boolean isExtends) {
        this(child, new TypePatternList(parents), isExtends);
    }

    protected DeclareParents(TypePattern child, TypePatternList parents, boolean isExtends) {
        this.child = child;
        this.parents = parents;
        this.isExtends = isExtends;
        WildChildFinder wildChildFinder = new WildChildFinder();
        child.accept(wildChildFinder, null);
        this.isWildChild = wildChildFinder.containedWildChild();
    }

    public boolean match(ResolvedType typeX) {
        if (!this.child.matchesStatically(typeX)) {
            return false;
        }
        if (typeX.getWorld().getLint().typeNotExposedToWeaver.isEnabled() && !typeX.isExposedToWeaver()) {
            typeX.getWorld().getLint().typeNotExposedToWeaver.signal(typeX.getName(), this.getSourceLocation());
        }
        return true;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Declare parameterizeWith(Map<String, UnresolvedType> typeVariableBindingMap, World w) {
        DeclareParents ret = new DeclareParents(this.child.parameterizeWith(typeVariableBindingMap, w), this.parents.parameterizeWith(typeVariableBindingMap, w), this.isExtends);
        ret.copyLocationFrom(this);
        return ret;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("declare parents: ");
        buf.append(this.child);
        buf.append(this.isExtends ? " extends " : " implements ");
        buf.append(this.parents);
        buf.append(";");
        return buf.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof DeclareParents)) {
            return false;
        }
        DeclareParents o = (DeclareParents)other;
        return o.child.equals(this.child) && o.parents.equals(this.parents);
    }

    public int hashCode() {
        int result = 23;
        result = 37 * result + this.child.hashCode();
        result = 37 * result + this.parents.hashCode();
        return result;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(2);
        this.child.write(s);
        this.parents.write(s);
        this.writeLocation(s);
    }

    public static Declare read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        DeclareParents ret = new DeclareParents(TypePattern.read(s, context), TypePatternList.read(s, context), true);
        ret.readLocation(context, s);
        return ret;
    }

    public boolean parentsIncludeInterface(World w) {
        for (int i = 0; i < this.parents.size(); ++i) {
            if (!this.parents.get(i).getExactType().resolve(w).isInterface()) continue;
            return true;
        }
        return false;
    }

    public boolean parentsIncludeClass(World w) {
        for (int i = 0; i < this.parents.size(); ++i) {
            if (!this.parents.get(i).getExactType().resolve(w).isClass()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void resolve(IScope scope) {
        TypePattern resolvedChild = this.child.resolveBindings(scope, Bindings.NONE, false, false);
        if (!resolvedChild.equals(this.child)) {
            WildChildFinder wildChildFinder = new WildChildFinder();
            resolvedChild.accept(wildChildFinder, null);
            this.isWildChild = wildChildFinder.containedWildChild();
            this.child = resolvedChild;
        }
        this.parents = this.parents.resolveBindings(scope, Bindings.NONE, false, true);
    }

    public TypePatternList getParents() {
        return this.parents;
    }

    public TypePattern getChild() {
        return this.child;
    }

    public boolean isExtends() {
        return this.isExtends;
    }

    @Override
    public boolean isAdviceLike() {
        return false;
    }

    private ResolvedType maybeGetNewParent(ResolvedType targetType, TypePattern typePattern, World world, boolean reportErrors) {
        boolean isOK;
        if (typePattern == TypePattern.NO) {
            return null;
        }
        UnresolvedType iType = typePattern.getExactType();
        ResolvedType parentType = iType.resolve(world);
        if (targetType.equals(world.getCoreType(UnresolvedType.OBJECT))) {
            world.showMessage(IMessage.ERROR, WeaverMessages.format("decpObject"), this.getSourceLocation(), null);
            return null;
        }
        if ((parentType.isParameterizedType() || parentType.isRawType()) && !(isOK = this.verifyNoInheritedAlternateParameterization(targetType, parentType, world))) {
            return null;
        }
        if (parentType.isAssignableFrom(targetType)) {
            return null;
        }
        if (reportErrors && this.isWildChild && targetType.isEnum()) {
            world.getLint().enumAsTargetForDecpIgnored.signal(targetType.toString(), this.getSourceLocation());
        }
        if (reportErrors && this.isWildChild && targetType.isAnnotation()) {
            world.getLint().annotationAsTargetForDecpIgnored.signal(targetType.toString(), this.getSourceLocation());
        }
        if (targetType.isEnum() && parentType.isInterface()) {
            if (reportErrors && !this.isWildChild) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("cantDecpOnEnumToImplInterface", targetType), this.getSourceLocation(), null);
            }
            return null;
        }
        if (targetType.isAnnotation() && parentType.isInterface()) {
            if (reportErrors && !this.isWildChild) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("cantDecpOnAnnotationToImplInterface", targetType), this.getSourceLocation(), null);
            }
            return null;
        }
        if (targetType.isEnum() && parentType.isClass()) {
            if (reportErrors && !this.isWildChild) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("cantDecpOnEnumToExtendClass", targetType), this.getSourceLocation(), null);
            }
            return null;
        }
        if (targetType.isAnnotation() && parentType.isClass()) {
            if (reportErrors && !this.isWildChild) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("cantDecpOnAnnotationToExtendClass", targetType), this.getSourceLocation(), null);
            }
            return null;
        }
        if (parentType.getSignature().equals(UnresolvedType.ENUM.getSignature())) {
            if (reportErrors && !this.isWildChild) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("cantDecpToMakeEnumSupertype", targetType), this.getSourceLocation(), null);
            }
            return null;
        }
        if (parentType.getSignature().equals(UnresolvedType.ANNOTATION.getSignature())) {
            if (reportErrors && !this.isWildChild) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("cantDecpToMakeAnnotationSupertype", targetType), this.getSourceLocation(), null);
            }
            return null;
        }
        if (parentType.isAssignableFrom(targetType)) {
            return null;
        }
        if (targetType.isAssignableFrom(parentType)) {
            world.showMessage(IMessage.ERROR, WeaverMessages.format("cantExtendSelf", targetType.getName()), this.getSourceLocation(), null);
            return null;
        }
        if (parentType.isClass()) {
            if (targetType.isInterface()) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("interfaceExtendClass"), this.getSourceLocation(), null);
                return null;
            }
            if (!targetType.getSuperclass().isAssignableFrom(parentType)) {
                world.showMessage(IMessage.ERROR, WeaverMessages.format("decpHierarchy", iType.getName(), targetType.getSuperclass().getName()), this.getSourceLocation(), null);
                return null;
            }
            return parentType;
        }
        return parentType;
    }

    private boolean verifyNoInheritedAlternateParameterization(ResolvedType typeToVerify, ResolvedType newParent, World world) {
        if (typeToVerify.equals(ResolvedType.OBJECT)) {
            return true;
        }
        ReferenceType newParentGenericType = newParent.getGenericType();
        Iterator<ResolvedType> iter = typeToVerify.getDirectSupertypes();
        while (iter.hasNext()) {
            ReferenceType generictype;
            ResolvedType supertype = iter.next();
            if ((supertype.isRawType() && newParent.isParameterizedType() || supertype.isParameterizedType() && newParent.isRawType()) && newParentGenericType.equals(supertype.getGenericType())) {
                world.getMessageHandler().handleMessage(new Message(WeaverMessages.format("cantDecpMultipleParameterizations", newParent.getName(), typeToVerify.getName(), supertype.getName()), this.getSourceLocation(), true, new ISourceLocation[]{typeToVerify.getSourceLocation()}));
                return false;
            }
            if (supertype.isParameterizedType() && ((ResolvedType)(generictype = supertype.getGenericType())).isAssignableFrom(newParentGenericType) && !supertype.isAssignableFrom(newParent)) {
                world.getMessageHandler().handleMessage(new Message(WeaverMessages.format("cantDecpMultipleParameterizations", newParent.getName(), typeToVerify.getName(), supertype.getName()), this.getSourceLocation(), true, new ISourceLocation[]{typeToVerify.getSourceLocation()}));
                return false;
            }
            if (this.verifyNoInheritedAlternateParameterization(supertype, newParent, world)) continue;
            return false;
        }
        return true;
    }

    public List<ResolvedType> findMatchingNewParents(ResolvedType onType, boolean reportErrors) {
        if (onType.isRawType()) {
            onType = onType.getGenericType();
        }
        if (!this.match(onType)) {
            return Collections.emptyList();
        }
        ArrayList<ResolvedType> ret = new ArrayList<ResolvedType>();
        for (int i = 0; i < this.parents.size(); ++i) {
            ResolvedType t = this.maybeGetNewParent(onType, this.parents.get(i), onType.getWorld(), reportErrors);
            if (t == null) continue;
            ret.add(t);
        }
        return ret;
    }

    @Override
    public String getNameSuffix() {
        return "parents";
    }

    public boolean isMixin() {
        return false;
    }
}

