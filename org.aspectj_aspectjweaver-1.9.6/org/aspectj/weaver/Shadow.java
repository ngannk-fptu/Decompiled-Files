/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.PartialOrder;
import org.aspectj.util.TypeSafeEnum;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Var;

public abstract class Shadow {
    private static int nextShadowID = 100;
    private final Kind kind;
    private final Member signature;
    private Member matchingSignature;
    private ResolvedMember resolvedSignature;
    protected final Shadow enclosingShadow;
    protected List<ShadowMunger> mungers = Collections.emptyList();
    protected boolean needAroundClosureStacking = false;
    public int shadowId = nextShadowID++;
    public static String METHOD_EXECUTION = "method-execution";
    public static String METHOD_CALL = "method-call";
    public static String CONSTRUCTOR_EXECUTION = "constructor-execution";
    public static String CONSTRUCTOR_CALL = "constructor-call";
    public static String FIELD_GET = "field-get";
    public static String FIELD_SET = "field-set";
    public static String STATICINITIALIZATION = "staticinitialization";
    public static String PREINITIALIZATION = "preinitialization";
    public static String INITIALIZATION = "initialization";
    public static String EXCEPTION_HANDLER = "exception-handler";
    public static String SYNCHRONIZATION_LOCK = "lock";
    public static String SYNCHRONIZATION_UNLOCK = "unlock";
    public static String ADVICE_EXECUTION = "adviceexecution";
    public static final Kind MethodCall = new Kind(METHOD_CALL, 1, true);
    public static final Kind ConstructorCall = new Kind(CONSTRUCTOR_CALL, 2, true);
    public static final Kind MethodExecution = new Kind(METHOD_EXECUTION, 3, false);
    public static final Kind ConstructorExecution = new Kind(CONSTRUCTOR_EXECUTION, 4, false);
    public static final Kind FieldGet = new Kind(FIELD_GET, 5, true);
    public static final Kind FieldSet = new Kind(FIELD_SET, 6, true);
    public static final Kind StaticInitialization = new Kind(STATICINITIALIZATION, 7, false);
    public static final Kind PreInitialization = new Kind(PREINITIALIZATION, 8, false);
    public static final Kind AdviceExecution = new Kind(ADVICE_EXECUTION, 9, false);
    public static final Kind Initialization = new Kind(INITIALIZATION, 10, false);
    public static final Kind ExceptionHandler = new Kind(EXCEPTION_HANDLER, 11, true);
    public static final Kind SynchronizationLock = new Kind(SYNCHRONIZATION_LOCK, 12, true);
    public static final Kind SynchronizationUnlock = new Kind(SYNCHRONIZATION_UNLOCK, 13, true);
    public static final int MethodCallBit = 2;
    public static final int ConstructorCallBit = 4;
    public static final int MethodExecutionBit = 8;
    public static final int ConstructorExecutionBit = 16;
    public static final int FieldGetBit = 32;
    public static final int FieldSetBit = 64;
    public static final int StaticInitializationBit = 128;
    public static final int PreInitializationBit = 256;
    public static final int AdviceExecutionBit = 512;
    public static final int InitializationBit = 1024;
    public static final int ExceptionHandlerBit = 2048;
    public static final int SynchronizationLockBit = 4096;
    public static final int SynchronizationUnlockBit = 8192;
    public static final int MAX_SHADOW_KIND = 13;
    public static final Kind[] SHADOW_KINDS = new Kind[]{MethodCall, ConstructorCall, MethodExecution, ConstructorExecution, FieldGet, FieldSet, StaticInitialization, PreInitialization, AdviceExecution, Initialization, ExceptionHandler, SynchronizationLock, SynchronizationUnlock};
    public static final int ALL_SHADOW_KINDS_BITS = 16382;
    public static final int NO_SHADOW_KINDS_BITS = 0;

    protected Shadow(Kind kind, Member signature, Shadow enclosingShadow) {
        this.kind = kind;
        this.signature = signature;
        this.enclosingShadow = enclosingShadow;
    }

    public abstract World getIWorld();

    public List<ShadowMunger> getMungers() {
        return this.mungers;
    }

    public final boolean hasThis() {
        if (this.getKind().neverHasThis()) {
            return false;
        }
        if (this.getKind().isEnclosingKind()) {
            return !Modifier.isStatic(this.getSignature().getModifiers());
        }
        if (this.enclosingShadow == null) {
            return false;
        }
        return this.enclosingShadow.hasThis();
    }

