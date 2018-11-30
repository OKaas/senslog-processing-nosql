package cz.senslog.processing.db.repository;

import cz.senslog.model.db.UserGroup;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "userGroup", path = "userGroup")
public interface UserGroupRepository extends MongoRepository<UserGroup, ObjectId> {

    List<UserGroup> findByUsersIdsContains(ObjectId userId);

    /* --- Collaborates --- */
}


