import React from 'react';

import PersonalSectionsTable from 'main/components/PersonalSections/PersonalSectionsTable';
import { oneSection, threeSections, fiveSections } from 'fixtures/personalSectionsFixtures';
import { currentUserFixtures } from 'fixtures/currentUserFixtures';

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

export const OneSection = Template.bind({});

OneSection.args = {
    personalSections: oneSection
};

export const ThreeSections = Template.bind({});

ThreeSections.args = {
    personalSections: threeSections
};

export const FiveSections = Template.bind({});

FiveSections.args = {
    personalSections: fiveSections
};



export const ThreeSubjectsUser = Template.bind({});
ThreeSubjectsUser.args = {
    personalSections: threeSections,
    currentUser: currentUserFixtures.adminUser
};

