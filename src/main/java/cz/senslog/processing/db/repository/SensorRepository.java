package cz.senslog.processing.db.repository;

import cz.senslog.model.db.Sensor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "sensor", path = "sensor")
public interface SensorRepository extends MongoRepository<Sensor, ObjectId> {

    /* --- Collaborates --- */
}


