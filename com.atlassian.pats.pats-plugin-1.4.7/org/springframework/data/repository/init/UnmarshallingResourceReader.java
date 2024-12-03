/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.oxm.Unmarshaller
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.init;

import java.io.IOException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.lang.Nullable;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;

public class UnmarshallingResourceReader
implements ResourceReader {
    private final Unmarshaller unmarshaller;

    public UnmarshallingResourceReader(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    public Object readFrom(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
        Assert.notNull((Object)resource, (String)"Resource must not be null!");
        StreamSource source = new StreamSource(resource.getInputStream());
        return this.unmarshaller.unmarshal((Source)source);
    }
}

