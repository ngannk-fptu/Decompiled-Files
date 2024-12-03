/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.event.Observes
 *  javax.enterprise.inject.Any
 *  javax.enterprise.inject.Default
 *  javax.enterprise.inject.spi.AfterDeploymentValidation
 *  javax.enterprise.inject.spi.AnnotatedType
 *  javax.enterprise.inject.spi.BeanManager
 *  javax.enterprise.inject.spi.Extension
 *  javax.enterprise.inject.spi.ProcessAnnotatedType
 *  javax.enterprise.util.AnnotationLiteral
 *  javax.inject.Qualifier
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.log.LogMessage
 */
package org.springframework.data.repository.cdi;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.log.LogMessage;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.cdi.CdiRepositoryBean;
import org.springframework.data.repository.cdi.CdiRepositoryContext;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;

public abstract class CdiRepositoryExtensionSupport
implements Extension {
    private static final Log logger = LogFactory.getLog(CdiRepositoryExtensionSupport.class);
    private final Map<Class<?>, Set<Annotation>> repositoryTypes = new HashMap();
    private final Set<CdiRepositoryBean<?>> eagerRepositories = new HashSet();
    private final CdiRepositoryContext context = new CdiRepositoryContext(this.getClass().getClassLoader());

    protected CdiRepositoryExtensionSupport() {
    }

    protected <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> processAnnotatedType) {
        AnnotatedType annotatedType = processAnnotatedType.getAnnotatedType();
        Class repositoryType = annotatedType.getJavaClass();
        if (this.isRepository(repositoryType)) {
            Set<Annotation> qualifiers = this.getQualifiers(repositoryType);
            if (logger.isDebugEnabled()) {
                logger.debug((Object)String.format("Discovered repository type '%s' with qualifiers %s.", repositoryType.getName(), qualifiers));
            }
            this.repositoryTypes.put(repositoryType, qualifiers);
        }
    }

    private boolean isRepository(Class<?> type) {
        boolean isInterface = type.isInterface();
        boolean extendsRepository = Repository.class.isAssignableFrom(type);
        boolean isAnnotated = type.isAnnotationPresent(RepositoryDefinition.class);
        boolean excludedByAnnotation = type.isAnnotationPresent(NoRepositoryBean.class);
        return isInterface && (extendsRepository || isAnnotated) && !excludedByAnnotation;
    }

    private Set<Annotation> getQualifiers(Class<?> type) {
        Annotation[] annotations;
        HashSet<Annotation> qualifiers = new HashSet<Annotation>();
        for (Annotation annotation : annotations = type.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!annotationType.isAnnotationPresent(Qualifier.class)) continue;
            qualifiers.add(annotation);
        }
        if (qualifiers.isEmpty()) {
            qualifiers.add((Annotation)((Object)DefaultAnnotationLiteral.INSTANCE));
        }
        qualifiers.add((Annotation)((Object)AnyAnnotationLiteral.INSTANCE));
        return qualifiers;
    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {
        for (CdiRepositoryBean<?> bean : this.eagerRepositories) {
            logger.debug((Object)LogMessage.format((String)"Eagerly instantiating CDI repository bean for %s.", bean.getBeanClass()));
            bean.initialize();
        }
    }

    protected Iterable<Map.Entry<Class<?>, Set<Annotation>>> getRepositoryTypes() {
        return this.repositoryTypes.entrySet();
    }

    protected void registerBean(CdiRepositoryBean<?> bean) {
        Class<?> repositoryInterface = bean.getBeanClass();
        if (AnnotationUtils.findAnnotation(repositoryInterface, Eager.class) != null) {
            this.eagerRepositories.add(bean);
        }
    }

    protected CustomRepositoryImplementationDetector getCustomImplementationDetector() {
        return this.context.getCustomRepositoryImplementationDetector();
    }

    protected CdiRepositoryContext getRepositoryContext() {
        return this.context;
    }

    static class AnyAnnotationLiteral
    extends AnnotationLiteral<Any>
    implements Any {
        private static final long serialVersionUID = 7261821376671361463L;
        private static final AnyAnnotationLiteral INSTANCE = new AnyAnnotationLiteral();

        AnyAnnotationLiteral() {
        }
    }

    static class DefaultAnnotationLiteral
    extends AnnotationLiteral<Default>
    implements Default {
        private static final long serialVersionUID = 511359421048623933L;
        private static final DefaultAnnotationLiteral INSTANCE = new DefaultAnnotationLiteral();

        DefaultAnnotationLiteral() {
        }
    }
}

