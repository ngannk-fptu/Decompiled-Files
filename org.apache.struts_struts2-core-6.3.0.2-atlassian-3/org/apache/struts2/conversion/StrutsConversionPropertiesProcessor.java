/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.conversion;

import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.inject.EarlyInitializable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class StrutsConversionPropertiesProcessor
implements ConversionPropertiesProcessor,
EarlyInitializable {
    private static final Logger LOG = LogManager.getLogger(StrutsConversionPropertiesProcessor.class);
    private static final String STRUTS_DEFAULT_CONVERSION_PROPERTIES = "struts-default-conversion.properties";
    private static final String XWORK_CONVERSION_PROPERTIES = "xwork-conversion.properties";
    private static final String STRUTS_CONVERSION_PROPERTIES = "struts-conversion.properties";
    private TypeConverterCreator converterCreator;
    private TypeConverterHolder converterHolder;

    @Inject
    public void setTypeConverterCreator(TypeConverterCreator converterCreator) {
        this.converterCreator = converterCreator;
    }

    @Inject
    public void setTypeConverterHolder(TypeConverterHolder converterHolder) {
        this.converterHolder = converterHolder;
    }

    @Override
    public void init() {
        LOG.debug("Processing default conversion properties files");
        this.processRequired(STRUTS_DEFAULT_CONVERSION_PROPERTIES);
        this.process(STRUTS_CONVERSION_PROPERTIES);
        this.process(XWORK_CONVERSION_PROPERTIES);
    }

    @Override
    public void process(String propsName) {
        this.loadConversionProperties(propsName, false);
    }

    @Override
    public void processRequired(String propsName) {
        this.loadConversionProperties(propsName, true);
    }

    public void loadConversionProperties(String propsName, boolean require) {
        try {
            Iterator<URL> resources = ClassLoaderUtil.getResources(propsName, this.getClass(), true);
            while (resources.hasNext()) {
                if (XWORK_CONVERSION_PROPERTIES.equals(propsName)) {
                    LOG.warn("Instead of using deprecated {} please use the new file name {}", (Object)XWORK_CONVERSION_PROPERTIES, (Object)STRUTS_CONVERSION_PROPERTIES);
                }
                URL url = resources.next();
                Properties props = new Properties();
                props.load(url.openStream());
                LOG.debug("Processing conversion file [{}]", (Object)propsName);
                Iterator<Map.Entry<Object, Object>> iterator = props.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Object, Object> o;
                    Map.Entry<Object, Object> entry = o = iterator.next();
                    String key = (String)entry.getKey();
                    try {
                        TypeConverter typeConverter = this.converterCreator.createTypeConverter((String)entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t{}:{} [treated as TypeConverter {}]", (Object)key, entry.getValue(), (Object)typeConverter);
                        }
                        this.converterHolder.addDefaultMapping(key, typeConverter);
                    }
                    catch (Exception e) {
                        LOG.error("Conversion registration error", (Throwable)e);
                    }
                }
            }
        }
        catch (IOException ex) {
            if (require) {
                throw new StrutsException("Cannot load conversion properties file: " + propsName, ex);
            }
            LOG.debug("Cannot load conversion properties file: {}", (Object)propsName, (Object)ex);
        }
    }
}

