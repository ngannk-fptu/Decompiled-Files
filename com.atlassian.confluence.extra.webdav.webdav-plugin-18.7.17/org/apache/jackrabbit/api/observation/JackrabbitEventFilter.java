/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.observation;

import java.util.Arrays;

public class JackrabbitEventFilter {
    private int eventTypes;
    private String absPath;
    private boolean isDeep;
    private String[] identifiers;
    private String[] nodeTypeNames;
    private boolean noLocal;
    private String[] absPaths = new String[0];
    private String[] excludedPaths = new String[0];
    private boolean noExternal;
    private boolean noInternal;

    public JackrabbitEventFilter setEventTypes(int eventTypes) {
        this.eventTypes = eventTypes;
        return this;
    }

    public int getEventTypes() {
        return this.eventTypes;
    }

    public JackrabbitEventFilter setAbsPath(String absPath) {
        this.absPath = absPath;
        return this;
    }

    public String getAbsPath() {
        return this.absPath;
    }

    public JackrabbitEventFilter setIsDeep(boolean isDeep) {
        this.isDeep = isDeep;
        return this;
    }

    public boolean getIsDeep() {
        return this.isDeep;
    }

    public JackrabbitEventFilter setIdentifiers(String[] identifiers) {
        this.identifiers = Arrays.copyOf(identifiers, identifiers.length);
        return null;
    }

    public String[] getIdentifiers() {
        return this.identifiers == null ? null : Arrays.copyOf(this.identifiers, this.identifiers.length);
    }

    public JackrabbitEventFilter setNodeTypes(String[] nodeTypeNames) {
        this.nodeTypeNames = Arrays.copyOf(nodeTypeNames, nodeTypeNames.length);
        return this;
    }

    public String[] getNodeTypes() {
        return this.nodeTypeNames == null ? null : Arrays.copyOf(this.nodeTypeNames, this.nodeTypeNames.length);
    }

    public JackrabbitEventFilter setNoLocal(boolean noLocal) {
        this.noLocal = noLocal;
        return this;
    }

    public boolean getNoLocal() {
        return this.noLocal;
    }

    public JackrabbitEventFilter setAdditionalPaths(String ... absPaths) {
        this.absPaths = Arrays.copyOf(absPaths, absPaths.length);
        return this;
    }

    public String[] getAdditionalPaths() {
        return Arrays.copyOf(this.absPaths, this.absPaths.length);
    }

    public JackrabbitEventFilter setExcludedPaths(String ... excludedPaths) {
        this.excludedPaths = Arrays.copyOf(excludedPaths, excludedPaths.length);
        return this;
    }

    public String[] getExcludedPaths() {
        return Arrays.copyOf(this.excludedPaths, this.excludedPaths.length);
    }

    public JackrabbitEventFilter setNoExternal(boolean noExternal) {
        this.noExternal = noExternal;
        return this;
    }

    public boolean getNoExternal() {
        return this.noExternal;
    }

    public JackrabbitEventFilter setNoInternal(boolean noInternal) {
        this.noInternal = noInternal;
        return this;
    }

    public boolean getNoInternal() {
        return this.noInternal;
    }
}

