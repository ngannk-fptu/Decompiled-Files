/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.IResourceLoadingCallback
 *  com.aspose.words.ResourceLoadingArgs
 */
package com.atlassian.plugins.conversion.convert.html.word;

import com.aspose.words.IResourceLoadingCallback;
import com.aspose.words.ResourceLoadingArgs;

public class RestrictiveResourceLoadingCallback
implements IResourceLoadingCallback {
    public int resourceLoading(ResourceLoadingArgs resourceLoadingArgs) {
        return 1;
    }
}

