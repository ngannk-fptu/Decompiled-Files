/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.NamespaceContext;

public class ValidationState
implements ValidationContext {
    private boolean fExtraChecking = true;
    private boolean fFacetChecking = true;
    private boolean fNormalize = true;
    private boolean fNamespaces = true;
    private EntityState fEntityState = null;
    private NamespaceContext fNamespaceContext = null;
    private SymbolTable fSymbolTable = null;
    private Locale fLocale = null;
    private final HashMap fIdTable = new HashMap();
    private final HashMap fIdRefTable = new HashMap();
    private static final Object fNullValue = new Object();

    public void setExtraChecking(boolean bl) {
        this.fExtraChecking = bl;
    }

    public void setFacetChecking(boolean bl) {
        this.fFacetChecking = bl;
    }

    public void setNormalizationRequired(boolean bl) {
        this.fNormalize = bl;
    }

    public void setUsingNamespaces(boolean bl) {
        this.fNamespaces = bl;
    }

    public void setEntityState(EntityState entityState) {
        this.fEntityState = entityState;
    }

    public void setNamespaceSupport(NamespaceContext namespaceContext) {
        this.fNamespaceContext = namespaceContext;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
    }

    public Iterator checkIDRefID() {
        HashSet<String> hashSet = null;
        for (String string : this.fIdRefTable.keySet()) {
            if (this.fIdTable.containsKey(string)) continue;
            if (hashSet == null) {
                hashSet = new HashSet<String>();
            }
            hashSet.add(string);
        }
        return hashSet != null ? hashSet.iterator() : null;
    }

    public void reset() {
        this.fExtraChecking = true;
        this.fFacetChecking = true;
        this.fNamespaces = true;
        this.fIdTable.clear();
        this.fIdRefTable.clear();
        this.fEntityState = null;
        this.fNamespaceContext = null;
        this.fSymbolTable = null;
    }

    public void resetIDTables() {
        this.fIdTable.clear();
        this.fIdRefTable.clear();
    }

    @Override
    public boolean needExtraChecking() {
        return this.fExtraChecking;
    }

    @Override
    public boolean needFacetChecking() {
        return this.fFacetChecking;
    }

    @Override
    public boolean needToNormalize() {
        return this.fNormalize;
    }

    @Override
    public boolean useNamespaces() {
        return this.fNamespaces;
    }

    @Override
    public boolean isEntityDeclared(String string) {
        if (this.fEntityState != null) {
            return this.fEntityState.isEntityDeclared(this.getSymbol(string));
        }
        return false;
    }

    @Override
    public boolean isEntityUnparsed(String string) {
        if (this.fEntityState != null) {
            return this.fEntityState.isEntityUnparsed(this.getSymbol(string));
        }
        return false;
    }

    @Override
    public boolean isIdDeclared(String string) {
        return this.fIdTable.containsKey(string);
    }

    @Override
    public void addId(String string) {
        this.fIdTable.put(string, fNullValue);
    }

    @Override
    public void addIdRef(String string) {
        this.fIdRefTable.put(string, fNullValue);
    }

    @Override
    public String getSymbol(String string) {
        if (this.fSymbolTable != null) {
            return this.fSymbolTable.addSymbol(string);
        }
        return string.intern();
    }

    @Override
    public String getURI(String string) {
        if (this.fNamespaceContext != null) {
            return this.fNamespaceContext.getURI(string);
        }
        return null;
    }

    public void setLocale(Locale locale) {
        this.fLocale = locale;
    }

    @Override
    public Locale getLocale() {
        return this.fLocale;
    }
}

