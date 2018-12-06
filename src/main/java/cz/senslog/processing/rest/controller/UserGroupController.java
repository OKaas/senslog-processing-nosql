package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.UserGroup;
import cz.senslog.model.dto.create.UserGroupCreate;
import cz.senslog.model.dto.output.UserGroupOut;
import cz.senslog.processing.db.repository.UserGroupRepository;
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

@RepositoryRestController
public class UserGroupController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupController.class);

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private ModelMapper modelMapper;

    @ResponseBody
    @RequestMapping(value = "/userGroup", method = RequestMethod.POST)
    public HttpStatus create(@RequestBody UserGroupCreate userGroupCreate) {

        // TODO check if user have privileges for this operation

        UserGroup userGroup = modelMapper.map(userGroupCreate, UserGroup.class);


        userGroupRepository.insert(userGroup);
        LOGGER.info("New userGroup with id: \'{}\' was created", userGroup.getUid());

        return HttpStatus.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/userGroup", method = RequestMethod.GET)
    public UserGroupOut read(@RequestBody cz.senslog.model.dto.UserGroup userGroupDto) {

        // TODO check if user have privileges for this operation

        if (userGroupDto.getUid() == null) {
            LOGGER.warn("UserGroup id can not be null!");
            return null;
        }

        UserGroup userGroup = userGroupRepository.findOne(userGroupDto.getUid());
        if (userGroup == null) {
            LOGGER.warn("UserGroup with id \'{}\' does not exists", userGroupDto.getUid());
            return null;
        }

        return modelMapper.map(userGroup, UserGroupOut.class);
    }
}
