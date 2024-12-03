/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;

public class DeclareErrorOrWarning
extends Declare {
    private boolean isError;
    private Pointcut pointcut;
    private String message;

    public DeclareErrorOrWarning(boolean isError, Pointcut pointcut, String message) {
        this.isError = isError;
        this.pointcut = pointcut;
        this.message = message;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("declare ");
        if (this.isError) {
            buf.append("error: ");
        } else {
            buf.append("warning: ");
        }
        buf.append(this.pointcut);
        buf.append(": ");
        buf.append("\"");
        buf.append(this.message);
        buf.append("\";");
        return buf.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof DeclareErrorOrWarning)) {
            return false;
        }
        DeclareErrorOrWarning o = (DeclareErrorOrWarning)other;
        return o.isError == this.isError && o.pointcut.equals(this.pointcut) && o.message.equals(this.message);
    }

    public int hashCode() {
        int result = this.isError ? 19 : 23;
        result = 37 * result + this.pointcut.hashCode();
        result = 37 * result + this.message.hashCode();
        return result;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(1);
        s.writeBoolean(this.isError);
        this.pointcut.write(s);
        s.writeUTF(this.message);
        this.writeLocation(s);
    }

    public static Declare read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        DeclareErrorOrWarning ret = new DeclareErrorOrWarning(s.readBoolean(), Pointcut.read(s, context), s.readUTF());
        ret.readLocation(context, s);
        return ret;
    }

    public boolean isError() {
        return this.isError;
    }

    public String getMessage() {
        return this.message;
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public void resolve(IScope scope) {
        this.pointcut = this.pointcut.resolve(scope);
    }

    @Override
    public Declare parameterizeWith(Map<String, UnresolvedType> typeVariableBindingMap, World w) {
        DeclareErrorOrWarning ret = new DeclareErrorOrWarning(this.isError, this.pointcut.parameterizeWith(typeVariableBindingMap, w), this.message);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public boolean isAdviceLike() {
        return true;
    }

    @Override
    public String getNameSuffix() {
        return "eow";
    }

    public String getName() {
        StringBuffer buf = new StringBuffer();
        buf.append("declare ");
        if (this.isError) {
            buf.append("error");
        } else {
            buf.append("warning");
        }
        return buf.toString();
    }
}

