/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner
 *  com.atlassian.plugins.projectcreate.spi.AggregateRoot
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootSubType
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability
 *  com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.apache.commons.lang3.RandomUtils
 *  org.junit.After
 *  org.junit.Assert
 *  org.junit.Before
 *  org.junit.Test
 *  org.junit.runner.RunWith
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package it.com.atlassian.plugins.projectcreate.spi;

import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.atlassian.plugins.projectcreate.spi.AggregateRoot;
import com.atlassian.plugins.projectcreate.spi.AggregateRootSubType;
import com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability;
import com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(value=AtlassianPluginsTestRunner.class)
public class BaseAggregateRootTypeCapabilityTest {
    private static final Logger log = LoggerFactory.getLogger(BaseAggregateRootTypeCapabilityTest.class);
    private ServiceTracker serviceTracker = null;
    private final BundleContext bundleContext;
    private final TransactionTemplate transactionTemplate;
    private final UserManager userManager;
    public static final Function<AggregateRoot, String> AGGREGATE_ROOT_TO_KEY_MAPPER = input -> input.key();

    public BaseAggregateRootTypeCapabilityTest(BundleContext bundleContext, final TransactionTemplate realTransactionTemplate, UserManager userManager) {
        this.bundleContext = bundleContext;
        this.transactionTemplate = new TransactionTemplate(){

            public <T> T execute(TransactionCallback<T> action) {
                return BaseAggregateRootTypeCapabilityTest.this.aroundTransaction(new Function<TransactionCallback<? extends T>, T>(){

                    public T apply(TransactionCallback<? extends T> input) {
                        return realTransactionTemplate.execute(input);
                    }
                }, action);
            }
        };
        this.userManager = userManager;
    }

    protected <T> T aroundTransaction(Function<TransactionCallback<? extends T>, T> transaction, TransactionCallback<T> action) {
        return (T)transaction.apply(action);
    }

    @Before
    public void setupData() {
        this.serviceTracker = new ServiceTracker(this.bundleContext, AggregateRootTypeCapability.class.getName(), null);
        this.serviceTracker.open();
        log.info("Service tracker created and opened for " + AggregateRootTypeCapability.class.getName());
    }

    @After
    public void removeData() {
        this.serviceTracker.close();
        log.info("Service tracker closed for " + AggregateRootTypeCapability.class.getName());
    }

    protected AggregateRootTypeCapability getUniqueService() {
        Object[] services = this.serviceTracker.getServices();
        if (services == null || services.length < 1) {
            throw new IllegalStateException("No AggregateRootTypeCapability service is found.");
        }
        if (services.length > 1) {
            throw new IllegalStateException("More than one AggregateRootTypeCapability services is found.");
        }
        return (AggregateRootTypeCapability)services[0];
    }

