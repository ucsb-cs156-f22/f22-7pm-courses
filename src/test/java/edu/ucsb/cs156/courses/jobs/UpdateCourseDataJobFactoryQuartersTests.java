package edu.ucsb.cs156.courses.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;

import edu.ucsb.cs156.courses.collections.ConvertedSectionCollection;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;
import edu.ucsb.cs156.courses.services.UCSBSubjectsService;



@RestClientTest(UpdateCourseDataJobFactoryQuarters.class)
@AutoConfigureDataJpa
public class UpdateCourseDataJobFactoryQuartersTests {

    @MockBean
    UCSBCurriculumService ucsbCurriculumService;

    @MockBean
    ConvertedSectionCollection convertedSectionCollection;

    @MockBean
    UCSBSubjectsService ucsbSubjectsService;

    @Autowired
    UpdateCourseDataJobFactoryQuarters updateCourseDataOneQuarterJobFactory;

    @Test
    void test_create() throws Exception {

        // Act

        UpdateCourseDataJobQuarters updateCourseDataJobQuarters = updateCourseDataOneQuarterJobFactory.create("20211");

        // Assert
        
        assertEquals("20211",updateCourseDataJobQuarters.getQuarterYYYYQ());
        assertEquals(ucsbSubjectsService,updateCourseDataJobQuarters.getUcsbSubjectsService());
        assertEquals(ucsbCurriculumService,updateCourseDataJobQuarters.getUcsbCurriculumService());
        assertEquals(convertedSectionCollection,updateCourseDataJobQuarters.getConvertedSectionCollection());

    }
}