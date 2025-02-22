/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.nio.Address;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

final class MemberMap {
    static final int SINGLETON_MEMBER_LIST_VERSION = 1;
    private final int version;
    private final Map<Address, MemberImpl> addressToMemberMap;
    private final Map<String, MemberImpl> uuidToMemberMap;
    private final Set<MemberImpl> members;

    MemberMap(int version, Map<Address, MemberImpl> addressMap, Map<String, MemberImpl> uuidMap) {
        this.version = version;
        assert (new HashSet<MemberImpl>(addressMap.values()).equals(new HashSet<MemberImpl>(uuidMap.values()))) : "Maps are different! AddressMap: " + addressMap + ", UuidMap: " + uuidMap;
        this.addressToMemberMap = addressMap;
        this.uuidToMemberMap = uuidMap;
        this.members = Collections.unmodifiableSet(new LinkedHashSet<MemberImpl>(this.addressToMemberMap.values()));
    }

    static MemberMap empty() {
        return new MemberMap(0, Collections.emptyMap(), Collections.emptyMap());
    }

    static MemberMap singleton(MemberImpl member) {
        return new MemberMap(1, Collections.singletonMap(member.getAddress(), member), Collections.singletonMap(member.getUuid(), member));
    }

    static MemberMap createNew(MemberImpl ... members) {
        return MemberMap.createNew(0, members);
    }

    static MemberMap createNew(int version, MemberImpl ... members) {
        Map<Address, MemberImpl> addressMap = MapUtil.createLinkedHashMap(members.length);
        Map<String, MemberImpl> uuidMap = MapUtil.createLinkedHashMap(members.length);
        for (MemberImpl member : members) {
            MemberMap.putMember(addressMap, uuidMap, member);
        }
        return new MemberMap(version, addressMap, uuidMap);
    }

    static MemberMap cloneExcluding(MemberMap source, MemberImpl ... excludeMembers) {
        if (source.size() == 0) {
            return source;
        }
        LinkedHashMap<Address, MemberImpl> addressMap = new LinkedHashMap<Address, MemberImpl>(source.addressToMemberMap);
        LinkedHashMap<String, MemberImpl> uuidMap = new LinkedHashMap<String, MemberImpl>(source.uuidToMemberMap);
        for (MemberImpl member : excludeMembers) {
            MemberImpl removed = (MemberImpl)addressMap.remove(member.getAddress());
            if (removed != null) {
                uuidMap.remove(removed.getUuid());
            }
            if ((removed = (MemberImpl)uuidMap.remove(member.getUuid())) == null) continue;
            addressMap.remove(removed.getAddress());
        }
        return new MemberMap(source.version + excludeMembers.length, addressMap, uuidMap);
    }

    static MemberMap cloneAdding(MemberMap source, MemberImpl ... newMembers) {
        LinkedHashMap<Address, MemberImpl> addressMap = new LinkedHashMap<Address, MemberImpl>(source.addressToMemberMap);
        LinkedHashMap<String, MemberImpl> uuidMap = new LinkedHashMap<String, MemberImpl>(source.uuidToMemberMap);
        for (MemberImpl member : newMembers) {
            MemberMap.putMember(addressMap, uuidMap, member);
        }
        return new MemberMap(source.version + newMembers.length, addressMap, uuidMap);
    }

    private static void putMember(Map<Address, MemberImpl> addressMap, Map<String, MemberImpl> uuidMap, MemberImpl member) {
        MemberImpl current = addressMap.put(member.getAddress(), member);
        if (current != null) {
            throw new IllegalArgumentException("Replacing existing member with address: " + member);
        }
        current = uuidMap.put(member.getUuid(), member);
        if (current != null) {
            throw new IllegalArgumentException("Replacing existing member with UUID: " + member);
        }
    }

    MemberImpl getMember(Address address) {
        return this.addressToMemberMap.get(address);
    }

    MemberImpl getMember(String uuid) {
        return this.uuidToMemberMap.get(uuid);
    }

    MemberImpl getMember(Address address, String uuid) {
        MemberImpl member1 = this.addressToMemberMap.get(address);
        MemberImpl member2 = this.uuidToMemberMap.get(uuid);
        if (member1 != null && member1.equals(member2)) {
            return member1;
        }
        return null;
    }

    boolean contains(Address address) {
        return this.addressToMemberMap.containsKey(address);
    }

    boolean contains(String uuid) {
        return this.uuidToMemberMap.containsKey(uuid);
    }

    Set<MemberImpl> getMembers() {
        return this.members;
    }

    Collection<Address> getAddresses() {
        return Collections.unmodifiableCollection(this.addressToMemberMap.keySet());
    }

    int size() {
        return this.members.size();
    }

    int getVersion() {
        return this.version;
    }

    MembersView toMembersView() {
        return MembersView.createNew(this.version, this.members);
    }

    MembersView toTailMembersView(MemberImpl member, boolean inclusive) {
        return MembersView.createNew(this.version, this.tailMemberSet(member, inclusive));
    }

    Set<MemberImpl> tailMemberSet(MemberImpl member, boolean inclusive) {
        this.ensureMemberExist(member);
        LinkedHashSet<MemberImpl> result = new LinkedHashSet<MemberImpl>();
        boolean found = false;
        for (MemberImpl m : this.members) {
            if (!found && m.equals(member)) {
                found = true;
                if (!inclusive) continue;
                result.add(m);
                continue;
            }
            if (!found) continue;
            result.add(m);
        }
        assert (found) : member + " should have been found!";
        return result;
    }

    Set<MemberImpl> headMemberSet(Member member, boolean inclusive) {
        this.ensureMemberExist(member);
        LinkedHashSet<MemberImpl> result = new LinkedHashSet<MemberImpl>();
        for (MemberImpl m : this.members) {
            if (!m.equals(member)) {
                result.add(m);
                continue;
            }
            if (!inclusive) break;
            result.add(m);
            break;
        }
        return result;
    }

    boolean isBeforeThan(Address address1, Address address2) {
        if (address1.equals(address2)) {
            return false;
        }
        if (!this.addressToMemberMap.containsKey(address1)) {
            return false;
        }
        if (!this.addressToMemberMap.containsKey(address2)) {
            return false;
        }
        for (MemberImpl member : this.members) {
            if (member.getAddress().equals(address1)) {
                return true;
            }
            if (!member.getAddress().equals(address2)) continue;
            return false;
        }
        throw new AssertionError((Object)"Unreachable!");
    }

    private void ensureMemberExist(Member member) {
        if (!this.addressToMemberMap.containsKey(member.getAddress())) {
            throw new IllegalArgumentException(member + " not found!");
        }
        if (!this.uuidToMemberMap.containsKey(member.getUuid())) {
            throw new IllegalArgumentException(member + " not found!");
        }
    }
}