    protected AggregateRootTypeCapability getServiceByType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type used to look up AggregateRootTypeCapability is null.");
        }
        Object[] services = this.serviceTracker.getServices();
        if (services != null) {
            for (Object service : services) {
                AggregateRootTypeCapability capability = (AggregateRootTypeCapability)service;
                if (!type.equals(capability.getType())) continue;
                return capability;
            }
        }
        return null;
    }

    protected AggregateRootTypeCapability getServiceUnderTest() {
        return this.getUniqueService();
    }

    private void assertNotNullOrEmpty(String name, String value) {
        Assert.assertNotNull((String)(name + " is null."), (Object)value);
        Assert.assertFalse((String)(name + " is empty string."), (boolean)"".equals(value.trim()));
    }

    @Test
    public final void typeNotNullOrEmpty() {
        this.assertNotNullOrEmpty("Type", this.getServiceUnderTest().getType());
    }

    @Test
    public final void labelI18nKeyNotNullOrEmpty() {
        this.assertNotNullOrEmpty("LabelI18nKey", this.getServiceUnderTest().getLabelI18nKey());
    }

    @Test
    public final void descriptionI18nKeyNotNullOrEmpty() {
        this.assertNotNullOrEmpty("DescriptionI18nKey", this.getServiceUnderTest().getDescriptionI18nKey());
    }

    private String defaulted(String message) {
        return message + " If that's not the case, extend this class and override this test.";
    }

    @Test
    public void availability() {
        Assert.assertTrue((String)this.defaulted("Should always be available."), (boolean)this.getServiceUnderTest().isAvailable());
    }

    @Test
    public void subTypes() {
        Assert.assertFalse((String)this.defaulted("There should be no sub-types."), (boolean)this.getServiceUnderTest().getSubTypes().iterator().hasNext());
    }

    protected String adminUsername() {
        return "admin";
    }

    protected UserProfile adminUser() {
        String adminUsername = this.adminUsername();
        UserProfile adminUserProfile = this.userManager.getUserProfile(adminUsername);
        Assert.assertNotNull((String)("Admin user [" + adminUsername + "] does not exist "), (Object)adminUserProfile);
        return adminUserProfile;
    }

    private String generateKey(int length, Iterable<String> excludes) {
        Preconditions.checkArgument((length > 1 ? 1 : 0) != 0, (Object)"Key length must be at least 2 characters long.");
        HashSet exclusionSet = Sets.newHashSet(excludes);
        for (int i = 0; i < 3; ++i) {
            String key = RandomStringUtils.randomAlphabetic((int)length).toUpperCase();
            if (exclusionSet.contains(key)) continue;
            return key;
        }
        throw new IllegalStateException("Unable to generate a non-existing key after multiple attempts.");
    }

    protected Option<String> subTypeForCreation() {
        ImmutableList subTypes = ImmutableList.copyOf((Iterable)this.getServiceUnderTest().getSubTypes());
        return io.atlassian.fugue.Iterables.findFirst((Iterable)subTypes, (Predicate)new com.google.common.base.Predicate<AggregateRootSubType>(){

            public boolean apply(AggregateRootSubType subType) {
                return subType.isDefault();
            }
        }).orElse(io.atlassian.fugue.Iterables.first((Iterable)subTypes)).map((java.util.function.Function)new Function<AggregateRootSubType, String>(){

            public String apply(AggregateRootSubType subType) {
                return subType.getKey();
            }
        });
    }

    protected int descriptionMaxLength() {
        return 80;
    }

    protected int descriptionMinLength() {
        return 2;
    }

    protected String generateDescription(String key) {
        int min;
        int max = this.descriptionMaxLength() - 1 - key.length();
        Preconditions.checkArgument((max > (min = this.descriptionMinLength()) ? 1 : 0) != 0, (Object)"Maximum and minimum length of a root description do not agree.");
        return key + " " + RandomStringUtils.randomAlphanumeric((int)(RandomUtils.nextInt((int)0, (int)(max - min)) + min));
    }

    @Test
    public final void adminCanCreateRoot() {
        UserProfile adminUser = this.adminUser();
        Assert.assertTrue((String)("Admin user [" + adminUser.getUsername() + "] cannot create root."), (boolean)this.getServiceUnderTest().canUserCreateRoot(adminUser.getUsername()));
    }

    protected void login(UserProfile user) {
    }

    protected void logout() {
    }

    protected <R> R executeAsUser(UserProfile user, Callable<R> toExecute) {
        this.login(user);
        try {
            R r = toExecute.call();
            return r;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            this.logout();
        }
    }

    protected Iterable<AggregateRoot> getExistingRootsInTransaction() {
        return (Iterable)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Iterable<AggregateRoot>>(){

            public Iterable<AggregateRoot> doInTransaction() {
                return BaseAggregateRootTypeCapabilityTest.this.getServiceUnderTest().getExistingRoots();
            }
        });
    }

    @Test
    public void createRetrieveDeleteAsAdmin() {
        final UserProfile adminUser = this.adminUser();
        this.executeAsUser(adminUser, new Callable<Void>(){

            @Override
            public Void call() {
                ArrayList originalRoots = Lists.newArrayList(BaseAggregateRootTypeCapabilityTest.this.getExistingRootsInTransaction());
                AggregateRoot newRoot = BaseAggregateRootTypeCapabilityTest.this.testCreateRootHappily(adminUser, Lists.transform((List)originalRoots, AGGREGATE_ROOT_TO_KEY_MAPPER));
                BaseAggregateRootTypeCapabilityTest.this.deleteNewRoot(adminUser, newRoot);
                return null;
            }
        });
    }

    protected Either<ResponseStatusWithMessage, AggregateRoot> createRootInTransaction(final String maybeUsername, final String key, final String name, final Option<String> subtypeKey) {
        return (Either)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Either<ResponseStatusWithMessage, AggregateRoot>>(){

            public Either<ResponseStatusWithMessage, AggregateRoot> doInTransaction() {
                return BaseAggregateRootTypeCapabilityTest.this.getServiceUnderTest().createRoot(maybeUsername, key, name, subtypeKey, new HashMap());
            }
        });
    }

    private AggregateRoot testCreateRootHappily(UserProfile adminUser, List<String> originalRootKeys) {
        final String newRootKey = this.generateKey(6, originalRootKeys);
        final String newRootDescription = this.generateDescription(newRootKey);
        AggregateRoot createdRoot = (AggregateRoot)this.createRootInTransaction(adminUser.getUsername(), newRootKey, newRootDescription, this.subTypeForCreation()).fold((java.util.function.Function)new Function<ResponseStatusWithMessage, AggregateRoot>(){

            public AggregateRoot apply(ResponseStatusWithMessage statusWithMessage) {
                Assert.fail((String)("Failed to create new root with key [" + newRootKey + "] and descripotion [" + newRootDescription + "]. Response status: " + statusWithMessage.status() + ". Response message key: " + statusWithMessage.messageI18nKey()));
                return null;
            }
        }, (java.util.function.Function)new Function<AggregateRoot, AggregateRoot>(){

            public AggregateRoot apply(AggregateRoot createdRoot) {
                Assert.assertEquals((String)"Key is different.", (Object)newRootKey, (Object)createdRoot.key());
                Assert.assertEquals((String)"Description is different.", (Object)newRootDescription, (Object)createdRoot.label());
                Assert.assertNotNull((String)"Home URI should not be null.", (Object)createdRoot.homeUri());
                return createdRoot;
            }
        });
        this.verifyRootsIncludeNewOne(originalRootKeys, createdRoot);
        return createdRoot;
    }

    private void verifyRootsIncludeNewOne(List<String> originalRootKeys, AggregateRoot newRoot) {
        ImmutableSet expectedRootKeys = ImmutableSet.builder().addAll(originalRootKeys).add((Object)newRoot.key()).build();
        Assert.assertEquals((String)"Duplicate aggregate root exists.", (long)(originalRootKeys.size() + 1), (long)expectedRootKeys.size());
        ImmutableSet actualRootKeys = ImmutableSet.copyOf((Iterable)Iterables.transform(this.getExistingRootsInTransaction(), AGGREGATE_ROOT_TO_KEY_MAPPER));
        Assert.assertEquals((String)"Roots retrieved should include the newly created root.", (Object)expectedRootKeys, (Object)actualRootKeys);
    }

    protected Either<ResponseStatusWithMessage, ResponseStatusWithMessage> deleteRootInTransaction(final String maybeRemoteUsername, final String entityKey) {
        return (Either)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Either<ResponseStatusWithMessage, ResponseStatusWithMessage>>(){

            public Either<ResponseStatusWithMessage, ResponseStatusWithMessage> doInTransaction() {
                return BaseAggregateRootTypeCapabilityTest.this.getServiceUnderTest().deleteRoot(maybeRemoteUsername, entityKey);
            }
        });
    }

    private void deleteNewRoot(UserProfile adminUser, final AggregateRoot newRoot) {
        this.deleteRootInTransaction(adminUser.getUsername(), newRoot.key()).fold((java.util.function.Function)new Function<ResponseStatusWithMessage, Void>(){

            public Void apply(ResponseStatusWithMessage statusWithMessage) {
                Assert.fail((String)("Failed to delete newly root with key [" + newRoot.key() + "]. Response status: " + statusWithMessage.status() + ". Response message key: " + statusWithMessage.messageI18nKey()));
                return null;
            }
        }, (java.util.function.Function)new Function<ResponseStatusWithMessage, Void>(){

            public Void apply(ResponseStatusWithMessage input) {
                BaseAggregateRootTypeCapabilityTest.this.assertSuccessStatus(input.status());
                return null;
            }
        });
    }

    protected final void assertSuccessStatus(Response.Status status) {
        Assert.assertNotNull((String)"Response status is null.", (Object)status);
        Assert.assertTrue((String)("Response status is not a success. Status code: " + status.getStatusCode()), (status.getStatusCode() < 400 ? 1 : 0) != 0);
    }
}

