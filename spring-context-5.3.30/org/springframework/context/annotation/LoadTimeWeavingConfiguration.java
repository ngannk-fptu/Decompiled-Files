/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotatedTypeMetadata
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.LoadTimeWeavingConfigurer;
import org.springframework.context.annotation.Role;
import org.springframework.context.weaving.AspectJWeavingEnabler;
import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Configuration(proxyBeanMethods=false)
@Role(value=2)
public class LoadTimeWeavingConfiguration
implements ImportAware,
BeanClassLoaderAware {
    @Nullable
    private AnnotationAttributes enableLTW;
    @Nullable
    private LoadTimeWeavingConfigurer ltwConfigurer;
    @Nullable
    private ClassLoader beanClassLoader;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableLTW = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)importMetadata, EnableLoadTimeWeaving.class);
        if (this.enableLTW == null) {
            throw new IllegalArgumentException("@EnableLoadTimeWeaving is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required=false)
    public void setLoadTimeWeavingConfigurer(LoadTimeWeavingConfigurer ltwConfigurer) {
        this.ltwConfigurer = ltwConfigurer;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Bean(name={"loadTimeWeaver"})
    @Role(value=2)
    public LoadTimeWeaver loadTimeWeaver() {
        Assert.state((this.beanClassLoader != null ? 1 : 0) != 0, (String)"No ClassLoader set");
        LoadTimeWeaver loadTimeWeaver = null;
        if (this.ltwConfigurer != null) {
            loadTimeWeaver = this.ltwConfigurer.getLoadTimeWeaver();
        }
        if (loadTimeWeaver == null) {
            loadTimeWeaver = new DefaultContextLoadTimeWeaver(this.beanClassLoader);
        }
        if (this.enableLTW != null) {
            EnableLoadTimeWeaving.AspectJWeaving aspectJWeaving = (EnableLoadTimeWeaving.AspectJWeaving)this.enableLTW.getEnum("aspectjWeaving");
            switch (aspectJWeaving) {
                case DISABLED: {
                    break;
                }
                case AUTODETECT: {
                    if (this.beanClassLoader.getResource("META-INF/aop.xml") == null) break;
                    AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                    break;
                }
                case ENABLED: {
                    AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                }
            }
        }
        return loadTimeWeaver;
    }
}

