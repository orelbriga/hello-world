package com.spring.helloworld;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void helloWorld() {
        HelloWorldController controller = new HelloWorldController();
        String response = controller.helloWorld();
        assertEquals("Hello World",response);
    }
}