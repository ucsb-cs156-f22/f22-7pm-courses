import React from 'react';

import PersonalSectionsTable from 'main/components/PersonalSections/PersonalSectionsTable';
import { oneSection } from 'fixtures/personalSectionsFixtures';
// import { currentUserFixtures } from 'fixtures/currentUserFixtures';

export default {
    title: 'components/PersonalSections/PersonalSectionsTable',
    component: PersonalSectionsTable
};

const Template = (args) => {
    return (
        <PersonalSectionsTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    personalSections: []
};

export const oneSection = Template.bind({});

oneSection.args = {
    personalSections: oneSection
    // currentUser: currentUserFixtures.adminUser
};

// export const ThreeSections = Template.bind({});

// ThreeSections.args = {
//     personalSections: personalSectionsFixtures.threePersonalSections
// };

// export const FiveSections = Template.bind({});

// FiveSections.args = {
//     personalSections: personalSectionsFixtures.fivePersonalSections
// };



// export const ThreeSubjectsUser = Template.bind({});
// ThreeSubjectsUser.args = {
//     personalSections: personalSectionsFixtures.threePersonalSections,
//     currentUser: currentUserFixtures.adminUser
// };

