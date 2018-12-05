package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.Phenomenon;
import cz.senslog.model.dto.create.PhenomenonCreate;
import cz.senslog.model.dto.output.PhenomenonOut;
import cz.senslog.processing.db.repository.PhenomenonRepository;
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
public class PhenomenonController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenomenonController.class);

    @Autowired
    private PhenomenonRepository phenomenonRepository;

    @Autowired
    private ModelMapper modelMapper;

    @ResponseBody
    @RequestMapping(value = "/phenomenon", method = RequestMethod.POST)
    public HttpStatus create(@RequestBody PhenomenonCreate phenomenonCreate) {

        // TODO check if user have privileges for this operation

        Phenomenon phenomenon = modelMapper.map(phenomenonCreate, Phenomenon.class);
        phenomenonRepository.insert(phenomenon);

        LOGGER.info("New phenomenon with was created!");
        return HttpStatus.CREATED;
    }

    @ResponseBody
    @RequestMapping(value = "/phenomenon", method = RequestMethod.GET)
    public PhenomenonOut read(@RequestBody cz.senslog.model.dto.Phenomenon phenomenonDto) {

        // TODO check if user have privileges for this operation

        if (phenomenonDto.getUid() == null){
            LOGGER.warn("Phenomenon id can not be null!");
            return null;
        }

        Phenomenon phenomenon = phenomenonRepository.findOne(phenomenonDto.getUid());
        if(phenomenon == null){
            LOGGER.warn("Phenomenon with id \'{}\' does not exists", phenomenonDto.getUid());
            return null;
        }

        return modelMapper.map(phenomenon, PhenomenonOut.class);
    }
}
