/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.ast.ProvidesStatement;
import org.eclipse.jdt.internal.compiler.ast.RequiresStatement;
import org.eclipse.jdt.internal.compiler.ast.UsesStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredExportsStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredOpensStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredProvidesStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredRequiresStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredUsesStatement;

public class RecoveredModule
extends RecoveredElement {
    public RecoveredExportsStatement[] exports;
    public int exportCount;
    public RecoveredOpensStatement[] opens;
    public int opensCount;
    public RecoveredRequiresStatement[] requires;
    public int requiresCount;
    public RecoveredUsesStatement[] uses;
    public int usesCount;
    public RecoveredProvidesStatement[] services;
    public int servicesCount;
    public ModuleDeclaration moduleDeclaration;

    public RecoveredModule(ModuleDeclaration moduleDeclaration, RecoveredElement parent, int bracketBalance) {
        super(parent, bracketBalance);
        this.moduleDeclaration = moduleDeclaration;
    }

    @Override
    public RecoveredElement add(ModuleStatement moduleStatement, int bracketBalanceValue) {
        if (moduleStatement instanceof ExportsStatement) {
            return this.add((ExportsStatement)moduleStatement, bracketBalanceValue);
        }
        if (moduleStatement instanceof OpensStatement) {
            return this.add((OpensStatement)moduleStatement, bracketBalanceValue);
        }
        if (moduleStatement instanceof RequiresStatement) {
            return this.add((RequiresStatement)moduleStatement, bracketBalanceValue);
        }
        if (moduleStatement instanceof ProvidesStatement) {
            return this.add((ProvidesStatement)moduleStatement, bracketBalanceValue);
        }
        if (moduleStatement instanceof UsesStatement) {
            return this.add((UsesStatement)moduleStatement, bracketBalanceValue);
        }
        return this;
    }

    public RecoveredElement add(ExportsStatement exportsStatement, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.exports == null) {
            this.exports = new RecoveredExportsStatement[5];
            this.exportCount = 0;
        } else if (this.exportCount == this.exports.length) {
            this.exports = new RecoveredExportsStatement[2 * this.exportCount];
            System.arraycopy(this.exports, 0, this.exports, 0, this.exportCount);
        }
        RecoveredExportsStatement element = new RecoveredExportsStatement(exportsStatement, (RecoveredElement)this, bracketBalanceValue);
        this.exports[this.exportCount++] = element;
        return element;
    }

    public RecoveredElement add(OpensStatement opensStatement, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.opens == null) {
            this.opens = new RecoveredOpensStatement[5];
            this.opensCount = 0;
        } else if (this.opensCount == this.opens.length) {
            this.opens = new RecoveredOpensStatement[2 * this.opensCount];
            System.arraycopy(this.opens, 0, this.opens, 0, this.opensCount);
        }
        RecoveredOpensStatement element = new RecoveredOpensStatement(opensStatement, (RecoveredElement)this, bracketBalanceValue);
        this.opens[this.opensCount++] = element;
        return element;
    }

    public RecoveredElement add(RequiresStatement requiresStatement, int bracketBalanceValue) {
        if (this.requires == null) {
            this.requires = new RecoveredRequiresStatement[5];
            this.requiresCount = 0;
        } else if (this.requiresCount == this.requires.length) {
            this.requires = new RecoveredRequiresStatement[2 * this.requiresCount];
            System.arraycopy(this.requires, 0, this.requires, 0, this.requiresCount);
        }
        RecoveredRequiresStatement element = new RecoveredRequiresStatement(requiresStatement, (RecoveredElement)this, bracketBalanceValue);
        this.requires[this.requiresCount++] = element;
        return this;
    }

    public RecoveredElement add(ProvidesStatement providesStatement, int bracketBalanceValue) {
        if (this.services == null) {
            this.services = new RecoveredProvidesStatement[5];
            this.servicesCount = 0;
        } else if (this.servicesCount == this.services.length) {
            this.services = new RecoveredProvidesStatement[2 * this.servicesCount];
            System.arraycopy(this.services, 0, this.services, 0, this.servicesCount);
        }
        RecoveredProvidesStatement element = new RecoveredProvidesStatement(providesStatement, (RecoveredElement)this, bracketBalanceValue);
        this.services[this.servicesCount++] = element;
        return element;
    }

    public RecoveredElement add(UsesStatement usesStatement, int bracketBalanceValue) {
        this.genAssign(usesStatement, bracketBalanceValue);
        return this;
    }

    private void genAssign(UsesStatement usesStatement, int bracketBalanceValue) {
        if (this.uses == null) {
            this.uses = new RecoveredUsesStatement[5];
            this.usesCount = 0;
        } else if (this.usesCount == this.uses.length) {
            this.uses = new RecoveredUsesStatement[2 * this.usesCount];
            System.arraycopy(this.uses, 0, this.uses, 0, this.usesCount);
        }
        RecoveredUsesStatement element = new RecoveredUsesStatement(usesStatement, (RecoveredElement)this, bracketBalanceValue);
        this.uses[this.usesCount++] = element;
    }

    @Override
    public String toString(int tab) {
        int i;
        StringBuffer result = new StringBuffer(this.tabString(tab));
        result.append("Recovered module:\n");
        result.append("module ");
        result.append(CharOperation.charToString(this.moduleDeclaration.moduleName));
        result.append(" {");
        if (this.exportCount > 0) {
            i = 0;
            while (i < this.exportCount) {
                result.append("\n");
                result.append(this.exports[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.requiresCount > 0) {
            i = 0;
            while (i < this.requiresCount) {
                result.append("\n");
                result.append(this.requires[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.usesCount > 0) {
            i = 0;
            while (i < this.usesCount) {
                result.append("\n");
                result.append(this.uses[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.servicesCount > 0) {
            i = 0;
            while (i < this.servicesCount) {
                result.append("\n");
                result.append(this.services[i].toString(tab + 1));
                ++i;
            }
        }
        result.append("\n}");
        return result.toString();
    }

    public ModuleDeclaration updatedModuleDeclaration() {
        this.updateExports(this.moduleDeclaration);
        this.updateOpens(this.moduleDeclaration);
        this.updateRequires(this.moduleDeclaration);
        this.updateUses(this.moduleDeclaration);
        this.updateServices(this.moduleDeclaration);
        return this.moduleDeclaration;
    }

    private void updateExports(ModuleDeclaration mod) {
        if (this.exportCount > 0) {
            int existingCount = mod.exportsCount;
            int actualCount = 0;
            ExportsStatement[] exports1 = new ExportsStatement[existingCount + this.exportCount];
            if (existingCount > 0) {
                System.arraycopy(mod.exports, 0, exports1, 0, existingCount);
                actualCount = existingCount;
            }
            int i = 0;
            while (i < this.exportCount) {
                exports1[actualCount++] = (ExportsStatement)this.exports[i].updatedPackageVisibilityStatement();
                ++i;
            }
            mod.exports = exports1;
            mod.exportsCount = actualCount;
        }
    }

    private void updateOpens(ModuleDeclaration mod) {
        if (this.opensCount > 0) {
            int existingCount = mod.opensCount;
            int actualCount = 0;
            OpensStatement[] opens1 = new OpensStatement[existingCount + this.opensCount];
            if (existingCount > 0) {
                System.arraycopy(mod.exports, 0, opens1, 0, existingCount);
                actualCount = existingCount;
            }
            int i = 0;
            while (i < this.opensCount) {
                opens1[actualCount++] = (OpensStatement)this.opens[i].updatedPackageVisibilityStatement();
                ++i;
            }
            mod.opens = opens1;
            mod.opensCount = actualCount;
        }
    }

    private void updateRequires(ModuleDeclaration mod) {
        if (this.requiresCount > 0) {
            int existingCount = mod.requiresCount;
            int actualCount = 0;
            RequiresStatement[] requiresStmts = new RequiresStatement[existingCount + this.requiresCount];
            if (existingCount > 0) {
                System.arraycopy(mod.requires, 0, requiresStmts, 0, existingCount);
                actualCount = existingCount;
            }
            int i = 0;
            while (i < this.requiresCount) {
                requiresStmts[actualCount++] = this.requires[i].updatedRequiresStatement();
                ++i;
            }
            mod.requires = requiresStmts;
            mod.requiresCount = actualCount;
        }
    }

    private void updateUses(ModuleDeclaration mod) {
        if (this.usesCount > 0) {
            int existingCount = mod.usesCount;
            int actualCount = 0;
            UsesStatement[] usesStmts = new UsesStatement[existingCount + this.usesCount];
            if (existingCount > 0) {
                System.arraycopy(mod.uses, 0, usesStmts, 0, existingCount);
                actualCount = existingCount;
            }
            int i = 0;
            while (i < this.usesCount) {
                usesStmts[actualCount++] = this.uses[i].updatedUsesStatement();
                ++i;
            }
            mod.uses = usesStmts;
            mod.usesCount = actualCount;
        }
    }

    private void updateServices(ModuleDeclaration mod) {
        if (this.servicesCount > 0) {
            int existingCount = mod.servicesCount;
            int actualCount = 0;
            ProvidesStatement[] providesStmts = new ProvidesStatement[existingCount + this.servicesCount];
            if (existingCount > 0) {
                System.arraycopy(mod.services, 0, providesStmts, 0, existingCount);
                actualCount = existingCount;
            }
            int i = 0;
            while (i < this.servicesCount) {
                providesStmts[actualCount++] = this.services[i].updatedProvidesStatement();
                ++i;
            }
            mod.services = providesStmts;
            mod.servicesCount = actualCount;
        }
    }

    @Override
    public void updateParseTree() {
        this.updatedModuleDeclaration();
    }
}

