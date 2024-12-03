/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.impl.hibernate.Hibernate
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.spaces;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.impl.hibernate.Hibernate;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.servlet.simpledisplay.SpacePathConverter;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceGroup;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.Message;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings(value={"SE_NO_SERIALVERSIONID"})
public class Space
extends ConfluenceEntityObject
implements Searchable,
Addressable {
    public static final String PERSONAL_SPACEKEY_IDENTIFIER = "~";
    public static final String CONTENT_TYPE = "space";
    public static final int MAX_SPACE_NAME_LENGTH = 255;
    private String name;
    private String key;
    private String lowerKey;
    private SpaceType spaceType = SpaceType.GLOBAL;
    private SpaceStatus spaceStatus = SpaceStatus.CURRENT;
    private SpaceDescription description;
    private Page homePage;
    private List<SpacePermission> permissions = new ArrayList<SpacePermission>();
    private List pageTemplates = new ArrayList();
    private SpaceGroup spaceGroup;
    @Deprecated
    private transient SpaceManager spaceManager;

    public static boolean isValidSpaceKey(String key) {
        if (StringUtils.isEmpty((CharSequence)key)) {
            return false;
        }
        if (key.startsWith(PERSONAL_SPACEKEY_IDENTIFIER)) {
            return Space.isValidPersonalSpaceKey(key);
        }
        return Space.isValidGlobalSpaceKey(key);
    }

    public static boolean isValidGlobalSpaceKey(String key) {
        return StringUtils.isNotEmpty((CharSequence)key) && GeneralUtil.isAllAscii(key) && GeneralUtil.isAllLettersOrNumbers(key);
    }

    public static boolean isValidPersonalSpaceKey(String key) {
        return StringUtils.isNotEmpty((CharSequence)key) && key.indexOf(PERSONAL_SPACEKEY_IDENTIFIER) == 0;
    }

    public Space() {
    }

    public Space(String spaceKey) {
        this.setKey(spaceKey);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name != null && name.length() > 255 ? name.substring(0, 255) : name;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
        this.lowerKey = StringUtils.lowerCase((String)key);
    }

    public String getLowerKey() {
        return this.lowerKey;
    }

    private void setLowerKey(String lowerKey) {
        this.lowerKey = lowerKey;
    }

    public SpaceType getSpaceType() {
        return this.spaceType;
    }

    public void setSpaceType(SpaceType spaceType) {
        this.spaceType = spaceType;
    }

    public SpaceDescription getDescription() {
        return this.description;
    }

    public void setDescription(SpaceDescription description) {
        this.description = description;
    }

    public Page getHomePage() {
        return this.homePage;
    }

    public void setHomePage(Page homePage) {
        this.homePage = homePage;
    }

    public List<SpacePermission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<SpacePermission> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(SpacePermission permission) {
        this.getPermissions().add(permission);
        permission.setSpace(this);
    }

    public void removePermission(SpacePermission permission) {
        this.getPermissions().remove(permission);
        permission.setSpace(null);
    }

    public void removeAllPermissions() {
        for (SpacePermission spacePermission : this.permissions) {
            spacePermission.setSpace(null);
        }
        this.permissions.clear();
    }

    public List getPageTemplates() {
        return this.pageTemplates;
    }

    private void setPageTemplates(List pageTemplates) {
        this.pageTemplates = pageTemplates;
    }

    public void addPageTemplate(PageTemplate pageTemplate) {
        this.getPageTemplates().add(pageTemplate);
        pageTemplate.setSpace(this);
    }

    public void removePageTemplate(PageTemplate pageTemplate) {
        this.getPageTemplates().remove(pageTemplate);
        pageTemplate.setSpace(null);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass((Object)this) != Hibernate.getClass((Object)o)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Space space = (Space)o;
        if (this.getSpaceStatus() != null ? !this.getSpaceStatus().equals((Object)space.getSpaceStatus()) : space.getSpaceStatus() != null) {
            return false;
        }
        if (this.getSpaceType() != null ? !this.getSpaceType().equals(space.getSpaceType()) : space.getSpaceType() != null) {
            return false;
        }
        if (this.getKey() != null ? !this.getKey().equalsIgnoreCase(space.getKey()) : space.getKey() != null) {
            return false;
        }
        return !(this.getName() != null ? !this.getName().equals(space.getName()) : space.getName() != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.getSpaceType() != null ? this.getSpaceType().hashCode() : 0);
        result = 29 * result + (this.getSpaceStatus() != null ? this.getSpaceStatus().hashCode() : 0);
        result = 29 * result + (this.getName() != null ? this.getName().hashCode() : 0);
        result = 29 * result + (this.getKey() != null ? this.getKey().toLowerCase().hashCode() : 0);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        Space clone = (Space)super.clone();
        if (this.getDescription() != null) {
            clone.setDescription((SpaceDescription)this.getDescription().clone());
        }
        return clone;
    }

    public Collection getSearchableDependants() {
        return Collections.EMPTY_LIST;
    }

    public boolean isIndexable() {
        return true;
    }

    @Deprecated
    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public String getUrlPath() {
        return this.isPersonal() ? new SpacePathConverter().getPath(this.key).getPath() : "/display/" + HtmlUtil.urlEncode(this.key);
    }

    public URI getDeepLinkUri() {
        Page homePage = this.getHomePage();
        if (homePage != null) {
            return URI.create(homePage.getUrlPath());
        }
        return URI.create(this.getUrlPath());
    }

    public String getBrowseUrlPath() {
        return "/pages/listpages.action?key=" + HtmlUtil.urlEncode(this.key);
    }

    public String getAdvancedTabUrlPath() {
        return "/spaces/viewspacesummary.action?key=" + HtmlUtil.urlEncode(this.key);
    }

    public String getBlogTabUrlPath() {
        return "/pages/listpages.action?key=" + HtmlUtil.urlEncode(this.key);
    }

    @Override
    public String getDisplayTitle() {
        return this.name;
    }

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    public boolean isPersonal() {
        return SpaceType.isPersonal(this);
    }

    public boolean isGlobal() {
        return SpaceType.isGlobal(this);
    }

    @Deprecated
    public SpaceGroup getSpaceGroup() {
        return this.spaceGroup;
    }

    @Deprecated
    public void setSpaceGroup(SpaceGroup spaceGroup) {
        this.spaceGroup = spaceGroup;
    }

    public SpaceStatus getSpaceStatus() {
        return this.spaceStatus;
    }

    public void setSpaceStatus(SpaceStatus spaceStatus) {
        this.spaceStatus = spaceStatus;
    }

    public Message getDefaultHomepageTitle() {
        if (this.isPersonal()) {
            return Message.getInstance("default.personal.space.homepage.title", this.getDisplayTitle());
        }
        return Message.getInstance("default.space.homepage.title", this.getDisplayTitle());
    }

    public boolean isArchived() {
        return SpaceStatus.ARCHIVED.equals((Object)this.spaceStatus);
    }

    public String toString() {
        return "Space{key='" + this.key + "'}";
    }
}

