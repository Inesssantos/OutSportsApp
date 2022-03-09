package com.example.outsports.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListGroups implements Serializable {

    private final List<Group> groups;

    public ListGroups() {
        groups = new ArrayList<>();
    }

    // GETS //
    public List<Group> getListGroups() {
        return groups;
    }

    public Group getGroup(int index) {
        return groups.get(index);
    }

    public int getSizeGroups() {
        return groups.size();
    }

    // SETS //
    public void addGroup(Group newgroup) {
        groups.add(newgroup);
    }

    public void addAllGroup(List<Group> new_groups) {
        groups.addAll(new_groups);
    }

    public void deleteGroup(int index) {
        groups.remove(index);
    }

    public void deleteAllGroups() {
        groups.clear();
    }
}