    public final UnresolvedType getThisType() {
        if (!this.hasThis()) {
            throw new IllegalStateException("no this");
        }
        if (this.getKind().isEnclosingKind()) {
            return this.getSignature().getDeclaringType();
        }
        return this.enclosingShadow.getThisType();
    }

    public abstract Var getThisVar();

    public final boolean hasTarget() {
        if (this.getKind().neverHasTarget()) {
            return false;
        }
        if (this.getKind().isTargetSameAsThis()) {
            return this.hasThis();
        }
        return !Modifier.isStatic(this.getSignature().getModifiers());
    }

    public final UnresolvedType getTargetType() {
        if (!this.hasTarget()) {
            throw new IllegalStateException("no target");
        }
        return this.getSignature().getDeclaringType();
    }

    public abstract Var getTargetVar();

    public UnresolvedType[] getArgTypes() {
        if (this.getKind() == FieldSet) {
            return new UnresolvedType[]{this.getSignature().getReturnType()};
        }
        return this.getSignature().getParameterTypes();
    }

    public boolean isShadowForArrayConstructionJoinpoint() {
        return this.getKind() == ConstructorCall && this.signature.getDeclaringType().isArray();
    }

    public boolean isShadowForMonitor() {
        return this.getKind() == SynchronizationLock || this.getKind() == SynchronizationUnlock;
    }

    public ResolvedType[] getArgumentTypesForArrayConstructionShadow() {
        String s = this.signature.getDeclaringType().getSignature();
        int pos = s.indexOf("[");
        int dims = 1;
        while (pos < s.length()) {
            if (++pos >= s.length()) continue;
            dims += s.charAt(pos) == '[' ? 1 : 0;
        }
        ResolvedType intType = UnresolvedType.INT.resolve(this.getIWorld());
        if (dims == 1) {
            return new ResolvedType[]{intType};
        }
        ResolvedType[] someInts = new ResolvedType[dims];
        for (int i = 0; i < dims; ++i) {
            someInts[i] = intType;
        }
        return someInts;
    }

    public UnresolvedType[] getGenericArgTypes() {
        if (this.isShadowForArrayConstructionJoinpoint()) {
            return this.getArgumentTypesForArrayConstructionShadow();
        }
        if (this.isShadowForMonitor()) {
            return UnresolvedType.ARRAY_WITH_JUST_OBJECT;
        }
        if (this.getKind() == FieldSet) {
            return new UnresolvedType[]{this.getResolvedSignature().getGenericReturnType()};
        }
        return this.getResolvedSignature().getGenericParameterTypes();
    }

    public UnresolvedType getArgType(int arg) {
        if (this.getKind() == FieldSet) {
            return this.getSignature().getReturnType();
        }
        return this.getSignature().getParameterTypes()[arg];
    }

    public int getArgCount() {
        if (this.getKind() == FieldSet) {
            return 1;
        }
        return this.getSignature().getParameterTypes().length;
    }

    public abstract UnresolvedType getEnclosingType();

    public abstract Var getArgVar(int var1);

    public abstract Var getThisJoinPointVar();

    public abstract Var getThisJoinPointStaticPartVar();

    public abstract Var getThisEnclosingJoinPointStaticPartVar();

    public abstract Var getThisAspectInstanceVar(ResolvedType var1);

    public abstract Var getKindedAnnotationVar(UnresolvedType var1);

    public abstract Var getWithinAnnotationVar(UnresolvedType var1);

    public abstract Var getWithinCodeAnnotationVar(UnresolvedType var1);

    public abstract Var getThisAnnotationVar(UnresolvedType var1);

    public abstract Var getTargetAnnotationVar(UnresolvedType var1);

    public abstract Var getArgAnnotationVar(int var1, UnresolvedType var2);

    public abstract Member getEnclosingCodeSignature();

    public Kind getKind() {
        return this.kind;
    }

    public Member getSignature() {
        return this.signature;
    }

    public Member getMatchingSignature() {
        return this.matchingSignature != null ? this.matchingSignature : this.signature;
    }

    public void setMatchingSignature(Member member) {
        this.matchingSignature = member;
    }

    public ResolvedMember getResolvedSignature() {
        if (this.resolvedSignature == null) {
            this.resolvedSignature = this.signature.resolve(this.getIWorld());
        }
        return this.resolvedSignature;
    }

