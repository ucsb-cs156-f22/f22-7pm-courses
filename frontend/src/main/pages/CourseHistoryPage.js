import React from "react";
import { useState } from "react";
import { Jumbotron } from "react-bootstrap";
import BasicCourseTable from "main/components/Courses/BasicCourseTable";
import CourseSearchCourseStartEndQtr from "main/components/BasicCourseSearch/CourseSearchCourseStartEndQtr";
import fetch from "isomorphic-unfetch";

import TableLegend from "main/components/BasicCourseSearch/TableLegend";

import CourseFilters from "main/components/BasicCourseSearch/CourseFilters";

const CourseHistoryPage = () => {
    const initialCourseJSON = {
        "pageNumber": 1,
        "pageSize": 1,
        "total": 0,
        "classes": []
    };

    const [courseJSON, setCourseJSON] = useState(initialCourseJSON);

    //Check for closed, cancelled, full status
    const [cancelled, setCancelledChecked] = useState(false);
    const [closed, setClosedChecked] = useState(false);
    const [full, setFullChecked] = useState(false); 

    const handleCancelledOnChange = () => {
        setCancelledChecked(!cancelled);
    };
    const handleClosedOnChange = () => {
        setClosedChecked(!closed);
    };
    const handleFullOnChange = () => {
        setFullChecked(!full);
    };

    return (
        <Jumbotron>
            <div className="text-left">
                <h2>Search Archived Course Data from MongoDB</h2>
                <h5>Search By Course Number Across a Range of Quarters</h5>
                <CourseSearchCourseStartEndQtr setCourseJSON={setCourseJSON} fetchJSON={fetchCourseHistoryNameQtrJSON} />

                <TableLegend legend />
                <CourseFilters cancelled={cancelled} handleCancelledOnChange={handleCancelledOnChange} closed={closed} handleClosedOnChange={handleClosedOnChange} full={full} handleFullOnChange={handleFullOnChange}/>

                <BasicCourseTable classes={courseJSON.classes} checks={[cancelled,closed,full]} displayQuarter allowExport={true}/>
            </div>
        </Jumbotron>
    ); 
};

export default CourseHistoryPage;

const fetchCourseHistoryNameQtrJSON = async (_event, fields) => {
    const url = `/api/public/courseHistory/coursesearch?courseNumber=${fields.courseNumber}&courseSuf=${fields.courseSuf}&endQtr=${fields.endQuarter}&startQtr=${fields.startQuarter}&subjectArea=C${fields.subject}`;
    const courseResponse = await fetch(url);
    return courseResponse.json();
};