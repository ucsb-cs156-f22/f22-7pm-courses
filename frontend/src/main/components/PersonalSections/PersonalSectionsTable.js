import React from "react";
// import OurTable, { ButtonColumn } from "main/components/OurTable";
import SectionsTableBase from "main/components/SectionsTableBase";

import { convertToFraction, formatDays, formatInstructors, formatLocation, formatTime, isSection } from "main/utils/sectionUtils.js";

// import { useBackendMutation } from "main/utils/useBackend";
// import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/PersonalScheduleUtils"
// import { useNavigate } from "react-router-dom";
// import { yyyyqToQyy } from "main/utils/quarterUtilities.js";

export default function PersonalSectionsTable({ personalSections }) {

    const columns = [
        {
            Header: 'Course ID',
            accessor: 'courseInfo.courseId',

            Cell: ({ cell: { value } }) => value.substring(0, value.length-2)
        },
        {
            Header: 'Title',
            accessor: 'courseInfo.title',
            disableGroupBy: true,

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            // Stryker disable next-line StringLiteral: this column is hidden, very hard to test
            Header: 'Is Section?',
            accessor: (row) => isSection(row.section.section),
            // Stryker disable next-line StringLiteral: this column is hidden, very hard to test
            id: 'isSection',
        },
        {
            Header: 'Enrolled',
            accessor: (row) => convertToFraction(row.section.enrolledTotal, row.section.maxEnroll),
            disableGroupBy: true,
            id: 'enrolled',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Location',
            accessor: (row) => formatLocation(row.section.timeLocations),
            disableGroupBy: true,
            id: 'location',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Days',
            accessor: (row) => formatDays(row.section.timeLocations),
            disableGroupBy: true,
            id: 'days',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Time',
            accessor: (row) => formatTime(row.section.timeLocations),
            disableGroupBy: true,
            id: 'time',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },
        {
            Header: 'Instructor',
            accessor: (row) => formatInstructors(row.section.instructors),
            disableGroupBy: true,
            id: 'instructor',

            aggregate: getFirstVal,
            Aggregated: ({ cell: { value } }) => `${value}`
        },        
        {
            Header: 'Enroll Code',
            accessor: 'section.enrollCode', 
            disableGroupBy: true,

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