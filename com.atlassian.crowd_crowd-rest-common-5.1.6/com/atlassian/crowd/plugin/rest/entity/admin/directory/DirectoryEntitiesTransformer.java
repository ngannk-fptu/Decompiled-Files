/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.plugin.rest.entity.admin.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryData;
import com.atlassian.crowd.plugin.rest.entity.admin.user.UserData;
import com.atlassian.crowd.plugin.rest.entity.directory.GroupAdministrationMappingRestDTO;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectoryEntitiesTransformer {
    private final Function<Long, DirectoryData> directoryFinder;

    private DirectoryEntitiesTransformer(Function<Long, DirectoryData> directoryFinder) {
        this.directoryFinder = directoryFinder;
    }

    private <F, T> T transform(F entity, Function<F, Long> directoryIdGetter, BiFunction<F, DirectoryData, T> transformer) {
        DirectoryData directory = this.directoryFinder.apply(directoryIdGetter.apply(entity));
        return transformer.apply(entity, directory);
    }

    public List<UserData> toUserData(List<? extends User> users) {
        return users.stream().map(this::toUserData).collect(Collectors.toList());
    }

    public UserData toUserData(User user) {
        return this.transform(user, User::getDirectoryId, UserData::fromUser);
    }

    public UserData toUserDataWithAvatar(User user, String avatarUrl) {
        DirectoryData directoryData = this.directoryFinder.apply(user.getDirectoryId());
        return UserData.fromUserWithAvatarUrl(user, directoryData, avatarUrl);
    }

    public List<GroupAdministrationMappingRestDTO> fromUserToGroupAdministrationMappingRestDTO(List<? extends User> users) {
        return users.stream().map(this::toGroupAdministrationMappingRestDTO).collect(Collectors.toList());
    }

    public GroupAdministrationMappingRestDTO toGroupAdministrationMappingRestDTO(User user) {
        return this.transform(user, User::getDirectoryId, GroupAdministrationMappingRestDTO::fromUser);
    }

    public List<GroupAdministrationMappingRestDTO> fromGroupToGroupAdministrationMappingRestDTO(List<Group> users) {
        return users.stream().map(this::toGroupAdministrationMappingRestDTO).collect(Collectors.toList());
    }

    public GroupAdministrationMappingRestDTO toGroupAdministrationMappingRestDTO(Group group) {
        return this.transform(group, DirectoryEntity::getDirectoryId, GroupAdministrationMappingRestDTO::fromGroup);
    }

    public static DirectoryEntitiesTransformer withDirectoryCaching(DirectoryFinder directoryFinder) {
        Function<Long, DirectoryData> directoryDataFinder = DirectoryEntitiesTransformer.toDirectoryDataFunction(directoryFinder);
        HashMap directoryDataCache = new HashMap();
        return new DirectoryEntitiesTransformer(id -> (DirectoryData)directoryDataCache.computeIfAbsent(id, directoryDataFinder));
    }

    private static Function<Long, DirectoryData> toDirectoryDataFunction(DirectoryFinder finder) {
        return id -> {
            try {
                return DirectoryData.fromDirectory(finder.findById((long)id));
            }
            catch (DirectoryNotFoundException e) {
                throw new IllegalArgumentException();
            }
        };
    }

    @FunctionalInterface
    public static interface DirectoryFinder {
        public Directory findById(long var1) throws DirectoryNotFoundException;
    }
}

