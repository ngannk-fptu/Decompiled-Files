/*
 * Decompiled with CFR 0.152.
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
        Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableMBeanExport.class.getName());
        this.enableMBeanExport = AnnotationAttributes.fromMap(map);
        if (this.enableMBeanExport == null) {
            throw new IllegalArgumentException("@EnableMBeanExport is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Override
    public void setEnvironment(Environment environment2) {
        this.environment = environment2;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean(name={"mbeanExporter"})
    @Role(value=2)
    public AnnotationMBeanExporter mbeanExporter() {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        Assert.state(this.enableMBeanExport != null, "No EnableMBeanExport annotation found");
        this.setupDomain(exporter, this.enableMBeanExport);
        this.setupServer(exporter, this.enableMBeanExport);
        this.setupRegistrationPolicy(exporter, this.enableMBeanExport);
        return exporter;
    }

    private void setupDomain(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        String defaultDomain = enableMBeanExport.getString("defaultDomain");
        if (StringUtils.hasLength(defaultDomain) && this.environment != null) {
            defaultDomain = this.environment.resolvePlaceholders(defaultDomain);
        }
        if (StringUtils.hasText(defaultDomain)) {
            exporter.setDefaultDomain(defaultDomain);
        }
    }

    private void setupServer(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        String server = enableMBeanExport.getString("server");
        if (StringUtils.hasLength(server) && this.environment != null) {
            server = this.environment.resolvePlaceholders(server);
        }
        if (StringUtils.hasText(server)) {
            Assert.state(this.beanFactory != null, "No BeanFactory set");
            exporter.setServer(this.beanFactory.getBean(server, MBeanServer.class));
        } else {
            MBeanServer mbeanServer;
            SpecificPlatform specificPlatform = SpecificPlatform.get();
            if (specificPlatform != null && (mbeanServer = specificPlatform.getMBeanServer()) != null) {
                exporter.setServer(mbeanServer);
            }
        }
    }

    private void setupRegistrationPolicy(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        RegistrationPolicy registrationPolicy = (RegistrationPolicy)((Object)enableMBeanExport.getEnum("registration"));
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
                if (!ClassUtils.isPresent(environment2.identifyingClass, classLoader)) continue;
                return environment2;
            }
            return null;
        }
    }
}

