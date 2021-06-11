package me.kirok.restapi.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("이벤트를 정상적으로 생성하고 created를 반환")
    public void createEvent() throws Exception {

        EventDto eventDto = EventDto.builder()
            .name("spring")
            .description("rest api")
            .beginEnrollmentDateTime(LocalDateTime.of(2021, 7, 30, 21, 21))
            .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 30, 21, 21))
            .beginEventDateTime(LocalDateTime.of(2021, 9, 1, 21, 21))
            .endEventDateTime(LocalDateTime.of(2021, 9, 2, 21, 21))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남")
            .build();

        mockMvc.perform(
            post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("eventStatus").value("DRAFT"));


    }

    @Test
    @DisplayName("DTO에 없는 속성을 입력했을 경우에 bad request를 반환")
    public void createEvent_Bad_Request() throws Exception {

        Event event = Event.builder()
            .id(100)
            .name("spring")
            .description("rest api")
            .beginEnrollmentDateTime(LocalDateTime.of(2021, 7, 30, 21, 21))
            .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 30, 21, 21))
            .beginEventDateTime(LocalDateTime.of(2021, 9, 1, 21, 21))
            .endEventDateTime(LocalDateTime.of(2021, 9, 2, 21, 21))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남")
            .eventStatus(EventStatus.PUBLISHED)
            .build();

        mockMvc.perform(
            post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
            .andDo(print())
            .andExpect(status().isBadRequest())

        ;


    }

    @Test
    @DisplayName("필수 속성의 입력값이 비어있을 때 bad request를 반환")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {

        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(eventDto))
        )
            .andExpect(status().isBadRequest());


    }


    @Test
    @DisplayName("입력 값이 잘못되었을 때 bad request를 반환")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {

        EventDto eventDto = EventDto.builder()
            .name("spring")
            .description("rest api")
            .beginEnrollmentDateTime(LocalDateTime.of(2021, 7, 30, 21, 21))
            .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 25, 21, 21))
            .beginEventDateTime(LocalDateTime.of(2021, 9, 1, 21, 21))
            .endEventDateTime(LocalDateTime.of(2021, 9, 2, 21, 21))
            .basePrice(10000)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남")
            .build();

        this.mockMvc.perform(post("/api/events/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(eventDto))
        )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("bad request 시 응답 본문 반환 테스트")
    public void createEvent_Bad_Request_Message() throws Exception {

        EventDto eventDto = EventDto.builder()
            .name("spring")
            .description("rest api")
            .beginEnrollmentDateTime(LocalDateTime.of(2021, 9, 30, 21, 21))
            .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 25, 21, 21))
            .beginEventDateTime(LocalDateTime.of(2021, 9, 1, 21, 21))
            .endEventDateTime(LocalDateTime.of(2021, 9, 2, 21, 21))
            .basePrice(10000)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남")
            .build();

        this.mockMvc.perform(post("/api/events/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(eventDto))
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].objectName").exists())
            .andExpect(jsonPath("$[0].defaultMessage").exists())
            .andExpect(jsonPath("$[0].code").exists());
    }


}
