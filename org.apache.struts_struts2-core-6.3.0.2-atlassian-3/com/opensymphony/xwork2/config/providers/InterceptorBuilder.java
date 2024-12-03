/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ParameterizedMessage
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorLocator;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.location.Location;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

public class InterceptorBuilder {
    private static final Logger LOG = LogManager.getLogger(InterceptorBuilder.class);

    public static List<InterceptorMapping> constructInterceptorReference(InterceptorLocator interceptorLocator, String refName, Map<String, String> refParams, Location location, ObjectFactory objectFactory) throws ConfigurationException {
        Object referencedConfig = interceptorLocator.getInterceptorConfig(refName);
        List<InterceptorMapping> result = new ArrayList<InterceptorMapping>();
        if (referencedConfig == null) {
            throw new ConfigurationException("Unable to find interceptor class referenced by ref-name " + refName, (Object)location);
        }
        if (referencedConfig instanceof InterceptorConfig) {
            InterceptorConfig config = (InterceptorConfig)referencedConfig;
            try {
                Interceptor inter = objectFactory.buildInterceptor(config, refParams);
                result.add(new InterceptorMapping(refName, inter, refParams));
            }
            catch (ConfigurationException ex) {
                LOG.warn((Message)new ParameterizedMessage("Unable to load config class {} at {} probably due to a missing jar, which might be fine if you never plan to use the {} interceptor", new Object[]{config.getClassName(), ex.getLocation(), config.getName()}), (Throwable)ex);
            }
        } else if (referencedConfig instanceof InterceptorStackConfig) {
            InterceptorStackConfig stackConfig = (InterceptorStackConfig)referencedConfig;
            if (refParams != null && refParams.size() > 0) {
                result = InterceptorBuilder.constructParameterizedInterceptorReferences(interceptorLocator, stackConfig, refParams, objectFactory);
            } else {
                result.addAll(stackConfig.getInterceptors());
            }
        } else {
            LOG.error("Got unexpected type for interceptor {}. Got {}", (Object)refName, referencedConfig);
        }
        return result;
    }

    private static List<InterceptorMapping> constructParameterizedInterceptorReferences(InterceptorLocator interceptorLocator, InterceptorStackConfig stackConfig, Map<String, String> refParams, ObjectFactory objectFactory) {
        String key;
        LinkedHashMap params = new LinkedHashMap();
        for (Map.Entry<String, String> entry : refParams.entrySet()) {
            key = entry.getKey();
            try {
                String name = key.substring(0, key.indexOf(46));
                key = key.substring(key.indexOf(46) + 1);
                Map map = params.containsKey(name) ? (Map)params.get(name) : new LinkedHashMap();
                map.put(key, entry.getValue());
                params.put(name, map);
            }
            catch (Exception e) {
                LOG.warn("No interceptor found for name = {}", (Object)key);
            }
        }
        ArrayList<InterceptorMapping> result = new ArrayList<InterceptorMapping>(stackConfig.getInterceptors());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            key = entry.getKey();
            Map map = (Map)((Object)entry.getValue());
            Object interceptorCfgObj = interceptorLocator.getInterceptorConfig(key);
            if (interceptorCfgObj instanceof InterceptorConfig) {
                InterceptorConfig cfg = (InterceptorConfig)interceptorCfgObj;
                Interceptor interceptor = objectFactory.buildInterceptor(cfg, map);
                InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
                if (result.contains(mapping)) {
                    for (int index = 0; index < result.size(); ++index) {
                        InterceptorMapping interceptorMapping = (InterceptorMapping)result.get(index);
                        if (!interceptorMapping.getName().equals(key)) continue;
                        LOG.debug("Overriding interceptor config [{}] with new mapping {} using new params {}", (Object)key, (Object)interceptorMapping, (Object)map);
                        result.set(index, mapping);
                    }
                    continue;
                }
                result.add(mapping);
                continue;
            }
            if (!(interceptorCfgObj instanceof InterceptorStackConfig)) continue;
            InterceptorStackConfig stackCfg = (InterceptorStackConfig)interceptorCfgObj;
            List<InterceptorMapping> tmpResult = InterceptorBuilder.constructParameterizedInterceptorReferences(interceptorLocator, stackCfg, map, objectFactory);
            for (InterceptorMapping tmpInterceptorMapping : tmpResult) {
                if (result.contains(tmpInterceptorMapping)) {
                    int index = result.indexOf(tmpInterceptorMapping);
                    result.set(index, tmpInterceptorMapping);
                    continue;
                }
                result.add(tmpInterceptorMapping);
            }
        }
        return result;
    }
}

