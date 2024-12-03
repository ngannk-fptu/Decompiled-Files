/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class FileGroup
implements Serializable {
    public List users = new ArrayList();
    public String name;

    FileGroup() {
    }
}

