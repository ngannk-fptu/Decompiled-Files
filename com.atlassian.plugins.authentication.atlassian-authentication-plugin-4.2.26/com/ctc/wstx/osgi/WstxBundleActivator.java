/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 */
package com.ctc.wstx.osgi;

import com.ctc.wstx.osgi.InputFactoryProviderImpl;
import com.ctc.wstx.osgi.OutputFactoryProviderImpl;
import com.ctc.wstx.osgi.ValidationSchemaFactoryProviderImpl;
import java.util.Dictionary;
import java.util.Properties;
import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;
import org.codehaus.stax2.osgi.Stax2OutputFactoryProvider;
import org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class WstxBundleActivator
implements BundleActivator {
    public void start(BundleContext ctxt) {
        InputFactoryProviderImpl inputP = new InputFactoryProviderImpl();
        Properties inputProps = inputP.getProperties();
        ctxt.registerService(Stax2InputFactoryProvider.class.getName(), (Object)inputP, (Dictionary)inputProps);
        OutputFactoryProviderImpl outputP = new OutputFactoryProviderImpl();
        Properties outputProps = outputP.getProperties();
        ctxt.registerService(Stax2OutputFactoryProvider.class.getName(), (Object)outputP, (Dictionary)outputProps);
        for (ValidationSchemaFactoryProviderImpl impl : ValidationSchemaFactoryProviderImpl.createAll()) {
            Properties implProps = impl.getProperties();
            ctxt.registerService(Stax2ValidationSchemaFactoryProvider.class.getName(), (Object)impl, (Dictionary)implProps);
        }
    }

    public void stop(BundleContext ctxt) {
    }
}

