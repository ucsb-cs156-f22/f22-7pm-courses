package edu.ucsb.cs156.courses.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import edu.ucsb.cs156.courses.collections.ConvertedSectionCollection;
import edu.ucsb.cs156.courses.config.SecurityConfig;
import edu.ucsb.cs156.courses.documents.ConvertedSection;
import edu.ucsb.cs156.courses.documents.CourseInfo;
import edu.ucsb.cs156.courses.documents.CoursePage;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(value = HistoryCourseSearchController.class)
@Import(SecurityConfig.class)
@AutoConfigureDataJpa
@Slf4j
public class HistoryCourseSearchControllerTests {
    private final Logger logger = LoggerFactory.getLogger(HistoryCourseSearchControllerTests.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ConvertedSectionCollection convertedSectionCollection;

    @Test
    public void test_search_request1() throws Exception {
        
        ConvertedSection convertedSection1  = ConvertedSection.builder().build();
        ConvertedSection convertedSection2  = ConvertedSection.builder().build();

        ArrayList<ConvertedSection> expectedSections = new ArrayList<ConvertedSection>(); 
        expectedSections.addAll(Arrays.asList(convertedSection1,convertedSection2));

        when (
            convertedSectionCollection.findByQuarterIntervalAndCourseId(
                any(String.class), any(String.class), any(String.class))
        ).thenReturn( expectedSections );

        String url = "/api/public/courseHistory/coursesearch?courseNumber=130&courseSuf=A&endQtr=20224&startQtr=20221&subjectArea=CMPSC";
        
        MvcResult response = mockMvc.perform(get(url).contentType("application/json")).andExpect(status().isOk()).andReturn();
        
        verify(convertedSectionCollection, times(1)).findByQuarterIntervalAndCourseId( any(String.class), any(String.class), any(String.class));
        String expectedJson = mapper.writeValueAsString(expectedSections);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


    @Test
    public void test_search_request2() throws Exception {
        ConvertedSection convertedSection1  = ConvertedSection.builder().build();
        ConvertedSection convertedSection2  = ConvertedSection.builder().build();
        ConvertedSection convertedSection3  = ConvertedSection.builder().build();
        ConvertedSection convertedSection4  = ConvertedSection.builder().build();


        CourseInfo courseInfo = CourseInfo.builder()
                                .quarter("20221")
                                .courseId("CMPSC   130A -1")
                                .title("DATA STRUCT ALGOR")
                                .description("Data structures and applications with proofs of correctness and analysis. H ash tables, priority queues (heaps); balanced search trees. Graph traversal techniques and their applications.")
                                .build();

        convertedSection1.setCourseInfo(courseInfo);
        convertedSection2.setCourseInfo(courseInfo);
        convertedSection3.setCourseInfo(courseInfo);
        convertedSection4.setCourseInfo(courseInfo);

        ArrayList<ConvertedSection> expectedSections = new ArrayList<ConvertedSection>(); 
        expectedSections.addAll(Arrays.asList(convertedSection1,convertedSection2,convertedSection3,convertedSection4));

        when (
            convertedSectionCollection.findByQuarterIntervalAndCourseId(
                eq("20221"), eq("20221"), eq("CMPSC   130A "))
        ).thenReturn( expectedSections );

        String url = "/api/public/courseHistory/coursesearch?courseNumber=130&courseSuf=A&endQtr=20221&startQtr=20221&subjectArea=CMPSC";
        
        MvcResult response = mockMvc.perform(get(url).contentType("application/json")).andExpect(status().isOk()).andReturn();
        
        verify(convertedSectionCollection, times(1)).findByQuarterIntervalAndCourseId( any(String.class), any(String.class), any(String.class));
        String expectedJson = mapper.writeValueAsString(expectedSections);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

}