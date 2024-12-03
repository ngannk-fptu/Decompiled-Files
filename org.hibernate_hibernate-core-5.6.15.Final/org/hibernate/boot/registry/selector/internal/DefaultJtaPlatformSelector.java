/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector.internal;

import java.util.Objects;
import org.hibernate.boot.registry.selector.internal.LazyServiceResolver;
import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.BorlandEnterpriseServerJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JBossAppServerJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JOTMJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JOnASJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.JRun4JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.OC4JJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.OrionJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.ResinJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.SapNetWeaverJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.SunOneJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.WebSphereExtendedJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.WebSphereJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.WebSphereLibertyJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.WeblogicJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;

public class DefaultJtaPlatformSelector
implements LazyServiceResolver<JtaPlatform> {
    @Override
    public Class<? extends JtaPlatform> resolve(String name) {
        Objects.requireNonNull(name);
        if (name.isEmpty()) {
            return null;
        }
        char n = name.charAt(0);
        switch (n) {
            case 'B': {
                return DefaultJtaPlatformSelector.caseB(name);
            }
            case 'J': {
                return DefaultJtaPlatformSelector.caseJ(name);
            }
            case 'W': {
                return DefaultJtaPlatformSelector.caseW(name);
            }
            case 'o': {
                return DefaultJtaPlatformSelector.caseLegacy(name, this);
            }
        }
        return DefaultJtaPlatformSelector.caseOthers(name);
    }

    private static Class<? extends JtaPlatform> caseB(String name) {
        if ("Bitronix".equals(name)) {
            return BitronixJtaPlatform.class;
        }
        if ("Borland".equals(name)) {
            return BorlandEnterpriseServerJtaPlatform.class;
        }
        return null;
    }

    private static Class<? extends JtaPlatform> caseJ(String name) {
        if ("JBossAS".equals(name)) {
            return JBossAppServerJtaPlatform.class;
        }
        if ("JBossTS".equals(name)) {
            return JBossStandAloneJtaPlatform.class;
        }
        if ("JOnAS".equals(name)) {
            return JOnASJtaPlatform.class;
        }
        if ("JOTM".equals(name)) {
            return JOTMJtaPlatform.class;
        }
        if ("JRun4".equals(name)) {
            return JRun4JtaPlatform.class;
        }
        return null;
    }

    private static Class<? extends JtaPlatform> caseW(String name) {
        if ("Weblogic".equals(name)) {
            return WeblogicJtaPlatform.class;
        }
        if ("WebSphereLiberty".equals(name)) {
            return WebSphereLibertyJtaPlatform.class;
        }
        if ("WebSphere".equals(name)) {
            return WebSphereJtaPlatform.class;
        }
        if ("WebSphereExtended".equals(name)) {
            return WebSphereExtendedJtaPlatform.class;
        }
        return null;
    }

    private static Class<? extends JtaPlatform> caseOthers(String name) {
        if ("Atomikos".equals(name)) {
            return AtomikosJtaPlatform.class;
        }
        if ("OC4J".equals(name)) {
            return OC4JJtaPlatform.class;
        }
        if ("Orion".equals(name)) {
            return OrionJtaPlatform.class;
        }
        if ("Resin".equals(name)) {
            return ResinJtaPlatform.class;
        }
        if ("SapNetWeaver".equals(name)) {
            return SapNetWeaverJtaPlatform.class;
        }
        if ("SunOne".equals(name)) {
            return SunOneJtaPlatform.class;
        }
        return null;
    }

    private static Class<? extends JtaPlatform> caseLegacy(String name, DefaultJtaPlatformSelector defaultJtaPlatformSelector) {
        if (name.equals("org.hibernate.service.jta.platform.internal.BorlandEnterpriseServerJtaPlatform")) {
            return BorlandEnterpriseServerJtaPlatform.class;
        }
        if (name.equals("org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform")) {
            return JBossAppServerJtaPlatform.class;
        }
        if (name.equals("org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform")) {
            return JBossStandAloneJtaPlatform.class;
        }
        if (name.equals("org.hibernate.engine.transaction.jta.platform.internal.WebSphereLibertyJtaPlatform")) {
            return WebSphereLibertyJtaPlatform.class;
        }
        String LEGACY_PREFIX = "org.hibernate.service.jta.platform.internal.";
        String LEGACY_POSTFIX = "JtaPlatform";
        if (name.startsWith("org.hibernate.service.jta.platform.internal.") && name.endsWith("JtaPlatform")) {
            String cleanName = name.substring("org.hibernate.service.jta.platform.internal.".length(), name.length() - "JtaPlatform".length());
            return defaultJtaPlatformSelector.resolve(cleanName);
        }
        return null;
    }
}

