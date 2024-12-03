/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheEntrySerializationException;
import org.apache.http.client.cache.HttpCacheEntrySerializer;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class DefaultHttpCacheEntrySerializer
implements HttpCacheEntrySerializer {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(HttpCacheEntry cacheEntry, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        try {
            oos.writeObject(cacheEntry);
        }
        finally {
            oos.close();
        }
    }

    @Override
    public HttpCacheEntry readFrom(InputStream is) throws IOException {
        RestrictedObjectInputStream ois = new RestrictedObjectInputStream(is);
        try {
            HttpCacheEntry httpCacheEntry = (HttpCacheEntry)ois.readObject();
            return httpCacheEntry;
        }
        catch (ClassNotFoundException ex) {
            throw new HttpCacheEntrySerializationException("Class not found: " + ex.getMessage(), ex);
        }
        finally {
            ois.close();
        }
    }

    static class RestrictedObjectInputStream
    extends ObjectInputStream {
        private static final List<Pattern> ALLOWED_CLASS_PATTERNS = Collections.unmodifiableList(Arrays.asList(Pattern.compile("^(?:\\[+L)?org\\.apache\\.http\\..*$"), Pattern.compile("^(?:\\[+L)?java\\.util\\..*$"), Pattern.compile("^(?:\\[+L)?java\\.lang\\..*$"), Pattern.compile("^\\[+Z$"), Pattern.compile("^\\[+B$"), Pattern.compile("^\\[+C$"), Pattern.compile("^\\[+D$"), Pattern.compile("^\\[+F$"), Pattern.compile("^\\[+I$"), Pattern.compile("^\\[+J$"), Pattern.compile("^\\[+S$")));

        private RestrictedObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
            String className = objectStreamClass.getName();
            if (!RestrictedObjectInputStream.isAllowedClassName(className)) {
                String message = String.format("Class %s is not allowed for deserialization", className);
                throw new HttpCacheEntrySerializationException(message);
            }
            return super.resolveClass(objectStreamClass);
        }

        static boolean isAllowedClassName(String className) {
            for (Pattern allowedClassPattern : ALLOWED_CLASS_PATTERNS) {
                if (!allowedClassPattern.matcher(className).matches()) continue;
                return true;
            }
            return false;
        }
    }
}

