/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.ConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.ConfigurationDeclaration;
import org.apache.commons.configuration2.builder.combined.MultiWrapDynaBean;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class BaseConfigurationBuilderProvider
implements ConfigurationBuilderProvider {
    private static final Class<?>[] CTOR_PARAM_TYPES = new Class[]{Class.class, Map.class, Boolean.TYPE};
    private final String builderClass;
    private final String reloadingBuilderClass;
    private final String configurationClass;
    private final Collection<String> parameterClasses;

    public BaseConfigurationBuilderProvider(String bldrCls, String reloadBldrCls, String configCls, Collection<String> paramCls) {
        if (bldrCls == null) {
            throw new IllegalArgumentException("Builder class must not be null!");
        }
        if (configCls == null) {
            throw new IllegalArgumentException("Configuration class must not be null!");
        }
        this.builderClass = bldrCls;
        this.reloadingBuilderClass = reloadBldrCls;
        this.configurationClass = configCls;
        this.parameterClasses = BaseConfigurationBuilderProvider.initParameterClasses(paramCls);
    }

    public String getBuilderClass() {
        return this.builderClass;
    }

    public String getReloadingBuilderClass() {
        return this.reloadingBuilderClass;
    }

    public String getConfigurationClass() {
        return this.configurationClass;
    }

    public Collection<String> getParameterClasses() {
        return this.parameterClasses;
    }

    @Override
    public ConfigurationBuilder<? extends Configuration> getConfigurationBuilder(ConfigurationDeclaration decl) throws ConfigurationException {
        try {
            Collection<BuilderParameters> params = this.createParameterObjects();
            this.initializeParameterObjects(decl, params);
            BasicConfigurationBuilder<? extends Configuration> builder = this.createBuilder(decl, params);
            this.configureBuilder(builder, decl, params);
            return builder;
        }
        catch (ConfigurationException cex) {
            throw cex;
        }
        catch (Exception ex) {
            throw new ConfigurationException(ex);
        }
    }

    protected boolean isAllowFailOnInit(ConfigurationDeclaration decl) {
        return decl.isOptional() && decl.isForceCreate();
    }

    protected Collection<BuilderParameters> createParameterObjects() throws Exception {
        ArrayList<BuilderParameters> params = new ArrayList<BuilderParameters>(this.getParameterClasses().size());
        for (String paramcls : this.getParameterClasses()) {
            params.add(BaseConfigurationBuilderProvider.createParameterObject(paramcls));
        }
        return params;
    }

    protected void initializeParameterObjects(ConfigurationDeclaration decl, Collection<BuilderParameters> params) throws Exception {
        this.inheritParentBuilderProperties(decl, params);
        MultiWrapDynaBean wrapBean = new MultiWrapDynaBean(params);
        decl.getConfigurationBuilder().initBean(wrapBean, decl);
    }

    protected void inheritParentBuilderProperties(ConfigurationDeclaration decl, Collection<BuilderParameters> params) {
        params.forEach(p -> decl.getConfigurationBuilder().initChildBuilderParameters((BuilderParameters)p));
    }

    protected BasicConfigurationBuilder<? extends Configuration> createBuilder(ConfigurationDeclaration decl, Collection<BuilderParameters> params) throws Exception {
        Class<?> bldCls = ConfigurationUtils.loadClass(this.determineBuilderClass(decl));
        Class<?> configCls = ConfigurationUtils.loadClass(this.determineConfigurationClass(decl, params));
        Constructor<?> ctor = bldCls.getConstructor(CTOR_PARAM_TYPES);
        BasicConfigurationBuilder builder = (BasicConfigurationBuilder)ctor.newInstance(configCls, null, this.isAllowFailOnInit(decl));
        return builder;
    }

    protected void configureBuilder(BasicConfigurationBuilder<? extends Configuration> builder, ConfigurationDeclaration decl, Collection<BuilderParameters> params) throws Exception {
        builder.configure(params.toArray(new BuilderParameters[params.size()]));
    }

    protected String determineBuilderClass(ConfigurationDeclaration decl) throws ConfigurationException {
        if (decl.isReload()) {
            if (this.getReloadingBuilderClass() == null) {
                throw new ConfigurationException("No support for reloading for builder class " + this.getBuilderClass());
            }
            return this.getReloadingBuilderClass();
        }
        return this.getBuilderClass();
    }

    protected String determineConfigurationClass(ConfigurationDeclaration decl, Collection<BuilderParameters> params) throws ConfigurationException {
        return this.getConfigurationClass();
    }

    private static BuilderParameters createParameterObject(String paramcls) throws ReflectiveOperationException {
        return (BuilderParameters)ConfigurationUtils.loadClass(paramcls).newInstance();
    }

    private static Collection<String> initParameterClasses(Collection<String> paramCls) {
        if (paramCls == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(new ArrayList<String>(paramCls));
    }
}

