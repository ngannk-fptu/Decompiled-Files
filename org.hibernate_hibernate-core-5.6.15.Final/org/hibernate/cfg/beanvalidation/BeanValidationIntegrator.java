/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.beanvalidation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.beanvalidation.ActivationContext;
import org.hibernate.cfg.beanvalidation.IntegrationException;
import org.hibernate.cfg.beanvalidation.ValidationMode;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.jboss.logging.Logger;

public class BeanValidationIntegrator
implements Integrator {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)BeanValidationIntegrator.class.getName());
    public static final String APPLY_CONSTRAINTS = "hibernate.validator.apply_to_ddl";
    public static final String BV_CHECK_CLASS = "javax.validation.ConstraintViolation";
    public static final String JAKARTA_BV_CHECK_CLASS = "jakarta.validation.ConstraintViolation";
    public static final String MODE_PROPERTY = "javax.persistence.validation.mode";
    public static final String JAKARTA_MODE_PROPERTY = "jakarta.persistence.validation.mode";
    private static final String ACTIVATOR_CLASS_NAME = "org.hibernate.cfg.beanvalidation.TypeSafeActivator";
    private static final String VALIDATE_SUPPLIED_FACTORY_METHOD_NAME = "validateSuppliedFactory";
    private static final String ACTIVATE_METHOD_NAME = "activate";

    public static void validateFactory(Object object) {
        try {
            Class<?> activatorClass = BeanValidationIntegrator.class.getClassLoader().loadClass(ACTIVATOR_CLASS_NAME);
            try {
                Method validateMethod = activatorClass.getMethod(VALIDATE_SUPPLIED_FACTORY_METHOD_NAME, Object.class);
                try {
                    validateMethod.invoke(null, object);
                }
                catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof HibernateException) {
                        throw (HibernateException)((Object)e.getTargetException());
                    }
                    throw new HibernateException("Unable to check validity of passed ValidatorFactory", e);
                }
                catch (IllegalAccessException e) {
                    throw new HibernateException("Unable to check validity of passed ValidatorFactory", e);
                }
            }
            catch (HibernateException e) {
                throw e;
            }
            catch (Exception e) {
                throw new HibernateException("Could not locate method needed for ValidatorFactory validation", e);
            }
        }
        catch (HibernateException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HibernateException("Could not locate TypeSafeActivator class", e);
        }
    }

    @Override
    public void integrate(final Metadata metadata, final SessionFactoryImplementor sessionFactory, final SessionFactoryServiceRegistry serviceRegistry) {
        block10: {
            Set<ValidationMode> modes;
            ConfigurationService cfgService = serviceRegistry.getService(ConfigurationService.class);
            Object modeSetting = cfgService.getSettings().get(MODE_PROPERTY);
            if (modeSetting == null) {
                modeSetting = cfgService.getSettings().get(JAKARTA_MODE_PROPERTY);
            }
            if ((modes = ValidationMode.getModes(modeSetting)).size() > 1) {
                LOG.multipleValidationModes(ValidationMode.loggable(modes));
            }
            if (modes.size() == 1 && modes.contains((Object)ValidationMode.NONE)) {
                return;
            }
            ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
            if (this.isBeanValidationApiAvailable(classLoaderService)) {
                try {
                    Class typeSafeActivatorClass = this.loadTypeSafeActivatorClass(classLoaderService);
                    Method activateMethod = typeSafeActivatorClass.getMethod(ACTIVATE_METHOD_NAME, ActivationContext.class);
                    ActivationContext activationContext = new ActivationContext(){

                        @Override
                        public Set<ValidationMode> getValidationModes() {
                            return modes;
                        }

                        @Override
                        public Metadata getMetadata() {
                            return metadata;
                        }

                        @Override
                        public SessionFactoryImplementor getSessionFactory() {
                            return sessionFactory;
                        }

                        @Override
                        public SessionFactoryServiceRegistry getServiceRegistry() {
                            return serviceRegistry;
                        }
                    };
                    try {
                        activateMethod.invoke(null, activationContext);
                        break block10;
                    }
                    catch (InvocationTargetException e) {
                        if (HibernateException.class.isInstance(e.getTargetException())) {
                            throw (HibernateException)((Object)e.getTargetException());
                        }
                        throw new IntegrationException("Error activating Bean Validation integration", e.getTargetException());
                    }
                    catch (Exception e) {
                        throw new IntegrationException("Error activating Bean Validation integration", e);
                    }
                }
                catch (NoSuchMethodException e) {
                    throw new HibernateException("Unable to locate TypeSafeActivator#activate method", e);
                }
            }
            this.validateMissingBeanValidationApi(modes);
        }
    }

    private boolean isBeanValidationApiAvailable(ClassLoaderService classLoaderService) {
        try {
            classLoaderService.classForName(BV_CHECK_CLASS);
            return true;
        }
        catch (Exception e) {
            try {
                classLoaderService.classForName(JAKARTA_BV_CHECK_CLASS);
                return true;
            }
            catch (Exception e2) {
                return false;
            }
        }
    }

    private void validateMissingBeanValidationApi(Set<ValidationMode> modes) {
        if (modes.contains((Object)ValidationMode.CALLBACK)) {
            throw new IntegrationException("Bean Validation API was not available, but 'callback' validation was requested");
        }
        if (modes.contains((Object)ValidationMode.DDL)) {
            throw new IntegrationException("Bean Validation API was not available, but 'ddl' validation was requested");
        }
    }

    private Class loadTypeSafeActivatorClass(ClassLoaderService classLoaderService) {
        try {
            return classLoaderService.classForName(ACTIVATOR_CLASS_NAME);
        }
        catch (Exception e) {
            throw new HibernateException("Unable to load TypeSafeActivator class", e);
        }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }
}

