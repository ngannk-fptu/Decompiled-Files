/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.ToolkitFactory
 *  org.terracotta.toolkit.ToolkitInstantiationException
 */
package org.terracotta.modules.ehcache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitFactory;
import org.terracotta.toolkit.ToolkitInstantiationException;

public class TerracottaToolkitBuilder {
    private static final String TC_TUNNELLED_MBEAN_DOMAINS_KEY = "tunnelledMBeanDomains";
    private static final String TC_CONFIG_SNIPPET_KEY = "tcConfigSnippet";
    private static final String REJOIN_KEY = "rejoin";
    private static final String PRODUCT_ID_KEY = "productId";
    private static final String CLIENT_NAME_KEY = "clientName";
    private static final String CLASSLOADER_KEY = "classloader";
    private static final String NONSTOP_INIT_ENABLED_KEY = "toolkit.nonstop.init.enabled";
    private boolean rejoin;
    private final TCConfigTypeStatus tcConfigTypeStatus = new TCConfigTypeStatus();
    private final Set<String> tunnelledMBeanDomains = Collections.synchronizedSet(new HashSet());
    private String productId;
    private String clientName;
    private ClassLoader classLoader;
    private final boolean NONSTOP_INIT_ENABLED = Boolean.getBoolean("toolkit.nonstop.init.enabled");

    public Toolkit buildToolkit() throws IllegalStateException {
        boolean isUrl;
        String tcConfigOrUrl;
        if (this.tcConfigTypeStatus.getState() == TCConfigTypeState.INIT) {
            throw new IllegalStateException("Please set the tcConfigSnippet or tcConfigUrl before attempting to create client");
        }
        switch (this.tcConfigTypeStatus.getState()) {
            case TC_CONFIG_SNIPPET: {
                tcConfigOrUrl = this.tcConfigTypeStatus.getTcConfigSnippet();
                isUrl = false;
                break;
            }
            case TC_CONFIG_URL: {
                tcConfigOrUrl = this.tcConfigTypeStatus.getTcConfigUrl();
                isUrl = true;
                break;
            }
            default: {
                throw new IllegalStateException("Unknown tc config type - " + this.tcConfigTypeStatus.getState());
            }
        }
        String toolkitUrl = this.createTerracottaToolkitUrl(isUrl, tcConfigOrUrl);
        return this.createToolkit(toolkitUrl, this.getTerracottaToolkitProperties(isUrl, tcConfigOrUrl, this.classLoader));
    }

    private Toolkit createToolkit(String url, Properties props) {
        try {
            return ToolkitFactory.createToolkit((String)url, (Properties)props);
        }
        catch (ToolkitInstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties getTerracottaToolkitProperties(boolean isUrl, String tcConfigOrUrl, ClassLoader loader) {
        Properties properties = new Properties();
        if (loader != null) {
            properties.put(CLASSLOADER_KEY, loader);
        }
        properties.setProperty(TC_TUNNELLED_MBEAN_DOMAINS_KEY, this.getTunnelledDomainCSV());
        if (!isUrl) {
            properties.setProperty(TC_CONFIG_SNIPPET_KEY, tcConfigOrUrl);
        }
        properties.setProperty(REJOIN_KEY, String.valueOf(this.rejoin));
        if (this.productId != null) {
            properties.setProperty(PRODUCT_ID_KEY, this.productId);
        }
        if (this.clientName != null) {
            properties.setProperty(CLIENT_NAME_KEY, this.clientName);
        }
        properties.setProperty(NONSTOP_INIT_ENABLED_KEY, String.valueOf(this.NONSTOP_INIT_ENABLED));
        return properties;
    }

    private String getTunnelledDomainCSV() {
        StringBuilder sb = new StringBuilder();
        for (String domain : this.tunnelledMBeanDomains) {
            sb.append(domain).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private String createTerracottaToolkitUrl(boolean isUrl, String tcConfigOrUrl) {
        if (isUrl) {
            return "toolkit:nonstop-terracotta://" + tcConfigOrUrl;
        }
        return "toolkit:nonstop-terracotta:";
    }

    public TerracottaToolkitBuilder addTunnelledMBeanDomain(String tunnelledMBeanDomain) {
        this.tunnelledMBeanDomains.add(tunnelledMBeanDomain);
        return this;
    }

    public Set<String> getTunnelledMBeanDomains() {
        return Collections.unmodifiableSet(this.tunnelledMBeanDomains);
    }

    public TerracottaToolkitBuilder removeTunnelledMBeanDomain(String tunnelledMBeanDomain) {
        this.tunnelledMBeanDomains.remove(tunnelledMBeanDomain);
        return this;
    }

    public TerracottaToolkitBuilder setTCConfigSnippet(String tcConfig) throws IllegalStateException {
        this.tcConfigTypeStatus.setTcConfigSnippet(tcConfig);
        return this;
    }

    public String getTCConfigSnippet() {
        return this.tcConfigTypeStatus.getTcConfigSnippet();
    }

    public TerracottaToolkitBuilder setTCConfigUrl(String tcConfigUrl) throws IllegalStateException {
        this.tcConfigTypeStatus.setTcConfigUrl(tcConfigUrl);
        return this;
    }

    public String getTCConfigUrl() {
        return this.tcConfigTypeStatus.getTcConfigUrl();
    }

    public boolean isConfigUrl() {
        return this.tcConfigTypeStatus.getState() == TCConfigTypeState.TC_CONFIG_URL;
    }

    public void setRejoinEnabled(boolean rejoin) {
        this.rejoin = rejoin;
    }

    public String getProductId() {
        return this.productId;
    }

    public TerracottaToolkitBuilder setClassLoader(ClassLoader loader) {
        this.classLoader = loader;
        return this;
    }

    public TerracottaToolkitBuilder setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public TerracottaToolkitBuilder setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    private static class TCConfigTypeStatus {
        private TCConfigTypeState state = TCConfigTypeState.INIT;
        private String tcConfigSnippet;
        private String tcConfigUrl;

        private TCConfigTypeStatus() {
        }

        public synchronized void setTcConfigSnippet(String tcConfigSnippet) {
            if (this.state == TCConfigTypeState.TC_CONFIG_URL) {
                throw new IllegalStateException("TCConfig url was already set to - " + this.tcConfigUrl);
            }
            this.state = TCConfigTypeState.TC_CONFIG_SNIPPET;
            this.tcConfigSnippet = tcConfigSnippet;
        }

        public synchronized void setTcConfigUrl(String tcConfigUrl) {
            if (this.state == TCConfigTypeState.TC_CONFIG_SNIPPET) {
                throw new IllegalStateException("TCConfig snippet was already set to - " + this.tcConfigSnippet);
            }
            this.state = TCConfigTypeState.TC_CONFIG_URL;
            this.tcConfigUrl = tcConfigUrl;
        }

        public synchronized String getTcConfigSnippet() {
            return this.tcConfigSnippet;
        }

        public synchronized String getTcConfigUrl() {
            return this.tcConfigUrl;
        }

        public TCConfigTypeState getState() {
            return this.state;
        }
    }

    private static enum TCConfigTypeState {
        INIT,
        TC_CONFIG_SNIPPET,
        TC_CONFIG_URL;

    }
}

