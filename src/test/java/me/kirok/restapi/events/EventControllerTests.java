package me.kirok.restapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext ctx;


    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
            .apply(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint()))
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .alwaysDo(print())
            .build();
    }

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
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.query-events").exists())
            .andExpect(jsonPath("_links.update-event").exists())
            .andDo(document(
                "create-event",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("query-events").description("link to query events"),
                    linkWithRel("update-event").description("link to update-event")
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
                    fieldWithPath("_links.update-event.href").description("link to update-event")

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
            .andExpect(jsonPath("$[0].objectName").exists())
            .andExpect(jsonPath("$[0].defaultMessage").exists())
            .andExpect(jsonPath("$[0].code").exists());
    }


}
