/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.tomcat.util.descriptor;

import java.io.InputStream;
import org.apache.tomcat.util.ExceptionUtils;
import org.xml.sax.InputSource;

public final class InputSourceUtil {
    private InputSourceUtil() {
    }

    public static void close(InputSource inputSource) {
        if (inputSource == null) {
            return;
        }
        InputStream is = inputSource.getByteStream();
        if (is != null) {
            try {
                is.close();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
            }
        }
    }
}

