/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionInfo;

public interface IntroductionAdvisor
extends Advisor,
IntroductionInfo {
    public ClassFilter getClassFilter();

    public void validateInterfaces() throws IllegalArgumentException;
}

