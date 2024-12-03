/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;

public class DeclareTypeErrorOrWarning
extends Declare {
    private boolean isError;
    private TypePattern typePattern;
    private String message;

    public DeclareTypeErrorOrWarning(boolean isError, TypePattern typePattern, String message) {
        this.isError = isError;
        this.typePattern = typePattern;
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
        buf.append(this.typePattern);
        buf.append(": ");
        buf.append("\"");
        buf.append(this.message);
        buf.append("\";");
        return buf.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof DeclareTypeErrorOrWarning)) {
            return false;
        }
        DeclareTypeErrorOrWarning o = (DeclareTypeErrorOrWarning)other;
        return o.isError == this.isError && o.typePattern.equals(this.typePattern) && o.message.equals(this.message);
    }

    public int hashCode() {
        int result = this.isError ? 19 : 23;
        result = 37 * result + this.typePattern.hashCode();
        result = 37 * result + this.message.hashCode();
        return result;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(7);
        s.writeBoolean(this.isError);
        this.typePattern.write(s);
        s.writeUTF(this.message);
        this.writeLocation(s);
    }

    public static Declare read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        DeclareTypeErrorOrWarning ret = new DeclareTypeErrorOrWarning(s.readBoolean(), TypePattern.read(s, context), s.readUTF());
        ret.readLocation(context, s);
        return ret;
    }

    public boolean isError() {
        return this.isError;
    }

    public String getMessage() {
        return this.message;
    }

    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    @Override
    public void resolve(IScope scope) {
        this.typePattern.resolve(scope.getWorld());
    }

    public Declare parameterizeWith(Map typeVariableBindingMap, World w) {
        DeclareTypeErrorOrWarning ret = new DeclareTypeErrorOrWarning(this.isError, this.typePattern.parameterizeWith(typeVariableBindingMap, w), this.message);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public boolean isAdviceLike() {
        return false;
    }

    @Override
    public String getNameSuffix() {
        return "teow";
    }

    public String getName() {
        StringBuffer buf = new StringBuffer();
        buf.append("declare type ");
        if (this.isError) {
            buf.append("error");
        } else {
            buf.append("warning");
        }
        return buf.toString();
    }
}

