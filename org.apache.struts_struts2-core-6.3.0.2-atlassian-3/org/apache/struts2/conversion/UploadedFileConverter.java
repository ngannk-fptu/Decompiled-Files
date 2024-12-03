/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.conversion;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public class UploadedFileConverter
extends DefaultTypeConverter {
    private static final Logger LOG = LogManager.getLogger(UploadedFileConverter.class);

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (File.class.equals((Object)toType)) {
            LOG.debug("Converting {} into {}, consider switching to {} and do not access {} directly!", (Object)File.class.getName(), (Object)UploadedFile.class.getName(), (Object)UploadedFile.class.getName(), (Object)File.class.getName());
            Object obj = value.getClass().isArray() && Array.getLength(value) == 1 ? Array.get(value, 0) : value;
            if (obj instanceof UploadedFile) {
                UploadedFile file = (UploadedFile)obj;
                if (file.getContent() instanceof File) {
                    return file.getContent();
                }
                return new File(file.getAbsolutePath());
            }
        }
        return super.convertValue(context, target, member, propertyName, value, toType);
    }
}

