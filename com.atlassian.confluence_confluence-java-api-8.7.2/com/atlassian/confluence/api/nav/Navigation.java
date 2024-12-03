/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.api.nav;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.sal.api.user.UserKey;

@ExperimentalApi
public interface Navigation {
    public ExperimentalNav experimental();

    public ContentNav content(Content var1);

    public ContentNav content(Reference<Content> var1);

    @Deprecated
    public ContentNav content(ContentId var1);

    public ContentNav content(ContentSelector var1);

    public SpaceNav space(Reference<Space> var1);

    public UserNav user(UserKey var1);

    public Builder collection(Content var1);

    public Builder collection(Space var1);

    public SpaceNav space(Space var1);

    public LongTaskNav longTask(LongTaskId var1);

    public GroupNav group(Group var1);

    public String contextPath();

    public String baseUrl();

    public Builder baseApiPath();

    public Builder fromReference(Reference<?> var1);

    @ExperimentalApi
    public static interface ExperimentalNav
    extends Builder {
        public ExperimentalContentNav content(Content var1);

        public ExperimentalContentNav content(Reference<Content> var1);

        @Deprecated
        public ExperimentalContentNav content(ContentId var1);

        public ExperimentalContentNav content(ContentSelector var1);

        public UserNav user(UserKey var1);

        public Builder group(Group var1);

        public ExperimentalContentTemplateNav template(ContentTemplate var1);
    }

    @ExperimentalApi
    public static interface ExperimentalSynchronyDataNav
    extends Builder {
        public static final String BASE_PATH = "/collab/history";
    }

    public static interface GroupNav
    extends Builder {
        public static final String GROUP_PATH = "/group";
    }

    public static interface LongTaskNav
    extends Builder {
        public static final String RESOURCE_BASE = "/longtask";
    }

    public static interface ContentRestrictionByOperationNav
    extends Builder {
        public static final String BY_OPERATION_SUBPATH = "/byOperation";

        public Builder operation(OperationKey var1);
    }

    public static interface SpaceContentNav
    extends Builder {
        public Builder type(ContentType var1);
    }

    public static interface UserNav
    extends Builder {
        public static final String USER_PATH = "/user";
        public static final String CURRENT_USER_SUBPATH = "/current";
        public static final String ANONYMOUS_USER_SUBPATH = "/anonymous";
        public static final String MEMBER_OF_PATH = "/memberof";
        public static final String USER_WATCH_SUBPATH = "/watch";
        public static final String USER_WATCH_SPACE_SUBPATH = "/space";
        public static final String USER_WATCH_CONTENT_SUBPATH = "/content";
        public static final String PASSWORD = "/password";
        public static final String USER_LIST_PATH = "/list";

        public Builder memberOf();
    }

    public static interface SpaceNav
    extends Builder {
        public static final String SPACE_PATH = "/space";
        public static final String SPACE_CONTENT_SUBPATH = "/content";
        public static final String SPACE_PROPERTY_SUBPATH = "/property";

        public SpaceContentNav content();

        public Builder property(JsonSpaceProperty var1);
    }

    @ExperimentalApi
    public static interface ExperimentalContentTemplateNav
    extends Builder {
        public static final String TEMPLATE_PATH = "/template";
    }

    @ExperimentalApi
    public static interface ExperimentalContentNav
    extends Builder {
        public Builder restrictions();

        public Builder version(Version var1);
    }

    public static interface ContentNav
    extends Builder {
        public static final String CONTENT_PATH = "/content";
        public static final String CONTENT_LABEL_SUBPATH = "/label";
        public static final String CONTENT_HISTORY_SUBPATH = "/history";
        public static final String CONTENT_CHILDREN_SUBPATH = "/child";
        public static final String CONTENT_DESCENDANT_SUBPATH = "/descendant";
        public static final String CONTENT_PROPERTY_SUBPATH = "/property";
        public static final String RESTRICTION_SUBPATH = "/restriction";
        public static final String CONTENT_VERSION_SUBPATH = "/version";

        public Builder history();

        public Builder label();

        public Builder children(Depth var1);

        public Builder children(ContentType var1, Depth var2);

        public Builder property(JsonContentProperty var1);

        public Builder properties();

        public ContentRestrictionByOperationNav restrictionByOperation();
    }

    public static interface Builder {
        public static final Builder NONE = null;

        public String buildAbsolute();

        public String buildCanonicalAbsolute();

        public String buildRelative();

        public String buildRelativeWithContext();
    }
}

