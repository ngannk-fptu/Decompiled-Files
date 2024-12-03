/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.osgi;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.stax.WstxOutputFactory;
import java.util.Properties;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.osgi.Stax2OutputFactoryProvider;

public class OutputFactoryProviderImpl
implements Stax2OutputFactoryProvider {
    public XMLOutputFactory2 createOutputFactory() {
        return new WstxOutputFactory();
    }

    protected Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("org.codehaus.stax2.implName", ReaderConfig.getImplName());
        props.setProperty("org.codehaus.stax2.implVersion", ReaderConfig.getImplVersion());
        return props;
    }
}

