import React, { useState, useEffect } from "react";
import { Form, Button} from "react-bootstrap";
import { useToasts } from "react-toast-notifications";
import { quarterRange } from "main/utils/quarterUtilities";
import SingleQuarterDropdown from "../Quarters/SingleQuarterDropdown";
import SingleSubjectDropdown from "../Subjects/SingleSubjectDropdown";
import { useBackend  } from "main/utils/useBackend";

const CourseSearchCourseStartEndQtr = ({ setCourseJSON, fetchJSON }) => {
    
    const quarters = quarterRange("20084", "20224");
    const [startQuarter, setStartQuarter] = useState(quarters[0].qqqqy);
    const [endQuarter, setEndQuarter] = useState(quarters[0].qqqqy);
    const [subject, setSubject] = useState("CMPSC   ");
    const [courseNumber, setCourseNumber] = useState("");
    const [courseSuf, setCourseSuf] = useState("");
    const { addToast } = useToasts()
    const [errorNotified, setErrorNotified] = useState(false);

    // const { data: subjects, error: _error } = useSWR(
	// 	"/api/public/subjects",
	// 	fetchSubjectAreas,
	// 	{
	// 		initialData: allTheSubjects,
	// 		revalidateOnMount: true,
	// 	}
	// );
    const { data: subjects, error: _error, status: _status } =
  useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    ["/api/UCSBSubjects/all"], 
    { method: "GET", url: "/api/UCSBSubjects/all" }, 
    []
  );

    useEffect(() => {
		if (!errorNotified && _error) {
			addToast(`${_error}`, { appearance: "error" });
			setErrorNotified(true);
		}
	}, [_error, errorNotified, addToast]);

    const handleSubmit = (event) => {
        event.preventDefault();
        fetchJSON(event, { startQuarter, endQuarter, subject, courseNumber, courseSuf}).then((courseJSON) => {
            if(courseJSON.total === 0){
                addToast("There are no courses that match the requested criteria.", { appearance: "error" });
            }
            setCourseJSON(courseJSON);
        });
    };

    const handleSubjectOnChange = (subject) => {
        setSubject(subject);
    };

    const handleCourseNumberOnChange = (event) => {
        const rawCourse = event.target.value;
        if (rawCourse.match(/\d+/g) != null) {
            const number = rawCourse.match(/\d+/g)[0];
            setCourseNumber(number);
        } else {
            setCourseNumber("");
        }
        
        if (rawCourse.match(/[a-zA-Z]+/g) != null) {
            const suffix = rawCourse.match(/[a-zA-Z]+/g)[0];
            setCourseSuf(suffix);
        } else {
            setCourseSuf("");
        }
    };

    // Note: Not all possible courses were able to be added in the subject area list as many of
    // the subject areas from the database (as well as GOLD) provided no information, almost as if the classes never existed
    // One example is Zoology
    return (
        <Form onSubmit={handleSubmit}>
            <SingleQuarterDropdown
                quarters={quarters}
                quarter={startQuarter}
                setQuarter={setStartQuarter}
                controlId={"CourseNameSearch.StartQuarter"}
                label={"Start Quarter"}
            />

            <SingleQuarterDropdown
                quarters={quarters}
                quarter={endQuarter}
                setQuarter={setEndQuarter}
                controlId={"CourseNameSearch.EndQuarter"}
                label={"End Quarter"}
            />
            <SingleSubjectDropdown
				subjects={subjects}
				subject={subject}
				setSubject={handleSubjectOnChange}
			/>
            <Form.Group controlId="CourseNameSearch.CourseNumber">
                <Form.Label>Course Number (Try searching '16' or '130A')</Form.Label>
                <Form.Control onChange={handleCourseNumberOnChange} defaultValue={courseNumber} />
            </Form.Group>
            <Button variant="primary" type="submit">
                Submit
            </Button>
        </Form>
    );
};

export default CourseSearchCourseStartEndQtr;