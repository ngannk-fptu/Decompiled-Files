/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.SimpleScope;

public class BindingScope
extends SimpleScope {
    private final ResolvedType enclosingType;
    private final ISourceContext sourceContext;
    private boolean importsUpdated = false;

    public BindingScope(ResolvedType type, ISourceContext sourceContext, FormalBinding[] bindings) {
        super(type.getWorld(), bindings);
        this.enclosingType = type;
        this.sourceContext = sourceContext;
    }

    @Override
    public ResolvedType getEnclosingType() {
        return this.enclosingType;
    }

    @Override
    public ISourceLocation makeSourceLocation(IHasPosition location) {
        return this.sourceContext.makeSourceLocation(location);
    }

    @Override
    public UnresolvedType lookupType(String name, IHasPosition location) {
        if (this.enclosingType != null && !this.importsUpdated) {
            String pkgName = this.enclosingType.getPackageName();
            if (pkgName != null && !pkgName.equals("")) {
                String[] existingImports = this.getImportedPrefixes();
                String pkgNameWithDot = pkgName.concat(".");
                boolean found = false;
                for (String existingImport : existingImports) {
                    if (!existingImport.equals(pkgNameWithDot)) continue;
                    found = true;
                    break;
                }
                if (!found) {
                    String[] newImports = new String[existingImports.length + 1];
                    System.arraycopy(existingImports, 0, newImports, 0, existingImports.length);
                    newImports[existingImports.length] = pkgNameWithDot;
                    this.setImportedPrefixes(newImports);
                }
            }
            this.importsUpdated = true;
        }
        return super.lookupType(name, location);
    }
}

