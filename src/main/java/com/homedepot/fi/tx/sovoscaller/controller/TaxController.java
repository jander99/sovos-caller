package com.homedepot.fi.tx.sovoscaller.controller;

import com.homedepot.fi.tx.sovoscaller.services.SovosCaller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TaxController {

    private SovosCaller sovosCaller;

    public TaxController(SovosCaller sovosCaller) {
        this.sovosCaller = sovosCaller;
    }

    @RequestMapping(method= RequestMethod.GET, path = "/rest")
    public ResponseEntity<String> getTestTax() throws Exception {
        return sovosCaller.callSovosRestTemplate();
    }

    @RequestMapping(method = RequestMethod.GET,
        path = "/reactive",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<String> getReactiveTax() {
        return sovosCaller.callSovosWebClient();
    }

    /*
    @RequestMapping(method = RequestMethod.GET,
        path = "/bean",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<String> getBean() {

    }
    */
}
