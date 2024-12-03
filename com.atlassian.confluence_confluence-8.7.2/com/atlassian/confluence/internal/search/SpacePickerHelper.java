/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.PairType
 *  com.atlassian.user.User
 *  com.opensymphony.xwork2.ActionSupport
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.util.PairType;
import com.atlassian.user.User;
import com.opensymphony.xwork2.ActionSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class SpacePickerHelper {
    private static final String SCOPE_PREFIX = "conf_";
    private static final String ALL = "conf_all";
    private static final String FAVOURITES = "conf_favorites";
    private static final String GLOBAL = "conf_global";
    private static final String PERSONAL = "conf_personal";
    private SpaceManager spaceManager;
    private LabelManager labelManager;
    private static final int MAX_SPACE_NAME_LENGTH = 20;

    public SpacePickerHelper(SpaceManager spaceManager, LabelManager labelManager) {
        this.spaceManager = spaceManager;
        this.labelManager = labelManager;
    }

    public List<SpaceDTO> getAvailableGlobalSpaces(ConfluenceUser user) {
        ArrayList<SpaceDTO> availableSpaces = new ArrayList<SpaceDTO>();
        SpacesQuery spacesQuery = SpacesQuery.newQuery().forUser(user).withSpaceType(SpaceType.GLOBAL).withSpaceStatus(SpaceStatus.CURRENT).build();
        List<Space> globalSpaces = this.spaceManager.getAllSpaces(spacesQuery);
        if (user != null) {
            globalSpaces.removeAll(this.labelManager.getFavouriteSpaces(user.getName()));
        }
        for (Space space : globalSpaces) {
            availableSpaces.add(new SpaceDTO(space.getKey(), space.getName()));
        }
        return availableSpaces;
    }

    public List<PairType> getAggregateOptions(ActionSupport action) {
        ArrayList<PairType> aggregateOptions = new ArrayList<PairType>();
        aggregateOptions.add(new PairType((Serializable)((Object)ALL), (Serializable)((Object)action.getText("inspace.allspace"))));
        aggregateOptions.add(new PairType((Serializable)((Object)FAVOURITES), (Serializable)((Object)action.getText("favourite.spaces"))));
        aggregateOptions.add(new PairType((Serializable)((Object)GLOBAL), (Serializable)((Object)action.getText("global.spaces"))));
        aggregateOptions.add(new PairType((Serializable)((Object)PERSONAL), (Serializable)((Object)action.getText("personal.spaces"))));
        return aggregateOptions;
    }

    public List<SpaceDTO> getFavouriteSpaces(User user) {
        ArrayList<SpaceDTO> availableSpaces = new ArrayList<SpaceDTO>();
        if (user != null) {
            this.labelManager.getFavouriteSpaces(user.getName()).forEach(space -> availableSpaces.add(new SpaceDTO(space.getKey(), space.getName())));
        }
        return availableSpaces;
    }

    public static class SpaceDTO {
        private final String key;
        private final String value;

        public SpaceDTO(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getSpaceKey() {
            return this.key;
        }

        public String getSpaceName() {
            return this.value;
        }

        public String getTruncatedSpaceName() {
            return GeneralUtil.shortenString(this.value, 20);
        }
    }
}

