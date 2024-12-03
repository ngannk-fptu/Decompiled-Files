/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Version
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$FieldCallback
 *  org.springframework.util.ReflectionUtils$FieldFilter
 */
package org.eclipse.gemini.blueprint.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Dictionary;
import org.eclipse.gemini.blueprint.util.OsgiPlatformDetector;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public abstract class OsgiBundleUtils {
    private static final boolean getBundleContextAvailable;
    private static volatile BundleContextExtractor extractor;

    public static BundleContext getBundleContext(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (extractor == null) {
            Method method = ReflectionUtils.findMethod(bundle.getClass(), (String)"getBundleContext", (Class[])new Class[0]);
            if (method != null) {
                if (Modifier.isPublic(method.getModifiers())) {
                    extractor = new Osgi41BundleContextExtractor();
                }
            } else {
                if (method == null) {
                    method = ReflectionUtils.findMethod(bundle.getClass(), (String)"getContext", (Class[])new Class[0]);
                }
                if (method != null) {
                    extractor = new ReflectionMethodInvocation(method);
                } else {
                    final Field[] fields = new Field[1];
                    ReflectionUtils.doWithFields(bundle.getClass(), (ReflectionUtils.FieldCallback)new ReflectionUtils.FieldCallback(){

                        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                            ReflectionUtils.makeAccessible((Field)field);
                            fields[0] = field;
                        }
                    }, (ReflectionUtils.FieldFilter)new ReflectionUtils.FieldFilter(){

                        public boolean matches(Field field) {
                            return fields[0] == null && BundleContext.class.isAssignableFrom(field.getType());
                        }
                    });
                    if (fields[0] != null) {
                        extractor = new FieldExtractor(fields[0]);
                    } else {
                        throw new IllegalArgumentException("Cannot extract bundleContext from bundle type " + bundle.getClass());
                    }
                }
            }
        }
        return extractor.getBundleContext(bundle);
    }

    public static boolean isBundleActive(Bundle bundle) {
        Assert.notNull((Object)bundle, (String)"bundle is required");
        return bundle.getState() == 32;
    }

    public static boolean isBundleResolved(Bundle bundle) {
        Assert.notNull((Object)bundle, (String)"bundle is required");
        return bundle.getState() >= 4;
    }

    public static boolean isBundleLazyActivated(Bundle bundle) {
        Object val;
        Dictionary headers;
        Assert.notNull((Object)bundle, (String)"bundle is required");
        if (OsgiPlatformDetector.isR41() && bundle.getState() == 8 && (headers = bundle.getHeaders()) != null && (val = headers.get("Bundle-ActivationPolicy")) != null) {
            String value = ((String)val).trim();
            return value.startsWith("lazy");
        }
        return false;
    }

    public static boolean isFragment(Bundle bundle) {
        Assert.notNull((Object)bundle, (String)"bundle is required");
        return bundle.getHeaders().get("Fragment-Host") != null;
    }

    public static boolean isSystemBundle(Bundle bundle) {
        Assert.notNull((Object)bundle);
        return bundle.getBundleId() == 0L;
    }

    public static Version getBundleVersion(Bundle bundle) {
        return OsgiBundleUtils.getHeaderAsVersion(bundle, "Bundle-Version");
    }

    public static Bundle findBundleBySymbolicName(BundleContext bundleContext, String symbolicName) {
        Assert.notNull((Object)bundleContext, (String)"bundleContext is required");
        Assert.hasText((String)symbolicName, (String)"a not-null/not-empty symbolicName isrequired");
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; ++i) {
            if (!symbolicName.equals(bundles[i].getSymbolicName())) continue;
            return bundles[i];
        }
        return null;
    }

    public static Version getHeaderAsVersion(Bundle bundle, String header) {
        Assert.notNull((Object)bundle);
        return Version.parseVersion((String)((String)bundle.getHeaders().get(header)));
    }

    static {
        boolean bl = getBundleContextAvailable = ReflectionUtils.findMethod(Bundle.class, (String)"getBundleContext", (Class[])new Class[0]) != null;
        if (getBundleContextAvailable) {
            extractor = new Osgi41BundleContextExtractor();
        }
    }

    private static class FieldExtractor
    implements BundleContextExtractor {
        private final Field field;

        private FieldExtractor(Field field) {
            ReflectionUtils.makeAccessible((Field)field);
            this.field = field;
        }

        @Override
        public BundleContext getBundleContext(Bundle bundle) {
            return (BundleContext)ReflectionUtils.getField((Field)this.field, (Object)bundle);
        }
    }

    private static class ReflectionMethodInvocation
    implements BundleContextExtractor {
        private final Method method;

        private ReflectionMethodInvocation(Method method) {
            ReflectionUtils.makeAccessible((Method)method);
            this.method = method;
        }

        @Override
        public BundleContext getBundleContext(Bundle bundle) {
            return (BundleContext)ReflectionUtils.invokeMethod((Method)this.method, (Object)bundle);
        }
    }

    private static class Osgi41BundleContextExtractor
    implements BundleContextExtractor {
        private Osgi41BundleContextExtractor() {
        }

        @Override
        public BundleContext getBundleContext(Bundle bundle) {
            return bundle.getBundleContext();
        }
    }

    private static interface BundleContextExtractor {
        public BundleContext getBundleContext(Bundle var1);
    }
}

