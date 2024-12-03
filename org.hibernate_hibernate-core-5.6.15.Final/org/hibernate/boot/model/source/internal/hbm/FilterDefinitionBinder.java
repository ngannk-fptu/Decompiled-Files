/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.io.Serializable;
import java.util.HashMap;
import javax.xml.bind.JAXBElement;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterParameterType;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class FilterDefinitionBinder {
    private static final Logger log = Logger.getLogger(FilterDefinitionBinder.class);

    public static void processFilterDefinition(HbmLocalMetadataBuildingContext context, JaxbHbmFilterDefinitionType jaxbFilterDefinitionMapping) {
        HashMap<String, Type> parameterMap = null;
        String condition = jaxbFilterDefinitionMapping.getCondition();
        for (Serializable content : jaxbFilterDefinitionMapping.getContent()) {
            JaxbHbmFilterParameterType jaxbParameterMapping;
            if (String.class.isInstance(content)) {
                String contentString = content.toString().trim();
                if (!StringHelper.isNotEmpty(contentString) || condition == null) continue;
                log.debugf("filter-def [name=%s, origin=%s] defined multiple conditions, accepting arbitrary one", (Object)jaxbFilterDefinitionMapping.getName(), (Object)context.getOrigin().toString());
                continue;
            }
            if (JaxbHbmFilterParameterType.class.isInstance(content)) {
                jaxbParameterMapping = (JaxbHbmFilterParameterType)content;
            } else if (JAXBElement.class.isInstance(content)) {
                JAXBElement jaxbElement = (JAXBElement)content;
                jaxbParameterMapping = (JaxbHbmFilterParameterType)jaxbElement.getValue();
            } else {
                throw new MappingException("Unable to decipher filter-def content type [" + content.getClass().getName() + "]", context.getOrigin());
            }
            if (parameterMap == null) {
                parameterMap = new HashMap<String, Type>();
            }
            parameterMap.put(jaxbParameterMapping.getParameterName(), context.getMetadataCollector().getTypeResolver().heuristicType(jaxbParameterMapping.getParameterValueTypeName()));
        }
        context.getMetadataCollector().addFilterDefinition(new FilterDefinition(jaxbFilterDefinitionMapping.getName(), condition, parameterMap));
        log.debugf("Processed filter definition : %s", (Object)jaxbFilterDefinitionMapping.getName());
    }
}

