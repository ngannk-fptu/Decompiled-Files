/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.about;

import org.jfree.ui.about.ProjectInfo;

public class Library
extends org.jfree.base.Library {
    public Library(String name, String version, String licence, String info) {
        super(name, version, licence, info);
    }

    public Library(ProjectInfo project) {
        this(project.getName(), project.getVersion(), project.getLicenceName(), project.getInfo());
    }
}

