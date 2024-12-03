/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import java.util.List;
import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.ConfigurationUtils;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.ToolConfiguration;
import org.apache.velocity.tools.config.ToolboxConfiguration;

public class EasyFactoryConfiguration
extends FactoryConfiguration {
    private boolean addedDefaults = false;
    private EasyWrap<ToolboxConfiguration> toolbox;

    public EasyFactoryConfiguration() {
        this(false);
    }

    public EasyFactoryConfiguration(boolean includeDefaults) {
        this(includeDefaults, String.valueOf(includeDefaults));
    }

    public EasyFactoryConfiguration(boolean includeDefaults, String source) {
        super(EasyFactoryConfiguration.class, source);
        if (includeDefaults) {
            this.addDefaultTools();
            List<String> sources = this.getSources();
            String first = sources.remove(0);
            sources.add(first);
        }
    }

    public EasyFactoryConfiguration addDefaultTools() {
        if (!this.addedDefaults) {
            this.addConfiguration(ConfigurationUtils.getDefaultTools());
            this.addedDefaults = true;
        }
        return this;
    }

    public EasyFactoryConfiguration autoLoad() {
        return this.autoLoad(true);
    }

    public EasyFactoryConfiguration autoLoad(boolean includeDefaults) {
        this.addConfiguration(ConfigurationUtils.getAutoLoaded(includeDefaults));
        this.addedDefaults = true;
        return this;
    }

    public EasyData data(String key, Object value) {
        Data datum = new Data();
        datum.setKey(key);
        datum.setValue(value);
        this.addData(datum);
        return new EasyData(datum, this);
    }

    public EasyFactoryConfiguration data(String key, String type, Object value) {
        EasyData datum = this.data(key, value);
        datum.type(type);
        return this;
    }

    protected EasyFactoryConfiguration data(String key, Data.Type type, Object value) {
        EasyData datum = this.data(key, value);
        datum.type(type);
        return this;
    }

    public EasyFactoryConfiguration string(String key, Object value) {
        return this.data(key, Data.Type.STRING, value);
    }

    public EasyFactoryConfiguration number(String key, Object value) {
        return this.data(key, Data.Type.NUMBER, value);
    }

    public EasyFactoryConfiguration bool(String key, Object value) {
        return this.data(key, Data.Type.BOOLEAN, value);
    }

    public EasyWrap<ToolboxConfiguration> toolbox(String scope) {
        ToolboxConfiguration toolbox = new ToolboxConfiguration();
        toolbox.setScope(scope);
        this.addToolbox(toolbox);
        this.toolbox = new EasyWrap(this, (Configuration)toolbox, (Configuration)this);
        return this.toolbox;
    }

    public EasyWrap<ToolConfiguration> tool(String classname) {
        return this.tool(null, classname);
    }

    public EasyWrap<ToolConfiguration> tool(Class clazz) {
        return this.tool(null, clazz);
    }

    public EasyWrap<ToolConfiguration> tool(String key, String classname) {
        if (this.toolbox == null) {
            this.toolbox("request");
        }
        return this.toolbox.tool(key, classname);
    }

    public EasyWrap<ToolConfiguration> tool(String key, Class clazz) {
        return this.tool(key, clazz.getName());
    }

    public EasyFactoryConfiguration property(String name, Object value) {
        this.setProperty(name, value);
        return this;
    }

    public static class EasyWrap<C extends Configuration> {
        private final C config;
        private final Configuration parent;
        final /* synthetic */ EasyFactoryConfiguration this$0;

        public EasyWrap(C config, Configuration parent) {
            this.this$0 = this$0;
            this.config = config;
            this.parent = parent;
        }

        public C getConfiguration() {
            return this.config;
        }

        public Configuration getParent() {
            return this.parent;
        }

        public EasyWrap<C> property(String name, Object value) {
            ((Configuration)this.config).setProperty(name, value);
            return this;
        }

        public EasyWrap<C> restrictTo(String path) {
            if (this.config instanceof ToolConfiguration) {
                ToolConfiguration tool = (ToolConfiguration)this.config;
                tool.setRestrictTo(path);
                return this;
            }
            if (this.config instanceof ToolboxConfiguration) {
                ToolboxConfiguration toolbox = (ToolboxConfiguration)this.config;
                for (ToolConfiguration tool : toolbox.getTools()) {
                    tool.setRestrictTo(path);
                }
                return this;
            }
            throw new IllegalStateException("Wrapping unknown " + Configuration.class.getName() + ": " + this.getConfiguration());
        }

        public EasyWrap addDefaultTools() {
            this.this$0.addDefaultTools();
            return this;
        }

        public EasyWrap tool(Class clazz) {
            return this.tool(null, clazz);
        }

        public EasyWrap tool(String classname) {
            return this.tool(null, classname);
        }

        public EasyWrap tool(String key, Class clazz) {
            return this.tool(key, clazz.getName());
        }

        public EasyWrap tool(String key, String classname) {
            ToolConfiguration tool = new ToolConfiguration();
            if (key != null) {
                tool.setKey(key);
            }
            tool.setClassname(classname);
            if (this.config instanceof ToolConfiguration) {
                ToolboxConfiguration toolbox = (ToolboxConfiguration)this.getParent();
                toolbox.addTool(tool);
                return new EasyWrap(this.this$0, (Configuration)tool, (Configuration)toolbox);
            }
            if (this.config instanceof ToolboxConfiguration) {
                ToolboxConfiguration toolbox = (ToolboxConfiguration)this.getConfiguration();
                toolbox.addTool(tool);
                return new EasyWrap(this.this$0, (Configuration)tool, (Configuration)toolbox);
            }
            throw new IllegalStateException("Wrapping unknown " + Configuration.class.getName() + ": " + this.getConfiguration());
        }
    }

    public static class EasyData {
        private final Data datum;
        private final Configuration parent;

        public EasyData(Data datum, Configuration parent) {
            this.datum = datum;
            this.parent = parent;
        }

        public Data getData() {
            return this.datum;
        }

        public Configuration getParent() {
            return this.parent;
        }

        public EasyData type(String type) {
            this.datum.setType(type);
            return this;
        }

        protected EasyData type(Data.Type type) {
            this.datum.setType(type);
            return this;
        }

        public EasyData target(Class clazz) {
            this.datum.setTargetClass(clazz);
            return this;
        }

        public EasyData classname(String classname) {
            this.datum.setClassname(classname);
            return this;
        }

        public EasyData converter(String converter) {
            this.datum.setConverter(converter);
            return this;
        }

        public EasyData converter(Class clazz) {
            this.datum.setConverter(clazz);
            return this;
        }
    }
}

