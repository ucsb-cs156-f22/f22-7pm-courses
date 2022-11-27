import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import "@testing-library/jest-dom/extend-expect";
import { useState } from "react";

import SinglePersonalScheduleDropdown from "main/components/PersonalSchedules/SinglePersonalScheduleDropdown";
import { personalSchedulesFixtures } from "fixtures/personalSchedulesFixtures";

jest.mock("react", () => ({
  ...jest.requireActual("react"),
  useState: jest.fn(),
  compareValues: jest.fn(),
}));

describe("SinglePersonalScheduleDropdown tests", () => {

  beforeEach(() => {
    jest.spyOn(console, 'error')
    console.error.mockImplementation(() => null);
  });

  beforeEach(() => {
    useState.mockImplementation(jest.requireActual("react").useState);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    console.error.mockRestore();
 })

  const personalSchedule = jest.fn();
  const setPersonalSchedule = jest.fn();

  test("renders without crashing on one personalSchedule", () => {
    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.onePersonalScheduleArray}
        personalSchedule={personalSchedulesFixtures.onePersonalScheduleArray}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );
  });

  test("renders without crashing on three personalSchedules", () => {
    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.threePersonalSchedules}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );
  });

  test("when I select an object, the value changes", async () => {
    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.threePersonalSchedules}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );
    
    expect(await screen.findByLabelText("Personal Schedule")).toBeInTheDocument();

    const selectPersonalSchedule = screen.getByLabelText("Personal Schedule");
    userEvent.selectOptions(selectPersonalSchedule, "3");
    expect(setPersonalSchedule).toBeCalledWith("3");
  });

  test("if I pass a non-null onChange, it gets called when the value changes", async () => {
    const onChange = jest.fn();
    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.threePersonalSchedules}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
        onChange={onChange}
      />
    );
    
    expect(await screen.findByLabelText("Personal Schedule")).toBeInTheDocument();

    const selectPersonalSchedule = screen.getByLabelText("Personal Schedule");
    userEvent.selectOptions(selectPersonalSchedule, "2");
    await waitFor(() => expect(setPersonalSchedule).toBeCalledWith("2"));
    await waitFor(() => expect(onChange).toBeCalledTimes(1));

    // x.mock.calls[0][0] is the first argument of the first call to the jest.fn() mock x
    const event = onChange.mock.calls[0][0];
    expect(event.target.value).toBe("2");
  });

  test("default label is Personal Schedule", async () => {
    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.threePersonalSchedules}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );
    
    expect(await screen.findByLabelText("Personal Schedule")).toBeInTheDocument();
  });

  test("keys / testids are set correctly on options", async () => {
    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.threePersonalSchedules}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );

    const expectedKey = "psd1-option-0";
    await waitFor(() => expect(screen.getByTestId(expectedKey).toBeInTheDocument));
  });

  test("when localstorage has a value, it is passed to useState", async () => {
    const getItemSpy = jest.spyOn(Storage.prototype, "getItem");
    getItemSpy.mockImplementation(() => "3");

    const setPersonalScheduleStateSpy = jest.fn();
    useState.mockImplementation((x) => [x, setPersonalScheduleStateSpy]);

    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.threePersonalSchedules}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );

    await waitFor(() => expect(useState).toBeCalledWith("3"));
  });

  test("when localstorage has no value, first element of personalSchedule list is passed to useState", async () => {
    const getItemSpy = jest.spyOn(Storage.prototype, "getItem");
    getItemSpy.mockImplementation(() => null);

    const setPersonalScheduleStateSpy = jest.fn();
    useState.mockImplementation((x) => [x, setPersonalScheduleStateSpy]);

    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={personalSchedulesFixtures.threePersonalSchedules}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );

    await waitFor(() =>
      expect(useState).toBeCalledWith(expect.objectContaining({}))
    );
  });

  test("When no personalSchedules, dropdown is blank", async () => {
    render(
      <SinglePersonalScheduleDropdown
        personalSchedules={[]}
        personalSchedule={personalSchedule}
        setPersonalSchedule={setPersonalSchedule}
        controlId="psd1"
      />
    );

    const expectedKey = "psd1";
    expect(screen.queryByTestId(expectedKey)).toBeNull();
  });
});
