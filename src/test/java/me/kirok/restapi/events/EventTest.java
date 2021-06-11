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

    @Test
    public void testFree() {

        //given
        Event event = Event.builder()
            .basePrice(0)
            .maxPrice(0)
            .build();
        //when
        event.update();
        //then
        assertThat(event.isFree()).isTrue();

        //given
        event = Event.builder()
            .basePrice(100)
            .maxPrice(0)
            .build();
        //when
        event.update();
        //then
        assertThat(event.isFree()).isFalse();

        //given
        event = Event.builder()
            .basePrice(0)
            .maxPrice(100)
            .build();
        //when
        event.update();
        //then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {
        //given
        Event event = Event.builder()
            .location("강남")
            .build();
        //when
        event.update();
        //then
        assertThat(event.isOffline()).isTrue();

        //given
        event = Event.builder()
            .build();
        //when
        event.update();
        //then
        assertThat(event.isOffline()).isFalse();


    }


}