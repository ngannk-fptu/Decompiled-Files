/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.hibernate.annotations.common.reflection.java.JavaXMember
 */
package org.hibernate.cfg.annotations;

import java.lang.reflect.Member;
import org.hibernate.HibernateException;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.annotations.common.reflection.java.JavaXMember;

public final class HCANNHelper {
    @Deprecated
    public static String annotatedElementSignature(XProperty xProperty) {
        return HCANNHelper.getUnderlyingMember(xProperty).toString();
    }

    public static String annotatedElementSignature(JavaXMember jxProperty) {
        return HCANNHelper.getUnderlyingMember(jxProperty).toString();
    }

    @Deprecated
    public static Member getUnderlyingMember(XProperty xProperty) {
        if (xProperty instanceof JavaXMember) {
            JavaXMember jx = (JavaXMember)xProperty;
            return jx.getMember();
        }
        throw new HibernateException("Can only extract Member from a XProperty which is a JavaXMember");
    }

    public static Member getUnderlyingMember(JavaXMember jxProperty) {
        return jxProperty.getMember();
    }
}