    public UnresolvedType getReturnType() {
        if (this.kind == ConstructorCall) {
            return this.getSignature().getDeclaringType();
        }
        if (this.kind == FieldSet) {
            return UnresolvedType.VOID;
        }
        if (this.kind == SynchronizationLock || this.kind == SynchronizationUnlock) {
            return UnresolvedType.VOID;
        }
        return this.getResolvedSignature().getGenericReturnType();
    }

    public static int howMany(int i) {
        int count = 0;
        for (int j = 0; j < SHADOW_KINDS.length; ++j) {
            if ((i & Shadow.SHADOW_KINDS[j].bit) == 0) continue;
            ++count;
        }
        return count;
    }

    protected boolean checkMunger(ShadowMunger munger) {
        if (munger.mustCheckExceptions()) {
            Iterator<ResolvedType> i = munger.getThrownExceptions().iterator();
            while (i.hasNext()) {
                if (this.checkCanThrow(munger, i.next())) continue;
                return false;
            }
        }
        return true;
    }

    protected boolean checkCanThrow(ShadowMunger munger, ResolvedType resolvedTypeX) {
        if (this.getKind() == ExceptionHandler) {
            return true;
        }
        if (!this.isDeclaredException(resolvedTypeX, this.getSignature())) {
            this.getIWorld().showMessage(IMessage.ERROR, WeaverMessages.format("cantThrowChecked", resolvedTypeX, this), this.getSourceLocation(), munger.getSourceLocation());
        }
        return true;
    }

    private boolean isDeclaredException(ResolvedType resolvedTypeX, Member member) {
        ResolvedType[] excs = this.getIWorld().resolve(member.getExceptions(this.getIWorld()));
        int len = excs.length;
        for (int i = 0; i < len; ++i) {
            if (!excs[i].isAssignableFrom(resolvedTypeX)) continue;
            return true;
        }
        return false;
    }

    public void addMunger(ShadowMunger munger) {
        if (this.checkMunger(munger)) {
            if (this.mungers == Collections.EMPTY_LIST) {
                this.mungers = new ArrayList<ShadowMunger>();
            }
            this.mungers.add(munger);
        }
    }

    public final void implement() {
        this.sortMungers();
        if (this.mungers == null) {
            return;
        }
        this.prepareForMungers();
        this.implementMungers();
    }

