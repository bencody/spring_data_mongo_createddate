package de.bencody.createdate.repository;

import de.bencody.createdate.model.AccessibleCreateDateModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by bco on 09.02.2017.
 */
public interface AccessibleCreateDateRepository extends MongoRepository<AccessibleCreateDateModel, ObjectId> {
}
