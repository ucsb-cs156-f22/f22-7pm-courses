import {convertToFraction, formatLocation, isSection, formatDays, formatTime, formatInstructors} from "main/utils/sectionUtils"

import { boldIfNotSection, fraction_w_percent } from "main/utils/sectionUtils";

const testTimeLocations = [
    {
      "room": "1",
      "building": "LOC1",
      "roomCapacity": "90",
      "days": "M W",
      "beginTime": "15:30",
      "endTime": "16:45"
    },
    {
        "room": "2",
        "building": "LOC2",
        "roomCapacity": "90",
        "days": "R F",
        "beginTime": "10:30",
        "endTime": "11:45"
    }
]

const testTimeLocations1 = [
    {
      "room": "1",
      "building": "LOC1",
      "roomCapacity": "90",
      "days": null,
      "beginTime": "15:30",
      "endTime": "16:45"
    },
    {
        "room": "2",
        "building": "LOC2",
        "roomCapacity": "90",
        "days": "R F",
        "beginTime": "10:30",
        "endTime": "11:45"
    }
]

const testInstructors = [
    {
      "instructor": "HESPANHA J P",
      "functionCode": "Teaching and in charge"
    },
    {
        "instructor": "JOHN S",
        "functionCode": "Teaching and in charge"
    }
]

describe("To bold tests", () => {
    test("If correct number bolds", () => {
        expect(boldIfNotSection("2000")).toStrictEqual(<div style={{fontWeight: "bold"}}>2000</div>);
        expect(() => {boldIfNotSection("abc"); }).toThrow("The parameter must be a number!");
        expect(boldIfNotSection("2002")).toBe("2002");
    });
});

describe ("section utils tests", () => {

    test("fraction with percent test", () => {
        expect(fraction_w_percent(null, null)).toBe("");
        expect(fraction_w_percent(null, "44")).toBe("44");
        expect(fraction_w_percent("10", "100")).toBe("10/100 (10%)");
        expect(fraction_w_percent('2', null)).toBe("");
    });

    test("convertToFraction one null test 1" , () => {
        expect(convertToFraction(null, "100")).toBe("");
    }); 

    test("convertToFraction one null test 3" , () => {
        expect(convertToFraction("10", "100")).toBe("10/100");
    }); 

    test("convertToFraction one null test 2" , () => {
        expect(convertToFraction("100", null)).toBe("");
    }); 

    test("isSection true test" , () => {
        expect(isSection("0104")).toBe(true);
    }); 

    test("isSection false test" , () => {
        expect(isSection("0100")).toBe(false);
    }); 

    test("formatLocation test" , () => {
        expect(formatLocation(testTimeLocations)).toBe("LOC1 1, LOC2 2");
    }); 

    test("formatDays test 1" , () => {
        expect(formatDays(testTimeLocations)).toBe("M W, R F");
    });
    
    test("formatDays test 2" , () => {
        expect(formatDays(testTimeLocations1)).toBe("R F");
    });

    test("formatTime test 3" , () => {
        expect(formatTime(testTimeLocations)).toBe("3:30 PM - 4:45 PM, 10:30 AM - 11:45 AM");
    }); 

    test("formatInstructors test" , () => {
        expect(formatInstructors(testInstructors)).toBe("HESPANHA J P, JOHN S");
    }); 
    test("formatLocation null test" , () => {
        expect(formatLocation(null)).toBe("");
    }); 

    test("formatDays null test" , () => {
        expect(formatDays(null)).toBe("");
    });

    test("formatTime null test" , () => {
        expect(formatTime(null)).toBe("");
    }); 

    test("formatInstructors null test" , () => {
        expect(formatInstructors(null)).toBe("");
    }); 

})