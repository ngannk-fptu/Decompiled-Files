/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReference;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;

public class ReferencePointcut
extends Pointcut {
    public UnresolvedType onType;
    public TypePattern onTypeSymbolic;
    public String name;
    public TypePatternList arguments;
    private Map<String, UnresolvedType> typeVariableMap;
    private boolean concretizing = false;

    public ReferencePointcut(TypePattern onTypeSymbolic, String name, TypePatternList arguments) {
        this.onTypeSymbolic = onTypeSymbolic;
        this.name = name;
        this.arguments = arguments;
        this.pointcutKind = (byte)8;
    }

    public ReferencePointcut(UnresolvedType onType, String name, TypePatternList arguments) {
        this.onType = onType;
        this.name = name;
        this.arguments = arguments;
        this.pointcutKind = (byte)8;
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        return FuzzyBoolean.NO;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (this.onType != null) {
            buf.append(this.onType);
            buf.append(".");
        }
        buf.append(this.name);
        buf.append(this.arguments.toString());
        return buf.toString();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(8);
        if (this.onType != null) {
            s.writeBoolean(true);
            this.onType.write(s);
        } else {
            s.writeBoolean(false);
        }
        s.writeUTF(this.name);
        this.arguments.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        UnresolvedType onType = null;
        if (s.readBoolean()) {
            onType = UnresolvedType.read(s);
        }
        ReferencePointcut ret = new ReferencePointcut(onType, s.readUTF(), TypePatternList.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        ResolvedType[] parameterTypes;
        ResolvedType searchType;
        if (this.onTypeSymbolic != null) {
            this.onType = this.onTypeSymbolic.resolveExactType(scope, bindings);
            if (ResolvedType.isMissing(this.onType)) {
                return;
            }
        }
        if ((searchType = this.onType != null ? scope.getWorld().resolve(this.onType) : scope.getEnclosingType()).isTypeVariableReference()) {
            searchType = ((TypeVariableReference)((Object)searchType)).getTypeVariable().getFirstBound().resolve(scope.getWorld());
        }
        this.arguments.resolveBindings(scope, bindings, true, true);
        ResolvedPointcutDefinition pointcutDef = searchType.findPointcut(this.name);
        if (pointcutDef == null && this.onType == null) {
            ResolvedType declaringType;
            while ((declaringType = searchType.getDeclaringType()) != null) {
                searchType = declaringType.resolve(scope.getWorld());
                pointcutDef = searchType.findPointcut(this.name);
                if (pointcutDef == null) continue;
                this.onType = searchType;
                break;
            }
        }
        if (pointcutDef == null) {
            scope.message(IMessage.ERROR, this, "can't find referenced pointcut " + this.name);
            return;
        }
        if (!pointcutDef.isVisible(scope.getEnclosingType())) {
            scope.message(IMessage.ERROR, this, "pointcut declaration " + pointcutDef + " is not accessible");
            return;
        }
        if (Modifier.isAbstract(pointcutDef.getModifiers())) {
            if (this.onType != null && !this.onType.isTypeVariableReference()) {
                scope.message(IMessage.ERROR, this, "can't make static reference to abstract pointcut");
                return;
            }
            if (!searchType.isAbstract()) {
                scope.message(IMessage.ERROR, this, "can't use abstract pointcut in concrete context");
                return;
            }
        }
        if ((parameterTypes = scope.getWorld().resolve(pointcutDef.getParameterTypes())).length != this.arguments.size()) {
            scope.message(IMessage.ERROR, this, "incompatible number of arguments to pointcut, expected " + parameterTypes.length + " found " + this.arguments.size());
            return;
        }
        if (this.onType != null) {
            if (this.onType.isParameterizedType()) {
                this.typeVariableMap = new HashMap<String, UnresolvedType>();
                ReferenceType underlyingGenericType = ((ResolvedType)this.onType).getGenericType();
                TypeVariable[] tVars = ((UnresolvedType)underlyingGenericType).getTypeVariables();
                ResolvedType[] typeParams = ((ResolvedType)this.onType).getResolvedTypeParameters();
                for (int i = 0; i < tVars.length; ++i) {
                    this.typeVariableMap.put(tVars[i].getName(), typeParams[i]);
                }
            } else if (this.onType.isGenericType()) {
                scope.message(MessageUtil.error(WeaverMessages.format("noRawTypePointcutReferences"), this.getSourceLocation()));
            }
        }
        int len = this.arguments.size();
        for (int i = 0; i < len; ++i) {
            TypePattern p = this.arguments.get(i);
            if (this.typeVariableMap != null) {
                p = p.parameterizeWith(this.typeVariableMap, scope.getWorld());
            }
            if (p == TypePattern.NO) {
                scope.message(IMessage.ERROR, this, "bad parameter to pointcut reference");
                return;
            }
            boolean reportProblem = false;
            if (parameterTypes[i].isTypeVariableReference() && p.getExactType().isTypeVariableReference()) {
                UnresolvedType One = ((TypeVariableReference)((Object)parameterTypes[i])).getTypeVariable().getFirstBound();
                UnresolvedType Two = ((TypeVariableReference)((Object)p.getExactType())).getTypeVariable().getFirstBound();
                reportProblem = !One.resolve(scope.getWorld()).isAssignableFrom(Two.resolve(scope.getWorld()));
            } else {
                boolean bl = reportProblem = !p.matchesSubtypes(parameterTypes[i]) && !p.getExactType().equals(UnresolvedType.OBJECT);
            }
            if (!reportProblem) continue;
            scope.message(IMessage.ERROR, this, "incompatible type, expected " + parameterTypes[i].getName() + " found " + p + ".  Check the type specified in your pointcut");
            return;
        }
    }

    @Override
    public void postRead(ResolvedType enclosingType) {
        this.arguments.postRead(enclosingType);
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        throw new RuntimeException("shouldn't happen");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Pointcut concretize1(ResolvedType searchStart, ResolvedType declaringType, IntMap bindings) {
        if (this.concretizing) {
            searchStart.getWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("circularPointcutDeclaration", this), this.getSourceLocation()));
            Pointcut p = Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
            p.sourceContext = this.sourceContext;
            return p;
        }
        try {
            ResolvedPointcutDefinition pointcutDec;
            boolean foundMatchingPointcut;
            Object typeVariableName;
            this.concretizing = true;
            if (this.onType != null) {
                if ((searchStart = this.onType.resolve(searchStart.getWorld())).isMissing()) {
                    Pointcut pointcut = Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
                    return pointcut;
                }
                if (this.onType.isTypeVariableReference() && declaringType.isParameterizedType()) {
                    TypeVariable[] tvs = declaringType.getGenericType().getTypeVariables();
                    typeVariableName = ((TypeVariableReference)((Object)this.onType)).getTypeVariable().getName();
                    for (int i = 0; i < tvs.length; ++i) {
                        if (!tvs[i].getName().equals(typeVariableName)) continue;
                        ResolvedType realOnType = declaringType.getTypeParameters()[i].resolve(declaringType.getWorld());
                        this.onType = realOnType;
                        searchStart = realOnType;
                        break;
                    }
                }
            }
            if (declaringType == null) {
                declaringType = searchStart;
            }
            boolean bl = foundMatchingPointcut = (pointcutDec = declaringType.findPointcut(this.name)) != null && Modifier.isPrivate(pointcutDec.getModifiers());
            if (!foundMatchingPointcut && (pointcutDec = searchStart.findPointcut(this.name)) == null) {
                searchStart.getWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("cantFindPointcut", this.name, searchStart.getName()), this.getSourceLocation()));
                typeVariableName = Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
                return typeVariableName;
            }
            if (pointcutDec.isAbstract()) {
                ShadowMunger enclosingAdvice = bindings.getEnclosingAdvice();
                searchStart.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("abstractPointcut", pointcutDec), this.getSourceLocation(), null == enclosingAdvice ? null : enclosingAdvice.getSourceLocation());
                Pointcut i = Pointcut.makeMatchesNothing(Pointcut.CONCRETE);
                return i;
            }
            TypePatternList arguments = this.arguments.resolveReferences(bindings);
            IntMap newBindings = new IntMap();
            int len = arguments.size();
            for (int i = 0; i < len; ++i) {
                TypePattern p = arguments.get(i);
                if (p == TypePattern.NO || !(p instanceof BindingTypePattern)) continue;
                newBindings.put(i, ((BindingTypePattern)p).getFormalIndex());
            }
            if (searchStart.isParameterizedType()) {
                this.typeVariableMap = new HashMap<String, UnresolvedType>();
                ReferenceType underlyingGenericType = searchStart.getGenericType();
                TypeVariable[] tVars = ((UnresolvedType)underlyingGenericType).getTypeVariables();
                ResolvedType[] typeParams = searchStart.getResolvedTypeParameters();
                for (int i = 0; i < tVars.length; ++i) {
                    this.typeVariableMap.put(tVars[i].getName(), typeParams[i]);
                }
            }
            newBindings.copyContext(bindings);
            newBindings.pushEnclosingDefinition(pointcutDec);
            try {
                Pointcut ret = pointcutDec.getPointcut();
                if (this.typeVariableMap != null && !this.hasBeenParameterized) {
                    ret = ret.parameterizeWith(this.typeVariableMap, searchStart.getWorld());
                    ret.hasBeenParameterized = true;
                }
                Pointcut pointcut = ret.concretize(searchStart, declaringType, newBindings);
                newBindings.popEnclosingDefinitition();
                return pointcut;
            }
            catch (Throwable throwable) {
                newBindings.popEnclosingDefinitition();
                throw throwable;
            }
        }
        finally {
            this.concretizing = false;
        }
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        ReferencePointcut ret = new ReferencePointcut(this.onType, this.name, this.arguments);
        ret.onTypeSymbolic = this.onTypeSymbolic;
        ret.typeVariableMap = typeVariableMap;
        return ret;
    }

    @Override
    protected boolean shouldCopyLocationForConcretize() {
        return false;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ReferencePointcut)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        ReferencePointcut o = (ReferencePointcut)other;
        return o.name.equals(this.name) && o.arguments.equals(this.arguments) && (o.onType == null ? this.onType == null : o.onType.equals(this.onType));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.onType == null ? 0 : this.onType.hashCode());
        result = 37 * result + this.arguments.hashCode();
        result = 37 * result + this.name.hashCode();
        return result;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

