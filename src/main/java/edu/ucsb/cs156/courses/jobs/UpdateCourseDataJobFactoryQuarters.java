package edu.ucsb.cs156.courses.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.courses.collections.ConvertedSectionCollection;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;
import edu.ucsb.cs156.courses.controllers.UCSBSubjectsController;
import edu.ucsb.cs156.courses.entities.UCSBSubject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UpdateCourseDataJobFactoryQuarters  {

    @Autowired 
    private UCSBCurriculumService ucsbCurriculumService;

    @Autowired
    private ConvertedSectionCollection convertedSectionCollection;

    @Autowired
    private UCSBSubjectsController subjectsController;

    public UpdateCourseDataJobQuarters create(String quarterYYYYQ) {
        log.info("ucsbCurriculumService = " + ucsbCurriculumService);
        log.info("convertedSectionCollection = " + convertedSectionCollection);

        List<String> subjects = new ArrayList<String>();
        Iterable<UCSBSubject> allSubs = subjectsController.allSubjects();

        for (UCSBSubject UCSBSubject : allSubs) {
            try {
                subjects.add(UCSBSubject.getSubjectCode());
            } catch (Exception e) {}
        }
        
        return new UpdateCourseDataJobQuarters(quarterYYYYQ, ucsbCurriculumService, convertedSectionCollection, subjects);
    }
}