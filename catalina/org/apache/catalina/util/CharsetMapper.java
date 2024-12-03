/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.catalina.util;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.compat.JreCompat;

public class CharsetMapper {
    public static final String DEFAULT_RESOURCE = "/org/apache/catalina/util/CharsetMapperDefault.properties";
    private Properties map = new Properties();

    public CharsetMapper() {
        this(DEFAULT_RESOURCE);
    }

    public CharsetMapper(String name) {
        if (JreCompat.isGraalAvailable()) {
            this.map.put("en", "ISO-8859-1");
        } else {
            try (InputStream stream = this.getClass().getResourceAsStream(name);){
                this.map.load(stream);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                throw new IllegalArgumentException(t);
            }
        }
    }

    public String getCharset(Locale locale) {
        String charset = this.map.getProperty(locale.toString());
        if (charset == null && (charset = this.map.getProperty(locale.getLanguage() + "_" + locale.getCountry())) == null) {
            charset = this.map.getProperty(locale.getLanguage());
        }
        return charset;
    }

    public void addCharsetMappingFromDeploymentDescriptor(String locale, String charset) {
        this.map.put(locale, charset);
    }
}

