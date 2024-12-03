/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.annotators;

import com.atlassian.plugin.webresource.impl.annotators.ResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Map;

public class TryCatchJsResourceContentAnnotator
extends ResourceContentAnnotator {
    public static final String CATCH_BLOCK = "WRMCB=function(e){var c=console;if(c&&c.log&&c.error){c.log('Error running batched script.');c.error(e);}}\n";
    private static final byte[] BEFORE_CHUNK = "try {\n".getBytes();
    private static final byte[] AFTER_CHUNK = "\n}catch(e){WRMCB(e)}".getBytes();

    @Override
    public int beforeResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        stream.write(BEFORE_CHUNK);
        return 1;
    }

    @Override
    public void afterResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        stream.write(AFTER_CHUNK);
    }

    @Override
    public int beforeAllResourcesInBatch(LinkedHashSet<String> requiredResources, String url, Map<String, String> params, OutputStream stream) throws IOException {
        stream.write(CATCH_BLOCK.getBytes());
        return 1;
    }

    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode();
    }
}

