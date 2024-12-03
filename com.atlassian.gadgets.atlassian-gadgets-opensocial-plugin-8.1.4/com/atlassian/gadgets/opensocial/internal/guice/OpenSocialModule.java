/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.name.Names
 *  org.apache.shindig.common.servlet.ParameterFetcher
 *  org.apache.shindig.social.core.util.BeanJsonConverter
 *  org.apache.shindig.social.core.util.BeanXStreamAtomConverter
 *  org.apache.shindig.social.core.util.BeanXStreamConverter
 *  org.apache.shindig.social.core.util.ContainerConf
 *  org.apache.shindig.social.core.util.JsonContainerConf
 *  org.apache.shindig.social.opensocial.service.ActivityHandler
 *  org.apache.shindig.social.opensocial.service.AppDataHandler
 *  org.apache.shindig.social.opensocial.service.BeanConverter
 *  org.apache.shindig.social.opensocial.service.DataServiceServletFetcher
 *  org.apache.shindig.social.opensocial.service.PersonHandler
 *  org.apache.shindig.social.opensocial.spi.ActivityService
 *  org.apache.shindig.social.opensocial.spi.AppDataService
 *  org.apache.shindig.social.opensocial.spi.PersonService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.opensocial.internal.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import org.apache.shindig.common.servlet.ParameterFetcher;
import org.apache.shindig.social.core.util.BeanJsonConverter;
import org.apache.shindig.social.core.util.BeanXStreamAtomConverter;
import org.apache.shindig.social.core.util.BeanXStreamConverter;
import org.apache.shindig.social.core.util.ContainerConf;
import org.apache.shindig.social.core.util.JsonContainerConf;
import org.apache.shindig.social.opensocial.service.ActivityHandler;
import org.apache.shindig.social.opensocial.service.AppDataHandler;
import org.apache.shindig.social.opensocial.service.BeanConverter;
import org.apache.shindig.social.opensocial.service.DataServiceServletFetcher;
import org.apache.shindig.social.opensocial.service.PersonHandler;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenSocialModule
extends AbstractModule {
    private final PersonService shindigPersonService;
    private final ActivityService shindigActivityService;
    private final AppDataService shindigAppDataService;

    @Autowired
    public OpenSocialModule(PersonService shindigPersonService, ActivityService shindigActivityService, AppDataService shindigAppDataService) {
        this.shindigPersonService = shindigPersonService;
        this.shindigActivityService = shindigActivityService;
        this.shindigAppDataService = shindigAppDataService;
    }

    protected void configure() {
        this.bind(PersonService.class).toInstance((Object)this.shindigPersonService);
        this.bind(ActivityService.class).toInstance((Object)this.shindigActivityService);
        this.bind(AppDataService.class).toInstance((Object)this.shindigAppDataService);
        this.bind(ParameterFetcher.class).annotatedWith((Annotation)Names.named((String)"DataServiceServlet")).to(DataServiceServletFetcher.class);
        this.bind(BeanConverter.class).annotatedWith((Annotation)Names.named((String)"shindig.bean.converter.xml")).to(BeanXStreamConverter.class);
        this.bind(BeanConverter.class).annotatedWith((Annotation)Names.named((String)"shindig.bean.converter.json")).to(BeanJsonConverter.class);
        this.bind(BeanConverter.class).annotatedWith((Annotation)Names.named((String)"shindig.bean.converter.atom")).to(BeanXStreamAtomConverter.class);
        this.bind(PersonHandler.class);
        this.bind(ActivityHandler.class);
        this.bind(AppDataHandler.class);
        this.bind(ContainerConf.class).to(JsonContainerConf.class);
    }
}

