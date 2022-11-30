package edu.ucsb.cs156.courses.collections;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.courses.documents.ConvertedSection;

@Repository
public interface ConvertedSectionCollection extends MongoRepository<ConvertedSection, ObjectId> {
    @Query("{'courseInfo.quarter': ?0, 'section.enrollCode': ?1}")
    Optional<ConvertedSection> findOneByQuarterAndEnrollCode(String quarter, String enrollCode);

    //@Query("{'courseInfo.quarter': {$gte : ?0, $lte : ?1}, 'courseInfo.courseId':  {$regex: ?2 }}")
    //@Query("{'courseInfo.quarter': {$gte : '20201', $lte : '20221'}, 'courseInfo.courseId':  'ART       1A -1'}")
    @Query("{}")
    List<ConvertedSection> findByQuarterIntervalAndCourseId(String startQuarter, String endQuarter, String formattedCourseId);
}