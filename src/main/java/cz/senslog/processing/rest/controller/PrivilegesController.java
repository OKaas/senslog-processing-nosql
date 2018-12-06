package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.*;
import cz.senslog.model.dto.create.PrivilegeCreate;
import cz.senslog.model.dto.Privilege;
import cz.senslog.model.dto.output.PrivilegeOut;
import cz.senslog.processing.db.repository.*;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@RepositoryRestController
public class PrivilegesController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivilegesController.class);


    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UnitGroupRepository unitGroupRepository;

    @Autowired
    private UserPrivilegeRepository userPrivilegeRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @ResponseBody
    @RequestMapping(value = "/privilege", method = RequestMethod.POST)
    public HttpStatus create(@RequestBody PrivilegeCreate privilegeCreate) {

        // TODO check if user have privileges for this operation

        UserGroup userGroup = userGroupRepository.findOne(privilegeCreate.getUserGroupId());
        if (userGroup == null) {
            LOGGER.warn("User group with id: \'{}\' does not exists!", privilegeCreate.getUserGroupId());
            return HttpStatus.BAD_REQUEST;
        }

        UnitGroup unitGroup = unitGroupRepository.findOne(privilegeCreate.getUnitGroupId());
        if (unitGroup == null) {
            LOGGER.warn("Unit group with id: \'{}\' does not exists!", privilegeCreate.getUnitGroupId());
            return HttpStatus.BAD_REQUEST;
        }

        if (privilegeRepository.existsByUnitGroupIdAndUserGroupId(unitGroup.getUid(), userGroup.getUid())) {
            LOGGER.warn("Privileges with Unit group id: \'{}\' and user group id \'{}\' already exists!",
                    unitGroup.getUid(), userGroup.getUid());
            return HttpStatus.BAD_REQUEST;
        }

        cz.senslog.model.db.Privilege privilege = modelMapper.map(privilegeCreate, cz.senslog.model.db.Privilege.class);
        privilegeRepository.insert(privilege);
        LOGGER.info("Privilege with Unit group id: \'{}\' and user group id \'{}\' was created!",
                unitGroup.getUid(), userGroup.getUid());

        // Update user privileges
        List<UserPrivilege> userPrivileges = userPrivilegeRepository.findAllByUserIdInAndUnitGroupId(
                userGroup.getUsersIds(), unitGroup.getUid());

        createMissingPrivileges(userPrivileges, userGroup.getUsersIds(), unitGroup.getUid());

        addUserPrivilege(userPrivileges, privilege.getUid());
        updateUserPrivileges(userPrivileges);
        return HttpStatus.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/privilege", method = RequestMethod.GET)
    public PrivilegeOut read(@RequestBody Privilege privilegeDto) {
        // TODO check if user have privileges for this operation

        if (privilegeDto.getUid() == null){
            LOGGER.warn("Privilege id can not be null!");
            return null;
        }

        cz.senslog.model.db.Privilege privilege = privilegeRepository.findOne(privilegeDto.getUid());
        if(privilege == null){
            LOGGER.warn("Privilege with id \'{}\' does not exists", privilegeDto.getUid());
            return null;
        }

        return modelMapper.map(privilege, PrivilegeOut.class);
    }

    @ResponseBody
    @RequestMapping(value = "/privilege", method = RequestMethod.PUT)
    public HttpStatus update(@RequestBody Privilege privilegeDto) {

        // TODO check if user have privileges for this operation

        cz.senslog.model.db.Privilege oldPrivilege = privilegeRepository.findOne(privilegeDto.getUid());

        if (oldPrivilege == null) {
            LOGGER.warn("Privilege with id: \'{}\' does not exists!", privilegeDto.getUid());
            return HttpStatus.BAD_REQUEST;
        }

        UserGroup newUserGroup = userGroupRepository.findOne(privilegeDto.getUserGroupId());
        if (newUserGroup == null) {
            LOGGER.warn("User group with id: \'{}\' does not exists!", privilegeDto.getUserGroupId());
            return HttpStatus.BAD_REQUEST;
        }

        UnitGroup newUnitGroup = unitGroupRepository.findOne(privilegeDto.getUnitGroupId());
        if (newUnitGroup == null) {
            LOGGER.warn("Unit group with id: \'{}\' does not exists!", privilegeDto.getUnitGroupId());
            return HttpStatus.BAD_REQUEST;
        }

        cz.senslog.model.db.Privilege newPrivilege = modelMapper.map(privilegeDto, cz.senslog.model.db.Privilege.class);
        List<UserPrivilege> userPrivilegesToUpdate = new ArrayList<UserPrivilege>();

        if (newPrivilege.getUserGroupId() != oldPrivilege.getUserGroupId() ||
                newPrivilege.getUnitGroupId() != oldPrivilege.getUnitGroupId()) {

            if (privilegeRepository.existsByUnitGroupIdAndUserGroupId(newPrivilege.getUnitGroupId(), newPrivilege.getUserGroupId())) {
                LOGGER.warn("Privilege with Unit group id: \'{}\' and user group id \'{}\' already exists!",
                        newUnitGroup.getUid(), newUserGroup.getUid());
                return HttpStatus.BAD_REQUEST;
            }

            UserGroup oldUserGroup = userGroupRepository.findOne(oldPrivilege.getUserGroupId());
            if (oldUserGroup == null) {
                LOGGER.error("User group with id: \'{}\' does not exists but privilege \'{}\' refers to it!",
                        oldPrivilege.getUserGroupId(), oldPrivilege.getUid());
                return HttpStatus.BAD_REQUEST;
            }

            // get all UserPrivileges where userId is in usersIds and unitGroup == old unit group
            List<UserPrivilege> oldUserPrivileges = userPrivilegeRepository.findAllByUserIdInAndUnitGroupId(
                    oldUserGroup.getUsersIds(), oldPrivilege.getUnitGroupId());

            // remove old privilege
            removeUserPrivilege(oldUserPrivileges, oldPrivilege.getUid());
            updateUserPrivileges(oldUserPrivileges);

            // find all UserPrivileges where userId is in usersIds and unitGroup == new unit group
            List<UserPrivilege> newUserPrivileges = userPrivilegeRepository.findAllByUserIdInAndUnitGroupId(
                    newUserGroup.getUsersIds(), newPrivilege.getUnitGroupId());

            // create missing usersPrivileges
            createMissingPrivileges(newUserPrivileges, newUserGroup.getUsersIds(), newPrivilege.getUnitGroupId());

            // add new privilege to UserPrivileges
            addUserPrivilege(newUserPrivileges, newPrivilege.getUid());

            // update UsersPrivileges
            userPrivilegesToUpdate.addAll(newUserPrivileges);

        } else if (newPrivilege.getPrivileges() != oldPrivilege.getPrivileges()) {
            List<UserPrivilege> userPrivileges = userPrivilegeRepository.findAllByUserIdInAndUnitGroupId(
                    newUserGroup.getUsersIds(), newPrivilege.getUnitGroupId());

            userPrivilegesToUpdate.addAll(userPrivileges);

        } else {
            return HttpStatus.BAD_REQUEST;
        }


        privilegeRepository.save(newPrivilege);
        LOGGER.info("Privilege id: \'{}\', unitGroupId: \'{}\', userGroupId: \'{}\', privilege: \'{}\' was updated " +
                        "to unitGroupId: \'{}\', userGroupId: \'{}\', privilege: \'{}\'",
                privilegeDto.getUid(), oldPrivilege.getUnitGroupId(), oldPrivilege.getUserGroupId(),
                oldPrivilege.getPrivileges(), newPrivilege.getUnitGroupId(), newPrivilege.getUserGroupId(),
                newPrivilege.getPrivileges());


        updateUserPrivileges(userPrivilegesToUpdate);
        return HttpStatus.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/privilege", method = RequestMethod.DELETE)
    public HttpStatus delete(@RequestBody Privilege privilegeDto) {

        // TODO check if user have privileges for this operation

        cz.senslog.model.db.Privilege privilege = privilegeRepository.findOne(privilegeDto.getUid());

        if (privilege == null) {
            LOGGER.warn("Privilege with id: \'{}\' does not exists!", privilegeDto.getUid());
            return HttpStatus.BAD_REQUEST;
        }

        UserGroup userGroup = userGroupRepository.findOne(privilege.getUserGroupId());
        if (userGroup == null) {
            LOGGER.error("User group with id: \'{}\' does not exists but privilege with id \'{}\' have reference to it!",
                    privilege.getUserGroupId(), privilege.getUid());
            return HttpStatus.BAD_REQUEST;
        }

        List<UserPrivilege> userPrivileges = userPrivilegeRepository.findAllByUserIdInAndUnitGroupId(
                userGroup.getUsersIds(), privilege.getUnitGroupId());

        // TODO userPrivilege with empty privilegesIds will remain in db.
        removeUserPrivilege(userPrivileges, privilege.getUid());
        updateUserPrivileges(userPrivileges);

        privilegeRepository.delete(privilege);
        return HttpStatus.OK;
    }

    private void removeUserPrivilege(List<UserPrivilege> userPrivileges, ObjectId removePrivilegeId) {
        for (UserPrivilege userPrivilege : userPrivileges) {
            userPrivilege.getPrivilegesIds().remove(removePrivilegeId);
        }
    }

    /**
     * !! NOT UPDATE DATABASE
     *
     * @param userPrivileges
     * @param createdPrivilegeId
     */
    private void addUserPrivilege(List<UserPrivilege> userPrivileges, ObjectId createdPrivilegeId) {
        for (UserPrivilege userPrivilege : userPrivileges) {
            userPrivilege.getPrivilegesIds().add(createdPrivilegeId);
        }
    }

    private void updateUserPrivileges(List<UserPrivilege> userPrivileges) {

        for (UserPrivilege userPrivilege : userPrivileges) {
            BitSet newPrivilege = new BitSet();
            Iterable<cz.senslog.model.db.Privilege> privileges = privilegeRepository.findAll(userPrivilege.getPrivilegesIds());
            for (cz.senslog.model.db.Privilege item : privileges) {
                newPrivilege.or(item.getPrivileges());
            }

            userPrivilege.setPrivileges(newPrivilege);
        }
        userPrivilegeRepository.save(userPrivileges);
    }

    /**
     * !! NOT UPDATE DATABASE
     *
     * @param userPrivileges
     * @param usersIdsInUserGroup
     * @param unitGroupId
     */
    private void createMissingPrivileges(List<UserPrivilege> userPrivileges, List<ObjectId> usersIdsInUserGroup, ObjectId unitGroupId) {
        if (userPrivileges.size() == usersIdsInUserGroup.size()) {
            // Every user have own userPrivilege
            return;
        }

        for (ObjectId userId : usersIdsInUserGroup) {
            boolean contains = false;

            for (UserPrivilege privilege : userPrivileges) {
                if (privilege.getUserId().equals(userId)) {
                    contains = true;
                    break;
                }
            }

            // user does not have privilege. Creating new privilege
            if (!contains) {
                UserPrivilege userPrivilege = new UserPrivilege();
                userPrivilege.setPrivilegesIds(new ArrayList<ObjectId>());
                userPrivilege.setUserId(userId);
                userPrivilege.setUnitGroupId(unitGroupId);
                userPrivileges.add(userPrivilege);
            }
        }
    }
}
