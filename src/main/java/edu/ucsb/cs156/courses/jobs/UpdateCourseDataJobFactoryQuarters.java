package edu.ucsb.cs156.courses.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.courses.collections.ConvertedSectionCollection;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;
import edu.ucsb.cs156.courses.controllers.UCSBSubjectsController;
import edu.ucsb.cs156.courses.services.UCSBSubjectsService;
import edu.ucsb.cs156.courses.entities.UCSBSubject;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UpdateCourseDataJobFactoryQuarters{

    @Autowired 
    private UCSBCurriculumService ucsbCurriculumService;

    @Autowired
    private ConvertedSectionCollection convertedSectionCollection;

    @Autowired
    private UCSBSubjectsService ucsbSubjectsService;

    public UpdateCourseDataJobQuarters create(String quarterYYYYQ) {
        log.info("ucsbCurriculumService = " + ucsbCurriculumService);
        log.info("convertedSectionCollection = " + convertedSectionCollection);
        log.info("ucsbSubjectsSerivce = " + ucsbSubjectsService);

        return new UpdateCourseDataJobQuarters(ucsbSubjectsService,quarterYYYYQ, ucsbCurriculumService, convertedSectionCollection);
    }
}