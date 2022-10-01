package com.spring.helloworld;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void Test_helloWorld() {
        HelloWorldController controller = new HelloWorldController();
        String response = controller.helloWorld();
        assertEquals("Hello World!",response);
    }
}