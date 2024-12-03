/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.config.BeanSelectionProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.impl.LocatableFactory;
import com.opensymphony.xwork2.config.providers.ValueSubstitutor;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.config.StrutsJavaConfiguration;
import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.config.entities.ConstantConfig;

public class StrutsJavaConfigurationProvider
implements ConfigurationProvider {
    private static final Logger LOG = LogManager.getLogger(StrutsJavaConfigurationProvider.class);
    private final StrutsJavaConfiguration javaConfig;
    private Configuration configuration;
    private boolean throwExceptionOnDuplicateBeans = true;
    private ValueSubstitutor valueSubstitutor;

    public StrutsJavaConfigurationProvider(StrutsJavaConfiguration javaConfig) {
        this.javaConfig = javaConfig;
    }

    public void setThrowExceptionOnDuplicateBeans(boolean val) {
        this.throwExceptionOnDuplicateBeans = val;
    }

    @Inject(required=false)
    public void setValueSubstitutor(ValueSubstitutor valueSubstitutor) {
        this.valueSubstitutor = valueSubstitutor;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        List<ConstantConfig> constantConfigList;
        HashMap<String, Object> loadedBeans = new HashMap<String, Object>();
        List<BeanConfig> beanConfigs = this.javaConfig.beans();
        if (beanConfigs != null) {
            for (BeanConfig beanConfig : beanConfigs) {
                if (beanConfig == null) continue;
                this.registerBean(loadedBeans, builder, beanConfig);
            }
        }
        if ((constantConfigList = this.javaConfig.constants()) != null) {
            for (ConstantConfig constantConf : constantConfigList) {
                if (constantConf == null) continue;
                Map<String, String> constantMap = constantConf.getAllAsStringsMap();
                for (Map.Entry<String, String> entr : constantMap.entrySet()) {
                    if (entr.getKey() == null || entr.getValue() == null) continue;
                    this.registerConstant(props, entr.getKey(), entr.getValue());
                }
            }
        }
        this.javaConfig.beanSelection().ifPresent(beanSelectionConfig -> {
            try {
                LOG.debug("Registering bean selection provider {} of type {}", (Object)beanSelectionConfig.getName(), (Object)beanSelectionConfig.getClazz().getName());
                BeanSelectionProvider provider = beanSelectionConfig.getClazz().newInstance();
                provider.register(builder, props);
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new ConfigurationException("Unable to load : name:" + beanSelectionConfig.getName() + " class:" + beanSelectionConfig.getClazz().getName());
            }
        });
        List<String> list = this.javaConfig.unknownHandlerStack();
        if (list != null) {
            ArrayList<UnknownHandlerConfig> unknownHandlerStack = new ArrayList<UnknownHandlerConfig>();
            for (String unknownHandler : list) {
                Location location = LocationUtils.getLocation(unknownHandler);
                unknownHandlerStack.add(new UnknownHandlerConfig(unknownHandler, location));
            }
            if (!unknownHandlerStack.isEmpty()) {
                this.configuration.setUnknownHandlerStack(unknownHandlerStack);
            }
        }
    }

    private void registerConstant(LocatableProperties props, String key, String value) {
        if (this.valueSubstitutor != null) {
            LOG.debug("Substituting value [{}] using [{}]", (Object)value, (Object)this.valueSubstitutor.getClass().getName());
            value = this.valueSubstitutor.substitute(value);
        }
        props.setProperty(key, value, this.javaConfig);
    }

    private void registerBean(Map<String, Object> loadedBeans, ContainerBuilder containerBuilder, BeanConfig beanConf) {
        try {
            if (beanConf.isOnlyStatic()) {
                beanConf.getClazz().getDeclaredClasses();
                containerBuilder.injectStatics(beanConf.getClazz());
            } else {
                if (containerBuilder.contains(beanConf.getType(), beanConf.getName())) {
                    Location loc = LocationUtils.getLocation(loadedBeans.get(beanConf.getType().getName() + beanConf.getName()));
                    if (this.throwExceptionOnDuplicateBeans) {
                        throw new ConfigurationException("Bean type " + beanConf.getType() + " with the name " + beanConf.getName() + " has already been loaded by " + loc, (Object)this.javaConfig);
                    }
                }
                beanConf.getClazz().getDeclaredConstructors();
                LOG.debug("Loaded type: {} name: {} clazz: {}", beanConf.getType(), (Object)beanConf.getName(), beanConf.getClazz());
                containerBuilder.factory(beanConf.getType(), beanConf.getName(), new LocatableFactory(beanConf.getName(), beanConf.getType(), beanConf.getClazz(), beanConf.getScope(), this.javaConfig), beanConf.getScope());
            }
            loadedBeans.put(beanConf.getType().getName() + beanConf.getName(), this.javaConfig);
        }
        catch (Throwable ex) {
            if (!beanConf.isOptional()) {
                throw new ConfigurationException("Unable to load bean: type:" + beanConf.getType() + " class:" + beanConf.getClazz(), ex);
            }
            LOG.debug("Unable to load optional class: {}", beanConf.getClazz());
        }
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
        this.configuration = configuration;
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void loadPackages() throws ConfigurationException {
    }

    @Override
    public void destroy() {
    }
}

