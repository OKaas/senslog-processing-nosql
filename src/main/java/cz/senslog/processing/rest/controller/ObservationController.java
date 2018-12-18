package cz.senslog.processing.rest.controller;

import cz.senslog.model.db.Observation;
import cz.senslog.model.db.Sensor;
import cz.senslog.model.dto.create.ObservationCreate;
import cz.senslog.processing.db.repository.ObservationRepository;
import cz.senslog.processing.db.repository.SensorRepository;
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
public class ObservationController {

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

    	Observation observation =  modelMapper.map(observationCreate, Observation.class);
    	observationRepository.save(observation);

        return HttpStatus.CREATED;
    }
}
