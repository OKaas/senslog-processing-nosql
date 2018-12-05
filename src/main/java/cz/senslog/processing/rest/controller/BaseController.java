package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.UserPrivilege;
import cz.senslog.processing.db.repository.UserPrivilegeRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.BitSet;

public class BaseController {

    @Autowired
    private UserPrivilegeRepository userPrivilegeRepository;

    protected boolean isApproved(ObjectId unitGroupId, ObjectId userId, BitSet requestedPrivilege){


        UserPrivilege privilege = userPrivilegeRepository.findByUnitGroupIdAndUserId(unitGroupId, userId);
        if(privilege == null){
            return false;
        }

        privilege.getPrivileges().and(requestedPrivilege);

        return privilege.getPrivileges().equals(requestedPrivilege);
    }

}
