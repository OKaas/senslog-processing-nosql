package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.Unit;
import cz.senslog.model.db.UnitGroup;
import cz.senslog.model.dto.create.UnitCreate;
import cz.senslog.model.dto.output.UnitOut;
import cz.senslog.processing.db.repository.UnitGroupRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UnitsController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitsController.class);

    @Autowired
    private UnitGroupRepository unitGroupRepository;

    @Autowired
    private ModelMapper modelMapper;

    @ResponseBody
    @RequestMapping(value = "/unit", method = RequestMethod.POST)
    public HttpStatus create(@RequestBody UnitCreate unitCreate) {

        // TODO check if user have privileges for this operation

        if (unitCreate.getUnitGroupId() == null) {
            LOGGER.warn("Unit group id can not be null");
            return HttpStatus.BAD_REQUEST;
        }

        UnitGroup unitGroup = unitGroupRepository.findOne(unitCreate.getUnitGroupId());
        if (unitGroup == null) {
            LOGGER.warn("Unit group with id \'{}\' not exists", unitCreate.getUnitGroupId());
            return HttpStatus.BAD_REQUEST;
        }

        Unit unit = modelMapper.map(unitCreate, Unit.class);
        unitGroup.getUnits().add(unit);

        unitGroupRepository.save(unitGroup);
        LOGGER.info("Unit was inserted to unitGroup with id: \'{}\'", unitGroup.getUid());

        return HttpStatus.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/unit", method = RequestMethod.GET)
    public UnitOut read(@RequestBody cz.senslog.model.dto.Unit unitDto) {

        // TODO check if user have privileges for this operation

        if (unitDto.getUid() == null) {
            LOGGER.warn("Unit id can not be null!");
            return null;
        }

        UnitGroup unitGroup = unitGroupRepository.findByUnitsIsContaining(unitDto.getUid());
        if (unitGroup == null) {
            LOGGER.warn("UnitGroup which contains unit with id \'{}\' does not exists", unitDto.getUid());
            return null;
        }

        Unit searchUnit = modelMapper.map(unitDto, Unit.class);
        int index = unitGroup.getUnits().indexOf(searchUnit);
        Unit unit = unitGroup.getUnits().get(index);

        UnitOut unitOut = modelMapper.map(unit, UnitOut.class);
        unitOut.setUnitGroupId(unitGroup.getUid().toString());

        return unitOut;
    }
}
