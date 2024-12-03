/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.util.Stack;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
import org.aspectj.runtime.reflect.SignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class JoinPointImpl
implements ProceedingJoinPoint {
    Object _this;
    Object target;
    Object[] args;
    JoinPoint.StaticPart staticPart;
    private AroundClosure arc = null;
    private Stack<AroundClosure> arcs = null;

    public JoinPointImpl(JoinPoint.StaticPart staticPart, Object _this, Object target, Object[] args) {
        this.staticPart = staticPart;
        this._this = _this;
        this.target = target;
        this.args = args;
    }

    @Override
    public Object getThis() {
        return this._this;
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    public Object[] getArgs() {
        if (this.args == null) {
            this.args = new Object[0];
        }
        Object[] argsCopy = new Object[this.args.length];
        System.arraycopy(this.args, 0, argsCopy, 0, this.args.length);
        return argsCopy;
    }

    @Override
    public JoinPoint.StaticPart getStaticPart() {
        return this.staticPart;
    }

    @Override
    public String getKind() {
        return this.staticPart.getKind();
    }

    @Override
    public Signature getSignature() {
        return this.staticPart.getSignature();
    }

    @Override
    public SourceLocation getSourceLocation() {
        return this.staticPart.getSourceLocation();
    }

    @Override
    public final String toString() {
        return this.staticPart.toString();
    }

    @Override
    public final String toShortString() {
        return this.staticPart.toShortString();
    }

    @Override
    public final String toLongString() {
        return this.staticPart.toLongString();
    }

    @Override
    public void set$AroundClosure(AroundClosure arc) {
        this.arc = arc;
    }

    @Override
    public void stack$AroundClosure(AroundClosure arc) {
        if (this.arcs == null) {
            this.arcs = new Stack();
        }
        if (arc == null) {
            this.arcs.pop();
        } else {
            this.arcs.push(arc);
        }
    }

    @Override
    public Object proceed() throws Throwable {
        if (this.arcs == null) {
            if (this.arc == null) {
                return null;
            }
            return this.arc.run(this.arc.getState());
        }
        return this.arcs.peek().run(this.arcs.peek().getState());
    }

    @Override
    public Object proceed(Object[] adviceBindings) throws Throwable {
        AroundClosure ac = null;
        ac = this.arcs == null ? this.arc : this.arcs.peek();
        if (ac == null) {
            return null;
        }
        int flags = ac.getFlags();
        boolean unset = (flags & 0x100000) != 0;
        boolean thisTargetTheSame = (flags & 0x10000) != 0;
        boolean hasThis = (flags & 0x1000) != 0;
        boolean bindsThis = (flags & 0x100) != 0;
        boolean hasTarget = (flags & 0x10) != 0;
        boolean bindsTarget = (flags & 1) != 0;
        Object[] state = ac.getState();
        int firstArgumentIndexIntoAdviceBindings = 0;
        int firstArgumentIndexIntoState = 0;
        firstArgumentIndexIntoState += hasThis ? 1 : 0;
        firstArgumentIndexIntoState += hasTarget && !thisTargetTheSame ? 1 : 0;
        if (hasThis && bindsThis) {
            firstArgumentIndexIntoAdviceBindings = 1;
            state[0] = adviceBindings[0];
        }
        if (hasTarget && bindsTarget) {
            if (thisTargetTheSame) {
                firstArgumentIndexIntoAdviceBindings = 1 + (bindsThis ? 1 : 0);
                state[0] = adviceBindings[bindsThis ? 1 : 0];
            } else {
                int targetPositionInAdviceBindings = hasThis && bindsThis ? 1 : 0;
                firstArgumentIndexIntoAdviceBindings = (hasThis && bindsThis ? 1 : 0) + (hasTarget && bindsTarget && !thisTargetTheSame ? 1 : 0);
                state[hasThis ? 1 : 0] = adviceBindings[targetPositionInAdviceBindings];
            }
        }
        for (int i = firstArgumentIndexIntoAdviceBindings; i < adviceBindings.length; ++i) {
            state[firstArgumentIndexIntoState + (i - firstArgumentIndexIntoAdviceBindings)] = adviceBindings[i];
        }
        return ac.run(state);
    }

    static class EnclosingStaticPartImpl
    extends StaticPartImpl
    implements JoinPoint.EnclosingStaticPart {
        public EnclosingStaticPartImpl(int count, String kind, Signature signature, SourceLocation sourceLocation) {
            super(count, kind, signature, sourceLocation);
        }
    }

    static class StaticPartImpl
    implements JoinPoint.StaticPart {
        String kind;
        Signature signature;
        SourceLocation sourceLocation;
        private int id;

        public StaticPartImpl(int id, String kind, Signature signature, SourceLocation sourceLocation) {
            this.kind = kind;
            this.signature = signature;
            this.sourceLocation = sourceLocation;
            this.id = id;
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public String getKind() {
            return this.kind;
        }

        @Override
        public Signature getSignature() {
            return this.signature;
        }

        @Override
        public SourceLocation getSourceLocation() {
            return this.sourceLocation;
        }

        String toString(StringMaker sm) {
            StringBuffer buf = new StringBuffer();
            buf.append(sm.makeKindName(this.getKind()));
            buf.append("(");
            buf.append(((SignatureImpl)this.getSignature()).toString(sm));
            buf.append(")");
            return buf.toString();
        }

        @Override
        public final String toString() {
            return this.toString(StringMaker.middleStringMaker);
        }

        @Override
        public final String toShortString() {
            return this.toString(StringMaker.shortStringMaker);
        }

        @Override
        public final String toLongString() {
            return this.toString(StringMaker.longStringMaker);
        }
    }
}

