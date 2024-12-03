/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.ParameterRegistry;

public interface ParameterContainer {
    public void registerParameters(ParameterRegistry var1);

    public static class Helper {
        public static void possibleParameter(Selection selection, ParameterRegistry registry) {
            if (ParameterContainer.class.isInstance(selection)) {
                ((ParameterContainer)selection).registerParameters(registry);
            }
        }
    }
}

