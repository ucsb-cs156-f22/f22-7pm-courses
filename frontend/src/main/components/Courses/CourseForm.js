import React, { useState } from 'react'
import { Button, Form } from 'react-bootstrap';
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'

import SinglePersonalScheduleDropdown from "../PersonalSchedules/SinglePersonalScheduleDropdown";
import { useBackend } from "main/utils/useBackend";

let initialState = true;
let initialRender = true;

function CourseForm({ initialCourse, submitAction, buttonLabel = "Create" }) {

    const { data: personalSchedules, error: _error, status: _status } =
    useBackend(
        ["/api/personalschedules/all"],
        { method: "GET", url: "/api/personalschedules/all" },
        []
    );

    if(initialState && personalSchedules[0] != null){
        setTimeout(() => {  
            localStorage.setItem("CourseForm-psId", personalSchedules[0].id);
            initialState = false;
        }, 200);
    }

    let localPersonalSchedule;
    if(initialRender){
        setTimeout(() => {  
            localPersonalSchedule = localStorage.getItem("CourseForm-psId");
            initialRender = false;
        }, 250);
    }
    else{
        localPersonalSchedule = localStorage.getItem("CourseForm-psId");
    }

    const [personalSchedule, setPersonalSchedule] = useState(localPersonalSchedule || {});

    // Stryker disable all
    const {
        register,
        formState: { errors },
        handleSubmit,
    } = useForm(
        { defaultValues: initialCourse || {}, }
    );
    // Stryker enable all

    const navigate = useNavigate();

    return (

        <Form onSubmit={handleSubmit(submitAction)}>

            {initialCourse && (
                <Form.Group className="mb-3" >
                    <Form.Label htmlFor="id">Id</Form.Label>
                    <Form.Control
                        data-testid="CourseForm-id"
                        id="id"
                        type="text"
                        {...register("id")}
                        value={initialCourse.id}
                        disabled
                    />
                </Form.Group>
            )}

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="enrollCd">Enrollment Code</Form.Label>
                <Form.Control
                    data-testid="CourseForm-enrollCd"
                    id="enrollCd"
                    type="text"
                    isInvalid={Boolean(errors.enrollCd)}
                    {...register("enrollCd", {
                        required: "Enroll Code is required."
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.enrollCd?.message}
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3" data-testid="CourseForm-psId">
                <SinglePersonalScheduleDropdown
                    personalSchedule={personalSchedule.id}
                    setPersonalSchedule={setPersonalSchedule} 
                    controlId={"CourseForm-psId"}
                    label={"Personal Schedule"}
                    personalSchedules={personalSchedules}/>
            </Form.Group>

            <Button
                type="submit"
                data-testid="CourseForm-submit"
            >
                {buttonLabel}
            </Button>
            <Button
                variant="Secondary"
                onClick={() => navigate(-1)}
                data-testid="CourseForm-cancel"
            >
                Cancel
            </Button>

        </Form>

    )
}

export default CourseForm;