    private void sortMungers() {
        List<ShadowMunger> sorted = PartialOrder.sort(this.mungers);
        this.possiblyReportUnorderedAdvice(sorted);
        if (sorted == null) {
            for (ShadowMunger m : this.mungers) {
                this.getIWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("circularDependency", this), m.getSourceLocation()));
            }
        }
        this.mungers = sorted;
    }

    private void possiblyReportUnorderedAdvice(List sorted) {
        if (sorted != null && this.getIWorld().getLint().unorderedAdviceAtShadow.isEnabled() && this.mungers.size() > 1) {
            HashSet<String> clashingAspects = new HashSet<String>();
            int max = this.mungers.size();
            for (int i = max - 1; i >= 0; --i) {
                for (int j = 0; j < i; ++j) {
                    Integer order;
                    ShadowMunger a = this.mungers.get(i);
                    ShadowMunger b = this.mungers.get(j);
                    if (!(a instanceof Advice) || !(b instanceof Advice)) continue;
                    Advice adviceA = (Advice)a;
                    Advice adviceB = (Advice)b;
                    if (adviceA.concreteAspect.equals(adviceB.concreteAspect)) continue;
                    AdviceKind adviceKindA = adviceA.getKind();
                    AdviceKind adviceKindB = adviceB.getKind();
                    if (adviceKindA.getKey() >= 6 || adviceKindB.getKey() >= 6 || adviceKindA.getPrecedence() != adviceKindB.getPrecedence() || (order = this.getIWorld().getPrecedenceIfAny(adviceA.concreteAspect, adviceB.concreteAspect)) == null || !order.equals(new Integer(0))) continue;
                    String key = adviceA.getDeclaringAspect() + ":" + adviceB.getDeclaringAspect();
                    String possibleExistingKey = adviceB.getDeclaringAspect() + ":" + adviceA.getDeclaringAspect();
                    if (clashingAspects.contains(possibleExistingKey)) continue;
                    clashingAspects.add(key);
                }
            }
            for (String element : clashingAspects) {
                String aspect1 = element.substring(0, element.indexOf(":"));
                String aspect2 = element.substring(element.indexOf(":") + 1);
                this.getIWorld().getLint().unorderedAdviceAtShadow.signal(new String[]{this.toString(), aspect1, aspect2}, this.getSourceLocation(), null);
            }
        }
    }

    protected void prepareForMungers() {
        throw new RuntimeException("Generic shadows cannot be prepared");
    }

    private void implementMungers() {
        World world = this.getIWorld();
        this.needAroundClosureStacking = false;
        int annotationStyleWithAroundAndProceedCount = 0;
        for (ShadowMunger munger : this.mungers) {
            if (munger.getDeclaringType() == null || !munger.getDeclaringType().isAnnotationStyleAspect() || !munger.isAroundAdvice() || !munger.bindsProceedingJoinPoint() || ++annotationStyleWithAroundAndProceedCount <= 1) continue;
            this.needAroundClosureStacking = true;
            break;
        }
        for (ShadowMunger munger : this.mungers) {
            if (!munger.implementOn(this)) continue;
            world.reportMatch(munger, this);
        }
    }

    public abstract ISourceLocation getSourceLocation();

    public String toString() {
        return this.getKind() + "(" + this.getSignature() + ")";
    }

    public String toResolvedString(World world) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getKind());
        sb.append("(");
        Member m = this.getSignature();
        if (m == null) {
            sb.append("<<missing signature>>");
        } else {
            ResolvedMember rm = world.resolve(m);
            if (rm == null) {
                sb.append("<<unresolvableMember:").append(m).append(">>");
            } else {
                String genString = rm.toGenericString();
                if (genString == null) {
                    sb.append("<<unableToGetGenericStringFor:").append(rm).append(">>");
                } else {
                    sb.append(genString);
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static Set<Kind> toSet(int i) {
        HashSet<Kind> results = new HashSet<Kind>();
        for (int j = 0; j < SHADOW_KINDS.length; ++j) {
            Kind k = SHADOW_KINDS[j];
            if (!k.isSet(i)) continue;
            results.add(k);
        }
        return results;
    }

    public static final class Kind
    extends TypeSafeEnum {
        public int bit;
        private static final int hasReturnValueFlag = 558;
        private static final int isEnclosingKindFlag = 1688;
        private static final int isTargetSameAsThisFlag = 1944;
        private static final int neverHasTargetFlag = 14724;
        private static final int neverHasThisFlag = 384;

        public Kind(String name, int key, boolean argsOnStack) {
            super(name, key);
            this.bit = 1 << key;
        }

        public String toLegalJavaIdentifier() {
            return this.getName().replace('-', '_');
        }

        public boolean argsOnStack() {
            return !this.isTargetSameAsThis();
        }

        public boolean allowsExtraction() {
            return true;
        }

        public boolean isSet(int i) {
            return (i & this.bit) != 0;
        }

        public boolean hasHighPriorityExceptions() {
            return !this.isTargetSameAsThis();
        }

        public boolean hasReturnValue() {
            return (this.bit & 0x22E) != 0;
        }

        public boolean isEnclosingKind() {
            return (this.bit & 0x698) != 0;
        }

        public boolean isTargetSameAsThis() {
            return (this.bit & 0x798) != 0;
        }

        public boolean neverHasTarget() {
            return (this.bit & 0x3984) != 0;
        }

        public boolean neverHasThis() {
            return (this.bit & 0x180) != 0;
        }

        public String getSimpleName() {
            int dash = this.getName().lastIndexOf(45);
            if (dash == -1) {
                return this.getName();
            }
            return this.getName().substring(dash + 1);
        }

        public static Kind read(DataInputStream s) throws IOException {
            byte key = s.readByte();
            switch (key) {
                case 1: {
                    return MethodCall;
                }
                case 2: {
                    return ConstructorCall;
                }
                case 3: {
                    return MethodExecution;
                }
                case 4: {
                    return ConstructorExecution;
                }
                case 5: {
                    return FieldGet;
                }
                case 6: {
                    return FieldSet;
                }
                case 7: {
                    return StaticInitialization;
                }
                case 8: {
                    return PreInitialization;
                }
                case 9: {
                    return AdviceExecution;
                }
                case 10: {
                    return Initialization;
                }
                case 11: {
                    return ExceptionHandler;
                }
                case 12: {
                    return SynchronizationLock;
                }
                case 13: {
                    return SynchronizationUnlock;
                }
            }
            throw new BCException("unknown kind: " + key);
        }
    }
}

