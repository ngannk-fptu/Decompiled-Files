/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net;

import ch.qos.logback.core.util.EnvUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HardenedObjectInputStream
extends ObjectInputStream {
    private final List<String> whitelistedClassNames;
    private static final String[] JAVA_PACKAGES = new String[]{"java.lang", "java.util"};
    private static final int DEPTH_LIMIT = 16;
    private static final int ARRAY_LIMIT = 10000;

    public HardenedObjectInputStream(InputStream in, String[] whitelist) throws IOException {
        super(in);
        this.initObjectFilter();
        this.whitelistedClassNames = new ArrayList<String>();
        if (whitelist != null) {
            for (int i = 0; i < whitelist.length; ++i) {
                this.whitelistedClassNames.add(whitelist[i]);
            }
        }
    }

    public HardenedObjectInputStream(InputStream in, List<String> whitelist) throws IOException {
        super(in);
        this.initObjectFilter();
        this.whitelistedClassNames = new ArrayList<String>();
        this.whitelistedClassNames.addAll(whitelist);
    }

    private void initObjectFilter() {
        if (EnvUtil.isJDK9OrHigher()) {
            try {
                ClassLoader classLoader = this.getClass().getClassLoader();
                Class<?> oifClass = classLoader.loadClass("java.io.ObjectInputFilter");
                Class<?> oifConfigClass = classLoader.loadClass("java.io.ObjectInputFilter$Config");
                Method setObjectInputFilterMethod = this.getClass().getMethod("setObjectInputFilter", oifClass);
                Method createFilterMethod = oifConfigClass.getMethod("createFilter", String.class);
                Object filter = createFilterMethod.invoke(null, "maxarray=10000;maxdepth=16;");
                setObjectInputFilterMethod.invoke((Object)this, filter);
            }
            catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("Failed to initialize object filter", e);
            }
        }
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass anObjectStreamClass) throws IOException, ClassNotFoundException {
        String incomingClassName = anObjectStreamClass.getName();
        if (!this.isWhitelisted(incomingClassName)) {
            throw new InvalidClassException("Unauthorized deserialization attempt", anObjectStreamClass.getName());
        }
        return super.resolveClass(anObjectStreamClass);
    }

    private boolean isWhitelisted(String incomingClassName) {
        for (int i = 0; i < JAVA_PACKAGES.length; ++i) {
            if (!incomingClassName.startsWith(JAVA_PACKAGES[i])) continue;
            return true;
        }
        for (String whiteListed : this.whitelistedClassNames) {
            if (!incomingClassName.equals(whiteListed)) continue;
            return true;
        }
        return false;
    }

    protected void addToWhitelist(List<String> additionalAuthorizedClasses) {
        this.whitelistedClassNames.addAll(additionalAuthorizedClasses);
    }
}

