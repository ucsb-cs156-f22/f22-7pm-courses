import { render, screen } from "@testing-library/react";
import { fiveSections } from "fixtures/personalSectionsFixtures";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import PersonalSectionsTable from "main/components/PersonalSections/PersonalSectionsTable";


const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));

describe("PersonalSections tests", () => {
  const queryClient = new QueryClient();


  test("renders without crashing for empty table", () => {

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <PersonalSectionsTable personalSections={[]} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });


  test("Has the expected column headers and content", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <PersonalSectionsTable personalSections={fiveSections} />
        </MemoryRouter>
      </QueryClientProvider>
      );

      const expectedHeaders = ["Course ID", "Enroll Code", "Section","Title", "Enrolled", "Location", "Days", "Time", "Instructor"];
      const expectedFields = ["courseId", "classSections[0].enrollCode", "classSections[0].section","title", "enrolled", "location", "days", "time", "instructor"];
      const testId = "PersonalSectionsTable";

      expectedHeaders.forEach((headerText) => {
        const header = screen.getByText(headerText);
        expect(header).toBeInTheDocument();
      });


      expectedFields.forEach((field) => {
        const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
        expect(header).toBeInTheDocument();
      });
      expect(screen.getByTestId(`${testId}-cell-row-0-col-courseId`)).toHaveTextContent("ECE 5");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-courseId`)).not.toHaveTextContent("ECE 5 "); // covers mutation of not successfully removing all whitespaces
      expect(screen.getByTestId(`${testId}-cell-row-0-col-classSections[0].enrollCode`)).toHaveTextContent("12591");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-classSections[0].section`)).toHaveTextContent("0100");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-title`)).toHaveTextContent("INTRO TO ECE");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-enrolled`)).toHaveTextContent("78/120");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-location`)).toHaveTextContent("PHELP 1260");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-days`)).toHaveTextContent("M");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-time`)).toHaveTextContent("12:30 PM - 1:45 PM");
      expect(screen.getByTestId(`${testId}-cell-row-0-col-instructor`)).toHaveTextContent("HESPANHA J P");
      

  });


});

