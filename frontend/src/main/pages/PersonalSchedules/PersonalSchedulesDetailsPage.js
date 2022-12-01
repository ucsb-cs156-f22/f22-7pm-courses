import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import PersonalSchedulesTable from 'main/components/PersonalSchedules/PersonalSchedulesTable';
import { useBackend, _useBackendMutation } from "main/utils/useBackend";
import PersonalSectionsTable from "main/components/PersonalSections/PersonalSectionsTable";


export default function PersonalSchedulesDetailsPage() {
  let { id } = useParams();

  const { data: personalSchedule, _scheduleError, _scheduleStatus } =
    useBackend(
        // Stryker disable all : hard to test for query caching
      [`/api/personalschedules?id=${id}`],
      {  // Stryker disable next-line all : GET is the default, so changing this to "" doesn't introduce a bug
        method: "GET",
        url: `/api/personalschedules?id=${id}`,
        params: {
          id
        }
      }
    );

  const { data: personalSections, _error, _status } =
  useBackend(
      // Stryker disable all : hard to test for query caching
    [`/api/personalSections/all`],
    {  // Stryker disable next-line all : GET is the default, so changing this to "" doesn't introduce a bug
      method: "GET",
      url: `/api/personalSections/all?psId=${id}`,
      params: {
        id
      }
    }
  );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>PersonalSchedules Details</h1>
        {personalSchedule &&
                <PersonalSchedulesTable personalSchedules={[personalSchedule]} showButtons={false} />
            }
        {personalSections && <PersonalSectionsTable personalSections={personalSections} />
            }
      </div>
    </BasicLayout>
  )
}
