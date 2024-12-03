/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.model;

import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;

public class AsmRelationshipUtils {
    public static final String DECLARE_PRECEDENCE = "precedence";
    public static final String DECLARE_SOFT = "soft";
    public static final String DECLARE_PARENTS = "parents";
    public static final String DECLARE_WARNING = "warning";
    public static final String DECLARE_ERROR = "error";
    public static final String DECLARE_UNKNONWN = "<unknown declare>";
    public static final String POINTCUT_ABSTRACT = "<abstract pointcut>";
    public static final String POINTCUT_ANONYMOUS = "<anonymous pointcut>";
    public static final String DOUBLE_DOTS = "..";
    public static final int MAX_MESSAGE_LENGTH = 18;
    public static final String DEC_LABEL = "declare";

    public static String genDeclareMessage(String message) {
        int length = message.length();
        if (length < 18) {
            return message;
        }
        return message.substring(0, 17) + DOUBLE_DOTS;
    }

    public static String genPointcutDetails(Pointcut pcd) {
        StringBuffer details = new StringBuffer();
        if (pcd instanceof ReferencePointcut) {
            ReferencePointcut rp = (ReferencePointcut)pcd;
            details.append(rp.name).append(DOUBLE_DOTS);
        } else if (pcd instanceof AndPointcut) {
            AndPointcut ap = (AndPointcut)pcd;
            if (ap.getLeft() instanceof ReferencePointcut) {
                details.append(ap.getLeft().toString()).append(DOUBLE_DOTS);
            } else {
                details.append(POINTCUT_ANONYMOUS).append(DOUBLE_DOTS);
            }
        } else if (pcd instanceof OrPointcut) {
            OrPointcut op = (OrPointcut)pcd;
            if (op.getLeft() instanceof ReferencePointcut) {
                details.append(op.getLeft().toString()).append(DOUBLE_DOTS);
            } else {
                details.append(POINTCUT_ANONYMOUS).append(DOUBLE_DOTS);
            }
        } else {
            details.append(POINTCUT_ANONYMOUS);
        }
        return details.toString();
    }
}

