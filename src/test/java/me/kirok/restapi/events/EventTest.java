package me.kirok.restapi.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
            .name("REST API")
            .description("with Spring")
            .build();

        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // given
        String name = "event";
        String description = "spring";

        // when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

}