package me.kirok.restapi.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception {

        Event event = Event.builder()
            .name("spring")
            .description("rest api")
            .beginEnrollmentDateTime(LocalDateTime.of(2021, 7, 30, 21, 21))
            .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 30, 21, 21))
            .beginEventDateTime(LocalDateTime.of(2021, 9, 1, 21, 21))
            .endEventDateTime(LocalDateTime.of(2021, 9, 2, 21, 21))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남").build();

        mockMvc.perform(
            post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists());
    }


}
