/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;

public interface MarshallingRegistry {
    public void register(Marshaller var1, Class var2, MarshallingType var3);

    public void register(Unmarshaller var1, Class var2, MarshallingType var3);

    public <T> Marshaller<T> getMarshaller(Class<T> var1, MarshallingType var2);

    public <T> Unmarshaller<T> getUnmarshaller(Class<T> var1, MarshallingType var2);
}

