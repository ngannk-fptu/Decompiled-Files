/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.schema.Table
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package com.atlassian.data.activeobjects.repository.config;

import com.atlassian.data.activeobjects.repository.ActiveObjectsRepository;
import com.atlassian.data.activeobjects.repository.config.InspectionClassLoader;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEvaluationContextExtension;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsRepositoryFactoryBean;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import net.java.ao.schema.Table;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class ActiveObjectsRepositoryConfigExtension
extends RepositoryConfigurationExtensionSupport {
    private static final String ESCAPE_CHARACTER_PROPERTY = "escapeCharacter";

    @Override
    public String getModuleName() {
        return "AO";
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return ActiveObjectsRepositoryFactoryBean.class.getName();
    }

    @Override
    protected String getModulePrefix() {
        return this.getModuleName().toLowerCase(Locale.US);
    }

    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Arrays.asList(Table.class);
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.singleton(ActiveObjectsRepository.class);
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource source) {
        builder.addPropertyValue(ESCAPE_CHARACTER_PROPERTY, (Object)ActiveObjectsRepositoryConfigExtension.getEscapeCharacter(source).orElse(Character.valueOf('\\')));
    }

    private static Optional<Character> getEscapeCharacter(RepositoryConfigurationSource source) {
        try {
            return AnnotationRepositoryConfigurationSource.class.isInstance(source) ? Optional.ofNullable((Character)((AnnotationRepositoryConfigurationSource)AnnotationRepositoryConfigurationSource.class.cast(source)).getAttributes().get((Object)ESCAPE_CHARACTER_PROPERTY)) : source.getAttribute(ESCAPE_CHARACTER_PROPERTY).map(it -> Character.valueOf(it.toCharArray()[0]));
        }
        catch (IllegalArgumentException iae) {
            return Optional.empty();
        }
    }

    @Override
    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource config) {
        super.registerBeansForRoot(registry, config);
        Object source = config.getSource();
        ActiveObjectsRepositoryConfigExtension.registerIfNotAlreadyRegistered(() -> {
            Character value = ActiveObjectsRepositoryConfigExtension.getEscapeCharacter(config).orElse(Character.valueOf('\\'));
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ActiveObjectsEvaluationContextExtension.class);
            builder.addConstructorArgValue((Object)value);
            return builder.getBeanDefinition();
        }, registry, ActiveObjectsEvaluationContextExtension.class.getName(), source);
    }

    @Override
    protected ClassLoader getConfigurationInspectionClassLoader(ResourceLoader loader) {
        ClassLoader classLoader = loader.getClassLoader();
        return classLoader != null && LazyJvmAgent.isActive(loader.getClassLoader()) ? new InspectionClassLoader(loader.getClassLoader()) : loader.getClassLoader();
    }

    static final class LazyJvmAgent {
        private static final Set<String> AGENT_CLASSES;

        static boolean isActive(@Nullable ClassLoader classLoader) {
            return AGENT_CLASSES.stream().anyMatch(agentClass -> ClassUtils.isPresent((String)agentClass, (ClassLoader)classLoader));
        }

        private LazyJvmAgent() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }

        static {
            LinkedHashSet<String> agentClasses = new LinkedHashSet<String>();
            agentClasses.add("org.springframework.instrument.InstrumentationSavingAgent");
            agentClasses.add("org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializerAgent");
            AGENT_CLASSES = Collections.unmodifiableSet(agentClasses);
        }
    }
}

