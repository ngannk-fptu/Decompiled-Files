/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.UnresolvedTypeVariableReferenceType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.IScope;

public class ScopeWithTypeVariables
implements IScope {
    private IScope delegateScope;
    private String[] typeVariableNames;
    private UnresolvedTypeVariableReferenceType[] typeVarTypeXs;

    public ScopeWithTypeVariables(String[] typeVarNames, IScope delegate) {
        this.delegateScope = delegate;
        this.typeVariableNames = typeVarNames;
        this.typeVarTypeXs = new UnresolvedTypeVariableReferenceType[typeVarNames.length];
    }

    @Override
    public UnresolvedType lookupType(String name, IHasPosition location) {
        for (int i = 0; i < this.typeVariableNames.length; ++i) {
            if (!this.typeVariableNames[i].equals(name)) continue;
            if (this.typeVarTypeXs[i] == null) {
                this.typeVarTypeXs[i] = new UnresolvedTypeVariableReferenceType(new TypeVariable(name));
            }
            return this.typeVarTypeXs[i];
        }
        return this.delegateScope.lookupType(name, location);
    }

    @Override
    public World getWorld() {
        return this.delegateScope.getWorld();
    }

    @Override
    public ResolvedType getEnclosingType() {
        return this.delegateScope.getEnclosingType();
    }

    @Override
    public IMessageHandler getMessageHandler() {
        return this.delegateScope.getMessageHandler();
    }

    @Override
    public FormalBinding lookupFormal(String name) {
        return this.delegateScope.lookupFormal(name);
    }

    @Override
    public FormalBinding getFormal(int i) {
        return this.delegateScope.getFormal(i);
    }

    @Override
    public int getFormalCount() {
        return this.delegateScope.getFormalCount();
    }

    @Override
    public String[] getImportedPrefixes() {
        return this.delegateScope.getImportedPrefixes();
    }

    @Override
    public String[] getImportedNames() {
        return this.delegateScope.getImportedNames();
    }

    @Override
    public void message(IMessage.Kind kind, IHasPosition location, String message) {
        this.delegateScope.message(kind, location, message);
    }

    @Override
    public void message(IMessage.Kind kind, IHasPosition location1, IHasPosition location2, String message) {
        this.delegateScope.message(kind, location1, location2, message);
    }

    @Override
    public void message(IMessage aMessage) {
        this.delegateScope.message(aMessage);
    }
}

