package com.essdeebee.action;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThemeToggleActionTest {

    @Test
    void delegatesActionPerformedToHandler() {
        AtomicInteger calls = new AtomicInteger();
        ThemeToggleAction action = new ThemeToggleAction(event -> calls.incrementAndGet());

        action.handleAction(null);

        assertEquals(1, calls.get());
    }
}
