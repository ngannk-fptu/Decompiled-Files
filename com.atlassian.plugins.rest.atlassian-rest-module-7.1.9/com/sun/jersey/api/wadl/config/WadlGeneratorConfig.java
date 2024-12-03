/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.wadl.config;

import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;
import com.sun.jersey.api.wadl.config.WadlGeneratorLoader;
import com.sun.jersey.server.wadl.WadlGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class WadlGeneratorConfig {
    public abstract List<WadlGeneratorDescription> configure();

    public WadlGenerator createWadlGenerator() {
        WadlGenerator wadlGenerator = null;
        List<WadlGeneratorDescription> wadlGeneratorDescriptions = this.configure();
        try {
            wadlGenerator = WadlGeneratorLoader.loadWadlGeneratorDescriptions(wadlGeneratorDescriptions);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load wadl generators from wadlGeneratorDescriptions.", e);
        }
        return wadlGenerator;
    }

    public static WadlGeneratorConfigDescriptionBuilder generator(Class<? extends WadlGenerator> generatorClass) {
        return new WadlGeneratorConfigDescriptionBuilder().generator(generatorClass);
    }

    static class WadlGeneratorConfigImpl
    extends WadlGeneratorConfig {
        public List<WadlGeneratorDescription> _descriptions;

        public WadlGeneratorConfigImpl(List<WadlGeneratorDescription> descriptions) {
            this._descriptions = descriptions;
        }

        @Override
        public List<WadlGeneratorDescription> configure() {
            return this._descriptions;
        }
    }

    public static class WadlGeneratorConfigDescriptionBuilder {
        private List<WadlGeneratorDescription> _descriptions = new ArrayList<WadlGeneratorDescription>();
        private WadlGeneratorDescription _description;

        public WadlGeneratorConfigDescriptionBuilder generator(Class<? extends WadlGenerator> generatorClass) {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            this._description = new WadlGeneratorDescription();
            this._description.setGeneratorClass(generatorClass);
            return this;
        }

        public WadlGeneratorConfigDescriptionBuilder prop(String propName, Object propValue) {
            if (this._description.getProperties() == null) {
                this._description.setProperties(new Properties());
            }
            this._description.getProperties().put(propName, propValue);
            return this;
        }

        public List<WadlGeneratorDescription> descriptions() {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            return this._descriptions;
        }

        public WadlGeneratorConfig build() {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            return new WadlGeneratorConfigImpl(this._descriptions);
        }
    }
}

