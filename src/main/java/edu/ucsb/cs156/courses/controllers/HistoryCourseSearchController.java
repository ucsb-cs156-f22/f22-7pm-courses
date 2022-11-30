package edu.ucsb.cs156.courses.controllers;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.courses.documents.ConvertedSection;
import edu.ucsb.cs156.courses.collections.ConvertedSectionCollection;

@RestController
@RequestMapping("/api/public/courseHistory")
public class HistoryCourseSearchController {
    private final Logger logger = LoggerFactory.getLogger(HistoryCourseSearchController.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ConvertedSectionCollection convertedSectionCollection;


    @GetMapping(value = "/coursesearch", produces = "application/json")
    public ResponseEntity<String> coursesearch(
        @RequestParam String startQtr, 
        @RequestParam String endQtr, 
        @RequestParam String subjectArea,
        @RequestParam String courseNumber,
        @RequestParam String courseSuf) 
        throws JsonProcessingException {

        
        List<ConvertedSection> courseResults = convertedSectionCollection.findByQuarterIntervalAndCourseId(
            startQtr, 
            endQtr, 
            makeFormattedCourseName(subjectArea, courseNumber, courseSuf)
        );

        String body = mapper.writeValueAsString(courseResults);

        return ResponseEntity.ok().body(body);
    }

    private static String makeFormattedCourseName (
        String subjectArea  ,
        String courseNumber ,
        String courseSuf    ) {

        return
              String.format( "%-8s", subjectArea                ) // 'CMPSC   '
            + String.format( "%3s" , courseNumber               ) // '  8'
            + String.format( "%-2s", courseSuf.toUpperCase()    ) // 'A '
        ;
    }

}
