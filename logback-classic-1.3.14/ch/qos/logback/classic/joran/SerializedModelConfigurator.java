/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.LogbackException
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.ModelUtil
 *  ch.qos.logback.core.model.processor.DefaultProcessor
 *  ch.qos.logback.core.model.processor.ModelInterpretationContext
 *  ch.qos.logback.core.spi.ContextAwareBase
 *  ch.qos.logback.core.status.InfoStatus
 *  ch.qos.logback.core.status.Status
 *  ch.qos.logback.core.status.StatusManager
 *  ch.qos.logback.core.util.Loader
 *  ch.qos.logback.core.util.OptionHelper
 */
package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.ModelClassToModelHandlerLinker;
import ch.qos.logback.classic.joran.serializedModel.HardenedModelInputStream;
import ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@ConfiguratorRank(value=10)
public class SerializedModelConfigurator
extends ContextAwareBase
implements Configurator {
    public static final String AUTOCONFIG_MODEL_FILE = "logback.scmo";
    public static final String TEST_AUTOCONFIG_MODEL_FILE = "logback-test.scmo";
    protected ModelInterpretationContext modelInterpretationContext;

    @Override
    public Configurator.ExecutionStatus configure(LoggerContext loggerContext) {
        URL url = this.performMultiStepModelFileSearch(true);
        if (url != null) {
            this.configureByResource(url);
            return Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
        }
        return Configurator.ExecutionStatus.INVOKE_NEXT_IF_ANY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void configureByResource(URL url) {
        String urlString = url.toString();
        if (urlString.endsWith(".scmo")) {
            Model model = this.retrieveModel(url);
            if (model == null) {
                this.addWarn("Empty model. Abandoning.");
                return;
            }
            ModelUtil.resetForReuse((Model)model);
            this.buildModelInterpretationContext(model);
            DefaultProcessor defaultProcessor = new DefaultProcessor(this.context, this.modelInterpretationContext);
            ModelClassToModelHandlerLinker mc2mhl = new ModelClassToModelHandlerLinker(this.context);
            mc2mhl.link(defaultProcessor);
            Object object = this.context.getConfigurationLock();
            synchronized (object) {
                defaultProcessor.process(model);
            }
        } else {
            throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be " + ".scmo");
        }
    }

    private void buildModelInterpretationContext(Model topModel) {
        this.modelInterpretationContext = new ModelInterpretationContext(this.context, (Object)this);
        this.modelInterpretationContext.setTopModel(topModel);
        LogbackClassicDefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(this.modelInterpretationContext.getDefaultNestedComponentRegistry());
        this.modelInterpretationContext.createAppenderBags();
    }

    private Model retrieveModel(URL url) {
        block10: {
            Model model;
            block9: {
                long start = System.currentTimeMillis();
                InputStream is = url.openStream();
                try {
                    HardenedModelInputStream hmis = new HardenedModelInputStream(is);
                    Model model2 = (Model)hmis.readObject();
                    long diff = System.currentTimeMillis() - start;
                    this.addInfo("Model at [" + url + "] read in " + diff + " milliseconds");
                    model = model2;
                    if (is == null) break block9;
                }
                catch (Throwable throwable) {
                    try {
                        if (is != null) {
                            try {
                                is.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        this.addError("Failed to open " + url, e);
                        break block10;
                    }
                    catch (ClassNotFoundException e) {
                        this.addError("Failed read model object in " + url, e);
                    }
                }
                is.close();
            }
            return model;
        }
        return null;
    }

    private URL performMultiStepModelFileSearch(boolean updateState) {
        ClassLoader myClassLoader = Loader.getClassLoaderOfObject((Object)this);
        URL url = this.findModelConfigFileURLFromSystemProperties(myClassLoader);
        if (url != null) {
            return url;
        }
        url = this.getResource(TEST_AUTOCONFIG_MODEL_FILE, myClassLoader, updateState);
        if (url != null) {
            return url;
        }
        url = this.getResource(AUTOCONFIG_MODEL_FILE, myClassLoader, updateState);
        return url;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    URL findModelConfigFileURLFromSystemProperties(ClassLoader classLoader) {
        String logbackModelFile = OptionHelper.getSystemProperty((String)"logback.scmoFile");
        if (logbackModelFile != null) {
            URL result = null;
            try {
                URL uRL = result = new URL(logbackModelFile);
                return uRL;
            }
            catch (MalformedURLException e) {
                result = Loader.getResource((String)logbackModelFile, (ClassLoader)classLoader);
                if (result != null) {
                    URL uRL = result;
                    return uRL;
                }
                File f = new File(logbackModelFile);
                if (f.exists() && f.isFile()) {
                    try {
                        URL uRL = result = f.toURI().toURL();
                        return uRL;
                    }
                    catch (MalformedURLException malformedURLException) {}
                }
            }
            finally {
                this.statusOnResourceSearch(logbackModelFile, result);
            }
        }
        return null;
    }

    private URL getResource(String filename, ClassLoader classLoader, boolean updateStatus) {
        URL url = Loader.getResource((String)filename, (ClassLoader)classLoader);
        if (updateStatus) {
            this.statusOnResourceSearch(filename, url);
        }
        return url;
    }

    private void statusOnResourceSearch(String resourceName, URL url) {
        StatusManager sm = this.context.getStatusManager();
        if (url == null) {
            sm.add((Status)new InfoStatus("Could NOT find resource [" + resourceName + "]", (Object)this.context));
        } else {
            sm.add((Status)new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", (Object)this.context));
        }
    }
}

