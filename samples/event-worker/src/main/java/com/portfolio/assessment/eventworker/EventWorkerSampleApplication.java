package com.portfolio.assessment.eventworker;

import com.portfolio.assessment.eventworker.adapter.memory.InMemoryEventRepository;
import com.portfolio.assessment.eventworker.adapter.memory.InMemoryIdempotencyStore;
import com.portfolio.assessment.eventworker.adapter.memory.InMemoryRetryQueue;
import com.portfolio.assessment.eventworker.adapter.memory.InMemoryStateStore;
import com.portfolio.assessment.eventworker.port.EventRepository;
import com.portfolio.assessment.eventworker.port.IdempotencyStore;
import com.portfolio.assessment.eventworker.port.RetryQueue;
import com.portfolio.assessment.eventworker.port.StateStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventWorkerSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventWorkerSampleApplication.class, args);
    }

    @Bean
    IdempotencyStore idempotencyStore() {
        return new InMemoryIdempotencyStore();
    }

    @Bean
    StateStore stateStore() {
        return new InMemoryStateStore();
    }

    @Bean
    EventRepository eventRepository() {
        return new InMemoryEventRepository();
    }

    @Bean
    RetryQueue retryQueue() {
        return new InMemoryRetryQueue();
    }
}
