import React from "react";
// import OurTable, { ButtonColumn } from "main/components/OurTable";
import SectionsTableBase from "main/components/SectionsTableBase";

import { convertToFraction, formatDays, formatInstructors, formatLocation, formatTime } from "main/utils/sectionUtils.js";

// import { useBackendMutation } from "main/utils/useBackend";
// import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/PersonalScheduleUtils"
// import { useNavigate } from "react-router-dom";
// import { yyyyqToQyy } from "main/utils/quarterUtilities.js";

function getFirstVal(values){
    return values[0];
}

export default function PersonalSectionsTable({ personalSections }) {

    const columns = [
        {
            Header: 'Course ID',
            accessor: 'courseId',

            Cell: ({ cell: { value } }) => value.substring(0, value.length-2)
        },
        {
            Header: 'Enroll Code',
            accessor: 'classSections.enrollCode', 
            disableGroupBy: true,

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Section',
            accessor: "classSections.section",
            disableGroupBy: true,


            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Title',
            accessor: 'title',
            disableGroupBy: true,

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },

        {
            Header: 'Enrolled',
            accessor: (row) => convertToFraction(row.classSections.enrolledTotal, row.classSections.maxEnroll),
            disableGroupBy: true,
            id: 'enrolled',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Location',
            accessor: (row) => formatLocation(row.classSections.timeLocations),
            disableGroupBy: true,
            id: 'location',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Days',
            accessor: (row) => formatDays(row.classSections.timeLocations),
            disableGroupBy: true,
            id: 'days',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Time',
            accessor: (row) => formatTime(row.classSections.timeLocations),
            disableGroupBy: true,
            id: 'time',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Instructor',
            accessor: (row) => formatInstructors(row.classSections.instructors),
            disableGroupBy: true,
            id: 'instructor',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        }   
    ];

    const columnsToDisplay = columns;


    return <SectionsTableBase
        data={personalSections}
        columns={columnsToDisplay}
        testid={"PersonalScectionsTable"}
    />;
};