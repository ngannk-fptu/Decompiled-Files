/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.iptc;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataWriter;
import com.twelvemonkeys.imageio.metadata.iptc.IPTC;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.stream.ImageOutputStream;

public final class IPTCWriter
extends MetadataWriter {
    @Override
    public boolean write(Directory directory, ImageOutputStream imageOutputStream) throws IOException {
        Validate.notNull((Object)directory, (String)"directory");
        Validate.notNull((Object)imageOutputStream, (String)"stream");
        for (Entry entry : directory) {
            int n = (Integer)entry.getIdentifier();
            Object object = entry.getValue();
            if (IPTC.Tags.isArray((short)n)) {
                Object[] objectArray;
                for (Object object2 : objectArray = (Object[])object) {
                    imageOutputStream.write(28);
                    imageOutputStream.writeShort(n);
                    this.writeValue(imageOutputStream, object2);
                }
                continue;
            }
            imageOutputStream.write(28);
            imageOutputStream.writeShort(n);
            this.writeValue(imageOutputStream, object);
        }
        return false;
    }

    private void writeValue(ImageOutputStream imageOutputStream, Object object) throws IOException {
        if (object instanceof String) {
            byte[] byArray = ((String)object).getBytes(StandardCharsets.UTF_8);
            imageOutputStream.writeShort(byArray.length);
            imageOutputStream.write(byArray);
        } else if (object instanceof byte[]) {
            byte[] byArray = (byte[])object;
            imageOutputStream.writeShort(byArray.length);
            imageOutputStream.write(byArray);
        } else if (object instanceof Integer) {
            imageOutputStream.writeShort(2);
            imageOutputStream.writeShort((Integer)object);
        }
    }
}

