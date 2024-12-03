/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Validation
 *  javax.validation.ValidatorFactory
 *  javax.validation.constraints.Digits
 *  javax.validation.constraints.Max
 *  javax.validation.constraints.Min
 *  javax.validation.constraints.NotNull
 *  javax.validation.constraints.Size
 *  javax.validation.metadata.BeanDescriptor
 *  javax.validation.metadata.ConstraintDescriptor
 *  javax.validation.metadata.PropertyDescriptor
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.beanvalidation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cfg.beanvalidation.ActivationContext;
import org.hibernate.cfg.beanvalidation.BeanValidationEventListener;
import org.hibernate.cfg.beanvalidation.DuplicationStrategyImpl;
import org.hibernate.cfg.beanvalidation.GroupsPerOperation;
import org.hibernate.cfg.beanvalidation.IntegrationException;
import org.hibernate.cfg.beanvalidation.ValidationMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SingleTableSubclass;
import org.jboss.logging.Logger;

class TypeSafeActivator {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)TypeSafeActivator.class.getName());

    TypeSafeActivator() {
    }

    public static void validateSuppliedFactory(Object object) {
        if (!ValidatorFactory.class.isInstance(object)) {
            throw new IntegrationException("Given object was not an instance of " + ValidatorFactory.class.getName() + "[" + object.getClass().getName() + "]");
        }
    }

    public static void activate(ActivationContext activationContext) {
        ValidatorFactory factory;
        try {
            factory = TypeSafeActivator.getValidatorFactory(activationContext);
        }
        catch (IntegrationException e) {
            if (activationContext.getValidationModes().contains((Object)ValidationMode.CALLBACK)) {
                throw new IntegrationException("Bean Validation provider was not available, but 'callback' validation was requested", (Throwable)((Object)e));
            }
            if (activationContext.getValidationModes().contains((Object)ValidationMode.DDL)) {
                throw new IntegrationException("Bean Validation provider was not available, but 'ddl' validation was requested", (Throwable)((Object)e));
            }
            LOG.debug("Unable to acquire Bean Validation ValidatorFactory, skipping activation");
            return;
        }
        TypeSafeActivator.applyRelationalConstraints(factory, activationContext);
        TypeSafeActivator.applyCallbackListeners(factory, activationContext);
    }

    public static void applyCallbackListeners(ValidatorFactory validatorFactory, ActivationContext activationContext) {
        Set<ValidationMode> modes = activationContext.getValidationModes();
        if (!modes.contains((Object)ValidationMode.CALLBACK) && !modes.contains((Object)ValidationMode.AUTO)) {
            return;
        }
        ConfigurationService cfgService = activationContext.getServiceRegistry().getService(ConfigurationService.class);
        ClassLoaderService classLoaderService = activationContext.getServiceRegistry().getService(ClassLoaderService.class);
        if (cfgService.getSettings().get("hibernate.check_nullability") == null) {
            activationContext.getSessionFactory().getSessionFactoryOptions().setCheckNullability(false);
        }
        BeanValidationEventListener listener = new BeanValidationEventListener(validatorFactory, cfgService.getSettings(), classLoaderService);
        EventListenerRegistry listenerRegistry = activationContext.getServiceRegistry().getService(EventListenerRegistry.class);
        listenerRegistry.addDuplicationStrategy(DuplicationStrategyImpl.INSTANCE);
        listenerRegistry.appendListeners(EventType.PRE_INSERT, listener);
        listenerRegistry.appendListeners(EventType.PRE_UPDATE, listener);
        listenerRegistry.appendListeners(EventType.PRE_DELETE, listener);
        listener.initialize(cfgService.getSettings(), classLoaderService);
    }

    private static void applyRelationalConstraints(ValidatorFactory factory, ActivationContext activationContext) {
        ConfigurationService cfgService = activationContext.getServiceRegistry().getService(ConfigurationService.class);
        if (!cfgService.getSetting("hibernate.validator.apply_to_ddl", StandardConverters.BOOLEAN, Boolean.valueOf(true)).booleanValue()) {
            LOG.debug("Skipping application of relational constraints from legacy Hibernate Validator");
            return;
        }
        Set<ValidationMode> modes = activationContext.getValidationModes();
        if (!modes.contains((Object)ValidationMode.DDL) && !modes.contains((Object)ValidationMode.AUTO)) {
            return;
        }
        TypeSafeActivator.applyRelationalConstraints(factory, activationContext.getMetadata().getEntityBindings(), cfgService.getSettings(), activationContext.getServiceRegistry().getService(JdbcServices.class).getDialect(), new ClassLoaderAccessImpl(null, activationContext.getServiceRegistry().getService(ClassLoaderService.class)));
    }

    public static void applyRelationalConstraints(ValidatorFactory factory, Collection<PersistentClass> persistentClasses, Map settings, Dialect dialect, ClassLoaderAccess classLoaderAccess) {
        Class<?>[] groupsArray = GroupsPerOperation.buildGroupsForOperation(GroupsPerOperation.Operation.DDL, settings, classLoaderAccess);
        HashSet groups = new HashSet(Arrays.asList(groupsArray));
        for (PersistentClass persistentClass : persistentClasses) {
            Class clazz;
            String className = persistentClass.getClassName();
            if (className == null || className.length() == 0) continue;
            try {
                clazz = classLoaderAccess.classForName(className);
            }
            catch (ClassLoadingException e) {
                throw new AssertionFailure("Entity class not found", (Throwable)((Object)e));
            }
            try {
                TypeSafeActivator.applyDDL("", persistentClass, clazz, factory, groups, true, dialect);
            }
            catch (Exception e) {
                LOG.unableToApplyConstraints(className, e);
            }
        }
    }

    private static void applyDDL(String prefix, PersistentClass persistentClass, Class<?> clazz, ValidatorFactory factory, Set<Class<?>> groups, boolean activateNotNull, Dialect dialect) {
        BeanDescriptor descriptor = factory.getValidator().getConstraintsForClass(clazz);
        for (PropertyDescriptor propertyDesc : descriptor.getConstrainedProperties()) {
            Property property = TypeSafeActivator.findPropertyByName(persistentClass, prefix + propertyDesc.getPropertyName());
            if (property == null) continue;
            boolean hasNotNull = TypeSafeActivator.applyConstraints(propertyDesc.getConstraintDescriptors(), property, propertyDesc, groups, activateNotNull, dialect);
            if (!property.isComposite() || !propertyDesc.isCascaded()) continue;
            Class componentClass = ((Component)property.getValue()).getComponentClass();
            boolean canSetNotNullOnColumns = activateNotNull && hasNotNull;
            TypeSafeActivator.applyDDL(prefix + propertyDesc.getPropertyName() + ".", persistentClass, componentClass, factory, groups, canSetNotNullOnColumns, dialect);
        }
    }

    private static boolean applyConstraints(Set<ConstraintDescriptor<?>> constraintDescriptors, Property property, PropertyDescriptor propertyDesc, Set<Class<?>> groups, boolean canApplyNotNull, Dialect dialect) {
        boolean hasNotNull = false;
        for (ConstraintDescriptor<?> descriptor : constraintDescriptors) {
            if (groups != null && Collections.disjoint(descriptor.getGroups(), groups)) continue;
            if (canApplyNotNull) {
                hasNotNull = hasNotNull || TypeSafeActivator.applyNotNull(property, descriptor);
            }
            TypeSafeActivator.applyDigits(property, descriptor);
            TypeSafeActivator.applySize(property, descriptor, propertyDesc);
            TypeSafeActivator.applyMin(property, descriptor, dialect);
            TypeSafeActivator.applyMax(property, descriptor, dialect);
            TypeSafeActivator.applyLength(property, descriptor, propertyDesc);
            boolean hasNotNullFromComposingConstraints = TypeSafeActivator.applyConstraints(descriptor.getComposingConstraints(), property, propertyDesc, null, canApplyNotNull, dialect);
            hasNotNull = hasNotNull || hasNotNullFromComposingConstraints;
        }
        return hasNotNull;
    }

    private static void applyMin(Property property, ConstraintDescriptor<?> descriptor, Dialect dialect) {
        if (Min.class.equals(descriptor.getAnnotation().annotationType())) {
            Selectable selectable;
            ConstraintDescriptor<?> minConstraint = descriptor;
            long min = ((Min)minConstraint.getAnnotation()).value();
            Iterator itor = property.getColumnIterator();
            if (itor.hasNext() && Column.class.isInstance(selectable = (Selectable)itor.next())) {
                Column col = (Column)selectable;
                String checkConstraint = col.getQuotedName(dialect) + ">=" + min;
                TypeSafeActivator.applySQLCheck(col, checkConstraint);
            }
        }
    }

    private static void applyMax(Property property, ConstraintDescriptor<?> descriptor, Dialect dialect) {
        if (Max.class.equals(descriptor.getAnnotation().annotationType())) {
            Selectable selectable;
            ConstraintDescriptor<?> maxConstraint = descriptor;
            long max = ((Max)maxConstraint.getAnnotation()).value();
            Iterator itor = property.getColumnIterator();
            if (itor.hasNext() && Column.class.isInstance(selectable = (Selectable)itor.next())) {
                Column col = (Column)selectable;
                String checkConstraint = col.getQuotedName(dialect) + "<=" + max;
                TypeSafeActivator.applySQLCheck(col, checkConstraint);
            }
        }
    }

    private static void applySQLCheck(Column col, String checkConstraint) {
        String existingCheck = col.getCheckConstraint();
        if (StringHelper.isNotEmpty(existingCheck) && !existingCheck.contains(checkConstraint)) {
            checkConstraint = col.getCheckConstraint() + " AND " + checkConstraint;
        }
        col.setCheckConstraint(checkConstraint);
    }

    private static boolean applyNotNull(Property property, ConstraintDescriptor<?> descriptor) {
        boolean hasNotNull = false;
        if (NotNull.class.equals(descriptor.getAnnotation().annotationType())) {
            if (!(property.getPersistentClass() instanceof SingleTableSubclass) && !property.isComposite()) {
                Iterator itr = property.getColumnIterator();
                while (itr.hasNext()) {
                    Selectable selectable = (Selectable)itr.next();
                    if (Column.class.isInstance(selectable)) {
                        ((Column)Column.class.cast(selectable)).setNullable(false);
                        continue;
                    }
                    LOG.debugf("@NotNull was applied to attribute [%s] which is defined (at least partially) by formula(s); formula portions will be skipped", property.getName());
                }
            }
            hasNotNull = true;
        }
        property.setOptional(!hasNotNull);
        return hasNotNull;
    }

    private static void applyDigits(Property property, ConstraintDescriptor<?> descriptor) {
        if (Digits.class.equals(descriptor.getAnnotation().annotationType())) {
            Selectable selectable;
            ConstraintDescriptor<?> digitsConstraint = descriptor;
            int integerDigits = ((Digits)digitsConstraint.getAnnotation()).integer();
            int fractionalDigits = ((Digits)digitsConstraint.getAnnotation()).fraction();
            Iterator itor = property.getColumnIterator();
            if (itor.hasNext() && Column.class.isInstance(selectable = (Selectable)itor.next())) {
                Column col = (Column)selectable;
                col.setPrecision(integerDigits + fractionalDigits);
                col.setScale(fractionalDigits);
            }
        }
    }

    private static void applySize(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
        if (Size.class.equals(descriptor.getAnnotation().annotationType()) && String.class.equals((Object)propertyDescriptor.getElementClass())) {
            ConstraintDescriptor<?> sizeConstraint = descriptor;
            int max = ((Size)sizeConstraint.getAnnotation()).max();
            Iterator itor = property.getColumnIterator();
            if (itor.hasNext()) {
                Selectable selectable = (Selectable)itor.next();
                Column col = (Column)selectable;
                if (max < Integer.MAX_VALUE) {
                    col.setLength(max);
                }
            }
        }
    }

    private static void applyLength(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
        if ("org.hibernate.validator.constraints.Length".equals(descriptor.getAnnotation().annotationType().getName()) && String.class.equals((Object)propertyDescriptor.getElementClass())) {
            Selectable selectable;
            int max = (Integer)descriptor.getAttributes().get("max");
            Iterator itor = property.getColumnIterator();
            if (itor.hasNext() && Column.class.isInstance(selectable = (Selectable)itor.next())) {
                Column col = (Column)selectable;
                if (max < Integer.MAX_VALUE) {
                    col.setLength(max);
                }
            }
        }
    }

    private static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
        Property property = null;
        Property idProperty = associatedClass.getIdentifierProperty();
        String idName = idProperty != null ? idProperty.getName() : null;
        try {
            if (propertyName == null || propertyName.length() == 0 || propertyName.equals(idName)) {
                property = idProperty;
            } else {
                if (propertyName.indexOf(idName + ".") == 0) {
                    property = idProperty;
                    propertyName = propertyName.substring(idName.length() + 1);
                }
                StringTokenizer st = new StringTokenizer(propertyName, ".", false);
                while (st.hasMoreElements()) {
                    String element = (String)st.nextElement();
                    if (property == null) {
                        property = associatedClass.getProperty(element);
                        continue;
                    }
                    if (!property.isComposite()) {
                        return null;
                    }
                    property = ((Component)property.getValue()).getProperty(element);
                }
            }
        }
        catch (MappingException e) {
            try {
                if (associatedClass.getIdentifierMapper() == null) {
                    return null;
                }
                StringTokenizer st = new StringTokenizer(propertyName, ".", false);
                while (st.hasMoreElements()) {
                    String element = (String)st.nextElement();
                    if (property == null) {
                        property = associatedClass.getIdentifierMapper().getProperty(element);
                        continue;
                    }
                    if (!property.isComposite()) {
                        return null;
                    }
                    property = ((Component)property.getValue()).getProperty(element);
                }
            }
            catch (MappingException ee) {
                return null;
            }
        }
        return property;
    }

    private static ValidatorFactory getValidatorFactory(ActivationContext activationContext) {
        ValidatorFactory factory = TypeSafeActivator.resolveProvidedFactory(activationContext.getSessionFactory().getSessionFactoryOptions());
        if (factory != null) {
            return factory;
        }
        factory = TypeSafeActivator.resolveProvidedFactory(activationContext.getServiceRegistry().getService(ConfigurationService.class));
        if (factory != null) {
            return factory;
        }
        try {
            return Validation.buildDefaultValidatorFactory();
        }
        catch (Exception e) {
            throw new IntegrationException("Unable to build the default ValidatorFactory", e);
        }
    }

    private static ValidatorFactory resolveProvidedFactory(SessionFactoryOptions options) {
        Object validatorFactoryReference = options.getValidatorFactoryReference();
        if (validatorFactoryReference == null) {
            return null;
        }
        try {
            return (ValidatorFactory)ValidatorFactory.class.cast(validatorFactoryReference);
        }
        catch (ClassCastException e) {
            throw new IntegrationException(String.format(Locale.ENGLISH, "ValidatorFactory reference (provided via %s) was not castable to %s : %s", SessionFactoryOptions.class.getName(), ValidatorFactory.class.getName(), validatorFactoryReference.getClass().getName()));
        }
    }

    private static ValidatorFactory resolveProvidedFactory(ConfigurationService cfgService) {
        return cfgService.getSetting("javax.persistence.validation.factory", new ConfigurationService.Converter<ValidatorFactory>(){

            @Override
            public ValidatorFactory convert(Object value) {
                try {
                    return (ValidatorFactory)ValidatorFactory.class.cast(value);
                }
                catch (ClassCastException e) {
                    throw new IntegrationException(String.format(Locale.ENGLISH, "ValidatorFactory reference (provided via `%s` setting) was not castable to %s : %s", "javax.persistence.validation.factory", ValidatorFactory.class.getName(), value.getClass().getName()));
                }
            }
        }, (ValidatorFactory)cfgService.getSetting("jakarta.persistence.validation.factory", new ConfigurationService.Converter<ValidatorFactory>(){

            @Override
            public ValidatorFactory convert(Object value) {
                try {
                    return (ValidatorFactory)ValidatorFactory.class.cast(value);
                }
                catch (ClassCastException e) {
                    throw new IntegrationException(String.format(Locale.ENGLISH, "ValidatorFactory reference (provided via `%s` setting) was not castable to %s : %s", "jakarta.persistence.validation.factory", ValidatorFactory.class.getName(), value.getClass().getName()));
                }
            }
        }, null));
    }
}

