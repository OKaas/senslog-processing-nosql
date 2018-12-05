package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.User;
import cz.senslog.model.dto.create.UserCreate;
import cz.senslog.model.dto.output.UserOut;
import cz.senslog.processing.db.repository.UserRepository;
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
public class UserController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @ResponseBody
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public HttpStatus create(@RequestBody UserCreate userCreate) {

        // TODO check if user have privileges for this operation

        // TODO check users attributes like password, username

        User user = modelMapper.map(userCreate, User.class);
        userRepository.insert(user);

        LOGGER.info("User with username: \'{}\' and email \'{}\' was created!",
                user.getName(), user.getPassword());

        return HttpStatus.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserOut read(@RequestBody cz.senslog.model.dto.User userDto) {

        // TODO check if user have privileges for this operation

        if (userDto.getUid() == null){
            LOGGER.warn("User id can not be null!");
            return null;
        }

        User user = userRepository.findOne(userDto.getUid());
        if(user == null){
            LOGGER.warn("User with id \'{}\' does not exists", userDto.getUid());
            return null;
        }

        return modelMapper.map(user, UserOut.class);
    }
}
