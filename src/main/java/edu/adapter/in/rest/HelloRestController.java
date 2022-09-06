package edu.adapter.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
class HelloRestController {

    @GetMapping(path = "/hello")
    public HelloResponse getHelloResponse() {
        log.info("Responding with Hello!");
        return new HelloResponse("Hello World!");
    }

}
