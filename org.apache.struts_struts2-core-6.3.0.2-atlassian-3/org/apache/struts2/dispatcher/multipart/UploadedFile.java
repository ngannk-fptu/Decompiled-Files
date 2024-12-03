/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher.multipart;

import java.io.Serializable;

public interface UploadedFile
extends Serializable {
    public Long length();

    public String getName();

    public String getOriginalName();

    public boolean isFile();

    public boolean delete();

    public String getAbsolutePath();

    public Object getContent();

    public String getContentType();
}

