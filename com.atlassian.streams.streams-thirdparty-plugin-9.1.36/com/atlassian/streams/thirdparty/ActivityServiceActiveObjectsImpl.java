/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.streams.api.FeedContentSanitizer
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.UserProfile$Builder
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Options
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.spi.EntityIdentifier
 *  com.atlassian.streams.spi.StandardStreamsFilterOption
 *  com.atlassian.streams.spi.UserProfileAccessor
 *  com.atlassian.streams.spi.renderer.Renderers
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.thirdparty;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.api.FeedContentSanitizer;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.spi.EntityIdentifier;
import com.atlassian.streams.spi.StandardStreamsFilterOption;
import com.atlassian.streams.spi.UserProfileAccessor;
import com.atlassian.streams.spi.renderer.Renderers;
import com.atlassian.streams.thirdparty.ActivityServiceActiveObjects;
import com.atlassian.streams.thirdparty.EntityAssociationProviders;
import com.atlassian.streams.thirdparty.ao.ActivityEntity;
import com.atlassian.streams.thirdparty.ao.ActivityObjEntity;
import com.atlassian.streams.thirdparty.ao.ActorEntity;
import com.atlassian.streams.thirdparty.ao.MediaLinkEntity;
import com.atlassian.streams.thirdparty.ao.ObjectEntity;
import com.atlassian.streams.thirdparty.ao.TargetEntity;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityObject;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.Application;
import com.atlassian.streams.thirdparty.api.Image;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityServiceActiveObjectsImpl
implements ActivityServiceActiveObjects {
    public static final int MAX_TITLE_LENGTH = 255;
    public static final int MAX_CONTENT_LENGTH = 5000;
    public static final int MAX_STRING_LENGTH = 255;
    private static final Logger log = LoggerFactory.getLogger(ActivityServiceActiveObjectsImpl.class);
    private final ActiveObjects ao;
    private final EntityAssociationProviders entityAssociationProviders;
    private final FeedContentSanitizer sanitizer;
    private final UserManager userManager;
    private final UserProfileAccessor userProfileAccessor;
    private final ApplicationProperties applicationProperties;
    private Function<ActivityEntity, Option<Activity>> toActivity = new Function<ActivityEntity, Option<Activity>>(){

        public Option<Activity> apply(ActivityEntity entity) {
            if (entity == null || entity.getPublished() == null) {
                return Option.none();
            }
            try {
                Option entityIdentifier = Option.none();
                Option entityLinkText = Option.none();
                Option<URI> entityLinkUri = null;
                if (entity.getTarget() != null) {
                    URI objectType = entity.getTarget().getObjectType();
                    if (entity.getIssueKey() != null || entity.getProjectKey() != null) {
                        EntityIdentifier ei = new EntityIdentifier(objectType, entity.getIssueKey() != null ? entity.getIssueKey() : entity.getProjectKey(), entity.getTarget().getUrl());
                        if (!ActivityServiceActiveObjectsImpl.this.entityAssociationProviders.getCurrentUserViewPermission(ei)) {
                            return Option.none();
                        }
                        entityLinkUri = ActivityServiceActiveObjectsImpl.this.entityAssociationProviders.getEntityURI(ei);
                        if (entityLinkUri.isDefined() && !((URI)entityLinkUri.get()).equals(ei.getUri())) {
                            ei = new EntityIdentifier(ei.getType(), ei.getValue(), (URI)entityLinkUri.get());
                        }
                        entityIdentifier = Option.some((Object)ei);
                        entityLinkText = Option.some((Object)ei.getValue());
                    }
                } else if (!ActivityServiceActiveObjectsImpl.this.entityAssociationProviders.getCurrentUserViewPermissionOfTargetlessEntity()) {
                    return Option.none();
                }
                Application application = Application.application(entity.getGeneratorDisplayName(), entity.getGeneratorId());
                UserProfile userProfile = ActivityServiceActiveObjectsImpl.this.getUserProfile(URI.create(ActivityServiceActiveObjectsImpl.this.applicationProperties.getBaseUrl()), entity.getUsername(), entity.getActor());
                Option title = Option.option((Object)entity.getTitle()).map(Html.html());
                Option content = Option.option((Object)entity.getContent()).map(Html.html());
                Activity.Builder builder = Activity.builder(application, new DateTime((Object)entity.getPublished()), userProfile).activityId(entity.getActivityId()).id((Option<URI>)Option.option((Object)entity.getId())).url((Option<URI>)Option.option((Object)entity.getUrl())).poster((Option<String>)Option.option((Object)entity.getPoster())).registeredUser(entity.getUsername() != null).verb((Option<URI>)Option.option((Object)entity.getVerb()));
                if (entity.getIcon() != null) {
                    builder.icon(Image.builder(entity.getIcon().getUrl()).height((Option<Integer>)Option.option((Object)entity.getIcon().getHeight())).width((Option<Integer>)Option.option((Object)entity.getIcon().getWidth())).build());
                }
                if (entity.getObject() != null) {
                    builder.object(ActivityServiceActiveObjectsImpl.activityObjectBuilder(entity.getObject()).build());
                }
                if (entity.getTarget() != null) {
                    ActivityObject.Builder targetBuilder = ActivityServiceActiveObjectsImpl.activityObjectBuilder(entity.getTarget());
                    if (entityIdentifier.isDefined()) {
                        targetBuilder.url((Option<URI>)Option.some((Object)((EntityIdentifier)entityIdentifier.get()).getUri()));
                    }
                    builder.target(targetBuilder.build());
                }
                if (entityLinkText.isDefined() && entityLinkUri.isDefined()) {
                    Iterator addEntityLinks = Renderers.replaceTextWithHyperlink((String)((String)entityLinkText.get()), (URI)((URI)entityLinkUri.get()));
                    title = title.map((Function)addEntityLinks);
                    content = content.map((Function)addEntityLinks);
                }
                if (entity.getUsername() != null) {
                    for (URI profileUri : userProfile.getProfilePageUri()) {
                        Function addUserLinks = Renderers.replaceTextWithHyperlink((String)ActivityServiceActiveObjectsImpl.this.sanitizer.sanitize(userProfile.getFullName()), (URI)profileUri);
                        title = title.map(addUserLinks);
                        content = content.map(addUserLinks);
                    }
                }
                builder.title((Option<Html>)title).content((Option<Html>)content);
                Either<ValidationErrors, Activity> ret = builder.build();
                if (ret.isRight()) {
                    return ret.right().toOption();
                }
                log.warn("Ignoring invalid activity in database (id=" + entity.getActivityId() + "): " + ((ValidationErrors)ret.left().get()).toString());
            }
            catch (Exception e) {
                log.warn("Unexpected error when retrieving activity (id=" + entity.getActivityId() + "): ", (Throwable)e);
            }
            return Option.none();
        }
    };
    private static Function<ActivityEntity, Application> toApplication = new Function<ActivityEntity, Application>(){

        public Application apply(ActivityEntity entity) {
            return Application.application(entity.getGeneratorDisplayName(), entity.getGeneratorId());
        }
    };

    public ActivityServiceActiveObjectsImpl(ActiveObjects ao, EntityAssociationProviders entityAssociationProviders, FeedContentSanitizer sanitizer, UserManager userManager, UserProfileAccessor userProfileAccessor, ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.ao = (ActiveObjects)Preconditions.checkNotNull((Object)ao, (Object)"ao");
        this.entityAssociationProviders = (EntityAssociationProviders)Preconditions.checkNotNull((Object)entityAssociationProviders, (Object)"entityAssociationProviders");
        this.sanitizer = (FeedContentSanitizer)Preconditions.checkNotNull((Object)sanitizer, (Object)"sanitizer");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
        this.userProfileAccessor = (UserProfileAccessor)Preconditions.checkNotNull((Object)userProfileAccessor, (Object)"userProfileAccessor");
    }

    @Override
    public Activity postActivity(Activity activity) {
        ActivityEntity entity = (ActivityEntity)this.ao.create(ActivityEntity.class, new DBParam[0]);
        entity.setPublished(activity.getPostedDate().toDate());
        entity.setGeneratorId(activity.getApplication().getId());
        entity.setGeneratorDisplayName(activity.getApplication().getDisplayName());
        Option<String> registeredUsername = this.getRegisteredUsername(activity.getUser().getUsername());
        if (registeredUsername.isDefined()) {
            entity.setUsername((String)registeredUsername.get());
            entity.setActor(null);
        } else {
            entity.setUsername(null);
            entity.setActor(this.actorEntity(activity.getUser()));
        }
        if (this.userManager.getRemoteUsername() != null) {
            entity.setPoster(this.userManager.getRemoteUsername());
        }
        entity.setId((URI)activity.getId().getOrElse((Object)null));
        for (Html html : activity.getContent()) {
            entity.setContent(this.sanitizer.sanitize(html.toString()));
        }
        for (Image image : activity.getIcon()) {
            entity.setIcon(this.mediaLinkEntity(image));
        }
        for (Object object : activity.getObject()) {
            entity.setObject(this.objectEntity((ActivityObject)object, ObjectEntity.class));
        }
        Iterable<EntityIdentifier> entityIdentifiers = this.getEntityIdentifiers(activity);
        if (Iterables.isEmpty(entityIdentifiers)) {
            for (Object target : activity.getTarget()) {
                entity.setTarget(this.objectEntity((ActivityObject)target, TargetEntity.class));
            }
        } else {
            Object target;
            Object firstIdentifier = Option.none();
            for (EntityIdentifier entityIdentifier : entityIdentifiers) {
                if (!this.entityAssociationProviders.getCurrentUserViewPermission(entityIdentifier)) continue;
                Option<String> filterKey = this.entityAssociationProviders.getFilterKey(entityIdentifier);
                if (filterKey.equals((Object)Option.some((Object)"key"))) {
                    entity.setProjectKey(entityIdentifier.getValue());
                } else if (filterKey.equals((Object)Option.some((Object)StandardStreamsFilterOption.ISSUE_KEY.getKey()))) {
                    entity.setIssueKey(entityIdentifier.getValue());
                }
                if (firstIdentifier.isDefined()) continue;
                firstIdentifier = Option.some((Object)entityIdentifier);
            }
            target = firstIdentifier.iterator();
            while (target.hasNext()) {
                EntityIdentifier entityIdentifier;
                entityIdentifier = (EntityIdentifier)target.next();
                entity.setTarget(this.objectEntity(entityIdentifier, TargetEntity.class));
            }
        }
        for (Html html : activity.getTitle()) {
            entity.setTitle(this.sanitizer.sanitize(html.toString()));
        }
        entity.setUrl((URI)activity.getUrl().getOrElse((Object)null));
        entity.setVerb((URI)activity.getVerb().getOrElse((Object)null));
        entity.save();
        Option<Activity> newActivity = this.getActivity(entity.getActivityId());
        if (!newActivity.isDefined()) {
            throw new IllegalStateException("Newly posted activity could not be retrieved");
        }
        return (Activity)newActivity.get();
    }

    @Override
    public Option<Activity> getActivity(long activityId) {
        Iterator iterator = this.getEntity(activityId).iterator();
        if (iterator.hasNext()) {
            ActivityEntity entity = (ActivityEntity)iterator.next();
            return (Option)this.toActivity.apply((Object)entity);
        }
        return Option.none();
    }

    @Override
    public boolean delete(long activityId) {
        Iterator iterator = this.getEntity(activityId).iterator();
        if (iterator.hasNext()) {
            ActivityEntity entity = (ActivityEntity)iterator.next();
            this.delete(entity);
            return true;
        }
        return false;
    }

    @Override
    public Iterable<Activity> activities(ActivityQuery query) {
        Query aoQuery = Query.select();
        WhereClause where = this.whereClause(WhereClause.Separator.AND).add(query.getStartDate(), "published", WhereClause.Operator.GT_EQ).add(query.getEndDate(), "published", WhereClause.Operator.LT);
        if (!Iterables.isEmpty(query.getUserNames())) {
            WhereClause namesClause = this.whereClause(WhereClause.Separator.OR);
            for (String name : query.getUserNames()) {
                namesClause.add(Option.some((Object)name), "username", WhereClause.Operator.EQ);
            }
            where.add(namesClause);
        }
        if (!Iterables.isEmpty(query.getExcludeUserNames())) {
            WhereClause excludesClause = this.whereClause(WhereClause.Separator.AND);
            for (String excludeName : query.getExcludeUserNames()) {
                excludesClause.add(Option.some((Object)excludeName), "username", WhereClause.Operator.NEQ);
            }
            WhereClause excludesOrNull = this.whereClause(WhereClause.Separator.OR);
            excludesOrNull.add(excludesClause);
            excludesOrNull.addIsNull("username");
            where.add(excludesOrNull);
        }
        this.addEntityFiltersClause(where, query, "key", "projectKey");
        this.addEntityFiltersClause(where, query, StandardStreamsFilterOption.ISSUE_KEY.getKey(), "issueKey");
        if (!Iterables.isEmpty(query.getProviderKeys()) || !Iterables.isEmpty(query.getExcludeProviderKeys())) {
            if (!Iterables.isEmpty(query.getProviderKeys())) {
                WhereClause providersClause = this.whereClause(WhereClause.Separator.OR);
                for (String providerKey : query.getProviderKeys()) {
                    String[] parts = providerKey.split("@");
                    providersClause.add(this.whereClause(WhereClause.Separator.AND).add(Option.some((Object)parts[0]), "generatorId", WhereClause.Operator.EQ).add(Option.some((Object)parts[1]), "generatorDisplayName", WhereClause.Operator.EQ));
                }
                where.add(providersClause);
            }
            for (String excludeProviderKey : query.getExcludeProviderKeys()) {
                String[] parts = excludeProviderKey.split("@");
                where.add(this.whereClause(WhereClause.Separator.OR).add(Option.some((Object)parts[0]), "generatorId", WhereClause.Operator.NEQ).add(Option.some((Object)parts[1]), "generatorDisplayName", WhereClause.Operator.NEQ));
            }
        }
        if (!where.isEmpty()) {
            aoQuery.where(where.getClause(), where.getParams());
        }
        aoQuery.setLimit(query.getMaxResults());
        aoQuery.setOffset(query.getStartIndex());
        aoQuery.setOrderClause(ActivityServiceActiveObjectsImpl.getColumnName("published") + " desc");
        ImmutableList entities = ImmutableList.copyOf((Object[])this.ao.find(ActivityEntity.class, aoQuery));
        return Options.catOptions((Iterable)Iterables.transform((Iterable)entities, this.toActivity));
    }

    @Override
    public Iterable<Application> applications() {
        Query query = Query.select((String)"GENERATOR_ID, GENERATOR_DISPLAY_NAME").group("GENERATOR_ID, GENERATOR_DISPLAY_NAME").order("GENERATOR_ID DESC");
        LinkedList<Application> applications = new LinkedList<Application>();
        this.ao.stream(ActivityEntity.class, query, app -> applications.add((Application)toApplication.apply((Object)app)));
        return applications;
    }

    private Iterable<EntityIdentifier> getEntityIdentifiers(Activity activity) {
        for (ActivityObject target : activity.getTarget()) {
            Iterator iterator = target.getUrl().iterator();
            if (!iterator.hasNext()) continue;
            URI targetUri = (URI)iterator.next();
            return this.entityAssociationProviders.getEntityAssociations(targetUri);
        }
        return ImmutableList.of();
    }

    private void addEntityFiltersClause(WhereClause where, ActivityQuery query, String filterKey, String propertyName) {
        WhereClause matchClause = this.whereClause(WhereClause.Separator.OR);
        for (Pair<String, String> pair : query.getEntityFilters()) {
            if (!((String)pair.first()).equals(filterKey)) continue;
            matchClause.add(Option.some((Object)pair.second()), propertyName, WhereClause.Operator.EQ);
        }
        if (!matchClause.isEmpty()) {
            where.add(matchClause);
        }
        WhereClause excludeClause = this.whereClause(WhereClause.Separator.AND);
        for (Pair<String, String> pair : query.getExcludeEntityFilters()) {
            if (!((String)pair.first()).equals(filterKey)) continue;
            excludeClause.add(Option.some((Object)pair.second()), propertyName, WhereClause.Operator.NEQ);
        }
        if (!excludeClause.isEmpty()) {
            WhereClause whereClause = this.whereClause(WhereClause.Separator.OR);
            whereClause.add(excludeClause);
            whereClause.addIsNull(propertyName);
            where.add(whereClause);
        }
    }

    private Option<ActivityEntity> getEntity(long activityId) {
        return Option.option((Object)this.ao.get(ActivityEntity.class, (Object)activityId));
    }

    private void delete(ActivityEntity entity) {
        if (entity != null) {
            TargetEntity target = entity.getTarget();
            ActorEntity actor = entity.getActor();
            ObjectEntity object = entity.getObject();
            MediaLinkEntity icon = entity.getIcon();
            this.ao.delete(new RawEntity[]{entity});
            this.delete(target);
            this.delete(actor);
            this.delete(object);
            this.delete(icon);
        }
    }

    private <T extends ActivityObjEntity> void delete(T entity) {
        if (entity != null) {
            this.ao.delete(new RawEntity[]{entity});
            this.delete(entity.getImage());
        }
    }

    private void delete(ActorEntity entity) {
        if (entity != null) {
            this.ao.delete(new RawEntity[]{entity});
        }
    }

    private void delete(MediaLinkEntity entity) {
        if (entity != null) {
            this.ao.delete(new RawEntity[]{entity});
        }
    }

    private ActorEntity actorEntity(UserProfile user) {
        ActorEntity entity = (ActorEntity)this.ao.create(ActorEntity.class, new DBParam[0]);
        entity.setUsername(user.getUsername());
        entity.setFullName(user.getFullName());
        for (URI profileUri : user.getProfilePageUri()) {
            entity.setProfilePageUri(profileUri);
        }
        for (URI pictureUri : user.getProfilePictureUri()) {
            entity.setProfilePictureUri(pictureUri);
        }
        entity.save();
        return entity;
    }

    private MediaLinkEntity mediaLinkEntity(Image image) {
        MediaLinkEntity entity = (MediaLinkEntity)this.ao.create(MediaLinkEntity.class, new DBParam[0]);
        entity.setUrl(image.getUrl());
        entity.setHeight((Integer)image.getHeight().getOrElse((Object)null));
        entity.setWidth((Integer)image.getWidth().getOrElse((Object)null));
        entity.save();
        return entity;
    }

    private <T extends ActivityObjEntity> T objectEntity(ActivityObject activityObject, Class<T> entityClass) {
        ActivityObjEntity entity = (ActivityObjEntity)this.ao.create(entityClass, new DBParam[0]);
        for (URI id : activityObject.getId()) {
            entity.setObjectId(id);
        }
        for (String s : activityObject.getDisplayName()) {
            entity.setDisplayName(this.stripHtml(s));
        }
        for (URI type : activityObject.getType()) {
            entity.setObjectType(type);
        }
        for (Html html : activityObject.getSummary()) {
            entity.setSummary(this.sanitizer.sanitize(html.toString()));
        }
        for (URI u : activityObject.getUrl()) {
            entity.setUrl(u);
        }
        entity.save();
        return (T)entity;
    }

    private <T extends ActivityObjEntity> T objectEntity(EntityIdentifier entityIdentifier, Class<T> entityClass) {
        ActivityObjEntity entity = (ActivityObjEntity)this.ao.create(entityClass, new DBParam[0]);
        entity.setDisplayName(entityIdentifier.getValue());
        entity.setObjectType(entityIdentifier.getType());
        entity.setUrl(entityIdentifier.getUri());
        entity.save();
        return (T)entity;
    }

    private UserProfile getUserProfile(URI baseUri, String username, ActorEntity actor) {
        UserProfile userProfile;
        if (username != null && (userProfile = this.userProfileAccessor.getUserProfile(baseUri, username)) != null) {
            return userProfile;
        }
        UserProfile defaultProfile = this.userProfileAccessor.getAnonymousUserProfile(baseUri);
        if (actor == null) {
            return defaultProfile;
        }
        UserProfile.Builder builder = new UserProfile.Builder(actor.getUsername().equals("") ? defaultProfile.getUsername() : actor.getUsername());
        builder.fullName(actor.getFullName() == null || actor.getFullName().equals("") ? defaultProfile.getFullName() : actor.getFullName());
        if (actor.getProfilePictureUri() != null) {
            builder.profilePictureUri(Option.some((Object)actor.getProfilePictureUri()));
        } else {
            builder.profilePictureUri(defaultProfile.getProfilePictureUri());
        }
        return builder.build();
    }

    private static ActivityObject.Builder activityObjectBuilder(ActivityObjEntity entity) {
        return ActivityObject.builder().displayName((Option<String>)Option.option((Object)entity.getDisplayName())).id((Option<URI>)Option.option((Object)entity.getObjectId())).type((Option<URI>)Option.option((Object)entity.getObjectType())).summary((Option<Html>)Option.option((Object)entity.getSummary()).map(Html.html())).url((Option<URI>)Option.option((Object)entity.getUrl()));
    }

    private Option<String> getRegisteredUsername(String username) {
        return this.userManager.getUserProfile(username) == null ? Option.none(String.class) : Option.some((Object)username);
    }

    private String stripHtml(String input) {
        return input.replaceAll("<[^>]*>", "");
    }

    private static String getColumnName(String property) {
        StringBuilder ret = new StringBuilder(property.length() * 2);
        for (int i = 0; i < property.length(); ++i) {
            char ch = property.charAt(i);
            if (Character.isUpperCase(ch)) {
                ret.append('_');
                ret.append(Character.toLowerCase(ch));
                continue;
            }
            ret.append(ch);
        }
        return ret.toString().toUpperCase();
    }

    private WhereClause whereClause(WhereClause.Separator separator) {
        return new WhereClause(separator);
    }

    static class WhereClause {
        private final StringBuilder where = new StringBuilder();
        private final ImmutableList.Builder<Object> list = ImmutableList.builder();
        private final Separator separator;

        WhereClause(Separator separator) {
            this.separator = separator;
        }

        boolean isEmpty() {
            return this.where.length() == 0;
        }

        WhereClause add(Option<?> option, String property, Operator operator) {
            if (option.isDefined()) {
                String column = ActivityServiceActiveObjectsImpl.getColumnName(property);
                if (this.where.length() > 0) {
                    this.where.append(this.separator.value());
                }
                this.where.append(column).append(" ").append(operator.value()).append(" ?");
                this.list.add(option.get());
            }
            return this;
        }

        WhereClause addIsNull(String property) {
            String column = ActivityServiceActiveObjectsImpl.getColumnName(property);
            if (this.where.length() > 0) {
                this.where.append(this.separator.value());
            }
            this.where.append(column).append(" IS NULL");
            return this;
        }

        WhereClause add(WhereClause subClause) {
            if (subClause.where.length() > 0) {
                if (this.where.length() > 0) {
                    this.where.append(this.separator.value());
                }
                this.where.append("(");
                this.where.append(subClause.getClause());
                this.where.append(")");
                this.list.addAll((Iterable)subClause.list.build());
            }
            return this;
        }

        String getClause() {
            return this.where.toString();
        }

        Object[] getParams() {
            return this.list.build().toArray();
        }

        static enum Operator {
            EQ("="),
            NEQ("!="),
            GT(">"),
            GT_EQ(">="),
            LT("<"),
            LT_EQ("<=");

            private final String value;

            private Operator(String value) {
                this.value = value;
            }

            String value() {
                return this.value;
            }
        }

        static enum Separator {
            AND(" and "),
            OR(" or ");

            private final String value;

            private Separator(String value) {
                this.value = value;
            }

            String value() {
                return this.value;
            }
        }
    }
}

