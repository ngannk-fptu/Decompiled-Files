/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 */
package org.hibernate.cfg;

import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.annotations.EntityBinder;

public class SecondaryTableSecondPass
implements SecondPass {
    private EntityBinder entityBinder;
    private PropertyHolder propertyHolder;
    private XAnnotatedElement annotatedClass;

    public SecondaryTableSecondPass(EntityBinder entityBinder, PropertyHolder propertyHolder, XAnnotatedElement annotatedClass) {
        this.entityBinder = entityBinder;
        this.propertyHolder = propertyHolder;
        this.annotatedClass = annotatedClass;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        this.entityBinder.finalSecondaryTableBinding(this.propertyHolder);
    }
}

