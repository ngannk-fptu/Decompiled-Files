/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintViolation
 *  javax.validation.ConstraintViolationException
 *  javax.validation.TraversableResolver
 *  javax.validation.Validation
 *  javax.validation.Validator
 *  javax.validation.ValidatorFactory
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.beanvalidation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.EntityMode;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.beanvalidation.GroupsPerOperation;
import org.hibernate.cfg.beanvalidation.HibernateTraversableResolver;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.logging.Logger;

public class BeanValidationEventListener
implements PreInsertEventListener,
PreUpdateEventListener,
PreDeleteEventListener {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)BeanValidationEventListener.class.getName());
    private ValidatorFactory factory;
    private ConcurrentHashMap<EntityPersister, Set<String>> associationsPerEntityPersister = new ConcurrentHashMap();
    private GroupsPerOperation groupsPerOperation;
    boolean initialized;

    public BeanValidationEventListener(ValidatorFactory factory, Map settings, ClassLoaderService classLoaderService) {
        this.init(factory, settings, classLoaderService);
    }

    public void initialize(Map settings, ClassLoaderService classLoaderService) {
        if (!this.initialized) {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            this.init(factory, settings, classLoaderService);
        }
    }

    private void init(ValidatorFactory factory, Map settings, ClassLoaderService classLoaderService) {
        this.factory = factory;
        this.groupsPerOperation = GroupsPerOperation.from(settings, new ClassLoaderAccessImpl(classLoaderService));
        this.initialized = true;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        this.validate(event.getEntity(), event.getPersister().getEntityMode(), event.getPersister(), event.getSession().getFactory(), GroupsPerOperation.Operation.INSERT);
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        this.validate(event.getEntity(), event.getPersister().getEntityMode(), event.getPersister(), event.getSession().getFactory(), GroupsPerOperation.Operation.UPDATE);
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        this.validate(event.getEntity(), event.getPersister().getEntityMode(), event.getPersister(), event.getSession().getFactory(), GroupsPerOperation.Operation.DELETE);
        return false;
    }

    private <T> void validate(T object, EntityMode mode, EntityPersister persister, SessionFactoryImplementor sessionFactory, GroupsPerOperation.Operation operation) {
        Set constraintViolations;
        if (object == null || mode != EntityMode.POJO) {
            return;
        }
        HibernateTraversableResolver tr = new HibernateTraversableResolver(persister, this.associationsPerEntityPersister, sessionFactory);
        Validator validator = this.factory.usingContext().traversableResolver((TraversableResolver)tr).getValidator();
        Class[] groups = this.groupsPerOperation.get(operation);
        if (groups.length > 0 && (constraintViolations = validator.validate(object, groups)).size() > 0) {
            HashSet<ConstraintViolation> propagatedViolations = new HashSet<ConstraintViolation>(constraintViolations.size());
            HashSet<String> classNames = new HashSet<String>();
            for (ConstraintViolation violation : constraintViolations) {
                LOG.trace(violation);
                propagatedViolations.add(violation);
                classNames.add(violation.getLeafBean().getClass().getName());
            }
            StringBuilder builder = new StringBuilder();
            builder.append("Validation failed for classes ");
            builder.append(classNames);
            builder.append(" during ");
            builder.append(operation.getName());
            builder.append(" time for groups ");
            builder.append(this.toString(groups));
            builder.append("\nList of constraint violations:[\n");
            for (ConstraintViolation violation : constraintViolations) {
                builder.append("\t").append(violation.toString()).append("\n");
            }
            builder.append("]");
            throw new ConstraintViolationException(builder.toString(), propagatedViolations);
        }
    }

    private String toString(Class<?>[] groups) {
        StringBuilder toString = new StringBuilder("[");
        for (Class<?> group : groups) {
            toString.append(group.getName()).append(", ");
        }
        toString.append("]");
        return toString.toString();
    }
}

