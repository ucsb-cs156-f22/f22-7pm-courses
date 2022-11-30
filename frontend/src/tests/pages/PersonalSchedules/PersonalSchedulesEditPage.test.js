import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import PersonalScheduleEditPage from "main/pages/PersonalSchedules/PersonalSchedulesEditPage";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        useParams: () => ({
            id: 5
        }),
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});

describe("PersonalScheduleEditPage tests", () => {
    describe("tests where backend is working normally", () => {
        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/personalschedules", { params: { id: 5 } }).reply(200, {
                "id": 5,
                "name": "TestName",
                "description": "TestDescription",
                "quarter": "20222"
            });
            axiosMock.onPut('/api/personalschedules/update').reply(200, {
                "id": 5,
                "name": "TestName2",
                "description": "TestDescription2",
                "quarter": "20223"
            });
        });

        const queryClient = new QueryClient();
        test("renders without crashing", () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <PersonalScheduleEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
        });

        test("Is populated with the data provided", async () => {
            const { getByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <PersonalScheduleEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("PersonalScheduleForm-id")).toBeInTheDocument());

            const nameField = getByTestId("PersonalScheduleForm-name");
            const descriptionField = getByTestId("PersonalScheduleForm-description");
            const quarterField = getByTestId("PersonalScheduleForm-quarter");
           

            expect(nameField).toHaveValue("TestName");
            expect(descriptionField).toHaveValue("TestDescription");
            expect(quarterField).toHaveValue("20222");
            
        });

        test("Changes when you click Update", async () => {
            const { getByTestId, getByText } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <PersonalScheduleEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("PersonalScheduleForm-id")).toBeInTheDocument());

            const nameField = getByTestId("PersonalScheduleForm-name");
            const descriptionField = getByTestId("PersonalScheduleForm-description");
            const quarterField = getByTestId("PersonalScheduleForm-quarter");
           
            expect(nameField).toHaveValue("TestName");
            expect(descriptionField).toHaveValue("TestDescription");
            expect(quarterField).toHaveValue("2022");

            const submitButton = screen.getByText("Update");

            expect(submitButton).toBeInTheDocument();

            fireEvent.change(nameField, { target: { value: "TestName2" } })
            fireEvent.change(descriptionField, { target: { value: "TestDescription2" } })
            fireEvent.change(quarterField, { target: { value: "20223" } })
            

            fireEvent.click(submitButton);

            await waitFor(() => expect(mockToast).toHaveBeenCalled());
            expect(mockToast).toBeCalledWith("PersonalSchedule Updated - id: 5 name: TestName2");
            expect(mockNavigate).toBeCalledWith({ "to": "/admin/personalschedules/list" });

            expect(axiosMock.history.put.length).toBe(1); // times called
            expect(axiosMock.history.put[0].params).toEqual({ id: 1 });
            expect(axiosMock.history.put[0].data).toBe(JSON.stringify({
                "id": 5,
                "name": "TestName2",
                "description": "TestDescription2",
                "quarter": "20223"
            })); // posted object
        });
    });
});