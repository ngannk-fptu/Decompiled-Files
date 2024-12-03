/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.security;

import java.security.BasicPermission;

public class DeployXmlPermission
extends BasicPermission {
    private static final long serialVersionUID = 1L;

    public DeployXmlPermission(String name) {
        super(name);
    }

    public DeployXmlPermission(String name, String actions) {
        super(name, actions);
    }
}

