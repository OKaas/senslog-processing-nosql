package cz.senslog.processing.db.repository;

import cz.senslog.model.db.Privilege;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "privilege", path = "privilege")
public interface PrivilegeRepository extends MongoRepository<Privilege, ObjectId> {

    List<Privilege> findByUnitGroupIdAndUserGroupId(ObjectId unitGroupId, ObjectId userGroupId);

    boolean existsByUnitGroupIdAndUserGroupId(ObjectId unitGroupId, ObjectId userGroupId);
    /* --- Collaborates --- */
}


