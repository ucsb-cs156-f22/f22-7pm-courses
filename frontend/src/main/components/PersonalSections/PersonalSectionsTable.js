import React from "react";
import SectionsTableBase from "main/components/SectionsTableBase";

import { convertToFraction, formatDays, formatInstructors, formatLocation, formatTime } from "main/utils/sectionUtils.js";



export default function PersonalSectionsTable({ personalSections }) {

    // Stryker enable all 
    // Stryker disable BooleanLiteral
    const removeSuffix = (s) => {
        if (s.charAt(s.length - 2) == '-') {
            return s.substring(0, s.length - 2);
        }
        return s;
    };

    const columns = [
        {
            Header: 'Course ID',
            accessor: 'courseId',

            Cell: ({ cell: { value } }) => removeSuffix(value)
        },
        {
            Header: 'Enroll Code',
            accessor: 'classSections[0].enrollCode', 
            disableGroupBy: true,
        },
        {
            Header: 'Section',
            accessor: "classSections[0].section",
            disableGroupBy: true,
        },
        {
            Header: 'Title',
            accessor: 'title',
            disableGroupBy: true
        },

        {
            Header: 'Enrolled',
            accessor: (row) => convertToFraction(row.classSections[0].enrolledTotal, row.classSections[0].maxEnroll),
            disableGroupBy: true,
            id: 'enrolled'
        },
        {
            Header: 'Location',
            accessor: (row) => formatLocation(row.classSections[0].timeLocations),
            disableGroupBy: true,
            id: 'location'
        },
        {
            Header: 'Days',
            accessor: (row) => formatDays(row.classSections[0].timeLocations),
            disableGroupBy: true,
            id: 'days'
        },
        {
            Header: 'Time',
            accessor: (row) => formatTime(row.classSections[0].timeLocations),
            disableGroupBy: true,
            id: 'time'
        },
        {
            Header: 'Instructor',
            accessor: (row) => formatInstructors(row.classSections[0].instructors),
            disableGroupBy: true,
            id: 'instructor'
        }   
    ];

    const columnsToDisplay = columns;


    return <SectionsTableBase
        data={personalSections}
        columns={columnsToDisplay}
        testid={"PersonalSectionsTable"}
    />;
};