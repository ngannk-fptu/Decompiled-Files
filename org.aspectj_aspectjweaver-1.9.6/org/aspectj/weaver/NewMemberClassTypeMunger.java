/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import java.util.List;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;

public class NewMemberClassTypeMunger
extends ResolvedTypeMunger {
    private UnresolvedType targetType;
    private String memberTypeName;
    private int version = 1;

    public NewMemberClassTypeMunger(UnresolvedType targetType, String memberTypeName) {
        super(ResolvedTypeMunger.InnerClass, null);
        this.targetType = targetType;
        this.memberTypeName = memberTypeName;
    }

    @Override
    public void write(CompressingDataOutputStream stream) throws IOException {
        this.kind.write(stream);
        stream.writeInt(this.version);
        this.targetType.write(stream);
        stream.writeUTF(this.memberTypeName);
        this.writeSourceLocation(stream);
        this.writeOutTypeAliases(stream);
    }

    public static ResolvedTypeMunger readInnerClass(VersionedDataInputStream stream, ISourceContext context) throws IOException {
        stream.readInt();
        UnresolvedType targetType = UnresolvedType.read(stream);
        String memberTypeName = stream.readUTF();
        ISourceLocation sourceLocation = NewMemberClassTypeMunger.readSourceLocation(stream);
        List<String> typeVarAliases = NewMemberClassTypeMunger.readInTypeAliases(stream);
        NewMemberClassTypeMunger newInstance = new NewMemberClassTypeMunger(targetType, memberTypeName);
        newInstance.setTypeVariableAliases(typeVarAliases);
        newInstance.setSourceLocation(sourceLocation);
        return newInstance;
    }

    public UnresolvedType getTargetType() {
        return this.targetType;
    }

    @Override
    public UnresolvedType getDeclaringType() {
        return this.targetType;
    }

    public String getMemberTypeName() {
        return this.memberTypeName;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.kind.hashCode();
        result = 37 * result + this.memberTypeName.hashCode();
        result = 37 * result + this.targetType.hashCode();
        result = 37 * result + (this.typeVariableAliases == null ? 0 : this.typeVariableAliases.hashCode());
        return result;
    }

    public boolean equals(Object other) {
        if (!(other instanceof NewMemberClassTypeMunger)) {
            return false;
        }
        NewMemberClassTypeMunger o = (NewMemberClassTypeMunger)other;
        return (this.kind == null ? o.kind == null : this.kind.equals(o.kind)) && this.memberTypeName.equals(o.memberTypeName) && this.targetType.equals(o.targetType) && (this.typeVariableAliases == null ? o.typeVariableAliases == null : this.typeVariableAliases.equals(o.typeVariableAliases));
    }
}

