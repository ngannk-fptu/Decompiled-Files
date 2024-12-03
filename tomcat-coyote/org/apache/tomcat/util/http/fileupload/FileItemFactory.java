/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import org.apache.tomcat.util.http.fileupload.FileItem;

public interface FileItemFactory {
    public FileItem createItem(String var1, String var2, boolean var3, String var4);
}

