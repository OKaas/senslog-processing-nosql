package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.UnitGroup;
import cz.senslog.model.dto.create.UnitGroupCreate;
import cz.senslog.model.dto.output.UnitGroupOut;
import cz.senslog.processing.db.repository.UnitGroupRepository;
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
public class UnitGroupController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitGroupController.class);

    @Autowired
    private UnitGroupRepository unitGroupRepository;

    @Autowired
    private ModelMapper modelMapper;

    @ResponseBody
    @RequestMapping(value = "/unitGroup", method = RequestMethod.POST)
    public HttpStatus create(@RequestBody UnitGroupCreate unitGroupCreate) {

        // TODO check if user have privileges for this operation

        UnitGroup unitGroup = modelMapper.map(unitGroupCreate, UnitGroup.class);

        unitGroupRepository.insert(unitGroup);
        LOGGER.info("New unitGroup with id: \'{}\' was created", unitGroup.getUid());

        return HttpStatus.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/unitGroup", method = RequestMethod.GET)
    public UnitGroupOut read(@RequestBody cz.senslog.model.dto.UnitGroup unitGroupDto) {

        // TODO check if user have privileges for this operation

        if (unitGroupDto.getUid() == null) {
            LOGGER.warn("UnitGroup id can not be null!");
            return null;
        }

        UnitGroup unitGroup = unitGroupRepository.findOne(unitGroupDto.getUid());
        if (unitGroup == null) {
            LOGGER.warn("UnitGroup with id \'{}\' does not exists", unitGroupDto.getUid());
            return null;
        }

        return modelMapper.map(unitGroup, UnitGroupOut.class);
    }
}
