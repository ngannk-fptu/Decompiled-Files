/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.deployment.wsdd;

import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDOperation;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.providers.WSDDBsfProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDComProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDHandlerProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaCORBAProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaEJBProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaMsgProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaRMIProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaRPCProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.logging.Log;

public abstract class WSDDProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$deployment$wsdd$WSDDProvider == null ? (class$org$apache$axis$deployment$wsdd$WSDDProvider = WSDDProvider.class$("org.apache.axis.deployment.wsdd.WSDDProvider")) : class$org$apache$axis$deployment$wsdd$WSDDProvider).getName());
    private static final String PLUGABLE_PROVIDER_FILENAME = "org.apache.axis.deployment.wsdd.Provider";
    private static Hashtable providers = new Hashtable();
    static /* synthetic */ Class class$org$apache$axis$deployment$wsdd$WSDDProvider;

    private static void loadPluggableProviders() {
        ClassLoader clzLoader = (class$org$apache$axis$deployment$wsdd$WSDDProvider == null ? (class$org$apache$axis$deployment$wsdd$WSDDProvider = WSDDProvider.class$("org.apache.axis.deployment.wsdd.WSDDProvider")) : class$org$apache$axis$deployment$wsdd$WSDDProvider).getClassLoader();
        ClassLoaders loaders = new ClassLoaders();
        loaders.put(clzLoader);
        DiscoverServiceNames dsn = new DiscoverServiceNames(loaders);
        ResourceNameIterator iter = dsn.findResourceNames(PLUGABLE_PROVIDER_FILENAME);
        while (iter.hasNext()) {
            String className = iter.nextResourceName();
            try {
                Object o = Class.forName(className).newInstance();
                if (!(o instanceof WSDDProvider)) continue;
                WSDDProvider provider = (WSDDProvider)o;
                String providerName = provider.getName();
                QName q = new QName("http://xml.apache.org/axis/wsdd/providers/java", providerName);
                providers.put(q, provider);
            }
            catch (Exception e) {
                String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
                log.info((Object)Messages.getMessage("exception01", msg));
            }
        }
    }

    public static void registerProvider(QName uri, WSDDProvider prov) {
        providers.put(uri, prov);
    }

    public WSDDOperation[] getOperations() {
        return null;
    }

    public WSDDOperation getOperation(String name) {
        return null;
    }

    public static Handler getInstance(QName providerType, WSDDService service, EngineConfiguration registry) throws Exception {
        if (providerType == null) {
            throw new WSDDException(Messages.getMessage("nullProvider00"));
        }
        WSDDProvider provider = (WSDDProvider)providers.get(providerType);
        if (provider == null) {
            throw new WSDDException(Messages.getMessage("noMatchingProvider00", providerType.toString()));
        }
        return provider.newProviderInstance(service, registry);
    }

    public abstract Handler newProviderInstance(WSDDService var1, EngineConfiguration var2) throws Exception;

    public abstract String getName();

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        providers.put(WSDDConstants.QNAME_JAVARPC_PROVIDER, new WSDDJavaRPCProvider());
        providers.put(WSDDConstants.QNAME_JAVAMSG_PROVIDER, new WSDDJavaMsgProvider());
        providers.put(WSDDConstants.QNAME_HANDLER_PROVIDER, new WSDDHandlerProvider());
        providers.put(WSDDConstants.QNAME_EJB_PROVIDER, new WSDDJavaEJBProvider());
        providers.put(WSDDConstants.QNAME_COM_PROVIDER, new WSDDComProvider());
        providers.put(WSDDConstants.QNAME_BSF_PROVIDER, new WSDDBsfProvider());
        providers.put(WSDDConstants.QNAME_CORBA_PROVIDER, new WSDDJavaCORBAProvider());
        providers.put(WSDDConstants.QNAME_RMI_PROVIDER, new WSDDJavaRMIProvider());
        try {
            WSDDProvider.loadPluggableProviders();
        }
        catch (Throwable t) {
            String msg = t + JavaUtils.LS + JavaUtils.stackToString(t);
            log.info((Object)Messages.getMessage("exception01", msg));
        }
    }
}

