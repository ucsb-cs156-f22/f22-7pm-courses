import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { BrowserRouter as Router } from "react-router-dom";
import userEvent from "@testing-library/user-event";

import CourseForm from "main/components/Courses/CourseForm";
import { coursesFixtures } from "fixtures/pscourseFixtures";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));

describe("CourseForm tests", () => {
    const axiosMock = new AxiosMockAdapter(axios);

    beforeEach(() => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
        axiosMock.onGet("/api/personalschedules/all").reply(200, [{
            "id": 13,
            "name": "TestName",
            "description": "TestDescription",
            "quarter": "20222"
        }]);
    });
    const queryClient = new QueryClient();

    test("renders correctly", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <Router>
                    <CourseForm />
                </Router>
            </QueryClientProvider>
        );

        expect(await screen.findByText(/Personal Schedule/)).toBeInTheDocument();
        expect(screen.getByText(/Enrollment Code/)).toBeInTheDocument();
        expect(screen.getByText(/Create/)).toBeInTheDocument();
    });

    test("renders correctly when passing in a Course", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <Router>
                    <CourseForm initialCourse={coursesFixtures.oneCourse} />
                </Router>
            </QueryClientProvider>
        );

        expect(await screen.findByTestId(/CourseForm-id/)).toBeInTheDocument();
        expect(screen.getByText(/Id/)).toBeInTheDocument();
        expect(screen.getByTestId(/CourseForm-id/)).toHaveValue("27");
    });

    test("Correct Error messages on missing input", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <Router  >
                    <CourseForm />
                </Router>
            </QueryClientProvider>
        );
        expect(await screen.findByTestId("CourseForm-submit")).toBeInTheDocument();
        const submitButton = screen.getByTestId("CourseForm-submit");

        fireEvent.click(submitButton);

        expect(await screen.findByText(/Enroll Code is required./)).toBeInTheDocument();
        
    });

    // test("No Error messages on good input", async () => {
    //     const mockSubmitAction = jest.fn();

    //     render(
    //         <QueryClientProvider client={queryClient}>
    //             <Router>
    //                 <CourseForm submitAction={mockSubmitAction} />
    //             </Router>
    //         </QueryClientProvider>
    //     );

    //     expect(await screen.findByTestId("CourseForm-psId")).toBeInTheDocument();

    //     const selectPsId = screen.getByLabelText("Personal Schedule");
    //     userEvent.selectOptions(selectPsId, "13");
    //     const enrollCd = screen.getByTestId("CourseForm-enrollCd");
    //     const submitButton = screen.getByTestId("CourseForm-submit");

    //     fireEvent.change(selectPsId, { target: { value: '13' } });
    //     fireEvent.change(enrollCd, { target: { value: '20124' } });
    //     fireEvent.click(submitButton);

    //     await waitFor(() => expect(mockSubmitAction).toHaveBeenCalled());

    //     expect(screen.queryByText(/Enroll Code is required./)).not.toBeInTheDocument();
    //     expect(selectPsId.value).toBe("13");
    //     expect(enrollCd).toHaveValue("20124");
    // });

    test("that navigate(-1) is called when Cancel is clicked", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <Router>
                    <CourseForm />
                </Router>
            </QueryClientProvider>
        );
        expect(await screen.findByTestId("CourseForm-cancel")).toBeInTheDocument();
        const cancelButton = screen.getByTestId("CourseForm-cancel");

        fireEvent.click(cancelButton);

        await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith(-1));
    });
});
