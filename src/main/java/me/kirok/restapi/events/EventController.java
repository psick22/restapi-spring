package me.kirok.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;
import javax.validation.Valid;
import me.kirok.restapi.index.IndexController;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper,
        EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {

        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedModel = assembler.toModel(page,
            e -> EntityModel.of(
                e,
                linkTo(EventController.class).slash(e.getId()).withSelfRel()
            )
        );

        pagedModel
            .add(Link.of("http://localhost:8080/docs/index.html#resources-events-list", "profile"));

        return ResponseEntity.ok(pagedModel);

    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EntityModel<Event> eventModel = EntityModel.of(
            event,
            linkTo(EventController.class).slash(event.getId()).withSelfRel(),
            Link.of("http://localhost:8080/docs/index.html#resources-events-get", "profile")
        );

        return ResponseEntity.ok(eventModel);
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequestResponse(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequestResponse(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(event.getId());
        URI createdUri = selfLinkBuilder.toUri();

        EntityModel<Event> eventResource =
            EntityModel.of(
                event,
                linkTo(EventController.class).withRel("query-events"),
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update-event"),
                Link.of("http://localhost:8080/docs/index.html", "profile")
            );

        System.out.println("eventResource = " + eventResource);

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
        @RequestBody @Valid EventDto eventDto, Errors errors) {

        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequestResponse(errors);
        }

        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequestResponse(errors);
        }

        Event event = optionalEvent.get();
        this.modelMapper.map(eventDto, event);
        Event savedEvent = this.eventRepository.save(event);

        EntityModel<Event> eventModel = EntityModel.of(
            savedEvent,
            linkTo(EventController.class).slash(event.getId()).withSelfRel(),
            Link.of("http://localhost:8080/docs/index.html#resources-events-update", "profile")

        );

        return ResponseEntity.ok(eventModel);

    }


    private ResponseEntity<EntityModel<HashMap<String, Errors>>> badRequestResponse(
        Errors errors) {
        HashMap<String, Errors> errorsHashMap = new HashMap<>();
        errorsHashMap.put("errors", errors);

        EntityModel<HashMap<String, Errors>> response =
            EntityModel.of(
                errorsHashMap,
                linkTo(methodOn(IndexController.class).index()).withRel("index")
            );

        return ResponseEntity.badRequest().body(response);
    }


}
