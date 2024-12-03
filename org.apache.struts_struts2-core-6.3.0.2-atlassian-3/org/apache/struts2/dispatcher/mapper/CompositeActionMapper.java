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
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class CompositeActionMapper
implements ActionMapper {
    private static final Logger LOG = LogManager.getLogger(CompositeActionMapper.class);
    protected List<ActionMapper> actionMappers = new LinkedList<ActionMapper>();

    @Inject
    public CompositeActionMapper(Container container, @Inject(value="struts.mapper.composite") String list) {
        String[] arr;
        for (String name : arr = StringUtils.split((String)StringUtils.trimToEmpty((String)list), (String)",")) {
            ActionMapper obj = container.getInstance(ActionMapper.class, name);
            if (obj == null) continue;
            this.actionMappers.add(obj);
        }
    }

    @Override
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        for (ActionMapper actionMapper : this.actionMappers) {
            ActionMapping actionMapping = actionMapper.getMapping(request, configManager);
            LOG.debug("Using ActionMapper: {}", (Object)actionMapper);
            if (actionMapping == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", (Object)actionMapper);
                continue;
            }
            return actionMapping;
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }

    @Override
    public ActionMapping getMappingFromActionName(String actionName) {
        for (ActionMapper actionMapper : this.actionMappers) {
            ActionMapping actionMapping = actionMapper.getMappingFromActionName(actionName);
            LOG.debug("Using ActionMapper: {}", (Object)actionMapper);
            if (actionMapping == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", (Object)actionMapper);
                continue;
            }
            return actionMapping;
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }

    @Override
    public String getUriFromActionMapping(ActionMapping mapping) {
        for (ActionMapper actionMapper : this.actionMappers) {
            String uri = actionMapper.getUriFromActionMapping(mapping);
            LOG.debug("Using ActionMapper: {}", (Object)actionMapper);
            if (uri == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", (Object)actionMapper);
                continue;
            }
            return uri;
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }
}

