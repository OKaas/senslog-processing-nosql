package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.Privilege;
import cz.senslog.model.db.UserGroup;
import org.bson.types.ObjectId;

import java.util.List;

public class BaseController {

    protected boolean isApproved(List<UserGroup> userGroups, ObjectId searchGroupId, String requestPrivilege){

        for (UserGroup userGroup : userGroups) {
            List<Privilege> privileges = userGroup.getPrivileges();
            for (Privilege privilege : privileges) {
                if (privilege.getUnitGroupId().equals(searchGroupId)) {
                    // TODO check privileges
                    return true;
                }
            }
        }
        return false;
    }

}
