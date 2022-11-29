import React from "react";
import SectionsTableBase from "main/components/SectionsTableBase";

import { convertToFraction, formatDays, formatInstructors, formatLocation, formatTime } from "main/utils/sectionUtils.js";


// function getFirstVal(values){
//     return values[0];
// }

export default function PersonalSectionsTable({ personalSections }) {

    // Stryker enable all 
    // Stryker disable BooleanLiteral
    const columns = [
        {
            Header: 'Course ID',
            accessor: 'courseId',

            Cell: ({ cell: { value } }) => value.substring(0, value.length-2)
        },
        {
            Header: 'Enroll Code',
            accessor: 'classSections[0].enrollCode', 
            disableGroupBy: true,

            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Section',
            accessor: "classSections[0].section",
            disableGroupBy: true,


            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Title',
            accessor: 'title',
            disableGroupBy: true,

            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        },

        {
            Header: 'Enrolled',
            accessor: (row) => convertToFraction(row.classSections[0].enrolledTotal, row.classSections[0].maxEnroll),
            disableGroupBy: true,
            id: 'enrolled',

            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Location',
            accessor: (row) => formatLocation(row.classSections[0].timeLocations),
            disableGroupBy: true,
            id: 'location',

            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Days',
            accessor: (row) => formatDays(row.classSections[0].timeLocations),
            disableGroupBy: true,
            id: 'days',

            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Time',
            accessor: (row) => formatTime(row.classSections[0].timeLocations),
            disableGroupBy: true,
            id: 'time',

            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Instructor',
            accessor: (row) => formatInstructors(row.classSections[0].instructors),
            disableGroupBy: true,
            id: 'instructor',

            // aggregate: getFirstVal,
            // Aggregated: ({ cell: { value } }) => `${value}`
        }   
    ];

    const columnsToDisplay = columns;


    return <SectionsTableBase
        data={personalSections}
        columns={columnsToDisplay}
        testid={"PersonalSectionsTable"}
    />;
};