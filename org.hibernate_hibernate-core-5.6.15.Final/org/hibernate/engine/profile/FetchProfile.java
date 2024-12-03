/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.profile;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.engine.profile.Association;
import org.hibernate.engine.profile.Fetch;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.BagType;
import org.hibernate.type.Type;

public class FetchProfile {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(FetchProfile.class);
    private final String name;
    private Map<String, Fetch> fetches = new HashMap<String, Fetch>();
    private boolean containsJoinFetchedCollection;
    private boolean containsJoinFetchedBag;
    private Fetch bagJoinFetch;

    public FetchProfile(String name) {
        this.name = name;
    }

    public void addFetch(Association association, String fetchStyleName) {
        this.addFetch(association, Fetch.Style.parse(fetchStyleName));
    }

    public void addFetch(Association association, Fetch.Style style) {
        this.addFetch(new Fetch(association, style));
    }

    public void addFetch(Fetch fetch) {
        String fetchAssociactionRole = fetch.getAssociation().getRole();
        Type associationType = fetch.getAssociation().getOwner().getPropertyType(fetch.getAssociation().getAssociationPath());
        if (associationType.isCollectionType()) {
            LOG.tracev("Handling request to add collection fetch [{0}]", fetchAssociactionRole);
            if (Fetch.Style.JOIN == fetch.getStyle()) {
                if (BagType.class.isInstance(associationType) && this.containsJoinFetchedCollection) {
                    LOG.containsJoinFetchedCollection(fetchAssociactionRole);
                    return;
                }
                if (this.containsJoinFetchedBag) {
                    if (this.fetches.remove(this.bagJoinFetch.getAssociation().getRole()) != this.bagJoinFetch) {
                        LOG.unableToRemoveBagJoinFetch();
                    }
                    this.bagJoinFetch = null;
                    this.containsJoinFetchedBag = false;
                }
                this.containsJoinFetchedCollection = true;
            }
        }
        this.fetches.put(fetchAssociactionRole, fetch);
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Fetch> getFetches() {
        return this.fetches;
    }

    public Fetch getFetchByRole(String role) {
        return this.fetches.get(role);
    }

    public boolean isContainsJoinFetchedCollection() {
        return this.containsJoinFetchedCollection;
    }

    public boolean isContainsJoinFetchedBag() {
        return this.containsJoinFetchedBag;
    }
}

