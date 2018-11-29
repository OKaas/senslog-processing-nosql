package cz.senslog.processing.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.senslog.model.db.Observation;
import cz.senslog.model.db.Sensor;
import cz.senslog.model.dto.create.ObservationCreate;
import cz.senslog.processing.db.repository.ObservationRepository;
import cz.senslog.processing.db.repository.SensorRepository;

@RepositoryRestController
public class ObservationController {

    @Autowired
    ObservationRepository observationRepository;
    @Autowired
    SensorRepository sensorRepository;

    @ResponseBody
    @RequestMapping(value = "/observation", method = RequestMethod.POST)
    public HttpStatus insertObservation(@RequestBody ObservationCreate observationCreate) {
    	Sensor sensor= sensorRepository.findOne(observationCreate.getSensorId());
    	
    	if(sensor == null) {
    		return HttpStatus.BAD_REQUEST;
    	}
    	sensor.getUnitGroup().getUid();
    	//TODO check unitGroupId with user
    	String unitGroupId = sensor.getUnitGroup().getUid();
    	Observation observation = new Observation();
    	observation.setSensor(sensor);
    	observation.setTimestamp(observationCreate.getTimestamp());
    	observation.setValue(observationCreate.getValue()); 
    	observationRepository.save(observation);
        return HttpStatus.CREATED;
    }
	
}
