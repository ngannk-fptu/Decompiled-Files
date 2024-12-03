/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;

public class Checker
extends ShadowMunger {
    private boolean isError;
    private String message;
    private volatile int hashCode = -1;

    private Checker() {
    }

    public Checker(DeclareErrorOrWarning deow) {
        super(deow.getPointcut(), deow.getStart(), deow.getEnd(), deow.getSourceContext(), 2);
        this.message = deow.getMessage();
        this.isError = deow.isError();
    }

    private Checker(Pointcut pointcut, int start, int end, ISourceContext context, String message, boolean isError) {
        super(pointcut, start, end, context, 2);
        this.message = message;
        this.isError = isError;
    }

    public boolean isError() {
        return this.isError;
    }

    public String getMessage(Shadow shadow) {
        return this.format(this.message, shadow);
    }

    @Override
    public void specializeOn(Shadow shadow) {
        throw new IllegalStateException("Cannot call specializeOn(...) for a Checker");
    }

    @Override
    public boolean implementOn(Shadow shadow) {
        throw new IllegalStateException("Cannot call implementOn(...) for a Checker");
    }

    @Override
    public boolean match(Shadow shadow, World world) {
        if (super.match(shadow, world)) {
            world.reportCheckerMatch(this, shadow);
        }
        return false;
    }

    @Override
    public int compareTo(Object other) {
        return 0;
    }

    @Override
    public boolean mustCheckExceptions() {
        return true;
    }

    @Override
    public Collection<ResolvedType> getThrownExceptions() {
        return Collections.emptyList();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Checker)) {
            return false;
        }
        Checker o = (Checker)other;
        return o.isError == this.isError && (o.pointcut == null ? this.pointcut == null : o.pointcut.equals(this.pointcut));
    }

    public int hashCode() {
        if (this.hashCode == -1) {
            int result = 17;
            result = 37 * result + (this.isError ? 1 : 0);
            this.hashCode = result = 37 * result + (this.pointcut == null ? 0 : this.pointcut.hashCode());
        }
        return this.hashCode;
    }

    @Override
    public ShadowMunger parameterizeWith(ResolvedType declaringType, Map<String, UnresolvedType> typeVariableMap) {
        Checker ret = new Checker(this.pointcut.parameterizeWith(typeVariableMap, declaringType.getWorld()), this.start, this.end, this.sourceContext, this.message, this.isError);
        return ret;
    }

    @Override
    public ShadowMunger concretize(ResolvedType theAspect, World world, PerClause clause) {
        this.pointcut = this.pointcut.concretize(theAspect, this.getDeclaringType(), 0, this);
        this.hashCode = -1;
        return this;
    }

    @Override
    public ResolvedType getConcreteAspect() {
        return this.getDeclaringType();
    }

    private int nextCurly(String string, int pos) {
        int curlyIndex;
        do {
            if ((curlyIndex = string.indexOf(123, pos)) == -1) {
                return -1;
            }
            if (curlyIndex == 0) {
                return 0;
            }
            if (string.charAt(curlyIndex - 1) == '\\') continue;
            return curlyIndex;
        } while ((pos = curlyIndex + 1) < string.length());
        return -1;
    }

    private String format(String msg, Shadow shadow) {
        int pos = 0;
        int curlyIndex = this.nextCurly(msg, 0);
        if (curlyIndex == -1) {
            if (msg.indexOf(123) != -1) {
                return msg.replace("\\{", "{");
            }
            return msg;
        }
        StringBuffer ret = new StringBuffer();
        while (curlyIndex >= 0) {
            int endCurly;
            if (curlyIndex > 0) {
                ret.append(msg.substring(pos, curlyIndex).replace("\\{", "{"));
            }
            if ((endCurly = msg.indexOf(125, curlyIndex)) == -1) {
                ret.append('{');
                pos = curlyIndex + 1;
            } else {
                ret.append(this.getValue(msg.substring(curlyIndex + 1, endCurly), shadow));
            }
            pos = endCurly + 1;
            curlyIndex = this.nextCurly(msg, pos);
        }
        ret.append(msg.substring(pos, msg.length()));
        return ret.toString();
    }

    private String getValue(String key, Shadow shadow) {
        if (key.equalsIgnoreCase("joinpoint")) {
            return shadow.toString();
        }
        if (key.equalsIgnoreCase("joinpoint.kind")) {
            return shadow.getKind().getName();
        }
        if (key.equalsIgnoreCase("joinpoint.enclosingclass")) {
            return shadow.getEnclosingType().getName();
        }
        if (key.equalsIgnoreCase("joinpoint.enclosingmember.name")) {
            Member member = shadow.getEnclosingCodeSignature();
            if (member == null) {
                return "";
            }
            return member.getName();
        }
        if (key.equalsIgnoreCase("joinpoint.enclosingmember")) {
            Member member = shadow.getEnclosingCodeSignature();
            if (member == null) {
                return "";
            }
            return member.toString();
        }
        if (key.equalsIgnoreCase("joinpoint.signature")) {
            return shadow.getSignature().toString();
        }
        if (key.equalsIgnoreCase("joinpoint.signature.declaringtype")) {
            return shadow.getSignature().getDeclaringType().toString();
        }
        if (key.equalsIgnoreCase("joinpoint.signature.name")) {
            return shadow.getSignature().getName();
        }
        if (key.equalsIgnoreCase("joinpoint.sourcelocation.sourcefile")) {
            ISourceLocation loc = shadow.getSourceLocation();
            if (loc != null && loc.getSourceFile() != null) {
                return loc.getSourceFile().toString();
            }
            return "UNKNOWN";
        }
        if (key.equalsIgnoreCase("joinpoint.sourcelocation.line")) {
            ISourceLocation loc = shadow.getSourceLocation();
            if (loc != null) {
                return Integer.toString(loc.getLine());
            }
            return "-1";
        }
        if (key.equalsIgnoreCase("advice.aspecttype")) {
            return this.getDeclaringType().getName();
        }
        if (key.equalsIgnoreCase("advice.sourcelocation.line")) {
            ISourceLocation loc = this.getSourceLocation();
            if (loc != null && loc.getSourceFile() != null) {
                return Integer.toString(loc.getLine());
            }
            return "-1";
        }
        if (key.equalsIgnoreCase("advice.sourcelocation.sourcefile")) {
            ISourceLocation loc = this.getSourceLocation();
            if (loc != null && loc.getSourceFile() != null) {
                return loc.getSourceFile().toString();
            }
            return "UNKNOWN";
        }
        return "UNKNOWN_KEY{" + key + "}";
    }
}

