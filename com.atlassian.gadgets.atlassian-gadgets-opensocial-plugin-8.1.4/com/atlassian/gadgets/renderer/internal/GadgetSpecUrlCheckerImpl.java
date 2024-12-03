/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetSpecUriNotAllowedException
 *  com.atlassian.gadgets.GadgetSpecUrlChecker
 *  com.atlassian.gadgets.Vote
 *  com.atlassian.gadgets.opensocial.spi.GadgetSpecUrlRenderPermission
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;
import com.atlassian.gadgets.GadgetSpecUrlChecker;
import com.atlassian.gadgets.Vote;
import com.atlassian.gadgets.opensocial.spi.GadgetSpecUrlRenderPermission;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GadgetSpecUrlCheckerImpl
implements GadgetSpecUrlChecker {
    private final Logger log = LoggerFactory.getLogger(GadgetSpecUrlCheckerImpl.class);
    private final Iterable<GadgetSpecUrlRenderPermission> permissions;

    public GadgetSpecUrlCheckerImpl(Iterable<GadgetSpecUrlRenderPermission> permissions) {
        Preconditions.checkNotNull(permissions);
        this.permissions = permissions;
    }

    public void assertRenderable(String gadgetSpecUri) {
        Preconditions.checkNotNull((Object)gadgetSpecUri);
        if (Iterables.isEmpty(this.permissions)) {
            throw new GadgetSpecUriNotAllowedException("no permissions defined: all rendering rejected by default");
        }
        int passes = 0;
        int totalPermissions = 0;
        for (GadgetSpecUrlRenderPermission permission : this.permissions) {
            Vote lastVote;
            try {
                lastVote = permission.voteOn(gadgetSpecUri);
            }
            catch (RuntimeException re) {
                if (this.log.isDebugEnabled()) {
                    this.log.warn("Could not check gadget render permission with " + permission, (Throwable)re);
                } else if (this.log.isWarnEnabled()) {
                    this.log.warn("Could not check gadget render permission with " + permission + ": " + re.getMessage());
                }
                throw new GadgetSpecUriNotAllowedException("exception while checking permission " + permission + ": " + re.getMessage());
            }
            switch (lastVote) {
                case DENY: {
                    throw new GadgetSpecUriNotAllowedException("permission '" + permission + "' vetoed render of gadget at " + gadgetSpecUri);
                }
                case PASS: {
                    ++passes;
                }
            }
            ++totalPermissions;
        }
        if (passes == totalPermissions) {
            throw new GadgetSpecUriNotAllowedException("no ALLOW permission for gadget at " + gadgetSpecUri);
        }
    }
}

