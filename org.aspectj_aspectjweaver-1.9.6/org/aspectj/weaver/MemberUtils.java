/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.ResolvedMember;

public class MemberUtils {
    public static boolean isConstructor(ResolvedMember member) {
        return member.getName().equals("<init>");
    }
}

