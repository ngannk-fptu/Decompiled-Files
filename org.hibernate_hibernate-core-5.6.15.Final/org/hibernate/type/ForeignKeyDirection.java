/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.engine.internal.CascadePoint;

public enum ForeignKeyDirection {
    TO_PARENT{

        @Override
        public boolean cascadeNow(CascadePoint cascadePoint) {
            return cascadePoint != CascadePoint.BEFORE_INSERT_AFTER_DELETE;
        }
    }
    ,
    FROM_PARENT{

        @Override
        public boolean cascadeNow(CascadePoint cascadePoint) {
            return cascadePoint != CascadePoint.AFTER_INSERT_BEFORE_DELETE;
        }
    };


    public abstract boolean cascadeNow(CascadePoint var1);
}

