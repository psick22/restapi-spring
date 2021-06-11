package me.kirok.restapi.events;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {

        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {

            errors.rejectValue("basePrice", "wrongValue", "BasePrice 입력값이 유효하지 않습니다.");
            errors.rejectValue("maxPrice", "wrongValue", "maxPrice 입력값이 유효하지 않습니다.");

        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();

        if (endEventDateTime.isBefore(beginEventDateTime) ||
            endEventDateTime.isBefore(closeEnrollmentDateTime) ||
            endEventDateTime.isBefore(beginEnrollmentDateTime)
        ) {
            errors
                .rejectValue("endEventDateTime", "wrongValue",
                    "endEventDateTime 입력값이 유효하지 않습니다.");
        }

        if (closeEnrollmentDateTime.isBefore(beginEnrollmentDateTime)) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue",
                "closeEnrollmentDateTime 입력값이 유효하지 않습니다.");
        }

        if (beginEventDateTime.isBefore(beginEnrollmentDateTime) || beginEventDateTime
            .isBefore(closeEnrollmentDateTime)) {
            errors.rejectValue("beginEventDateTime", "wrongValue",
                "beginEventDateTime 입력값이 유효하지 않습니다.");

        }

    }

}
