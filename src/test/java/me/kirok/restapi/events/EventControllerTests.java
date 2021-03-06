package me.kirok.restapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import me.kirok.restapi.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;


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
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(
                header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("eventStatus").value("DRAFT"))
            .andDo(document(
                "create-event",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("query-events").description("link to query events"),
                    linkWithRel("update-event").description("link to update-event"),
                    linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("name").description("name of new event"),
                    fieldWithPath("description").description("description of new event"),
                    fieldWithPath("beginEnrollmentDateTime")
                        .description("begin date time of event enrollment"),
                    fieldWithPath("closeEnrollmentDateTime")
                        .description("close date time of event enrollment"),
                    fieldWithPath("beginEventDateTime").description("begin date time of new event"),
                    fieldWithPath("endEventDateTime").description("close date time of new event"),
                    fieldWithPath("location").description("location of new event"),
                    fieldWithPath("basePrice").description("base price of new event"),
                    fieldWithPath("maxPrice").description("max price of new event"),
                    fieldWithPath("limitOfEnrollment").description("limit of event enrollment")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION)
                        .description("The url to view the newly created event"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of response")
                ),
                relaxedResponseFields(
                    fieldWithPath("id").description("identifier of new event"),
                    fieldWithPath("name").description("name of new event"),
                    fieldWithPath("description").description("description of new event"),
                    fieldWithPath("beginEnrollmentDateTime")
                        .description("begin date time of event enrollment"),
                    fieldWithPath("closeEnrollmentDateTime")
                        .description("close date time of event enrollment"),
                    fieldWithPath("beginEventDateTime").description("begin date time of new event"),
                    fieldWithPath("endEventDateTime").description("close date time of new event"),
                    fieldWithPath("location").description("location of new event"),
                    fieldWithPath("basePrice").description("base price of new event"),
                    fieldWithPath("maxPrice").description("max price of new event"),
                    fieldWithPath("limitOfEnrollment").description("limit of event enrollment"),
                    fieldWithPath("free").description("it tells if this event is free or not"),
                    fieldWithPath("offline")
                        .description("it tells if this event is offline or not"),
                    fieldWithPath("eventStatus").description("event status"),
                    fieldWithPath("_links.self.href").description("link to self"),
                    fieldWithPath("_links.query-events.href").description("link to query-events"),
                    fieldWithPath("_links.update-event.href").description("link to update-event"),
                    fieldWithPath("_links.profile.href").description("link to profile")

                )

            ))
        ;


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
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors[0].objectName").exists())
            .andExpect(jsonPath("errors[0].defaultMessage").exists())
            .andExpect(jsonPath("errors[0].code").exists())
            .andExpect(jsonPath("_links.index").exists())
        ;
    }


    @Test
    @DisplayName("30개의 이벤트를 10개씩(리스트) 두번쨰 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // when
        this.mockMvc.perform(
            get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("get-events"))
        ;
    }

    @Test
    @DisplayName("이벤트 1개 조회하기")
    public void getEvent() throws Exception {
        //given
        Event event = this.generateEvent(100);

        //when & then
        this.mockMvc.perform(
            get("/api/events/{id}", event.getId())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").exists())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("get-an-event"))
        ;

    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        //when & then
        this.mockMvc.perform(get("/api/events/22222"))
            .andExpect(status().isNotFound())
        ;

    }

    @Test
    @DisplayName("id를 받아서 해당 이벤트를 수정 성공하는 테스트")
    public void updateEvent() throws Exception {
        // given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String updateName = "updated event";
        eventDto.setName(updateName);

        // when
        this.mockMvc.perform(
            put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").value(updateName))
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("update-event"))
        ;

    }

    @Test
    @DisplayName("입력값이 비어있는 경우 이벤트 수정 실패(bad request)")
    public void updateEvent_400_Empty() throws Exception {
        // given
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        // when
        this.mockMvc.perform(
            put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("입력값이 잘못된 경우 이벤트 수정 실패(bad request)")
    public void updateEvent_400_Wrong() throws Exception {
        // given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(100);

        // when
        this.mockMvc.perform(
            put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("없는 이벤트를 수정 실패(404)")
    public void updateEvent_404() throws Exception {
        // given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // when
        this.mockMvc.perform(
            put("/api/events/223123", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isNotFound())
        ;

    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
            .name("event " + i)
            .description("rest api")
            .beginEnrollmentDateTime(LocalDateTime.of(2021, 7, 30, 21, 21))
            .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 30, 21, 21))
            .beginEventDateTime(LocalDateTime.of(2021, 9, 1, 21, 21))
            .endEventDateTime(LocalDateTime.of(2021, 9, 2, 21, 21))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남")
            .free(false)
            .offline(true)
            .eventStatus(EventStatus.DRAFT)
            .build();

        return this.eventRepository.save(event);
    }

}
