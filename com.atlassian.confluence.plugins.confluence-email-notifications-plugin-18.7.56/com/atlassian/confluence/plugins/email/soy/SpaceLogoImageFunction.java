/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.mail.embed.MimeBodyPartRecorder
 *  com.atlassian.confluence.mail.embed.MimeBodyPartReference
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 *  javax.activation.DataSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.activation.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceLogoImageFunction
implements SoyServerFunction<String> {
    private static final ImmutableSet<Integer> SIZE = ImmutableSet.of((Object)1);
    private static final Logger log = LoggerFactory.getLogger(SpaceLogoImageFunction.class);
    private final DataSourceFactory dataSourceFactory;
    private final MimeBodyPartRecorder bodyPartRecorder;
    private final SpaceManager spaceManager;

    public SpaceLogoImageFunction(DataSourceFactory dataSourceFactory, MimeBodyPartRecorder bodyPartRecorder, SpaceManager spaceManager) {
        this.dataSourceFactory = dataSourceFactory;
        this.bodyPartRecorder = bodyPartRecorder;
        this.spaceManager = spaceManager;
    }

    public String apply(Object ... args) {
        Space spaceObject;
        if (args[0] instanceof com.atlassian.confluence.api.model.content.Space) {
            com.atlassian.confluence.api.model.content.Space space = (com.atlassian.confluence.api.model.content.Space)args[0];
            spaceObject = this.spaceManager.getSpace(space.getId());
        } else if (args[0] instanceof Space) {
            spaceObject = (Space)args[0];
        } else if (args[0] instanceof String) {
            spaceObject = this.spaceManager.getSpace((String)args[0]);
        } else {
            return this.checkArgument(args[0]);
        }
        DataSource spaceLogoDataSource = this.dataSourceFactory.getSpaceLogo(spaceObject);
        log.debug("using avatar datasource {} for space {}[{}]", new Object[]{spaceLogoDataSource.getName(), spaceObject.getName(), spaceObject.getKey()});
        return ((MimeBodyPartReference)this.bodyPartRecorder.track(spaceLogoDataSource).get()).getLocator().toASCIIString();
    }

    private String checkArgument(Object arg) {
        if (arg != null) {
            throw new ClassCastException("argument 0 is not of type '" + Space.class.getName() + "' in '" + this.getName() + "' soy function : " + arg.getClass().getName());
        }
        throw new NullPointerException("argument 0 is null in '" + this.getName() + "' soy function");
    }

    public String getName() {
        return "spaceLogoImage";
    }

    public Set<Integer> validArgSizes() {
        return SIZE;
    }
}

