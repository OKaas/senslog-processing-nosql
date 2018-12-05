package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.Observation;
import cz.senslog.model.db.Sensor;
import cz.senslog.model.dto.create.ObservationCreate;
import cz.senslog.model.dto.output.ObservationOut;
import cz.senslog.processing.db.repository.ObservationRepository;
import cz.senslog.processing.db.repository.SensorRepository;
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

import java.util.BitSet;

@RepositoryRestController
public class ObservationController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObservationController.class);

	@Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
	private ModelMapper modelMapper;

    @ResponseBody
    @RequestMapping(value = "/observation", method = RequestMethod.POST)
    public HttpStatus create(@RequestBody ObservationCreate observationCreate) {
    	Sensor sensor = sensorRepository.findOne(observationCreate.getSensorId());

    	if (sensor == null) {
			LOGGER.warn("Sensor id: \'{}\' does not exists!", observationCreate.getSensorId());
			return HttpStatus.BAD_REQUEST;
    	}

		ObjectId unitGroupId = sensor.getUnitGroupId();

		// TODO use logged user
    	// TODO use real privilege
		if(!isApproved(unitGroupId, new ObjectId("5c01372970af0c44dcd75481"), new BitSet())) {
			// TODO use logged user id
			LOGGER.warn("User with id: \'{}\' try to perform not privilege operation!", "5c01372970af0c44dcd75481");
			return HttpStatus.UNAUTHORIZED;
		}

    	Observation observation =  modelMapper.map(observationCreate, Observation.class);
    	observationRepository.save(observation);

        return HttpStatus.CREATED;
    }

	@ResponseBody
	@RequestMapping(value = "/observation", method = RequestMethod.GET)
	public ObservationOut read(@RequestBody cz.senslog.model.dto.Observation observationDto) {

		// TODO check if user have privileges for this operation

		if (observationDto.getUid() == null){
			LOGGER.warn("Observation id can not be null!");
			return null;
		}

    	Observation observation = observationRepository.findOne(observationDto.getUid());
		if(observation == null){
			LOGGER.warn("Observation with id \'{}\' does not exists", observationDto.getUid());
			return null;
		}

		return modelMapper.map(observation,ObservationOut.class);
	}
}
