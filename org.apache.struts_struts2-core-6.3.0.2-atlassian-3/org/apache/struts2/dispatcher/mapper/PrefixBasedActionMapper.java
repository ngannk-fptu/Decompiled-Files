/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

public class PrefixBasedActionMapper
extends DefaultActionMapper
implements ActionMapper {
    private static final Logger LOG = LogManager.getLogger(PrefixBasedActionMapper.class);
    protected Container container;
    protected Map<String, ActionMapper> actionMappers = new HashMap<String, ActionMapper>();

    @Override
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value="struts.mapper.prefixMapping")
    public void setPrefixBasedActionMappers(String list) {
        String[] mappers;
        for (String mapper : mappers = StringUtils.split((String)StringUtils.trimToEmpty((String)list), (String)",")) {
            String[] thisMapper = mapper.split(":");
            if (thisMapper.length != 2) continue;
            String mapperPrefix = thisMapper[0].trim();
            String mapperName = thisMapper[1].trim();
            ActionMapper obj = this.container.getInstance(ActionMapper.class, mapperName);
            if (obj != null) {
                this.actionMappers.put(mapperPrefix, obj);
                continue;
            }
            LOG.debug("invalid PrefixBasedActionMapper config entry: [{}]", (Object)mapper);
        }
    }

    @Override
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        String uri = RequestUtils.getUri(request);
        int lastIndex = uri.lastIndexOf(47);
        while (lastIndex > -1) {
            ActionMapper actionMapper = this.actionMappers.get(uri.substring(0, lastIndex));
            if (actionMapper != null) {
                ActionMapping actionMapping = actionMapper.getMapping(request, configManager);
                LOG.debug("Using ActionMapper [{}]", (Object)actionMapper);
                if (actionMapping != null) {
                    if (LOG.isDebugEnabled() && actionMapping.getParams() != null) {
                        LOG.debug("ActionMapper found mapping. Parameters: [{}]", (Object)actionMapping.getParams().toString());
                        for (Map.Entry<String, Object> mappingParameterEntry : actionMapping.getParams().entrySet()) {
                            Object paramValue = mappingParameterEntry.getValue();
                            if (paramValue == null) {
                                LOG.debug("[{}] : null!", (Object)mappingParameterEntry.getKey());
                                continue;
                            }
                            if (paramValue instanceof String[]) {
                                LOG.debug("[{}] : (String[]) {}", (Object)mappingParameterEntry.getKey(), (Object)Arrays.toString((String[])paramValue));
                                continue;
                            }
                            if (paramValue instanceof String) {
                                LOG.debug("[{}] : (String) [{}]", (Object)mappingParameterEntry.getKey(), (Object)paramValue.toString());
                                continue;
                            }
                            LOG.debug("[{}] : (Object) [{}]", (Object)mappingParameterEntry.getKey(), (Object)paramValue.toString());
                        }
                    }
                    return actionMapping;
                }
                LOG.debug("ActionMapper [{}] failed to return an ActionMapping", (Object)actionMapper);
            }
            lastIndex = uri.lastIndexOf(47, lastIndex - 1);
        }
        LOG.debug("No ActionMapper found");
        return null;
    }

    @Override
    public String getUriFromActionMapping(ActionMapping mapping) {
        String namespace = mapping.getNamespace();
        int lastIndex = namespace.length();
        while (lastIndex > -1) {
            ActionMapper actionMapper = this.actionMappers.get(namespace.substring(0, lastIndex));
            if (actionMapper != null) {
                String uri = actionMapper.getUriFromActionMapping(mapping);
                LOG.debug("Using ActionMapper [{}]", (Object)actionMapper);
                if (uri != null) {
                    return uri;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ActionMapper [{}] failed to return an ActionMapping (null)", (Object)actionMapper);
                }
            }
            lastIndex = namespace.lastIndexOf(47, lastIndex - 1);
        }
        LOG.debug("ActionMapper failed to return a uri");
        return null;
    }
}

