/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.wadl;

import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.research.ws.wadl.Application;
import java.util.Set;
import javax.ws.rs.core.MediaType;

public class ApplicationDescription {
    private Application _application;
    private WadlGenerator.ExternalGrammarDefinition _externalGrammarDefiniton;

    ApplicationDescription(Application application, WadlGenerator.ExternalGrammarDefinition externalGrammarDefiniton) {
        this._application = application;
        this._externalGrammarDefiniton = externalGrammarDefiniton;
    }

    public Application getApplication() {
        return this._application;
    }

    public <T> T resolve(Class type, MediaType mt, Class<T> resolvedType) {
        return this._externalGrammarDefiniton.resolve(type, mt, resolvedType);
    }

    public ExternalGrammar getExternalGrammar(String path) {
        return this._externalGrammarDefiniton.map.get(path);
    }

    public Set<String> getExternalMetadataKeys() {
        return this._externalGrammarDefiniton.map.keySet();
    }

    public static class ExternalGrammar {
        private MediaType _type;
        private byte[] _content;
        private boolean _includedInGrammar;

        public ExternalGrammar(MediaType type, byte[] content, boolean includedInGrammar) {
            this._type = type;
            this._content = content;
            this._includedInGrammar = includedInGrammar;
        }

        public MediaType getType() {
            return this._type;
        }

        public byte[] getContent() {
            return (byte[])this._content.clone();
        }

        public boolean isIncludedInGrammar() {
            return this._includedInGrammar;
        }
    }
}

