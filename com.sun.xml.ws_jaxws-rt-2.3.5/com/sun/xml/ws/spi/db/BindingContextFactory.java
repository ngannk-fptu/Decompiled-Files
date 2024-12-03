/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.Marshaller
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.db.glassfish.JAXBRIContextFactory;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.BindingInfo;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.util.ServiceConfigurationError;
import com.sun.xml.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public abstract class BindingContextFactory {
    public static final String DefaultDatabindingMode = "glassfish.jaxb";
    public static final String JAXB_CONTEXT_FACTORY_PROPERTY = BindingContextFactory.class.getName();
    public static final Logger LOGGER = Logger.getLogger(BindingContextFactory.class.getName());

    public static Iterator<BindingContextFactory> serviceIterator() {
        ServiceFinder<BindingContextFactory> sf = ServiceFinder.find(BindingContextFactory.class);
        final Iterator<BindingContextFactory> ibcf = sf.iterator();
        return new Iterator<BindingContextFactory>(){
            private BindingContextFactory bcf;

            @Override
            public boolean hasNext() {
                while (true) {
                    try {
                        if (ibcf.hasNext()) {
                            this.bcf = (BindingContextFactory)ibcf.next();
                            return true;
                        }
                        return false;
                    }
                    catch (ServiceConfigurationError e) {
                        LOGGER.warning("skipping factory: ServiceConfigurationError: " + e.getMessage());
                        continue;
                    }
                    catch (NoClassDefFoundError ncdfe) {
                        LOGGER.fine("skipping factory: NoClassDefFoundError: " + ncdfe.getMessage());
                        continue;
                    }
                    break;
                }
            }

            @Override
            public BindingContextFactory next() {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer("SPI found provider: " + this.bcf.getClass().getName());
                }
                return this.bcf;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static List<BindingContextFactory> factories() {
        ArrayList<BindingContextFactory> factories = new ArrayList<BindingContextFactory>();
        Iterator<BindingContextFactory> ibcf = BindingContextFactory.serviceIterator();
        while (ibcf.hasNext()) {
            factories.add(ibcf.next());
        }
        if (factories.isEmpty()) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(Level.FINER, "No SPI providers for BindingContextFactory found, adding: " + JAXBRIContextFactory.class.getName());
            }
            factories.add(new JAXBRIContextFactory());
        }
        return factories;
    }

    protected abstract BindingContext newContext(JAXBContext var1);

    protected abstract BindingContext newContext(BindingInfo var1);

    protected abstract boolean isFor(String var1);

    protected abstract BindingContext getContext(Marshaller var1);

    private static BindingContextFactory getFactory(String mode) {
        for (BindingContextFactory f : BindingContextFactory.factories()) {
            if (!f.isFor(mode)) continue;
            return f;
        }
        return null;
    }

    public static BindingContext create(JAXBContext context) throws DatabindingException {
        return BindingContextFactory.getJAXBFactory(context).newContext(context);
    }

    public static BindingContext create(BindingInfo bi) {
        BindingContextFactory f;
        String mode = bi.getDatabindingMode();
        if (mode != null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Using SEI-configured databindng mode: " + mode);
            }
        } else {
            mode = System.getProperty("BindingContextFactory");
            if (mode != null) {
                bi.setDatabindingMode(mode);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Using databindng: " + mode + " based on 'BindingContextFactory' System property");
                }
            } else {
                mode = System.getProperty(JAXB_CONTEXT_FACTORY_PROPERTY);
                if (mode != null) {
                    bi.setDatabindingMode(mode);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "Using databindng: " + mode + " based on '" + JAXB_CONTEXT_FACTORY_PROPERTY + "' System property");
                    }
                } else {
                    BindingContext factory = BindingContextFactory.getBindingContextFromSpi(BindingContextFactory.factories(), bi);
                    if (factory != null) {
                        return factory;
                    }
                    LOGGER.log(Level.SEVERE, "No Binding Context Factories found.");
                    throw new DatabindingException("No Binding Context Factories found.");
                }
            }
        }
        if ((f = BindingContextFactory.getFactory(mode)) != null) {
            return f.newContext(bi);
        }
        LOGGER.severe("Unknown Databinding mode: " + mode);
        throw new DatabindingException("Unknown Databinding mode: " + mode);
    }

    private static BindingContext getBindingContextFromSpi(List<BindingContextFactory> factories, BindingInfo bindingInfo) {
        BindingContext result;
        ArrayList<BindingContextFactory> fallback = new ArrayList<BindingContextFactory>();
        for (BindingContextFactory factory : factories) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Found SPI-determined databindng mode: " + factory.getClass().getName());
            }
            if (factory.isFor("org.eclipse.persistence.jaxb") || factory.isFor("com.sun.xml.bind.v2.runtime")) {
                result = factory.newContext(bindingInfo);
                if (result == null) continue;
                return result;
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Skipped -> not JAXB.");
            }
            fallback.add(factory);
        }
        for (BindingContextFactory factory : fallback) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Fallback. Creating from: " + factory.getClass().getName());
            }
            if ((result = BindingContextFactory.getContextOrNullIfError(factory, bindingInfo)) == null) continue;
            return result;
        }
        return null;
    }

    private static BindingContext getContextOrNullIfError(BindingContextFactory factory, BindingInfo bindingInfo) {
        try {
            return factory.newContext(bindingInfo);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }

    public static boolean isContextSupported(Object o) {
        if (o == null) {
            return false;
        }
        String pkgName = o.getClass().getPackage().getName();
        for (BindingContextFactory f : BindingContextFactory.factories()) {
            if (!f.isFor(pkgName)) continue;
            return true;
        }
        return false;
    }

    static BindingContextFactory getJAXBFactory(Object o) {
        String pkgName = o.getClass().getPackage().getName();
        BindingContextFactory f = BindingContextFactory.getFactory(pkgName);
        if (f != null) {
            return f;
        }
        throw new DatabindingException("Unknown JAXBContext implementation: " + o.getClass());
    }

    public static BindingContext getBindingContext(Marshaller m) {
        return BindingContextFactory.getJAXBFactory(m).getContext(m);
    }
}

