/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.env.Environment
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.context.annotation;

import java.util.Map;
import javax.management.MBeanServer;
import javax.naming.NamingException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.jmx.support.WebSphereMBeanServerFactoryBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods=false)
@Role(value=2)
public class MBeanExportConfiguration
implements ImportAware,
EnvironmentAware,
BeanFactoryAware {
    private static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";
    @Nullable
    private AnnotationAttributes enableMBeanExport;
    @Nullable
    private Environment environment;
    @Nullable
    private BeanFactory beanFactory;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map map = importMetadata.getAnnotationAttributes(EnableMBeanExport.class.getName());
        this.enableMBeanExport = AnnotationAttributes.fromMap((Map)map);
        if (this.enableMBeanExport == null) {
            throw new IllegalArgumentException("@EnableMBeanExport is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Override
    public void setEnvironment(Environment environment2) {
        this.environment = environment2;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean(name={"mbeanExporter"})
    @Role(value=2)
    public AnnotationMBeanExporter mbeanExporter() {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        Assert.state((this.enableMBeanExport != null ? 1 : 0) != 0, (String)"No EnableMBeanExport annotation found");
        this.setupDomain(exporter, this.enableMBeanExport);
        this.setupServer(exporter, this.enableMBeanExport);
        this.setupRegistrationPolicy(exporter, this.enableMBeanExport);
        return exporter;
    }

    private void setupDomain(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        String defaultDomain = enableMBeanExport.getString("defaultDomain");
        if (StringUtils.hasLength((String)defaultDomain) && this.environment != null) {
            defaultDomain = this.environment.resolvePlaceholders(defaultDomain);
        }
        if (StringUtils.hasText((String)defaultDomain)) {
            exporter.setDefaultDomain(defaultDomain);
        }
    }

    private void setupServer(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        String server = enableMBeanExport.getString("server");
        if (StringUtils.hasLength((String)server) && this.environment != null) {
            server = this.environment.resolvePlaceholders(server);
        }
        if (StringUtils.hasText((String)server)) {
            Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory set");
            exporter.setServer((MBeanServer)this.beanFactory.getBean(server, MBeanServer.class));
        } else {
            MBeanServer mbeanServer;
            SpecificPlatform specificPlatform = SpecificPlatform.get();
            if (specificPlatform != null && (mbeanServer = specificPlatform.getMBeanServer()) != null) {
                exporter.setServer(mbeanServer);
            }
        }
    }

    private void setupRegistrationPolicy(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        RegistrationPolicy registrationPolicy = (RegistrationPolicy)enableMBeanExport.getEnum("registration");
        exporter.setRegistrationPolicy(registrationPolicy);
    }

    public static enum SpecificPlatform {
        WEBLOGIC("weblogic.management.Helper"){

            @Override
            public MBeanServer getMBeanServer() {
                try {
                    return new JndiLocatorDelegate().lookup("java:comp/env/jmx/runtime", MBeanServer.class);
                }
                catch (NamingException ex) {
                    throw new MBeanServerNotFoundException("Failed to retrieve WebLogic MBeanServer from JNDI", ex);
                }
            }
        }
        ,
        WEBSPHERE("com.ibm.websphere.management.AdminServiceFactory"){

            @Override
            public MBeanServer getMBeanServer() {
                WebSphereMBeanServerFactoryBean fb = new WebSphereMBeanServerFactoryBean();
                fb.afterPropertiesSet();
                return fb.getObject();
            }
        };

        private final String identifyingClass;

        private SpecificPlatform(String identifyingClass) {
            this.identifyingClass = identifyingClass;
        }

        @Nullable
        public abstract MBeanServer getMBeanServer();

        @Nullable
        public static SpecificPlatform get() {
            ClassLoader classLoader = MBeanExportConfiguration.class.getClassLoader();
            for (SpecificPlatform environment2 : SpecificPlatform.values()) {
                if (!ClassUtils.isPresent((String)environment2.identifyingClass, (ClassLoader)classLoader)) continue;
                return environment2;
            }
            return null;
        }
    }
}

