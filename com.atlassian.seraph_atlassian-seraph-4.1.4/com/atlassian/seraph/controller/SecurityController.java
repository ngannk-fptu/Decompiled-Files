/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.controller;

import com.atlassian.seraph.Initable;
import com.atlassian.seraph.controller.NullSecurityController;

public interface SecurityController
extends Initable {
    public static final String NULL_CONTROLLER = NullSecurityController.class.getName();

    public boolean isSecurityEnabled();
}

